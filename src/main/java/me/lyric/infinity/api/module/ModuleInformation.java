package me.lyric.infinity.api.module;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface ModuleInformation {
    public String getName();

    public String getDescription();

    public Category category();
}
