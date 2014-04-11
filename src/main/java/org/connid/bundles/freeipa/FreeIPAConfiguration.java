/**
 * ====================
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008-2009 Sun Microsystems, Inc. All rights reserved.
 * Copyright 2011-2013 Tirasa. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License("CDDL") (the "License"). You may not use this file
 * except in compliance with the License.
 *
 * You can obtain a copy of the License at https://oss.oracle.com/licenses/CDDL
 * See the License for the specific language governing permissions and limitations
 * under the License.
 *
 * When distributing the Covered Code, include this CDDL Header Notice in each file
 * and include the License file at https://oss.oracle.com/licenses/CDDL.
 * If applicable, add the following below this CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * ====================
 */
package org.connid.bundles.freeipa;

import org.connid.bundles.ldap.LdapConfiguration;
import org.identityconnectors.framework.spi.ConfigurationProperty;

public class FreeIPAConfiguration extends LdapConfiguration {

    private boolean trustAllCerts;

    private String kerberosRealm;
    
    private String serverBaseHomeDirectory;
    
    private String rootSuffix;
    
    private String[] userBaseContextsToSynchronize;
    
    private String[] groupBaseContextsToSynchronize;
    
    private String groupSearchFilter;
    
    private String cnAttribute;

    @ConfigurationProperty(displayMessageKey = "trustallcerts.display",
            helpMessageKey = "trustallcerts.help", order = 1)
    public boolean isTrustAllCerts() {
        return trustAllCerts;
    }

    public void setTrustAllCerts(final boolean trustAllCerts) {
        this.trustAllCerts = trustAllCerts;
    }

    @ConfigurationProperty(displayMessageKey = "kerberosrealm.display",
            helpMessageKey = "kerberosrealm.help", order = 2)
    public String getKerberosRealm() {
        return kerberosRealm;
    }

    public void setKerberosRealm(final String KerberosRealm) {
        this.kerberosRealm = KerberosRealm;
    }

    @ConfigurationProperty(displayMessageKey = "server.base.home.directory.display",
            helpMessageKey = "server.base.home.directory.help", order = 3)
    public String getServerBaseHomeDirectory() {
        return serverBaseHomeDirectory;
    }

    public void setServerBaseHomeDirectory(final String serverBaseHomeDirectory) {
        this.serverBaseHomeDirectory = serverBaseHomeDirectory;
    }

    @ConfigurationProperty(displayMessageKey = "server.base.home.directory.display",
            helpMessageKey = "server.base.home.directory.help", order = 4)
    public String getRootSuffix() {
        return rootSuffix;
    }

    public void setRootSuffix(final String rootSuffix) {
        this.rootSuffix = rootSuffix;
    }

    @ConfigurationProperty(displayMessageKey = "server.base.home.directory.display",
            helpMessageKey = "server.base.home.directory.help", order = 5)
    public String[] getUserBaseContextsToSynchronize() {
        return userBaseContextsToSynchronize;
    }

    public void setUserBaseContextsToSynchronize(final String[] userBaseContextsToSynchronize) {
        this.userBaseContextsToSynchronize = userBaseContextsToSynchronize;
    }

    @ConfigurationProperty(displayMessageKey = "server.base.home.directory.display",
            helpMessageKey = "server.base.home.directory.help", order = 6)
    public String[] getGroupBaseContextsToSynchronize() {
        return groupBaseContextsToSynchronize;
    }

    public void setGroupBaseContextsToSynchronize(final String[] groupBaseContextsToSynchronize) {
        this.groupBaseContextsToSynchronize = groupBaseContextsToSynchronize;
    }

    @ConfigurationProperty(displayMessageKey = "server.base.home.directory.display",
            helpMessageKey = "server.base.home.directory.help", order = 7)
    public String getGroupSearchFilter() {
        return groupSearchFilter;
    }

    public void setGroupSearchFilter(String groupSearchFilter) {
        this.groupSearchFilter = groupSearchFilter;
    }

    @ConfigurationProperty(displayMessageKey = "server.base.home.directory.display",
            helpMessageKey = "server.base.home.directory.help", order = 8)
    public String getCnAttribute() {
        return cnAttribute;
    }

    public void setCnAttribute(String cnAttribute) {
        this.cnAttribute = cnAttribute;
    }
}
