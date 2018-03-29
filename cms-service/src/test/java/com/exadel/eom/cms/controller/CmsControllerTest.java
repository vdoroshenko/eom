package com.exadel.eom.cms.controller;

import com.exadel.eom.cms.service.CmsService;
import com.exadel.eom.cms.service.storage.Storage;
import com.exadel.eom.cms.util.Consts;
import org.apache.http.HttpHeaders;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CmsControllerTest {

	@InjectMocks
	private CmsController cmsController;

	@Mock
	private CmsService cmsService;

	private MockMvc mockMvc;

	private class FakeStorage implements Storage {
		@Override
		public void initialize(Map<String, String> params) {
			// nothing to do in fake implementation
		}

		@Override
		public void close() {
			// stub
		}

		@Override
		public InputStream getResource(String path) {
            if (FILE_NAME.equalsIgnoreCase(path)) {
				return new ByteArrayInputStream(RAWJPEG);
			} else {
            	return null;
			}
		}

		@Override
		public String getMimeType(String path) {
			return Consts.MimeType.JPEG;
		}

		@Override
		public String getHash(String path) {
			return ETAG;
		}

        @Override
        public String list(String path) {
            return EMPTY_ARRAY;
        }
    }

	private final static byte[] RAWJPEG= {
			(byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0, (byte)0x00, (byte)0x10, (byte)0x4A, (byte)0x46, (byte)0x49, (byte)0x46, (byte)0x00, (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x00, (byte)0x48, (byte)0x00, (byte)0x48, (byte)0x00, (byte)0x00,
			(byte)0xFF, (byte)0xDB, (byte)0x00, (byte)0x43, (byte)0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
			(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
			(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
			(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xC2, (byte)0x00, (byte)0x0B, (byte)0x08, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x01, (byte)0x01, (byte)0x01,
			(byte)0x11, (byte)0x00, (byte)0xFF, (byte)0xC4, (byte)0x00, (byte)0x14, (byte)0x10, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0xDA, (byte)0x00, (byte)0x08, (byte)0x01, (byte)0x01, (byte)0x00, (byte)0x01, (byte)0x3F, (byte)0x10
	};

    private final static String ETAG = "17i2ghb2hj1g34gbu12i42i14gbrhxbc2uh21orphc";

    private final static String FILE_NAME = "fake_data.jpg";

    private final static String ETAG_EMPTY_ARRAY = "d751713988987e9331980363e24189ce";

    private final static String EMPTY_ARRAY = "[]";

    @Before
	public void setup() {
		initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(cmsController).build();
	}

	@Test
	public void shouldGetJpegWithEtagApproach() throws Exception {

		final Storage storage = new FakeStorage();

		when(cmsService.getStorage("fake_storage_name")).thenReturn(storage);

		// without if_none_match header
		mockMvc.perform(get("/fake_storage_name/" + FILE_NAME )).andDo(print())
                .andExpect(status().isOk())
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE, Consts.MimeType.JPEG))
                .andExpect(header().string(HttpHeaders.ETAG, "\""+ETAG+"\""))
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, Consts.CACHE_CONTROL_REVALIDATE))
				.andExpect(result -> {
                        //Open the binary response in memory
                        byte[] res = result.getResponse().getContentAsByteArray();
                        assertArrayEquals(res, RAWJPEG);
                });
        // with if_none_match header, and etag is match
        mockMvc.perform(get("/fake_storage_name/" + FILE_NAME )
                    .header(HttpHeaders.IF_NONE_MATCH, "\""+ETAG+"\"")).andDo(print())
                .andExpect(status().isNotModified());

        // with if_none_match header, and etag isn't match
        mockMvc.perform(get("/fake_storage_name/" + FILE_NAME )
                    .header(HttpHeaders.IF_NONE_MATCH, "\""+ETAG+"123\"")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, Consts.MimeType.JPEG))
                .andExpect(header().string(HttpHeaders.ETAG, "\""+ETAG+"\""))
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, Consts.CACHE_CONTROL_REVALIDATE))
                .andExpect(result -> {
                        //Open the binary response in memory
                        byte[] res = result.getResponse().getContentAsByteArray();
                        assertArrayEquals(res, RAWJPEG);
                });

        // wrong path
        mockMvc.perform(get("/fake_storage_name/badname" + FILE_NAME )).andDo(print())
                .andExpect(status().isNotFound());
	}

	@Test
	public void shouldGetDirListWithEtagApproach() throws Exception {
        final Storage storage = new FakeStorage();

        when(cmsService.getStorage("fake_storage_name")).thenReturn(storage);

        // without if_none_match header
        mockMvc.perform(get("/fake_storage_name/?cmd=ls")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, Consts.MimeType.JSON))
                .andExpect(header().string(HttpHeaders.ETAG, "\""+ETAG_EMPTY_ARRAY+"\""))
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, Consts.CACHE_CONTROL_REVALIDATE))
                .andExpect(result -> {
                        String res = result.getResponse().getContentAsString();
                        assertEquals(EMPTY_ARRAY, res);
                });

        // with if_none_match header, and etag is match
        mockMvc.perform(get("/fake_storage_name/?cmd=ls" )
                .header(HttpHeaders.IF_NONE_MATCH, "\""+ETAG_EMPTY_ARRAY+"\"")).andDo(print())
                .andExpect(status().isNotModified());

        // with if_none_match header, and etag isn't match
        mockMvc.perform(get("/fake_storage_name/?cmd=ls" )
                .header(HttpHeaders.IF_NONE_MATCH, "\""+ETAG_EMPTY_ARRAY+"123\"")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, Consts.MimeType.JSON))
                .andExpect(header().string(HttpHeaders.ETAG, "\""+ETAG_EMPTY_ARRAY+"\""))
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, Consts.CACHE_CONTROL_REVALIDATE))
                .andExpect(result -> {
                        String res = result.getResponse().getContentAsString();
                        assertEquals(EMPTY_ARRAY, res);
                });
	}
}
