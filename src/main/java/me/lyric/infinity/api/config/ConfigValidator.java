package me.lyric.infinity.api.config;

/**
 * @author zzurio
 */

public class ConfigValidator {

    private String metaData;
    private byte certifier;

    public ConfigValidator(String metaData, byte certifier) {
        this.metaData = metaData;
        this.certifier = certifier;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    public byte getCertifier() {
        return certifier;
    }

    public void setCertifier(byte certifier) {
        this.certifier = certifier;
    }
}
