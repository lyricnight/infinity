package me.lyric.infinity.api.util.metadata;

import net.minecraft.util.ResourceLocation;

import java.io.InputStream;

/**
 * @author lyric
 * this is used to get the image for splashprogress
 */

public class FileUtils {
    public static InputStream getFile(String pathToFile) {
        return FileUtils.class.getResourceAsStream("/assets/minecraft/" + (new ResourceLocation(pathToFile)).getPath());
    }
}