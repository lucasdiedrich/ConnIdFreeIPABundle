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
package org.connid.bundles.freeipa.operations.crud.users;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchScope;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.connid.bundles.freeipa.FreeIPAConfiguration;
import org.connid.bundles.freeipa.FreeIPAConnection;
import org.connid.bundles.freeipa.util.client.ConnectorUtils;
import org.connid.bundles.freeipa.beans.server.FreeIPAUserAccount;
import org.connid.bundles.freeipa.util.client.LDAPConstants;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.OperationalAttributes;
import org.identityconnectors.framework.common.objects.Uid;

public class FreeIPAUserUpdate {

    private static final Log LOG = Log.getLog(FreeIPAUserUpdate.class);

    private final Uid uid;

    private final Set<Attribute> attrs;

    private final FreeIPAConfiguration freeIPAConfiguration;

    private final FreeIPAConnection freeIPAConnection;

    public FreeIPAUserUpdate(final Uid uid,
            final Set<Attribute> replaceAttributes,
            final FreeIPAConfiguration freeIPAConfiguration) {
        this.uid = uid;
        this.attrs = replaceAttributes;
        this.freeIPAConfiguration = freeIPAConfiguration;
        this.freeIPAConnection = new FreeIPAConnection(freeIPAConfiguration);
    }

    public final Uid updateUser() {
        if (attrs == null || attrs.isEmpty()) {
            return uid;
        }
        try {
            return doUpdate();
        } catch (LDAPException e) {
            LOG.error(e, "error during update operation");
            throw new ConnectorException(e);
        } catch (GeneralSecurityException e) {
            LOG.error(e, "error during update operation");
            throw new ConnectorException(e);
        }
    }

    private Uid doUpdate() throws LDAPException, GeneralSecurityException {

        if (uid == null) {
            LOG.error("No uid attribute provided in the attributes");
            throw new IllegalArgumentException("No uid attribute provided in the attributes");
        }

        LOG.info("uid found {0}", uid.getUidValue());

        final Map<String, List<Object>> otherAttributes = new HashMap<String, List<Object>>();
        String password = "";
        Boolean attrStatus = null;
        for (final Attribute attribute : attrs) {
            LOG.info("Found attribute {0} to update", attribute.getName());
            if (attribute.is(OperationalAttributes.PASSWORD_NAME)) {
                password = ConnectorUtils.getPlainPassword((GuardedString) attribute.getValue().get(0));
            } else if (attribute.is(OperationalAttributes.ENABLE_NAME)) {
                if (attribute.getValue() != null && !attribute.getValue().isEmpty()) {
                    attrStatus = Boolean.parseBoolean(attribute.getValue().get(0).toString());
                }
            } else if (attribute.is(Name.NAME)) {
                //DO NOTHING
            } else {
                otherAttributes.put(attribute.getName(), attribute.getValue());
            }
        }

        final ModifyRequest modifyRequest = FreeIPAUserAccount.createModifyRequest(
                uid, password, attrStatus, otherAttributes, freeIPAConfiguration);

        LOG.info("Calling server to modify {0}", modifyRequest.getDN());

        try {
            final SearchResult sr = freeIPAConnection.lDAPConnection().search(
                    modifyRequest.getDN(),
                    SearchScope.BASE,
                    freeIPAConfiguration.getAccountSearchFilter(),
                    LDAPConstants.OBJECT_CLASS_STAR);
            if (ResultCode.SUCCESS.equals(sr.getResultCode())) {
                freeIPAConnection.lDAPConnection().modify(modifyRequest);
            }
        } catch (final LDAPSearchException e) {
            if (ResultCode.NO_SUCH_OBJECT.equals(e.getResultCode())) {
                throw new ConnectorException(String.format("User %s already exists", uid.getUidValue()));
            }

        }
        
        return uid;
    }
}
