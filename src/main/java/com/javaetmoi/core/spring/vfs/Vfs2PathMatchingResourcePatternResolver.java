/*
 * Copyright 2002-2013 the original author or authors.
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

import static org.springframework.util.ResourceUtils.URL_PROTOCOL_JAR;
import static org.springframework.util.ResourceUtils.URL_PROTOCOL_WSJAR;
import static org.springframework.util.ResourceUtils.URL_PROTOCOL_ZIP;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.PathMatcher;
import org.springframework.util.ResourceUtils;

/**
 * {@link PathMatchingResourcePatternResolver} subclass that is able to resolve a specified resource
 * location path into one or more matching {@link Resource}, inculding {@link Vfs2Resource}.
 * 
 * @see com.javaetmoi.core.spring.vfs.Vfs2Resource
 */
public class Vfs2PathMatchingResourcePatternResolver extends PathMatchingResourcePatternResolver {

    private static final Log LOGGER = LogFactory.getLog(Vfs2PathMatchingResourcePatternResolver.class);

    public Vfs2PathMatchingResourcePatternResolver(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    /**
     * Find all resources that match the given location pattern via the Ant-style PathMatcher.
     * Supports resources in jar files and zip files and in the file system.
     * 
     * @param locationPattern
     *            the location pattern to match
     * @return the result as Resource array
     * @throws IOException
     *             in case of I/O errors
     * @see #doFindPathMatchingJarResources
     * @see #doFindPathMatchingFileResources
     * @see org.springframework.util.PathMatcher
     */
    @Override
    protected Resource[] findPathMatchingResources(String locationPattern) throws IOException {
        String rootDirPath = determineRootDir(locationPattern);
        String subPattern = locationPattern.substring(rootDirPath.length());
        Resource[] rootDirResources = getResources(rootDirPath);
        Set<Resource> result = new LinkedHashSet<Resource>(16);
        for (Resource rootDirResource : rootDirResources) {
            rootDirResource = resolveRootDirResource(rootDirResource);
            if (isJarResource(rootDirResource)) {
                result.addAll(doFindPathMatchingJarResources(rootDirResource, subPattern));
            } else if (rootDirResource.getURL().getProtocol().startsWith(ResourceUtils.URL_PROTOCOL_VFS)) {
                result.addAll(VfsResourceMatchingDelegate.findMatchingResources(rootDirResource, subPattern,
                        getPathMatcher()));
            } else {
                result.addAll(doFindPathMatchingFileResources(rootDirResource, subPattern));
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Resolved location pattern [" + locationPattern + "] to resources " + result);
        }
        return result.toArray(new Resource[result.size()]);
    }

    /**
     * Inner delegate class, avoiding a hard JBoss VFS API dependency at runtime.
     */
    private static class VfsResourceMatchingDelegate {

        public static Set<Resource> findMatchingResources(Resource rootResource, String locationPattern,
                PathMatcher pathMatcher) throws IOException {
            Object root = Vfs2PatternUtils.findRoot(rootResource.getURL());
            PatternVirtualFileVisitor visitor = new PatternVirtualFileVisitor(Vfs2PatternUtils.getPath(root),
                    locationPattern, pathMatcher);
            Vfs2PatternUtils.visit(root, visitor);
            return visitor.getResources();
        }

        /**
         * VFS visitor for path matching purposes.
         */
        @SuppressWarnings("unused")
        private static class PatternVirtualFileVisitor implements InvocationHandler {

            private final String        subPattern;

            private final PathMatcher   pathMatcher;

            private final String        rootPath;

            private final Set<Resource> resources = new LinkedHashSet<Resource>();

            public PatternVirtualFileVisitor(String rootPath, String subPattern, PathMatcher pathMatcher) {
                this.subPattern = subPattern;
                this.pathMatcher = pathMatcher;
                this.rootPath = (rootPath.length() == 0 || rootPath.endsWith("/") ? rootPath : rootPath + "/");
            }

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String methodName = method.getName();
                if (Object.class.equals(method.getDeclaringClass())) {
                    if (methodName.equals("equals")) {
                        // Only consider equal when proxies are identical.
                        return (proxy == args[0]);
                    } else if (methodName.equals("hashCode")) {
                        return System.identityHashCode(proxy);
                    }
                } else if ("getAttributes".equals(methodName)) {
                    return getAttributes();
                } else if ("visit".equals(methodName)) {
                    visit(args[0]);
                    return null;
                } else if ("toString".equals(methodName)) {
                    return toString();
                }

                throw new IllegalStateException("Unexpected method invocation: " + method);
            }

            public void visit(Object vfsResource) {
                if (this.pathMatcher.match(this.subPattern,
                        Vfs2PatternUtils.getPath(vfsResource).substring(this.rootPath.length()))) {
                    this.resources.add(new Vfs2Resource(vfsResource));
                }
            }

            public Object getAttributes() {
                return Vfs2PatternUtils.getVisitorAttribute();
            }

            public Set<Resource> getResources() {
                return this.resources;
            }

            public int size() {
                return this.resources.size();
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append("sub-pattern: ").append(this.subPattern);
                sb.append(", resources: ").append(this.resources);
                return sb.toString();
            }
        }
    }

    /**
     * The fix https://jira.spring.io/browse/SPR-11676 for the 4.0.4 Spring release breaks VFS
     * support. By waiting the 4.0.6 release and its patch https://jira.spring.io/browse/SPR-11887,
     * we override this method in ordrer to remove the the URL_PROTOCOL_VFSZIP check. 
     * 
     */
    @Override
    protected boolean isJarResource(Resource resource) throws IOException {
        String protocol = resource.getURL().getProtocol();
        return (URL_PROTOCOL_JAR.equals(protocol) || URL_PROTOCOL_ZIP.equals(protocol) || URL_PROTOCOL_WSJAR.equals(protocol));

    }

}
