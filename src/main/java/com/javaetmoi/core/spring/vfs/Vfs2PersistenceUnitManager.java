package com.javaetmoi.core.spring.vfs;

import java.lang.reflect.Field;
import org.springframework.util.Assert;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.util.ReflectionUtils;

/**
 * Force the {@link DefaultPersistenceUnitManager} class to used the {@link Vfs2PathMatchingResourcePatternResolver}. 
 */
public class Vfs2PersistenceUnitManager extends DefaultPersistenceUnitManager implements
        ApplicationContextAware {

    private ApplicationContext applicationContext;

    public Vfs2PersistenceUnitManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Vfs2PersistenceUnitManager() {
        this(null);
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(applicationContext);
        Vfs2PathMatchingResourcePatternResolver resolver = new Vfs2PathMatchingResourcePatternResolver(
                applicationContext);
        Field resourcePatternResolverField = getResourcePatternResolverField();
        ReflectionUtils.setField(resourcePatternResolverField, this, resolver);
        super.afterPropertiesSet();
    }

    private Field getResourcePatternResolverField() {
        Field resourcePatternResolverField = ReflectionUtils.findField(
                DefaultPersistenceUnitManager.class, "resourcePatternResolver", null);
        Assert.notNull(resourcePatternResolverField, "The 'resourcePatternResolver' property of the DefaultPersistenceUnitManager class do not longuer exists" );
        ReflectionUtils.makeAccessible(resourcePatternResolverField);
        return resourcePatternResolverField;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
