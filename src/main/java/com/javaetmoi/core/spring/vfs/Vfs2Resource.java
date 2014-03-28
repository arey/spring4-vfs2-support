/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.javaetmoi.core.spring.vfs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.springframework.core.NestedIOException;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.VfsResource;
import org.springframework.util.Assert;

/**
 * VFS 2 based {@link Resource} implementation.
 * Supports the corresponding VFS API versions on JBoss AS 5.x and JBoss 5.x EAP.
 */
public class Vfs2Resource extends AbstractResource {

    private final Object resource;

    public Vfs2Resource(Object resources) {
        Assert.notNull(resources, "VirtualFile must not be null");
        this.resource = resources;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return Vfs2Utils.getInputStream(this.resource);
    }

    @Override
    public boolean exists() {
        return Vfs2Utils.exists(this.resource);
    }

    @Override
    public boolean isReadable() {
        return Vfs2Utils.isReadable(this.resource);
    }

    @Override
    public URL getURL() throws IOException {
        try {
            return Vfs2Utils.getURL(this.resource);
        } catch (Exception ex) {
            throw new NestedIOException("Failed to obtain URL for file " + this.resource, ex);
        }
    }

    @Override
    public URI getURI() throws IOException {
        try {
            return Vfs2Utils.getURI(this.resource);
        } catch (Exception ex) {
            throw new NestedIOException("Failed to obtain URI for " + this.resource, ex);
        }
    }

    @Override
    public File getFile() throws IOException {
        return Vfs2Utils.getFile(this.resource);
    }

    @Override
    public long contentLength() throws IOException {
        return Vfs2Utils.getSize(this.resource);
    }

    @Override
    public long lastModified() throws IOException {
        return Vfs2Utils.getLastModified(this.resource);
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        if (!relativePath.startsWith(".") && relativePath.contains("/")) {
            try {
                return new VfsResource(Vfs2Utils.getChild(this.resource, relativePath));
            } catch (IOException ex) {
                // fall back to getRelative
            }
        }

        return new VfsResource(Vfs2Utils.getRelative(new URL(getURL(), relativePath)));
    }

    @Override
    public String getFilename() {
        return Vfs2Utils.getName(this.resource);
    }

    @Override
    public String getDescription() {
        return this.resource.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj == this || (obj instanceof VfsResource && this.resource.equals(((Vfs2Resource) obj).resource)));
    }

    @Override
    public int hashCode() {
        return this.resource.hashCode();
    }

}