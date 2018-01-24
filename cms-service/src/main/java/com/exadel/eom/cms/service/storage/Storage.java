package com.exadel.eom.cms.service.storage;

import java.io.InputStream;
import java.util.Map;

public interface Storage {
    void initialize(Map<String,String> params);
    InputStream getResource(String path);
    String getMimeType(String path);
    String getEtag(String path);
}