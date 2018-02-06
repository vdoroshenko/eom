package com.exadel.eom.cms.util;

public final class Consts {

    public static final String METADATA_DIR = ".__cmsmd";

    public static final String MIME_EXT = ".mime";

    public static final String HASH_EXT = ".hash";

    public static final String SMAP_DELIM = "&";

    public static final String PATH_DELIMITER = "/";

    public static final String BIN_MIMETYPE = "application/octet-stream";

    public static final String JPEG_MIMETYPE = "image/jpeg";

    public static final String TEXT_MIMETYPE = "text/plain";

    public static final String UTF_8 = "UTF-8";

    public static final String DIGEST_ALG = "MD5";

    public static final String CACHE_CONTROL_REVALIDATE = "must-revalidate,proxy-revalidate";

    private Consts(){
        //this prevents even the native class from
        //calling this ctor as well :
        throw new AssertionError();
    }
}
