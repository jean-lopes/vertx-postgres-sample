package io.github.jean_lopes.domain.search_history;

import java.time.Instant;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.reactivex.Maybe;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class LastSearch {
    
    @JsonProperty(value = "last-search", required = true)
    private Instant value;

    public JsonObject toJson() {
        return new JsonObject().put("last-search", value);
    }
    
    public LastSearch(JsonObject json) {
        this.value = json.getInstant("last-search");
    }
    
    public LastSearch(Instant value) {
        this.value = value;
    }
    
    public Instant getValue() {
        return value;
    }

    public void setValue(Instant value) {
        this.value = value;
    }
    
    public Maybe<LastSearch> toMaybe() {
        if (Objects.isNull(value)) {
            return Maybe.empty();
        }
        
        return Maybe.just(this);
    }
}
