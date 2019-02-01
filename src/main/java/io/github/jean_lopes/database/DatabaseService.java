package io.github.jean_lopes.database;

import java.util.Map;

import io.github.jean_lopes.domain.search_history.LastSearch;
import io.github.jean_lopes.domain.transaction_history.LastTransaction;
import io.github.jean_lopes.domain.transaction_history.TransactionKind;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.JDBCClient;

@ProxyGen
@VertxGen
public interface DatabaseService {

    @GenIgnore
    static DatabaseService create(JDBCClient dbClient, Map<Command, String> sql, Handler<AsyncResult<DatabaseService>> readyHandler) {
        return new DatabaseServiceImpl(dbClient, sql, readyHandler);
    }

    @GenIgnore
    static io.github.reactivex.jean_lopes.database.DatabaseService createProxy(Vertx vertx, String address) {
        return new io.github.reactivex.jean_lopes.database.DatabaseService(new DatabaseServiceVertxEBProxy(vertx, address));
    }

    @Fluent
    DatabaseService lastSearch(String cpf, Handler<AsyncResult<LastSearch>> resultHandler);
    
    @Fluent
    DatabaseService lastTransaction(String cpf, TransactionKind kind, Handler<AsyncResult<LastTransaction>> resultHandler);
    
    @Fluent
    DatabaseService transactions(String cpf, PageRequest page, Handler<AsyncResult<JsonArray>> resultHandler);
}
