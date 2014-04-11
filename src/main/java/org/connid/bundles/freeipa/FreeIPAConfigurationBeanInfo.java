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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.ArrayList;
import java.util.List;
import org.connid.bundles.ldap.LdapConfiguration;
import org.identityconnectors.common.logging.Log;

public class FreeIPAConfigurationBeanInfo extends SimpleBeanInfo {

    private static final Log LOG = Log.getLog(FreeIPAConfigurationBeanInfo.class);

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        final List<PropertyDescriptor> props = new ArrayList<PropertyDescriptor>();
        try {
            props.add(new PropertyDescriptor("trustAllCerts", FreeIPAConfiguration.class));
            props.add(new PropertyDescriptor("kerberosRealm", FreeIPAConfiguration.class));
            props.add(new PropertyDescriptor("serverBaseHomeDirectory", FreeIPAConfiguration.class));
            props.add(new PropertyDescriptor("rootSuffix", FreeIPAConfiguration.class));
            props.add(new PropertyDescriptor("userBaseContextsToSynchronize", FreeIPAConfiguration.class));
            props.add(new PropertyDescriptor("groupBaseContextsToSynchronize", FreeIPAConfiguration.class));
            props.add(new PropertyDescriptor("groupSearchFilter", FreeIPAConfiguration.class));
            props.add(new PropertyDescriptor("cnAttribute", FreeIPAConfiguration.class));
            props.add(new PropertyDescriptor("ssl", LdapConfiguration.class));
            props.add(new PropertyDescriptor("host", LdapConfiguration.class));
            props.add(new PropertyDescriptor("port", LdapConfiguration.class));
            props.add(new PropertyDescriptor("principal", LdapConfiguration.class));
            props.add(new PropertyDescriptor("uidAttribute", LdapConfiguration.class));
            props.add(new PropertyDescriptor("credentials", LdapConfiguration.class));
            props.add(new PropertyDescriptor("failover", LdapConfiguration.class));
            props.add(new PropertyDescriptor("passwordAttribute", LdapConfiguration.class));
            props.add(new PropertyDescriptor("accountSearchFilter", LdapConfiguration.class));
            props.add(new PropertyDescriptor("groupMemberAttribute", LdapConfiguration.class));
            props.add(new PropertyDescriptor("useBlocks", LdapConfiguration.class));
            props.add(new PropertyDescriptor("blockSize", LdapConfiguration.class));
            props.add(new PropertyDescriptor("usePagedResultControl", LdapConfiguration.class));
            props.add(new PropertyDescriptor("vlvSortAttribute", LdapConfiguration.class));
            props.add(new PropertyDescriptor("readSchema", LdapConfiguration.class));
            props.add(new PropertyDescriptor("uidAttribute", LdapConfiguration.class));
            props.add(new PropertyDescriptor("accountSynchronizationFilter", LdapConfiguration.class));
            props.add(new PropertyDescriptor("changeLogBlockSize", LdapConfiguration.class));
            props.add(new PropertyDescriptor("changeNumberAttribute", LdapConfiguration.class));
            props.add(new PropertyDescriptor("dnAttribute", LdapConfiguration.class));
        } catch (IntrospectionException e) {
            LOG.error(e, "Failure retrieving properties");
            props.clear();
        }

        return props.toArray(new PropertyDescriptor[props.size()]);
    }
}
