package io.github.jean_lopes.database;

import java.util.Objects;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class PageRequest {
    public final Integer size;
    public final Integer offset;
    
    public JsonObject toJson() {
        return new JsonObject()
                .put("size", size)
                .put("offset", offset);
    }
    
    public PageRequest(JsonObject json) {
        this.size = json.getInteger("size");
        this.offset = json.getInteger("offset");
    }
    
    private PageRequest(Integer size, Integer offset) {
        this.size = size;
        this.offset = offset;
    }
    
    public static PageRequest of(Integer size, Integer offset) {
        return new PageRequest(size, offset);
    }
    
    public Integer pgOffset() {
        return size * offset;
    }
    
    public boolean isValid() {
        return Objects.isNull(size) || Objects.isNull(offset) || size < 0 || offset < 0;
    }
}
