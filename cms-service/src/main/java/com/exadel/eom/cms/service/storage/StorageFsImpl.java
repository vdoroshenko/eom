package com.exadel.eom.cms.service.storage;

import com.exadel.eom.cms.util.CopyUtil;
import com.exadel.eom.cms.util.ParseUtil;
import com.exadel.eom.cms.util.Consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public class StorageFsImpl implements Storage {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private FileSystem fs = null;

    private boolean bFsCreated = false;

    private String root;

    private String uriString;

    @Override
    public void initialize(Map<String, String> params) {
        root = params.get("root"); if(root == null) root = "/";
        uriString = params.get("uri");
        if (uriString == null || uriString.isEmpty()) {
            uriString = "[default]";
            fs = FileSystems.getDefault();
        } else {
            URI uri = URI.create(uriString);
            try {
                fs = FileSystems.getFileSystem(uri);
            } catch (FileSystemNotFoundException expected) {
                // go ahead
            } catch (Exception e) {
                log.error("Can't get file system for uri: " + uriString, e);
                return;
            }
            if (fs == null) {
                // try create
                try {
                    Map<String, String> env = new HashMap<>();
                    String senv = params.get("env");
                    if (senv != null) {
                        env = ParseUtil.string2map(senv, Consts.SMAP_DELIM);
                    }
                    fs = FileSystems.newFileSystem(uri, env);
                    bFsCreated = true;
                } catch (Exception e) {
                    log.error("Can't create new file system for uri: " + uriString, e);
                }
            }
        }
    }

    @Override
    public void close() {
        if (bFsCreated && fs != null) {
            try {
                 fs.close();
            } catch (Exception e) {
                log.error("Can't close file system for uri: " + uriString, e);
            }
        }
    }

    @Override
    public InputStream getResource(String path) {
        return openFile(path);
    }

    @Override
    public String getMimeType(String path) {
        String mimePath = path + Consts.MIME_EXT;
        InputStream is = openFile(mimePath);
        if (is == null) {
            String[] pathArr = path.split(Consts.PATH_DELIMITER);
            int i = pathArr.length - 2;
            do {
                StringBuilder subPath = new StringBuilder();
                if(i >= 0) subPath.append(ParseUtil.concat(pathArr, Consts.PATH_DELIMITER, 0, i)).append(Consts.PATH_DELIMITER);
                subPath.append(Consts.MIME_EXT);
                InputStream isf = openFile(subPath.toString());
                if (isf != null) {
                    try {
                        return CopyUtil.readAsString(isf, Consts.UTF_8);
                    } finally {
                        try {
                            isf.close();
                        } catch (Exception e) {
                            log.error("Close stream fail, fs: "+ uriString +" path: "+root + subPath, e);
                        }
                    }
                }
            } while (i-- >= 0);
            return Consts.BIN_MIMETYPE;
        } else {
            try {
                return CopyUtil.readAsString(is, Consts.UTF_8);
            } finally {
                try {
                    is.close();
                } catch (Exception e) {
                    log.error("Close stream fail, fs: "+ uriString +" path: "+root + mimePath, e);
                }
            }
        }
    }

    @Override
    public String getHash(String path) {
        String hashPath = path + Consts.HASH_EXT;
        InputStream is = openFile(hashPath);
        if (is == null) {
            if(log.isInfoEnabled()) log.info("Hash isn't found for fs: "+ uriString +" path: "+hashPath);
            InputStream isf = openFile(path);
            if (isf != null) {
                try {
                    StringBuilder hashb = new StringBuilder();
                    CopyUtil.calcHexHash(isf, Consts.DIGEST_ALG, hashb);
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
                        log.error("Close stream fail, fs: "+ uriString +" path: "+root + path, e);
                    }
                }
            }
        } else {
            try {
                return CopyUtil.readAsString(is, Consts.UTF_8);
            } finally {
                try {
                    is.close();
                } catch (Exception e) {
                    log.error("Close stream fail, fs: "+ uriString +" path: "+root + hashPath, e);
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
            if(log.isDebugEnabled()) log.debug("Open failed, fs: "+ uriString +" path: "+filePath, e);
            return null;
        }
    }

    private long saveFile(String path, InputStream is) {
        String filePath = root + path;
        Path fpath = fs.getPath(filePath);

        String[] pathArr = path.split(Consts.PATH_DELIMITER);
        String dirPath = root+ParseUtil.concat(pathArr, Consts.PATH_DELIMITER, 0, pathArr.length - 2);
        Path fdpath = fs.getPath(dirPath);

        OutputStream os = null;
        try {
            Files.createDirectories(fdpath);
            /* save data file*/
            os = Files.newOutputStream(fpath, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            return CopyUtil.copy(is, os);
        } catch (Exception e) {
            log.error("Save failed, fs: "+ uriString +" path: "+filePath, e);
            return 0L;
        } finally {
            if (os != null) try {
                os.close();
            } catch(Exception e) {
                log.error("Close stream fail, fs: "+ uriString +" path: "+filePath, e);
            }
        }
    }

    private BasicFileAttributes fileAttributes(String path) {
        String filePath = root + path;
        Path fpath = fs.getPath(filePath);
        try {
            return Files.readAttributes(fpath, BasicFileAttributes.class);
        } catch (Exception e) {
            log.error("Read attributes failed, fs: "+ uriString +" path: "+filePath, e);
            return null;
        }
    }


}
