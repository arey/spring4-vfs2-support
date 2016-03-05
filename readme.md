# JBoss VFS 2 support for Spring Framework 4.0 #

Spring Framework 4.0 removed support for JBoss AS 5's VFS variant. The Spring 4 VfsUtils class does not support any more the VFS 2 of JBoss AS 5 and JBoss 5.x EAP.
This project provides an AnnotationConfigWebApplicationContext subclass which support the VFS 2 of JBoss 5.
The JBoss5AnnotationConfigWebApplicationContext and JBoss5XmlWebApplicationContext classes worked with the Vfs2Utils class that is a simple copy/paste of the VfsUtils class of the Spring Framework 3.2.

## Quick Start ##

1. Download the jar though Maven:

```xml
<dependency>
  <groupId>com.javaetmoi.core</groupId>
  <artifactId>javaetmoi-spring4-vfs2-support</artifactId>
  <version>1.4.1</version>
</dependency> 
```

The Spring Batch Toolkit artefacts are available from [Maven Central](http://repo1.maven.org/maven2/com/javaetmoi/core/javaetmoi-spring4-vfs2-support/)

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.javaetmoi.core/javaetmoi-spring4-vfs2-support/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.javaetmoi.core/javaetmoi-spring4-vfs2-support)

2. For Spring Java Config, declare the JBoss5AnnotationConfigWebApplicationContext into the web.xml

Either with the Spring ContextLoaderListener:
```
<context-param>
  <param-name>contextClass</param-name>
  <param-value>com.javaetmoi.core.spring.JBoss5AnnotationConfigWebApplicationContext</param-value>
</context-param>
<context-param>
  <param-name>contextConfigLocation</param-name>
  <param-value>com.example.MyAppWebConfig</param-value>
</context-param>
<listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
```

Or with the Spring DispatcherServlet:
```
<servlet>
  <servlet-name>mvc</servlet-name>
  <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
  <init-param>
    <param-name>contextClass</param-name>
    <param-value>com.javaetmoi.core.spring.JBoss5AnnotationConfigWebApplicationContext</param-value>
  </init-param>
  <init-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>com.example.MyAppWebConfig</param-value>
  </init-param>
  <load-on-startup>1</load-on-startup>
</servlet>
```

3. For traditional XML configuration, declare the JBoss5XmlWebApplicationContext into the web.xml 

```
<context-param>
  <param-name>contextClass</param-name>
  <param-value>com.javaetmoi.core.spring.JBoss5XmlWebApplicationContext</param-value>
</context-param>
<listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
```
 
4. Spring JPA support

This module provides the Vfs2PersistenceUnitManager class that extends the Vfs2PersistenceUnitManager from Spring ORM
in order to use the Vfs2PathMatchingResourcePatternResolver.
How to use it: 
 
```
@Bean
public EntityManagerFactory entityManagerFactory() {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(dataSource);
    em.setPersistenceUnitName("myPersitenceUnit");
    em.setPersistenceUnitManager(persistenceUnitManager());
    // ...
    em.afterPropertiesSet();
    return em.getObject();
}

@Bean
public Vfs2PersistenceUnitManager persistenceUnitManager() {
    Vfs2PersistenceUnitManager pum = new Vfs2PersistenceUnitManager(applicationContext);
    pum.setDefaultDataSource(dataSource);
    pum.setDefaultPersistenceUnitName("myPersitenceUnit");
    pum.setPackagesToScan("com.javaetmoi.demo.domain.model");
    return pum;
} 
 ```

With Hibernate implementation, you have to disable the DefaultScanner by using the DisableHibernateScanner.

First, customize the ```hibernate.ejb.resource_scanner``` hibernate property:
```
<util:map id="hibernateAdditionalProperties">
   <entry key="hibernate.ejb.resource_scanner"value="com.javaetmoi.core.spring.vfs.DisableHibernateScanner"/>
 </util:map>
```

Then declare it into the ```entityManagerFactory```:
```
<bean id="entityManagerFactory" class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean"
    p:dataSource-ref="dataSource" p:jpaVendorAdapter-ref="jpaAdapter">
   ...
   <property name="jpaPropertyMap" ref="hibernateAdditionalProperties"/>
 </bean>
 ```

4. Spring MVC webjar support

With Spring MVC, static resources could be served from a webjar.
The Vfs2ResourceHandlerRegistry class prevents you for having the error java.lang.ClassNotFoundException: org.jboss.vfs.VFS from BaseClassLoader. 

How to use it:
```
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private ApplicationContext applicationContext;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        new Vfs2ResourceHandlerRegistry(registry, applicationContext)
                .addResourceHandler("/resources/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
```

## References ##
* [French article explaining how is working this JBoss VFS 2 extension](http://javaetmoi.com/2014/04/support-vfs2-jboss5-spring4/)
* [GitHub commit by Juergen Hoeller](https://github.com/spring-projects/spring-framework/commit/ca194261a42a0a4f0c8bdc36f447e1029a7d2e3e)
* [Post on the Spring Forum](http://forum.spring.io/forum/spring-projects/container/744173-spring-4-doesn-t-support-vfs2)


## Release Note ##

<table>
  <tr>
    <th>Version</th><th>Release date</th><th>Features</th>
  </tr>
  <tr>
    <td>1.4.2-SNAPSHOT</td><td>next version</td><td></td>
  </tr>
  <tr>
    <td>1.4.1</td><td>05/03/2016</td><td>JBoss5GenericXmlApplicationContext added</td>
  </tr>
  <tr>
    <td>1.4.0</td><td>20/03/2015</td><td>Add VFS2 support for Hibernate.</td>
  </tr>
  <tr>
    <td>1.3.0</td><td>01/10/2014</td><td>Add VFS2 support for Spring MVC webjars.</td>
  </tr>  
  <tr>
    <td>1.2.0</td><td>02/07/2014</td><td>Fix added for Spring Framework 4.0.4 and 4.0.5. Add VFS2 support for JPA (Vfs2PersistenceUnitManager)</td>
  </tr>
  <tr>
    <td>1.1.0</td><td>31/03/2014</td><td>JBoss5XmlWebApplicationContext added</td>
  </tr>  
  <tr>
    <td>1.0.0</td><td>29/03/2014</td><td>First release which supports Spring Framework 4.0.3</td>
  </tr>
</table>

## Build Status ##

Cloudbees Jenkins : [![Build
Status](https://javaetmoi.ci.cloudbees.com/job/spring4-vfs2-support/badge/icon)](https://javaetmoi.ci.cloudbees.com/job/spring4-vfs2-support/)
