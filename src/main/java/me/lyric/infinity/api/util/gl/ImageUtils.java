package me.lyric.infinity.api.util.gl;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class ImageUtils {
    /**
     * Reads the image to a byte buffer that works with LWJGL.
     * @author func16
     */
    public static ByteBuffer readImageToBuffer(BufferedImage bufferedImage){
        int[] rgbArray = bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null, 0, bufferedImage.getWidth());

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * rgbArray.length);
        for(int rgb : rgbArray){
            byteBuffer.putInt(rgb << 8 | rgb >> 24 & 255);
        }
        byteBuffer.flip();

        return byteBuffer;
    }
}
