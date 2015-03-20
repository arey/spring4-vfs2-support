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

import java.util.Collections;
import java.util.Set;

import org.hibernate.jpa.boot.archive.internal.StandardArchiveDescriptorFactory;
import org.hibernate.jpa.boot.scan.spi.AbstractScannerImpl;
import org.hibernate.jpa.boot.scan.spi.ScanOptions;
import org.hibernate.jpa.boot.scan.spi.ScanResult;
import org.hibernate.jpa.boot.spi.ClassDescriptor;
import org.hibernate.jpa.boot.spi.MappingFileDescriptor;
import org.hibernate.jpa.boot.spi.PackageDescriptor;
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * Disable Hibernate 4 scanner for compatibility with the JBoss 5 EAP VFS2.
 *
 * <p>
 * To use Hibernate 4 support of the Spring Framework with JBoss 5 EAP, you have to
 * use the method {@link LocalContainerEntityManagerFactoryBean#setPackagesToScan(String...)}.<br>
 * Hibernate does not have to scan archives in order to find JPA entities.<br/>
 * With StandardScanner implementation, the vfszip protocol triggers the stacktrace:
 *  <blockquote> Caused by:
 * java.lang.IllegalArgumentException: File [/C://servers/jboss-eap-5.1/jboss-as/server
 * /default/deploy/myapp-ear-1.0.0.ear/myapp-war-15.1.0.war/WEB-INF/classes/]
 * referenced by given URL [vfszip:/C://servers/jboss-eap-5.1/jboss-
 * as/server/default/deploy/myapp-ear-15.1.0.ear/vital-war-15.1.0.war/WEB-INF/classes/]
 * does not exist</blockquote>
 * </p>
 * 
 */
public class DisableHibernateScanner extends AbstractScannerImpl {

    public DisableHibernateScanner() {
        super(StandardArchiveDescriptorFactory.INSTANCE);
    }

    @Override
    public ScanResult scan(PersistenceUnitDescriptor persistenceUnit, ScanOptions scanOptions) {
        final ResultCollector resultCollector = new ResultCollector(scanOptions);
        // Don't scan any archive
        return ScanResultImpl.from(resultCollector);
    }

    private static class ScanResultImpl implements ScanResult {

        private final Set<PackageDescriptor>     packageDescriptorSet;
        private final Set<ClassDescriptor>       classDescriptorSet;
        private final Set<MappingFileDescriptor> mappingFileSet;

        private ScanResultImpl(Set<PackageDescriptor> packageDescriptorSet,
                Set<ClassDescriptor> classDescriptorSet, Set<MappingFileDescriptor> mappingFileSet) {
            this.packageDescriptorSet = packageDescriptorSet;
            this.classDescriptorSet = classDescriptorSet;
            this.mappingFileSet = mappingFileSet;
        }

        static ScanResult from(ResultCollector resultCollector) {
            return new ScanResultImpl(
                    Collections.unmodifiableSet(resultCollector.getPackageDescriptorSet()),
                    Collections.unmodifiableSet(resultCollector.getClassDescriptorSet()),
                    Collections.unmodifiableSet(resultCollector.getMappingFileSet()));
        }

        @Override
        public Set<PackageDescriptor> getLocatedPackages() {
            return packageDescriptorSet;
        }

        @Override
        public Set<ClassDescriptor> getLocatedClasses() {
            return classDescriptorSet;
        }

        @Override
        public Set<MappingFileDescriptor> getLocatedMappingFiles() {
            return mappingFileSet;
        }
    }
}
