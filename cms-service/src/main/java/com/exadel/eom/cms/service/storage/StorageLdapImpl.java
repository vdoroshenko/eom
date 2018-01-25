package com.exadel.eom.cms.service.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Map;

public class StorageLdapImpl implements Storage {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void initialize(Map<String, String> params) {

    }

    @Override
    public InputStream getResource(String path) {
        return null;
    }

    @Override
    public String getMimeType(String path) {
        return null;
    }

    @Override
    public String getHash(String path) {
        return null;
    }
}
