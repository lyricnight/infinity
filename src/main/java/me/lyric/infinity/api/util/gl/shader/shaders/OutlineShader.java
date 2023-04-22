package me.lyric.infinity.api.util.gl.shader.shaders;

import me.lyric.infinity.api.util.gl.shader.FramebufferShader;
import org.lwjgl.opengl.GL20;

/**
 * @author lyric
 */

public class OutlineShader extends FramebufferShader {

    public static final OutlineShader INSTANCE = new OutlineShader("outline.frag");

    public float rainbowSpeed = 0.5f;
    public float rainbowStrength = 0.25f;
    public float customSaturation = 0.5f;

    public OutlineShader(String fragmentShader) {
        super(fragmentShader);
    }

    public void setCustomValues(float rainbowSpeed, float rainbowStrength, float saturation) {
        this.rainbowSpeed = rainbowSpeed;
        this.rainbowStrength = rainbowStrength;
        this.customSaturation = saturation;
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("texture");
        this.setupUniform("texelSize");
        this.setupUniform("color");
        this.setupUniform("radius");
        this.setupUniform("rainbowStrength");
        this.setupUniform("rainbowSpeed");
        this.setupUniform("saturation");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform1i(this.getUniform("texture"), 0);
        GL20.glUniform2f(this.getUniform("texelSize"), 1.0f / this.mc.displayWidth * (this.radius * this.quality), 1.0f / this.mc.displayHeight * (this.radius * this.quality));
        GL20.glUniform4f(this.getUniform("color"), this.red, this.green, this.blue, this.alpha);
        GL20.glUniform1f(this.getUniform("radius"), this.radius);

        float strength = 1.0f / -(1000.0f * rainbowStrength);

        GL20.glUniform2f(this.getUniform("rainbowStrength"), strength, strength);
        GL20.glUniform1f(this.getUniform("rainbowSpeed"), rainbowSpeed);
        GL20.glUniform1f(this.getUniform("saturation"), customSaturation);
    }
}
