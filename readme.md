# JBoss VFS 2 support for Spring Framework 4.0 #

Spring Framework 4.0 removed support for JBoss AS 5's VFS variant. The Spring 4 VfsUtils class does not support any more the VFS 2 of JBoss AS 5 and JBoss 5.x EAP.
This project provides the AnnotationConfigWebApplicationContext and JBoss5XmlWebApplicationContext subclasses which support the VFS 2 of JBoss 5.
The JBoss5AnnotationConfigWebApplicationContext class worked with the Vfs2Utils class that is a simple copy/paste of the VfsUtils class of the Spring Framework 3.2.

## Quick Start ##

1. Download the jar though Maven:

```xml
<dependency>
  <groupId>com.javaetmoi.core</groupId>
  <artifactId>javaetmoi-spring4-vfs2-support</artifactId>
  <version>1.1.0</version>
</dependency> 
       
<repository>
  <id>javaetmoi-maven-release</id>
  <releases>
    <enabled>true</enabled>
  </releases>
  <name>Java et Moi Maven RELEASE Repository</name>
  <url>http://repository-javaetmoi.forge.cloudbees.com/release/</url>
</repository>
```

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
 

## References ##
* [GitHub commit by Juergen Hoeller](https://github.com/spring-projects/spring-framework/commit/ca194261a42a0a4f0c8bdc36f447e1029a7d2e3e)
* [Post on the Spring Forum](http://forum.spring.io/forum/spring-projects/container/744173-spring-4-doesn-t-support-vfs2)


## Release Note ##

<table>
  <tr>
    <th>Version</th><th>Release date</th><th>Features</th>
  </tr>
  <tr>
    <td>1.1.1-SNAPSHOT</td><td>next version</td><td></td>
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
