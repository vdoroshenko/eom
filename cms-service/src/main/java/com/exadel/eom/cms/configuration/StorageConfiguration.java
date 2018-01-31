package com.exadel.eom.cms.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "cms")
public class StorageConfiguration {
    private Map<String, Map<String, String>> storages;

    public Map<String, Map<String, String>> getStorages() {
        return storages;
    }

    public void setStorages(Map<String, Map<String, String>> storages) {
        this.storages = storages;
    }
}
