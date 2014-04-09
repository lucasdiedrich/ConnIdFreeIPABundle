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
package org.connid.bundles.freeipa.operations.crud.groups;

import org.connid.bundles.freeipa.operations.crud.users.*;
import com.unboundid.ldap.sdk.AddRequest;
import com.unboundid.ldap.sdk.LDAPException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.connid.bundles.freeipa.FreeIPAConfiguration;
import org.connid.bundles.freeipa.FreeIPAConnection;
import org.connid.bundles.freeipa.beans.server.FreeIPAGroupAccount;
import org.connid.bundles.freeipa.beans.server.PosixIDs;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.OperationalAttributes;
import org.identityconnectors.framework.common.objects.Uid;

public class FreeIPAGroupCreate {

    private static final Log LOG = Log.getLog(FreeIPAUserCreate.class);

    private final Set<Attribute> attrs;

    private final OperationOptions options;

    private final FreeIPAConfiguration freeIPAConfiguration;

    private final FreeIPAConnection freeIPAConnection;

    private final PosixIDs posixIDs;

    public FreeIPAGroupCreate(final ObjectClass oclass,
            final Set<Attribute> attrs,
            final OperationOptions options,
            final FreeIPAConfiguration freeIPAConfiguration) {
        this.attrs = attrs;
        this.options = options;
        this.freeIPAConfiguration = freeIPAConfiguration;
        this.freeIPAConnection = new FreeIPAConnection(freeIPAConfiguration);
        this.posixIDs = new PosixIDs(freeIPAConfiguration);
    }

    public final Uid createGroup() {
        try {
            return doCreate();
        } catch (LDAPException e) {
            LOG.error(e, "error during create operation");
            throw new ConnectorException(e);
        } catch (GeneralSecurityException e) {
            LOG.error(e, "error during create operation");
            throw new ConnectorException(e);
        }
    }

    private Uid doCreate() throws LDAPException, GeneralSecurityException {
        final Name nameAttr = AttributeUtil.getNameFromAttributes(attrs);

        if (nameAttr == null) {
            LOG.error("No Name attribute provided in the attributes");
            throw new IllegalArgumentException("No Name attribute provided in the attributes");
        }

        LOG.info("Name found {0}", nameAttr.getNameValue());
        final String posixIDsNumber = posixIDs.nextPosixIDs(freeIPAConfiguration);
        LOG.info("Next posix IDs {0}", posixIDsNumber);
        for (Attribute attribute : attrs) {
            LOG.info("Attribute name {0}", attribute.getName());
            LOG.info("Attribute value {0}", attribute.getValue().get(0));
        }

        final Map<String, List<Object>> otherAttributes = new HashMap<String, List<Object>>();
        String description = "";
        for (final Attribute attribute : attrs) {
            if (FreeIPAGroupAccount.DefaultAttributes.DESCRIPTION.ldapValue().equalsIgnoreCase(attribute.getName())) {
                description = attribute.getValue().get(0).toString();
            }
            if (attribute.is(OperationalAttributes.PASSWORD_NAME)) {
                //DO NOTHING
            } else if (attribute.is(OperationalAttributes.ENABLE_NAME)) {
                //DO NOTHING
            } else if (attribute.is(Name.NAME)) {
                //DO NOTHING
            } else {
                otherAttributes.put(attribute.getName(), attribute.getValue());
            }
        }

        if (StringUtil.isBlank(description)) {
            throw new IllegalArgumentException("Description attribute are required");
        }

        final FreeIPAGroupAccount freeIPAGroupAccount
                = new FreeIPAGroupAccount(nameAttr.getNameValue(), description, posixIDsNumber, freeIPAConfiguration);

        LOG.info("Group account {0}", freeIPAGroupAccount);

        final AddRequest addRequest = freeIPAGroupAccount.toAddRequest();

        if (!otherAttributes.isEmpty()) {
            freeIPAGroupAccount.fillOtherAttributesToAddRequest(otherAttributes, addRequest);
        }

        freeIPAConnection.lDAPConnection().add(addRequest);
        posixIDs.updatePosixIDs(posixIDsNumber, freeIPAConfiguration);
        return new Uid(nameAttr.getNameValue());
    }
}