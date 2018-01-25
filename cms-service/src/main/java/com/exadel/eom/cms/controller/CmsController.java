package com.exadel.eom.cms.controller;

import com.exadel.eom.cms.service.CmsService;
import com.exadel.eom.cms.service.storage.Storage;
import com.exadel.eom.cms.util.CopyUtil;
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

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private CmsService cmsService;

    @RequestMapping(path = "/{storage}/**", method = RequestMethod.GET)
    public void getResource(
            @PathVariable("storage") String storageName,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        String resourcePath = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        if (log.isDebugEnabled()) log.debug("Get resource request, storage: " + storageName + " path: " + resourcePath);

        if (resourcePath == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        Storage storage = cmsService.getStorage(storageName);

        String mimeType = storage.getMimeType(resourcePath);

        String etag = storage.getHash(resourcePath);

        InputStream is = storage.getResource(resourcePath);

        // Set the content type and attachment header.
        response.addHeader("Content-disposition", "attachment;filename=" + resourcePath);
        response.setContentType(mimeType);

        // Copy the stream to the response's output stream.
        CopyUtil.copy(is, response.getOutputStream());
        is.close();
        response.flushBuffer();
    }

}
