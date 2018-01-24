package com.exadel.eom.cms.service.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.util.LinkedHashMap;
import java.util.Map;

public class StorageFsImpl implements Storage {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private FileSystem fs = null;

    @Override
    public void initialize(Map<String, String> params) {
        String value = params.get("uri");
        if (value == null || value.isEmpty()) {
            fs = FileSystems.getDefault();
        } else {
            URI uri = URI.create(value);
            try {
                fs = FileSystems.getFileSystem(uri);
            } catch (FileSystemNotFoundException e) {
                // go ahead
            } catch (Exception e) {
                log.error("Can't get file system for uri: " + value, e);
                return;
            }
            if (fs == null) {
                // try create
                try {
                    Map<String, String> env = null;
                    String senv = params.get("env");
                    if (senv != null) {
                        env = string2map(senv, ";");
                    }
                    fs = FileSystems.newFileSystem(uri, env);
                } catch (Exception e) {
                    log.error("Can't create new file system for uri: " + value, e);
                }
            }
        }
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
    public String getEtag(String path) {
        return null;
    }

    private static Map<String, String> string2map(String text, String delimiter) {
        String pattern = " *"+delimiter+" *";
        Map<String, String> map = new LinkedHashMap<>();
        for(String keyValue : text.split(pattern)) {
            String[] pairs = keyValue.split(" *= *", 2);
            if (pairs.length > 0) {
                map.put(pairs[0], pairs.length == 1 ? "" : pairs[1]);
            }
        }
        return map;
    }
}
