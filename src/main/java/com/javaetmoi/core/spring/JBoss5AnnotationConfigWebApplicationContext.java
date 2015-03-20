package com.javaetmoi.core.spring;

import org.springframework.core.io.VfsUtils;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.javaetmoi.core.spring.vfs.Vfs2PathMatchingResourcePatternResolver;

/**
 * {@link AnnotationConfigWebApplicationContext} subclass which support the VFS 2 and JBoss 5.
 * 
 * <p>
 * Spring Framework 4.0 removed support for JBoss AS 5's VFS variant. The {@link VfsUtils} class
 * does not support any more the VFS 2 of JBoss AS 5 or JBoss 5.x EAP.
 * </p>
 * 
 * @see <a href="https://github.com/spring-projects/spring-framework/commit/ca194261a42a0a4f0c8bdc36f447e1029a7d2e3e">https://github.com/spring-projects/spring-framework/commit/ca194261a42a0a4f0c8bdc36f447e1029a7d2e3e</a>
 * @see <a href="http://forum.spring.io/forum/spring-projects/container/744173-spring-4-doesn-t-support-vfs2">http://forum.spring.io/forum/spring-projects/container/744173-spring-4-doesn-t-support-vfs2</a>
 */
public class JBoss5AnnotationConfigWebApplicationContext extends AnnotationConfigWebApplicationContext {

    @Override
    protected ResourcePatternResolver getResourcePatternResolver() {
        return new Vfs2PathMatchingResourcePatternResolver(this);
    }

}
