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

import java.lang.reflect.Field;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

/**
 * Enable Spring MVC webjar support for JBoss 5.1 EAP.
 * 
 * <p>
 * Inherits from the Spring MVC {@link ResourceHandlerRegistration} class in order to handle the
 * private fields <i>locations</i> and <i>cachePeriod</i> by overriding the
 * {@link #getRequestHandler()} method.<br>
 * A {@link Vfs2ResourceHttpRequestHandler} is used to process HTTP request retrievent static assets from a webjar.
 * </p>
 * 
 */
class Vfs2ResourceHandlerRegistration extends ResourceHandlerRegistration {

    public Vfs2ResourceHandlerRegistration(ResourceLoader resourceLoader, String... pathPatterns) {
        super(resourceLoader, pathPatterns);
    }

    @Override
    protected ResourceHttpRequestHandler getRequestHandler() {
        Field locationsField = ReflectionUtils.findField(ResourceHandlerRegistration.class, "locations");
        ReflectionUtils.makeAccessible(locationsField);
        @SuppressWarnings("unchecked")
        List<Resource> locations = (List<Resource>) ReflectionUtils.getField(locationsField, this);

        Field cachePeriodField = ReflectionUtils.findField(ResourceHandlerRegistration.class, "cachePeriod");
        ReflectionUtils.makeAccessible(cachePeriodField);
        Integer cachePeriod = (Integer) ReflectionUtils.getField(cachePeriodField, this);

        // Initial code is replace by a new Vfs2ResourceHttpRequestHandler()
        Assert.isTrue(!CollectionUtils.isEmpty(locations), "At least one location is required for resource handling.");
        ResourceHttpRequestHandler requestHandler = new Vfs2ResourceHttpRequestHandler();
        requestHandler.setLocations(locations);
        if (cachePeriod != null) {
            requestHandler.setCacheSeconds(cachePeriod);
        }
        return requestHandler;
    }
}
