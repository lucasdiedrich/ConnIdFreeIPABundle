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
package org.connid.bundles.freeipa.operations;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.schema.AttributeTypeDefinition;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Set;
import org.connid.bundles.freeipa.FreeIPAConfiguration;
import org.connid.bundles.freeipa.FreeIPAConnection;
import org.connid.bundles.freeipa.FreeIPAConnector;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.AttributeInfo;
import org.identityconnectors.framework.common.objects.AttributeInfoBuilder;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.ObjectClassInfo;
import org.identityconnectors.framework.common.objects.ObjectClassInfoBuilder;
import org.identityconnectors.framework.common.objects.Schema;
import org.identityconnectors.framework.common.objects.SchemaBuilder;

public class FreeIPASchema {

    private static final Log LOG = Log.getLog(FreeIPASchema.class);

    private final FreeIPAConfiguration freeIPAConfiguration;
    
    private final FreeIPAConnection freeIPAConnection;

    public FreeIPASchema(final FreeIPAConfiguration freeIPAConfiguration) {
        this.freeIPAConfiguration = freeIPAConfiguration;
        this.freeIPAConnection = new FreeIPAConnection(freeIPAConfiguration);
    }

    public Schema schema() {
        try {
            return doSchema();
        } catch (LDAPException e) {
            LOG.error(e, "error reading schema");
            throw new ConnectorException(e);
        } catch (GeneralSecurityException e) {
            LOG.error(e, "error reading schema");
            throw new ConnectorException(e);
        }
    }
    
    private Schema doSchema() throws GeneralSecurityException, LDAPException {
        final Set<AttributeInfo> attributes = new HashSet<AttributeInfo>();
        
        for (final AttributeTypeDefinition attributeTypeDefinition :
                freeIPAConnection.lDAPConnection().getSchema().getUserAttributeTypes()) {
            attributes.add(buildAttribute(attributeTypeDefinition.getNameOrOID()));
        }

        final SchemaBuilder schemaBld = new SchemaBuilder(FreeIPAConnector.class);

        final ObjectClassInfoBuilder objectclassInfoBuilder = new ObjectClassInfoBuilder();
        objectclassInfoBuilder.setType(ObjectClass.ACCOUNT_NAME);
        objectclassInfoBuilder.addAllAttributeInfo(attributes);

        final ObjectClassInfo objectclassInfo = objectclassInfoBuilder.build();
        schemaBld.defineObjectClass(objectclassInfo);
        return schemaBld.build();
    }
    
    private AttributeInfo buildAttribute(final String ldapAttributeName) {
        final AttributeInfoBuilder builder = new AttributeInfoBuilder();

        try {

            if (freeIPAConfiguration.getUidAttribute().equals(ldapAttributeName)) {
                builder.setName(Name.NAME);
                builder.setReadable(true);
                return builder.build();
            } else {
                builder.setName(ldapAttributeName);
            }

            return builder.build();
        } catch (final Throwable t) {
            LOG.error("Unexpected exception", t);

            throw new IllegalArgumentException(t);
        }
    }

}
