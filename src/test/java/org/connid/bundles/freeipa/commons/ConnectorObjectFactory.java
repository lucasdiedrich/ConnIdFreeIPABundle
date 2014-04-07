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
package org.connid.bundles.freeipa.commons;

import java.util.ResourceBundle;
import org.connid.bundles.freeipa.FreeIPAConfiguration;
import org.identityconnectors.common.security.GuardedString;

public class ConnectorObjectFactory {

    public static ResourceBundle freeipaServerProperties = ResourceBundle.getBundle("freeipaserver");
    
    public static FreeIPAConfiguration configurationWithRightUsernameAndPassword() {
        final FreeIPAConfiguration freeIPAConfiguration = configuration();
        freeIPAConfiguration.setPrincipal(freeipaServerProperties.getString("freeipa.server.administrator.principal"));
        freeIPAConfiguration.setCredentials(
                new GuardedString(freeipaServerProperties.getString("freeipa.server.administrator.credentials").
                        toCharArray()));
        return freeIPAConfiguration;
    }
    
    public static FreeIPAConfiguration configurationWithWrongUsername() {
        final FreeIPAConfiguration freeIPAConfiguration = configuration();
        freeIPAConfiguration.setPrincipal("WRONG_PRINCIPAL");
        freeIPAConfiguration.setCredentials(
                new GuardedString(freeipaServerProperties.getString("freeipa.server.administrator.credentials").
                        toCharArray()));
        return freeIPAConfiguration;
    }
    
    public static FreeIPAConfiguration configurationWithWrongPassword() {
        final FreeIPAConfiguration freeIPAConfiguration = configuration();
        freeIPAConfiguration.setPrincipal(freeipaServerProperties.getString("freeipa.server.administrator.principal"));
        freeIPAConfiguration.setCredentials(
                new GuardedString("WRONG_PASSWORD".toCharArray()));
        return freeIPAConfiguration;
    }

    private static FreeIPAConfiguration configuration() {
        final FreeIPAConfiguration freeIPAConfiguration = new FreeIPAConfiguration();
        freeIPAConfiguration.setHost(freeipaServerProperties.getString("freeipa.server.host"));
        freeIPAConfiguration.setSsl(
                Boolean.valueOf(freeipaServerProperties.getString("freeipa.server.ldap.protocol.ssl")));
        if (freeIPAConfiguration.isSsl()) {
            freeIPAConfiguration.setHost(freeipaServerProperties.getString("freeipa.server.ldap.protocol.port"));
        } else {
            freeIPAConfiguration.setHost(freeipaServerProperties.getString("freeipa.server.ldaps.protocol.port"));
        }
        
        freeIPAConfiguration.setBaseContexts(freeipaServerProperties.getString("freeipa.server.tree.basecontext"));
        freeIPAConfiguration.setHost(freeipaServerProperties.getString("freeipa.server.host"));
        freeIPAConfiguration.setUidAttribute(freeipaServerProperties.getString("freeipa.server.tree.users.attributes.uid"));
        return freeIPAConfiguration;
    }
}
