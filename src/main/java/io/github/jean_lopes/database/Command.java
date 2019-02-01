package io.github.jean_lopes.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;

public enum Command {
    FIND_LAST_SEARCH_BY_CPF,
    LAST_CREDIT_CARD_TRANSACTION_BY_CPF,
    TRANSACTIONS_BY_CPF;

    public static Map<Command, String> load() throws IOException {
        Map<Command, String> sql = new EnumMap<>(Command.class);
        
        Properties props = new Properties();
        
        try (InputStream is = System.class.getResourceAsStream("/db/queries.properties")) {
            props.load(is);           
            sql.put(Command.FIND_LAST_SEARCH_BY_CPF, props.getProperty("last-search-by-cpf"));
            sql.put(Command.LAST_CREDIT_CARD_TRANSACTION_BY_CPF, props.getProperty("last-transaction-by-cpf"));
            sql.put(Command.TRANSACTIONS_BY_CPF, props.getProperty("transactions-by-cpf"));
        }

        return sql;
    }
}
