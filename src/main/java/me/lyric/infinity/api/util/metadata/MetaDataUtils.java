package me.lyric.infinity.api.util.metadata;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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
