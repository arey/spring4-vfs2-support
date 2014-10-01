/*
 * Copyright 2002-2014 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.javaetmoi.core.spring.vfs;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.AbstractFileResolvingResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

/**
 * Enable Spring MVC webjar support for JBoss 5.1 EAP.
 * <p>
 * Inherits from the Spring MVC {@link Vfs2ResourceHttpRequestHandler} class.<br/>
 * The method {@link #handleRequest(HttpServletRequest, HttpServletResponse)} is overrided in order
 * to use a {@link Vfs2Resource} instead of of a {@link AbstractFileResolvingResource}.
 * </p>
 */
class Vfs2ResourceHttpRequestHandler extends ResourceHttpRequestHandler {

    private final static Log logger = LogFactory.getLog(Vfs2ResourceHttpRequestHandler.class);

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

        checkAndPrepare(request, response, true);

        // check whether a matching resource exists
        Resource resource = getResource(request);
        if (resource == null) {
            logger.debug("No matching resource found - returning 404");
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // check the resource's media type
        MediaType mediaType = getMediaType(resource);
        if (mediaType != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Determined media type '" + mediaType + "' for " + resource);
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("No media type found for " + resource + " - not sending a content-type header");
            }
        }

        // header phase
        // Use a Vfs2Resource when asset are probided by the JBoss 5 Virtaul File System
        URL url = resource.getURL();
        if (url.getProtocol().startsWith(ResourceUtils.URL_PROTOCOL_VFS)) {
            resource = new Vfs2Resource(Vfs2Utils.getRoot(url));
        }

        if (new ServletWebRequest(request, response).checkNotModified(resource.lastModified())) {
            logger.debug("Resource not modified - returning 304");
            return;
        }
        setHeaders(response, resource, mediaType);

        // content phase
        if (METHOD_HEAD.equals(request.getMethod())) {
            logger.trace("HEAD request - skipping content");
            return;
        }
        writeContent(response, resource);
    }

}
