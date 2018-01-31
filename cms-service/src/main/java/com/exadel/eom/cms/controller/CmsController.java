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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@RestController
public class CmsController {
    private static final String QT = "\"";

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private CmsService cmsService;

    @RequestMapping(path = "/{storage}/**", method = RequestMethod.GET)
    public void getResource(
            @PathVariable("storage") String storageName,
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
            log.error("getResource(...) bad resource path: "+fullResourcePath, e);
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

        if(ETagIf != null && !ETagIf.isEmpty() && !ETagString.isEmpty() && ETagIf.equals(ETagString)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        String mimeType = storage.getMimeType(resourcePath);

        int index = resourcePath.lastIndexOf(Consts.PATH_DELIMITER);
        String fileName = resourcePath;
        if(index >= 0) {
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
        response.flushBuffer();
    }

}
