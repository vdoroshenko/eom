package com.exadel.eom.cms.service;

import com.exadel.eom.cms.service.storage.Storage;
import com.exadel.eom.cms.service.storage.StorageLdapImpl;
import com.exadel.eom.cms.util.Consts;
import com.exadel.eom.cms.util.CopyUtil;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CmsServiceLdapTest {

    @Autowired
    private CmsServiceImpl cmsService;

    @Before
    public void setup() {

    }

    @Test
    public void shouldGetLdapStorage() {
        Storage storage = cmsService.getStorage("ldap");
        assertNotNull(storage);
    }

    @Test
    public void shouldGetLdapAttrPhoto() {
        Storage storage = cmsService.getStorage("ldap");
        assertNotNull(storage);

        String path = "john/"+ StorageLdapImpl.PHOTO;
        InputStream is = storage.getResource(path);
        assertNotNull(is);

        String type = storage.getMimeType(path);
        assertEquals(Consts.MimeType.JPEG, type);

        String hash = storage.getHash(path);
        assertNotNull(hash);
        try {
            StringBuilder sb = new StringBuilder();
            CopyUtil.calcHexHash(is, Consts.DIGEST_ALG, sb);
            assertEquals(hash, sb.toString());
            is.close();
        } catch (Exception e) {
            assertNotNull(null);
        }
    }

    @Test
    public void shouldGetLdapAttrName() {
        Storage storage = cmsService.getStorage("ldap");
        assertNotNull(storage);

        String path = "john/cn";
        InputStream is = storage.getResource(path);
        assertNotNull(is);

        String cn = convertStreamToString(is);
        assertEquals(cn, "John Doe");

        String hash = storage.getHash(path);
        assertNotNull(hash);

        String type = storage.getMimeType(path);
        assertEquals(Consts.MimeType.TEXT, type);
    }

    @Test
    public void shouldListLdapAttrByUid() throws JSONException {
        Storage storage = cmsService.getStorage("ldap");
        assertNotNull(storage);

        String path = "";
        String s = storage.list(path);
        assertNotNull(s);
        String expected = "{\"ls\":[{\"uid\":\"jahn\"},{\"uid\":\"jihn\"},{\"uid\":\"john\"}]}";
        JSONAssert.assertEquals(expected, s, false);

        path = "john";
        s = storage.list(path);
        assertNotNull(s);

        path = "jahn";
        s = storage.list(path);
        assertNotNull(s);
    }

    static private String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
