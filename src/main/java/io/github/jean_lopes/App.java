package io.github.jean_lopes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.VertxOptions;
import io.vertx.reactivex.core.Vertx;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        LOGGER.info("Creating vertx instance");
        Vertx vertx = Vertx.vertx(new VertxOptions()
                .setWorkerPoolSize(40)
                .setPreferNativeTransport(false));
        
        String verticleName = MainVerticle.class.getName();
        
        LOGGER.info("Deploying {}", verticleName);
        
        vertx.rxDeployVerticle(verticleName)
            .doOnError(error -> LOGGER.error("Failed to deploy main verticle", error))
            .doOnSuccess(name -> LOGGER.info("Successfully deployed {}", name))
            .subscribe();
    }
}
