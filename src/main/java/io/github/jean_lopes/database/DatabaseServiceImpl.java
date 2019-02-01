package io.github.jean_lopes.database;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.jean_lopes.domain.search_history.LastSearch;
import io.github.jean_lopes.domain.transaction_history.LastTransaction;
import io.github.jean_lopes.domain.transaction_history.TransactionKind;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.reactivex.MaybeHelper;
import io.vertx.reactivex.SingleHelper;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import io.vertx.reactivex.ext.sql.SQLClientHelper;

public class DatabaseServiceImpl implements DatabaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseServiceImpl.class);
    
    private final Map<Command, String> sql;

    private final JDBCClient client;

    DatabaseServiceImpl(io.vertx.ext.jdbc.JDBCClient client, Map<Command, String> sql, Handler<AsyncResult<DatabaseService>> readyHandler) {
        this.client = new JDBCClient(client);
        this.sql = sql;
        LOGGER.debug("Testing database connection");
        SQLClientHelper.usingConnectionSingle(this.client, conn -> conn
                .rxExecute("SELECT 1")
                .andThen(Single.just(this)))
                .doOnSuccess(ar -> LOGGER.debug("Connection test succeeded"))
                .doOnError(error -> LOGGER.error("Connection test failed", error))
                .subscribe(SingleHelper.toObserver(readyHandler));
    }

    @Override
    public DatabaseService lastSearch(String cpf, Handler<AsyncResult<LastSearch>> resultHandler) {
        LOGGER.debug("Executing Last search with CPF: {}", cpf);
        
        JsonArray params = new JsonArray().add(cpf);

        String cmd = sql.get(Command.FIND_LAST_SEARCH_BY_CPF);
        
        client.rxQuerySingleWithParams(cmd, params)
                .flatMap(this::createLastSearch)
                .toSingle(new LastSearch((Instant) null))
                .doOnError(logDatabaseError(cmd))
                .subscribe(SingleHelper.toObserver(resultHandler));

        return this;
    }

    private Maybe<LastSearch> createLastSearch(JsonArray result) {
        if (!Objects.isNull(result) && result.size() == 1) {
            return Optional.ofNullable(result.getInstant(0))
                    .map(LastSearch::new)
                    .map(Maybe::just)
                    .orElse(Maybe.empty());
        } else {
            return Maybe.empty();
        }
        
    }
    
    @Override
    public DatabaseService lastTransaction(String cpf, TransactionKind kind, Handler<AsyncResult<LastTransaction>> resultHandler) {
        LOGGER.debug("Executing last credit card transaction for CPF: {}", cpf);
        
        JsonArray params = new JsonArray()
                .add(cpf)
                .add(kind);

        String cmd = sql.get(Command.LAST_CREDIT_CARD_TRANSACTION_BY_CPF);

        client.rxQuerySingleWithParams(cmd, params)
            .filter(json -> !json.hasNull(0))
            .map(json -> new LastTransaction(json.getInstant(0)))
            .doOnError(logDatabaseError(cmd))
            .subscribe(MaybeHelper.toObserver(resultHandler));
        
        return this;
    }

    @Override
    public DatabaseService transactions(String cpf, PageRequest page, Handler<AsyncResult<JsonArray>> resultHandler) {
        JsonArray params = new JsonArray()
                .add(cpf)
                .add(page.size)
                .add(page.pgOffset());
        
        String cmd = sql.get(Command.TRANSACTIONS_BY_CPF);
        
        client.rxQueryWithParams(cmd, params)
            .map(ResultSet::getRows)
            .map(JsonArray::new)
            .doOnError(logDatabaseError(cmd))
            .subscribe(SingleHelper.toObserver(resultHandler));

        return this;
    }
    
    private Consumer<? super Throwable> logDatabaseError(String cmd) {
        return error -> LOGGER.error("Database error executing:\n{}\n", cmd, error);
    }
}


