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

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Enable Spring MVC webjar support for JBoss 5.1 EAP.
 * <p>
 * Wrap the Spring MVC {@link ResourceHandlerRegistry} class in order to register a
 * {@link Vfs2ResourceHttpRequestHandler}.<br/>
 * Without this hack, you will have this kind of error: java.lang.ClassNotFoundException:
 * org.jboss.vfs.VFS from BaseClassLoader.
 * <p>
 * <p>
 * This class shoud be used from the
 * {@link WebMvcConfigurer#addResourceHandlers(ResourceHandlerRegistry)} method:<br>
 * <code>
 * new Vfs2ResourceHandlerRegistry(registry, applicationContext).addResourceHandler("/resources/webjars/**").addResourceLocations(
 * "classpath:/META-INF/resources/webjars/");<br>
 * </code>
 * <br>
 * The <i>applicationContext parameter</i> could be inject into the {@link Configuration} class:<br> 
 * <code>
 * @Autowired private ApplicationContext applicationContext;
 * </code>
 * </p>
 */
public class Vfs2ResourceHandlerRegistry {

    private final ResourceHandlerRegistry resourceHandlerRegistry;

    private final ApplicationContext      applicationContext;

    Vfs2ResourceHandlerRegistry(ResourceHandlerRegistry resourceHandlerRegistry, ApplicationContext applicationContext) {
        this.resourceHandlerRegistry = resourceHandlerRegistry;
        this.applicationContext = applicationContext;
    }

    public ResourceHandlerRegistration addResourceHandler(String... pathPatterns) {
        Field registrationsField = ReflectionUtils.findField(ResourceHandlerRegistry.class, "registrations");
        ReflectionUtils.makeAccessible(registrationsField);
        @SuppressWarnings("unchecked")
        List<ResourceHandlerRegistration> registrations = (List<ResourceHandlerRegistration>) ReflectionUtils.getField(
                registrationsField, resourceHandlerRegistry);
        ResourceHandlerRegistration registration = new Vfs2ResourceHandlerRegistration(applicationContext, pathPatterns);
        registrations.add(registration);
        return registration;
    }

}
