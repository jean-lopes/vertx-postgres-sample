package io.github.jean_lopes.domain.transaction_history;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.reactivex.Maybe;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class LastTransaction {
    
    @JsonProperty(value = "last-transaction", required = true)
    private Instant value;

    public JsonObject toJson() {
        return new JsonObject().put("last-transaction", value);
    }
    
    public LastTransaction(JsonObject json) {
        this.value = json.getInstant("last-transaction");
    }
    
    public LastTransaction(Instant value) {
        this.value = value;
    }
    
    public Instant getValue() {
        return value;
    }

    public void setValue(Instant value) {
        this.value = value;
    }
    
    public Maybe<LastTransaction> toMaybe() {
        return Optional.of(this)
                .filter(e -> !Objects.isNull(e.value))
                .map(Maybe::just)
                .orElse(Maybe.empty());
    }
}
