package com.exadel.eom.cms.configuration;

import java.util.Map;

public interface StorageSource {
    Map<String, Map<String, String>> getConfiguration();
}
