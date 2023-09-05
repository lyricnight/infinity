package me.lyric.infinity.api.util.metadata;

import net.minecraft.util.ResourceLocation;

import java.io.InputStream;

public class FileUtils {
    public static InputStream getFile(String pathToFile) {
        return FileUtils.class.getResourceAsStream("/assets/minecraft/" + (new ResourceLocation(pathToFile)).getPath());
    }

    public static InputStream getFile(ResourceLocation location) {
        return getFile(location.getPath());
    }
}