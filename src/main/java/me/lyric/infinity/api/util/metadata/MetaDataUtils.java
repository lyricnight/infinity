package me.lyric.infinity.api.util.metadata;

import com.google.gson.JsonObject;

/**
 * @author lyric
 */

public enum MetaDataUtils {
    LOADABLE(), CACHE();

    JsonObject metaData;

    MetaDataUtils() {
    }

    public JsonObject getMetaData() {
        return metaData;
    }

    public void setMetaData(JsonObject metaData) {
        this.metaData = metaData;
    }
}
