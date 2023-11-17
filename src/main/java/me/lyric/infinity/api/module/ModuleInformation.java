package me.lyric.infinity.api.module;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface ModuleInformation {
    Category category();

    String name();

    String description();
}
