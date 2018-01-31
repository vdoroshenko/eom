package com.exadel.eom.cms.service;

import com.exadel.eom.cms.configuration.StorageConfiguration;
import com.exadel.eom.cms.service.storage.Storage;
import com.exadel.eom.cms.service.storage.StorageFsImpl;
import com.exadel.eom.cms.service.storage.StorageLdapImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CmsServiceImpl implements CmsService, InitializingBean, DisposableBean {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
	private StorageConfiguration storageConfiguration;

    private Map<String, Storage> storageMap = new HashMap<>(16);

    @Override
    public void afterPropertiesSet() throws Exception {
        initialize();
    }

    @Override
    public void destroy() {
        storageMap.forEach((key, value) -> {
            if(log.isInfoEnabled()) log.info("Try close FS: "+key);
            value.close();
        });
    }

    private void initialize() throws Exception {
        Map<String, Map<String, String>> storages = storageConfiguration.getStorages();
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

    public StorageConfiguration getStorageConfiguration() {
        return storageConfiguration;
    }

    public void setStorageConfiguration(StorageConfiguration storageConfiguration) {
        this.storageConfiguration = storageConfiguration;
    }
}
