package me.lyric.infinity.api.config;

import me.lyric.infinity.api.util.bytes.ByteChanger;

/**
 * @author CPacketCustomPayload - thanks
 */

public class Config {

    private final ConfigValidator validator;
    private String name;
    private String tag;
    private String data;

    public Config(String name, String tag, String data) {
        this.name = name;
        this.tag = tag;
        this.data = data;

        this.validator = new ConfigValidator(name + "-" + tag + "-" + data, ByteChanger.FALSE);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void updateValidator() {
        this.validator.setMetaData(this.name + "-" + this.tag + "-" + this.data);
    }

    public void setValidator() {
        this.validator.setCertifier(ByteChanger.TRUE);
    }

    public void unsetValidator() {
        this.validator.setCertifier(ByteChanger.FALSE);
    }

    public boolean isCurrent() {
        return ByteChanger.byteToBoolean(this.validator.getCertifier());
    }

    public byte getCertification() {
        return this.validator.getCertifier();
    }
}
