package io.github.jean_lopes.http;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.jean_lopes.ConfigKeys;
import io.github.jean_lopes.database.DatabaseVerticle;
import io.github.jean_lopes.database.PageRequest;
import io.github.jean_lopes.domain.search_history.LastSearch;
import io.github.jean_lopes.domain.transaction_history.LastTransaction;
import io.github.jean_lopes.domain.transaction_history.TransactionKind;
import io.github.reactivex.jean_lopes.database.DatabaseService;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;

public class HttpVerticle extends AbstractVerticle {

    private static final String APPLICATION_JSON = "application/json";

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpVerticle.class);

    private DatabaseService database;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        LOGGER.info("Starting: {}", HttpVerticle.class.getSimpleName());

        String address = DatabaseVerticle.CONFIG_QUEUE;
        
        database = io.github.jean_lopes.database.DatabaseService.createProxy(vertx.getDelegate(), address);

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        
        BiConsumer<String, Handler<RoutingContext>> post = (path, handler) ->
            router.post(path)
                .consumes(APPLICATION_JSON)
                .handler(handler);
        
        post.accept("/last_search", this::lastSearch);
        post.accept("/last_transaction", this::lastTransaction);
        post.accept("/transactions", this::transactions);
        
        router.routeWithRegex(".*").handler(req -> req.response()
                .setStatusCode(418)
                .end());
        
        router.exceptionHandler(req -> LOGGER.error("Routing error", req.getCause()));

        vertx.createHttpServer()
                .requestHandler(router::handle)
                .rxListen(config().getInteger(ConfigKeys.HTTP_SERVER_PORT, 8080))
                .subscribe(startServerSucess(startFuture), startServerFailure(startFuture));
    }

    private Consumer<? super Throwable> startServerFailure(Future<Void> startFuture) {
        return error -> {
            LOGGER.error("Could not start a HTTP server", error);
            startFuture.fail(error);
        };
    }

    private Consumer<? super HttpServer> startServerSucess(Future<Void> startFuture) {
        return server -> {
            LOGGER.info("HTTP server running on port {}", server.actualPort());
            startFuture.complete();
        };
    }
    
   
    private Action noContent(RoutingContext context) {
        return () -> response(context, 204).accept(null);
    }
    
    private Consumer<JsonArray> arrayResponse(RoutingContext context, int statusCode) {
        return jsonArray -> {
            JsonObject wrapped = new JsonObject()
                    .put("success", true)
                    .put("data", Optional.ofNullable(jsonArray)
                            .orElse(new JsonArray()));
            
            wrapedJsonResponse(context, statusCode, wrapped);
        };
    }
    
    private Consumer<JsonObject> response(RoutingContext context, int statusCode) {
        return jsonData -> {
            JsonObject wrapped = new JsonObject()
                    .put("success", true)
                    .put("data", Optional.ofNullable(jsonData)
                            .orElse(new JsonObject()));
            
            wrapedJsonResponse(context, statusCode, wrapped);
        };
    }

    private void wrapedJsonResponse(RoutingContext context, int statusCode, JsonObject wrapped) {
        context.response().setStatusCode(statusCode);
        context.response().putHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
        context.response().end(wrapped.encodePrettily().concat("\n"));
    }
    
    private Consumer<Throwable> failure(RoutingContext context) {
        return error -> failure(context, 500, error.getMessage());
    }
    
    private void failure(RoutingContext context, int statusCode, String error) {
        context.response().setStatusCode(statusCode);
        
        context.response().putHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
        
        context.response().end(new JsonObject()
                    .put("success", false)
                    .put("error", error)
                    .encodePrettily()
                    .concat("\n"));
    }

    private void cpfHandler(RoutingContext ctx, 
            java.util.function.Consumer<String> validCPFHandler) {
        String cpf = ctx.getBodyAsJson()
                .getString("cpf");
        
        if (StringUtils.isBlank(cpf)) {
            failure(ctx, 400, "CPF inválido");
        } else {
            validCPFHandler.accept(cpf);
        }
    }
    
    private void lastSearch(RoutingContext ctx) {
        cpfHandler(ctx, cpf -> database.rxLastSearch(cpf)
                .flatMapMaybe(LastSearch::toMaybe)
                .map(LastSearch::toJson)
                .doOnError(failure(ctx))
                .doOnSuccess(response(ctx, 200))
                .doOnComplete(noContent(ctx))
                .subscribe());
    }
    
    private void lastTransaction(RoutingContext ctx) {
        JsonObject json = ctx.getBodyAsJson();
        
        String kindAbr = json.getString("kind");
        
        TransactionKind kind = TransactionKind.from(kindAbr);
        
        if (Objects.isNull(kindAbr) && Objects.isNull(kind)) {
            failure(ctx, 400, "Invalid kind value");
        }
        
        cpfHandler(ctx, cpf -> database.rxLastTransaction(cpf, kind)
                .flatMapMaybe(LastTransaction::toMaybe)
                .map(LastTransaction::toJson)
                .doOnError(failure(ctx))
                .doOnSuccess(response(ctx, 200))
                .doOnComplete(noContent(ctx))
                .subscribe());
    }
    
    private void transactions(RoutingContext ctx) {
        JsonObject json = ctx.getBodyAsJson();
        
        JsonObject pageJson = json.getJsonObject("page");
        
        PageRequest page = new PageRequest(pageJson);
        
        if (page.isValid()) {
            failure(ctx, 400, "Invalid page object");
        } else {
            cpfHandler(ctx, cpf -> database.rxTransactions(cpf, page)
                    .filter(arr -> !arr.isEmpty())
                    .doOnError(failure(ctx))
                    .doOnSuccess(arrayResponse(ctx, 200))
                    .doOnComplete(noContent(ctx))
                    .subscribe());
        }
    }
}
