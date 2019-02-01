package io.github.jean_lopes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.jean_lopes.database.DatabaseVerticle;
import io.github.jean_lopes.http.HttpVerticle;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file")
                .setOptional(false)
                .setConfig(new JsonObject().put("path", "vertx-config.json"));

        ConfigStoreOptions sysPropsStore = new ConfigStoreOptions()
                .setType("env");
        
        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(fileStore)
                .addStore(sysPropsStore);
        
        ConfigRetriever.create(vertx, options)
            .rxGetConfig()
            .doOnError(error -> LOGGER.error("Failed to load configuration file", error))
            .subscribe(this::run);
    }

    private void run(JsonObject cfg) {
        DeploymentOptions databaseOpts = new DeploymentOptions()
                .setConfig(cfg);
        
        DeploymentOptions httpServerOpts = new DeploymentOptions(databaseOpts)
                .setInstances(cfg.getInteger(ConfigKeys.HTTP_SERVER_INSTANCES, 1));
    
        vertx.rxDeployVerticle(DatabaseVerticle.class.getName(), databaseOpts)
            .flatMap(id -> vertx.rxDeployVerticle(HttpVerticle.class.getName(), httpServerOpts))
            .subscribe();
    }
}
