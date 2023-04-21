package me.lyric.infinity.api.util.gl.shader.shaders;

import me.lyric.infinity.api.util.gl.shader.FramebufferShader;
import me.lyric.infinity.api.util.minecraft.IGlobals;
import org.lwjgl.opengl.GL20;

/**
 * @author zzurio
 */

public class SpaceShader extends FramebufferShader {

    public static final FramebufferShader INSTANCE = new SpaceShader("space.frag");

    float time = 1.0f;

    public SpaceShader(String fragmentShader) {
        super(fragmentShader);
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("texture");
        this.setupUniform("texelSize");
        this.setupUniform("color");
        this.setupUniform("time");
        this.setupUniform("resolution");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform1i(this.getUniform("texture"), 0);
        GL20.glUniform2f(this.getUniform("texelSize"), 1.0f / this.mc.displayWidth * (this.radius * this.quality), 1.0f / this.mc.displayHeight * (this.radius * this.quality));
        GL20.glUniform4f(this.getUniform("color"), this.red, this.green, this.blue, this.alpha);
        GL20.glUniform1f(this.getUniform("time"), time);
        GL20.glUniform2f(this.getUniform("resolution"), IGlobals.mc.displayWidth, IGlobals.mc.displayHeight);
        time += 0.003;
    }
}
