package io.github.jean_lopes.database;

import java.util.Map;

import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.jean_lopes.ConfigKeys;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.serviceproxy.ServiceBinder;

public class DatabaseVerticle extends AbstractVerticle {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseVerticle.class);

    public static final String CONFIG_QUEUE = "database.queue";
    
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        LOGGER.info("Starting: {}", DatabaseVerticle.class.getSimpleName());

        Map<Command, String> sql = Command.load();
        
        JsonObject config = config();
        
        String url = config.getString(ConfigKeys.DB_JDBC, "jdbc:postgresql://localhost:5432/postgres");
        String user = config.getString(ConfigKeys.DB_USER, "postgres");
        String pass = config.getString(ConfigKeys.DB_PASS, "postgres");
        Integer maxPoolSize = config.getInteger(ConfigKeys.DB_MAX_POOL_SIZE, 30);
        
        migrateDatabase(url, user, pass);
        
        JDBCClient jdbcClient = JDBCClient.createShared(vertx, new JsonObject()
                .put("url", url)
                .put("user", user)
                .put("password", pass)
                .put("max_pool_size", maxPoolSize));

        DatabaseService.create(jdbcClient, sql, ready -> {
            if (ready.succeeded()) {
                ServiceBinder binder = new ServiceBinder(vertx);              
                binder.setAddress(CONFIG_QUEUE).register(DatabaseService.class, ready.result());
                startFuture.complete();
                LOGGER.info("Database service created");
            } else {
                LOGGER.error("Failed to create database service", ready.cause());
                startFuture.fail(ready.cause());
            }
        });
        
        startFuture.complete();
    }

    private void migrateDatabase(String url, String user, String pass) {       
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setURL(url);
        ds.setUser(user);
        ds.setPassword(pass);

        Flyway flyway = Flyway.configure()
                .dataSource(ds)
                .load();

        flyway.migrate();
    }
}
