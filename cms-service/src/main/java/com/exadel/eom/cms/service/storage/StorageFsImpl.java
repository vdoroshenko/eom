package com.exadel.eom.cms.service.storage;

import com.exadel.eom.cms.util.Consts;
import com.exadel.eom.cms.util.CopyUtil;
import com.exadel.eom.cms.util.ParseUtil;
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

public final class StorageFsImpl implements Storage {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String METADATA_DIR_CHECK1 = Consts.File.METADATA_DIR + Consts.File.PATH_DELIMITER;

    private static final String METADATA_DIR_CHECK2 = Consts.File.PATH_DELIMITER + Consts.File.METADATA_DIR + Consts.File.PATH_DELIMITER;

    private static final String FNAME = "\"name\":";

    private static final String SIZE = "\"size\":";

    private static final String FOLDER = "\"folder\":";

    private FileSystem fs = null;

    private boolean bFsCreated = false;

    private String root;

    private String uriString;

    @Override
    public void initialize(Map<String, String> params) {
        root = params.get("root");
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
                    return;
                }
            }
        }
        if (root == null) {
            Iterable<Path> dirs = fs.getRootDirectories();
            for (Path name : dirs) {
                root = name.toString(); /* get first root */
                break;
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
        if (path.startsWith(METADATA_DIR_CHECK1) || path.contains(METADATA_DIR_CHECK2)) {
            return null;
        }
        return openFile(path);
    }

    @Override
    public String getMimeType(String path) {
        String[] pathArr = path.split(Consts.File.PATH_DELIMITER);
        int i = pathArr.length - 2;
        StringBuilder mimePath = new StringBuilder();
        if (i >= 0)
            mimePath.append(ParseUtil.concat(pathArr, Consts.File.PATH_DELIMITER, 0, i)).append(Consts.File.PATH_DELIMITER);
        mimePath.append(Consts.File.METADATA_DIR).append(Consts.File.PATH_DELIMITER)
                .append(pathArr[pathArr.length - 1])
                .append(Consts.File.MIME_EXT);

        InputStream is = openFile(mimePath.toString());
        if (is == null) {
            do {
                StringBuilder subPath = new StringBuilder();
                if (i >= 0)
                    subPath.append(ParseUtil.concat(pathArr, Consts.File.PATH_DELIMITER, 0, i)).append(Consts.File.PATH_DELIMITER);
                subPath.append(Consts.File.METADATA_DIR).append(Consts.File.PATH_DELIMITER).append(Consts.File.MIME_EXT);
                InputStream isf = openFile(subPath.toString());
                if (isf != null) {
                    try {
                        return CopyUtil.readAsString(isf, Consts.UTF_8);
                    } finally {
                        try {
                            isf.close();
                        } catch (Exception e) {
                            log.error("Close stream fail, fs: " + uriString + " path: " + root + subPath, e);
                        }
                    }
                }
            } while (i-- >= 0);
            return Consts.MimeType.BIN;
        } else {
            try {
                return CopyUtil.readAsString(is, Consts.UTF_8);
            } finally {
                try {
                    is.close();
                } catch (Exception e) {
                    log.error("Close stream fail, fs: " + uriString + " path: " + root + mimePath, e);
                }
            }
        }
    }

    @Override
    public String getHash(String path) {
        if (path.startsWith(METADATA_DIR_CHECK1) || path.contains(METADATA_DIR_CHECK2)) {
            return null;
        }

        String[] pathArr = path.split(Consts.File.PATH_DELIMITER);
        int i = pathArr.length - 2;
        StringBuilder hashPathb = new StringBuilder();
        if (i >= 0)
            hashPathb.append(ParseUtil.concat(pathArr, Consts.File.PATH_DELIMITER, 0, i)).append(Consts.File.PATH_DELIMITER);
        hashPathb.append(Consts.File.METADATA_DIR).append(Consts.File.PATH_DELIMITER)
                .append(pathArr[pathArr.length - 1])
                .append(Consts.File.HASH_EXT);

        String hashPath = hashPathb.toString();
        InputStream is = openFile(hashPath);
        if (is == null) {
            if (log.isInfoEnabled()) log.info("Hash isn't found for fs: " + uriString + " path: " + hashPath);
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
                        log.error("Close stream fail, fs: " + uriString + " path: " + root + path, e);
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
                    log.error("Close stream fail, fs: " + uriString + " path: " + root + hashPath, e);
                }
            }
        }
        return null;
    }

    @Override
    public String list(String path) {
        StringBuilder sb = new StringBuilder();
        sb.append(Consts.Json.BRT_OPN);

        String filePath = root + path;
        Path fpath = fs.getPath(filePath);

        boolean bComma = false;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(fpath)) {
            for (Path entry : stream) {
                if (Consts.File.METADATA_DIR.equals(entry.getFileName())) continue;

                BasicFileAttributes bfa = Files.readAttributes(entry, BasicFileAttributes.class);

                if(bComma) {
                    sb.append(Consts.Json.CMA);
                } else {
                    bComma = true;
                }

                sb.append(Consts.Json.BR_OPN);
                sb.append(FNAME).append(entry.getFileName());
                sb.append(Consts.Json.CMA);
                sb.append(FOLDER).append(bfa.isDirectory());
                sb.append(Consts.Json.CMA);
                sb.append(SIZE).append(bfa.size());
                sb.append(Consts.Json.BR_CLS);
            }
        } catch(IOException e) {
            log.warn("Scan dir was failed, fs: " + uriString + " path: " + filePath, e);
        }

        sb.append(Consts.Json.BRT_CLS);
        return sb.toString();
    }

    private InputStream openFile(String path) {
        String filePath = root + path;
        Path fpath = fs.getPath(filePath);
        try {
            return Files.newInputStream(fpath, StandardOpenOption.READ);
        } catch (Exception e) {
            if (log.isDebugEnabled()) log.debug("Open failed, fs: " + uriString + " path: " + filePath, e);
            return null;
        }
    }

    private long saveFile(String path, InputStream is) {
        String filePath = root + path;
        Path fpath = fs.getPath(filePath);

        String[] pathArr = path.split(Consts.File.PATH_DELIMITER);
        String dirPath = root + ParseUtil.concat(pathArr, Consts.File.PATH_DELIMITER, 0, pathArr.length - 2);
        Path fdpath = fs.getPath(dirPath);

        OutputStream os = null;
        try {
            Files.createDirectories(fdpath);
            /* save data file*/
            os = Files.newOutputStream(fpath, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            return CopyUtil.copy(is, os);
        } catch (Exception e) {
            log.error("Save failed, fs: " + uriString + " path: " + filePath, e);
            return 0L;
        } finally {
            if (os != null) try {
                os.close();
            } catch (Exception e) {
                log.error("Close stream fail, fs: " + uriString + " path: " + filePath, e);
            }
        }
    }

    private BasicFileAttributes fileAttributes(String path) {
        String filePath = root + path;
        Path fpath = fs.getPath(filePath);
        try {
            return Files.readAttributes(fpath, BasicFileAttributes.class);
        } catch (Exception e) {
            log.error("Read attributes failed, fs: " + uriString + " path: " + filePath, e);
            return null;
        }
    }


}
