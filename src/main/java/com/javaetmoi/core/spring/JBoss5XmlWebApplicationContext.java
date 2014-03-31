/**
 * Copyright 2013 the original author or authors.
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
package com.javaetmoi.core.spring;

import org.springframework.core.io.VfsUtils;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.javaetmoi.core.spring.vfs.Vfs2PathMatchingResourcePatternResolver;


/**
 * {@link XmlWebApplicationContext} subclass which support the VFS 2 and JBoss 5.
 * 
 * <p>
 * Spring Framework 4.0 removed support for JBoss AS 5's VFS variant. The {@link VfsUtils} class
 * does not support any more the VFS 2 of JBoss AS 5 or JBoss 5.x EAP.
 * </p>
 * 
 * @see https://github.com/spring-projects/spring-framework/commit/ca194261a42a0a4f0c8bdc36f447e1029a7d2e3e
 * @see http://forum.spring.io/forum/spring-projects/container/744173-spring-4-doesn-t-support-vfs2
 */
public class JBoss5XmlWebApplicationContext  extends XmlWebApplicationContext {

    @Override
    protected ResourcePatternResolver getResourcePatternResolver() {
        return new Vfs2PathMatchingResourcePatternResolver(this);
    }

}


