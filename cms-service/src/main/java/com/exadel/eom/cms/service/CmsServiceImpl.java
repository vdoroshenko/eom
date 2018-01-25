package com.exadel.eom.cms.service;

import com.exadel.eom.cms.service.storage.Storage;
import com.exadel.eom.cms.service.storage.StorageFsImpl;
import com.exadel.eom.cms.service.storage.StorageLdapImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@ConfigurationProperties(prefix = "cms")
public class CmsServiceImpl implements CmsService, InitializingBean {

	private final Logger log = LoggerFactory.getLogger(getClass());

    private Map<String, Map<String, String>> storages;

    private Map<String, Storage> storageMap = new HashMap<>(16);

    @Override
    public void afterPropertiesSet() throws Exception {
        initialize();
    }

    private void initialize() throws Exception {
        if(storages == null) {
            throw new Exception("initialize(): the storages is not initialized in configuration.");
        } else {
            storages.forEach((key, params) -> {
                String type = params.get("type");
                if("fs".equalsIgnoreCase(type)) {
                    Storage storage = new StorageFsImpl();
                    storage.initialize(params);
                    storageMap.put(key, storage);
                } else if("ldap".equalsIgnoreCase(type)) {
                    Storage storage = new StorageLdapImpl();
                    storage.initialize(params);
                    storageMap.put(key, storage);
                }
            });
        }
    }

    @Override
    public Storage getStorage(String name) {
        return storageMap.get(name);
    }

    public Map<String, Map<String, String>> getStorages() {
        return storages;
    }

    public void setStorages(Map<String, Map<String, String>> storages) {
        this.storages = storages;
    }
}
