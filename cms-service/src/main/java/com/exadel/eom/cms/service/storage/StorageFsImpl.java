package com.exadel.eom.cms.service.storage;

import com.exadel.eom.cms.util.CopyUtil;
import com.exadel.eom.cms.util.ParseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;

public class StorageFsImpl implements Storage {

    private static final String MIME_EXT = ".mime";

    private static final String HASH_EXT = ".md5";

    private static final String SMAP_DELIM = "&";

    private static final String PATH_DELIMITER = "/";

    private static final String BIN_MIMETYPE = "application/octet-stream";

    private static final String UTF_8 = "UTF-8";

    private static final String DIGEST_ALG = "MD5";


    private final Logger log = LoggerFactory.getLogger(getClass());

    private FileSystem fs = null;

    private String root;

    private String uriName;

    @Override
    public void initialize(Map<String, String> params) {
        root = params.get("root"); if(root == null) root = "/";
        uriName = params.get("uri");
        if (uriName == null || uriName.isEmpty()) {
            uriName = "[default]";
            fs = FileSystems.getDefault();
        } else {
            URI uri = URI.create(uriName);
            try {
                fs = FileSystems.getFileSystem(uri);
            } catch (FileSystemNotFoundException e) {
                // go ahead
            } catch (Exception e) {
                log.error("Can't get file system for uri: " + uriName, e);
                return;
            }
            if (fs == null) {
                // try create
                try {
                    Map<String, String> env = null;
                    String senv = params.get("env");
                    if (senv != null) {
                        env = ParseUtil.string2map(senv, SMAP_DELIM);
                    }
                    fs = FileSystems.newFileSystem(uri, env);
                } catch (Exception e) {
                    log.error("Can't create new file system for uri: " + uriName, e);
                }
            }
        }
    }

    @Override
    public InputStream getResource(String path) {
        return openFile(path);
    }

    @Override
    public String getMimeType(String path) {
        String mimePath = path + MIME_EXT;
        InputStream is = openFile(mimePath);
        if (is == null) {
            String[] pathArr = path.split(PATH_DELIMITER);
            int i = pathArr.length - 2;
            do {
                String subPath = ParseUtil.concat(pathArr, PATH_DELIMITER, 0, i) + MIME_EXT;
                InputStream isf = openFile(subPath);
                if (isf != null) {
                    try {
                        return CopyUtil.readAsString(isf, UTF_8);
                    } finally {
                        try {
                            isf.close();
                        } catch (Exception e) {
                            log.error("Close stream fail, fs: "+uriName+" path: "+root + subPath, e);
                        }
                    }
                }
            } while (--i >= 0);
            return BIN_MIMETYPE;
        } else {
            try {
                return CopyUtil.readAsString(is, UTF_8);
            } finally {
                try {
                    is.close();
                } catch (Exception e) {
                    log.error("Close stream fail, fs: "+uriName+" path: "+root + mimePath, e);
                }
            }
        }
    }

    @Override
    public String getHash(String path) {
        String hashPath = path + HASH_EXT;
        InputStream is = openFile(hashPath);
        if (is == null) {
            if(log.isInfoEnabled()) log.info("Hash isn't found for fs: "+uriName+" path: "+hashPath);
            InputStream isf = openFile(path);
            if (isf != null) {
                try {
                    StringBuilder hashb = new StringBuilder();
                    CopyUtil.calcHexHash(isf, DIGEST_ALG, hashb);
                    String hash = hashb.toString();
                    saveFile(hashPath, new ByteArrayInputStream(
                            hash.getBytes(StandardCharsets.UTF_8.name())));
                    return hash;
                } catch (Exception e) {
                    return null;
                } finally {
                    try {
                        isf.close();
                    } catch (Exception e) {
                        log.error("Close stream fail, fs: "+uriName+" path: "+root + path, e);
                    }
                }
            }
        } else {
            try {
                return CopyUtil.readAsString(is, UTF_8);
            } finally {
                try {
                    is.close();
                } catch (Exception e) {
                    log.error("Close stream fail, fs: "+uriName+" path: "+root + hashPath, e);
                }
            }
        }
        return null;
    }

    private InputStream openFile(String path) {
        String filePath = root + path;
        Path fpath = fs.getPath(filePath);
        try {
            return Files.newInputStream(fpath, StandardOpenOption.READ);
        } catch (Exception e) {
            if(log.isDebugEnabled()) log.debug("Open failed, fs: "+uriName+" path: "+filePath, e);
            return null;
        }
    }

    private long saveFile(String path, InputStream is) {
        String filePath = root + path;
        Path fpath = fs.getPath(filePath);

        String[] pathArr = path.split(PATH_DELIMITER);
        String dirPath = root+ParseUtil.concat(pathArr, PATH_DELIMITER, 0, pathArr.length - 2);
        Path fdpath = fs.getPath(dirPath);

        OutputStream os = null;
        try {
            Files.createDirectories(fdpath);
            /* save data file*/
            os = Files.newOutputStream(fpath, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            return CopyUtil.copy(is, os);
        } catch (Exception e) {
            log.error("Save failed, fs: "+uriName+" path: "+filePath, e);
            return 0L;
        } finally {
            if (os != null) try {
                os.close();
            } catch(Exception e) {
                log.error("Close stream fail, fs: "+uriName+" path: "+filePath, e);
            }
        }
    }

    private BasicFileAttributes fileAttributes(String path) {
        String filePath = root + path;
        Path fpath = fs.getPath(filePath);
        try {
            return Files.readAttributes(fpath, BasicFileAttributes.class);
        } catch (Exception e) {
            log.error("Read attributes failed, fs: "+uriName+" path: "+filePath, e);
            return null;
        }
    }


}
