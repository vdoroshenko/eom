package com.exadel.eom.cms.controller;

import com.exadel.eom.cms.service.CmsService;
import com.exadel.eom.cms.service.storage.Storage;
import com.exadel.eom.cms.util.Consts;
import com.exadel.eom.cms.util.CopyUtil;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

@RestController
public class CmsController {
    private static final String QT = "\"";

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private CmsService cmsService;

    @RequestMapping(path = "/{storage}/**", method = RequestMethod.GET)
    public void getResource(
            @PathVariable("storage") String storageName, /* storage name from config */
            @RequestParam(value="cmd", required=false) String cmd, /* command applied to path (ls,copy,move etc.) */
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        String fullResourcePath = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        if (log.isDebugEnabled())
            log.debug("Get resource request, storage: " + storageName + " path: " + fullResourcePath);

        if (fullResourcePath == null) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }

        int prefixLen = storageName.length() + 2;

        String resourcePath = null;
        try {
            resourcePath = fullResourcePath.substring(prefixLen);
        } catch (Exception e) {
            log.error("Get resource request, bad resource path: "+fullResourcePath, e);
        }
        if (resourcePath == null) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }

        Storage storage = cmsService.getStorage(storageName);

        if (storage == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        // Execute command
        if (Consts.Command.LIST.equalsIgnoreCase(cmd)) {

            String result = storage.list(resourcePath);
            ByteArrayInputStream is = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8.name()));

            try {
                String ETagIf = request.getHeader(HttpHeaders.IF_NONE_MATCH);

                StringBuilder sb = new StringBuilder();
                CopyUtil.calcHexHash(is, Consts.DIGEST_ALG, sb);
                String ETag = sb.toString();

                String ETagString = "";

                if (ETag != null && !ETag.isEmpty()) {
                    ETagString = new StringBuilder().append(QT).append(ETag).append(QT).toString();
                }

                if (ETagIf != null && !ETagIf.isEmpty() && !ETagString.isEmpty() && ETagIf.equals(ETagString)) {
                    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                    return;
                }

                // Set ETag
                if (!ETagString.isEmpty()) {
                    response.addHeader(HttpHeaders.ETAG, ETagString);
                    response.setHeader(HttpHeaders.CACHE_CONTROL, Consts.CACHE_CONTROL_REVALIDATE);
                }

            } catch (NoSuchAlgorithmException e) {
                log.error("Get resource request, bad digest algorithm path: "+fullResourcePath, e);
            }

            response.setContentType(Consts.MimeType.JSON);
            is.reset();
            CopyUtil.copy(is, response.getOutputStream());
            is.close();

        } else {

            InputStream is = storage.getResource(resourcePath);
            if (is == null) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                return;
            }

            String ETagIf = request.getHeader(HttpHeaders.IF_NONE_MATCH);

            String ETag = storage.getHash(resourcePath);

            String ETagString = "";

            if (ETag != null && !ETag.isEmpty()) {
                ETagString = new StringBuilder().append(QT).append(ETag).append(QT).toString();
            }

            if (ETagIf != null && !ETagIf.isEmpty() && !ETagString.isEmpty() && ETagIf.equals(ETagString)) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }

            String mimeType = storage.getMimeType(resourcePath);

            int index = resourcePath.lastIndexOf(Consts.File.PATH_DELIMITER);
            String fileName = resourcePath;
            if (index >= 0) {
                try {
                    fileName = resourcePath.substring(index + 1);
                } catch (Exception e) {
                    // do nothing
                }
            }
            // Set the content type and attachment header.
            response.addHeader("Content-disposition", "attachment;filename=" + fileName);
            response.setContentType(mimeType);

            // Set ETag
            if (!ETagString.isEmpty()) {
                response.addHeader(HttpHeaders.ETAG, ETagString);
                response.setHeader(HttpHeaders.CACHE_CONTROL, Consts.CACHE_CONTROL_REVALIDATE);
            }

            // Copy the stream to the response's output stream.
            CopyUtil.copy(is, response.getOutputStream());
            is.close();

        }
        response.flushBuffer();
    }

    @RequestMapping(path = "/{storage}/**", method = {RequestMethod.PUT, RequestMethod.POST})
    public void putResource(
            @PathVariable("storage") String storageName, /* storage name from config */
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        // TODO: implementation
    }

    @RequestMapping(path = "/{storage}/**", method = RequestMethod.DELETE)
    public void deleteResource(
            @PathVariable("storage") String storageName, /* storage name from config */
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        // TODO: implementation
    }
}
