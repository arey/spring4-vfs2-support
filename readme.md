# JBoss VFS 2 support for Spring Framework 4.0 #

Spring Framework 4.0 removed support for JBoss AS 5's VFS variant. The Spring 4 VfsUtils class does not support any more the VFS 2 of JBoss AS 5 and JBoss 5.x EAP.
This project provides an AnnotationConfigWebApplicationContext subclass which support the VFS 2 of JBoss 5.
The JBoss5AnnotationConfigWebApplicationContext class worked with the Vfs2Utils class that is a simple copy/paste of the VfsUtils class of the Spring Framework 3.2.

References: 
* [GitHub commit by Juergen Hoeller](https://github.com/spring-projects/spring-framework/commit/ca194261a42a0a4f0c8bdc36f447e1029a7d2e3e)
* [Post on the Spring Forum](http://forum.spring.io/forum/spring-projects/container/744173-spring-4-doesn-t-support-vfs2)
## Build Status ##

Cloudbees Jenkins : [![Build
Status](https://javaetmoi.ci.cloudbees.com/job/spring4-vfs2-support/badge/icon)](https://javaetmoi.ci.cloudbees.com/job/spring4-vfs2-support/)