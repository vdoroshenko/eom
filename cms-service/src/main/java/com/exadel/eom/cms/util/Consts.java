package com.exadel.eom.cms.util;

public final class Consts {

    public static final class Command {

        public static final String LIST = "ls";

        private Command(){
            //this prevents even the native class from
            //calling this ctor as well :
            throw new AssertionError();
        }
    }

    public static final class File {

        public static final String METADATA_DIR = ".cmsmd";

        public static final String MIME_EXT = ".mime";

        public static final String HASH_EXT = ".hash";

        public static final String PATH_DELIMITER = "/";

        private File(){
            //this prevents even the native class from
            //calling this ctor as well :
            throw new AssertionError();
        }
    }

    public static final class MimeType {

        public static final String BIN = "application/octet-stream";

        public static final String JPEG = "image/jpeg";

        public static final String TEXT = "text/plain";

        public static final String JSON = "application/json";

        private MimeType(){
            //this prevents even the native class from
            //calling this ctor as well :
            throw new AssertionError();
        }
    }

    public static final class Json {

        public static final Character QT = '"';

        public static final Character BR_OPN = '{';

        public static final Character BR_CLS = '}';

        public static final Character CMA = ',';

        public static final Character BRT_OPN = '[';

        public static final Character BRT_CLS = ']';

        public static final Character COL = ':';

        private Json(){
            //this prevents even the native class from
            //calling this ctor as well :
            throw new AssertionError();
        }
    }

    public static final String SMAP_DELIM = "&";

    public static final String UTF_8 = "UTF-8";

    public static final String DIGEST_ALG = "MD5";

    public static final String CACHE_CONTROL_REVALIDATE = "must-revalidate,proxy-revalidate";

    private Consts(){
        //this prevents even the native class from
        //calling this ctor as well :
        throw new AssertionError();
    }
}
