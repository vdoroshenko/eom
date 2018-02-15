package com.exadel.eom.cms.service;

import com.exadel.eom.cms.configuration.StorageConfiguration;
import com.exadel.eom.cms.configuration.StorageSource;
import com.exadel.eom.cms.service.storage.Storage;
import com.exadel.eom.cms.util.Consts;
import com.exadel.eom.cms.util.CopyUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CmsServiceFsTest {

    @InjectMocks
	private CmsServiceImpl cmsService;

    @Mock
    private StorageSource storageConfiguration;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("application.yml").getFile());
        String path = file.getAbsolutePath();
        int i = path.lastIndexOf("application.yml");
        String base = path.substring(0,i);

        Map<String, String> images = new HashMap<>();
        images.put("type","fs");
        images.put("root",base+"storages/images/");
        Map<String, String> images_zip = new HashMap<>();
        images_zip.put("type","fs");
        images_zip.put("uri","jar:file:"+base+"storages/images1.zip");
        images_zip.put("root","/images/");
        Map<String, String> ldap = new HashMap<>();
        ldap.put("type","ldap");
        ldap.put("url","");
        ldap.put("userDn","");
        ldap.put("password","");
        ldap.put("userSearchBase","");
        ldap.put("userSearchFilter","");

        Map<String, Map<String, String>> storages = new HashMap<>();
        storages.put("image",images);
        storages.put("image-zip",images_zip);
        storages.put("ldap",ldap);

        when(storageConfiguration.getConfiguration()).thenReturn(storages);

        cmsService.afterPropertiesSet();
    }

    @After
    public void close() {
        cmsService.destroy();
    }

	@Test
	public void shouldGetFsStorageDefault() {
		Storage storage = cmsService.getStorage("image");
        assertNotNull(storage);

        testStorage(storage);
	}

    @Test
    public void shouldGetFsStorageZip() throws Exception  {
        Storage storage = cmsService.getStorage("image-zip");
        assertNotNull(storage);

        testStorage(storage);
    }

    @Test
    public void shouldListFsStorageZip() throws Exception {
        Storage storage = cmsService.getStorage("image-zip");
        assertNotNull(storage);
        String json = storage.list("floors");
        assertNotNull(json);
        final String expected = "{\"path\":\"floors\",\"ls\":[{\"name\":\"floor2.png\",\"folder\":false,\"size\":609461,\"lastModifiedTime\":\"2018-01-29T16:09:17.000Z\"},{\"name\":\"floor1.png\",\"folder\":false,\"size\":621385,\"lastModifiedTime\":\"2018-01-29T16:09:11.000Z\"}]}";
        JSONAssert.assertEquals(expected, json, false);
    }


    private void testStorage(Storage storage) {
        InputStream is1 = storage.getResource("floors/floor1.png");
        assertNotNull(is1);
        String hash = storage.getHash("floors/floor1.png");
        assertNotNull(hash);
        hash = storage.getHash("floors/floor1.png");
        assertNotNull(hash);
        String mimetype = storage.getMimeType("floors/floor1.png");
        assertEquals(mimetype,"image/png");
        try {
            StringBuilder sb = new StringBuilder();
            CopyUtil.calcHexHash(is1, Consts.DIGEST_ALG, sb);
            assertEquals(hash, sb.toString());
            is1.close();
        } catch (Exception e) {
            assertNotNull(null);
        }

        InputStream is2 = storage.getResource("floors/floor2.png");
        assertNotNull(is2);
        hash = storage.getHash("floors/floor2.png");
        assertNotNull(hash);
        mimetype = storage.getMimeType("floors/floor2.png");
        assertEquals(mimetype,"image/png");
        try {
            StringBuilder sb = new StringBuilder();
            CopyUtil.calcHexHash(is2, Consts.DIGEST_ALG, sb);
            assertEquals(hash, sb.toString());
            is2.close();
        } catch (Exception e) {
            assertNotNull(null);
        }

        InputStream is3 = storage.getResource("test/test.jpg");
        assertNotNull(is3);
        hash = storage.getHash("test/test.jpg");
        assertNotNull(hash);
        mimetype = storage.getMimeType("test/test.jpg");
        assertEquals(mimetype,"image/jpeg");
        try {
            StringBuilder sb = new StringBuilder();
            CopyUtil.calcHexHash(is3, Consts.DIGEST_ALG, sb);
            assertEquals(hash, sb.toString());
            is3.close();
        } catch (Exception e) {
            assertNotNull(null);
        }

    }

}
