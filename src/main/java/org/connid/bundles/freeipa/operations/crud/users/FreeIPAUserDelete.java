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
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchScope;
import java.security.GeneralSecurityException;
import org.connid.bundles.freeipa.FreeIPAConfiguration;
import org.connid.bundles.freeipa.FreeIPAConnection;
import org.connid.bundles.freeipa.beans.server.FreeIPAIpaUsersGroup;
import org.connid.bundles.freeipa.beans.server.FreeIPAUserAccount;
import org.connid.bundles.freeipa.util.client.LDAPConstants;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.Uid;

public class FreeIPAUserDelete {

    private static final Log LOG = Log.getLog(FreeIPAUserDelete.class);

    private final Uid uid;

    private final FreeIPAConnection freeIPAConnection;

    public FreeIPAUserDelete(final Uid uid, final FreeIPAConfiguration freeIPAConfiguration) {
        this.uid = uid;
        this.freeIPAConnection = new FreeIPAConnection(freeIPAConfiguration);
    }

    public final void deleteUser() {
        try {
            doDelete();
        } catch (LDAPException e) {
            LOG.error(e, "error during delete operation");
            throw new ConnectorException(e);
        } catch (GeneralSecurityException e) {
            LOG.error(e, "error during delete operation");
            throw new ConnectorException(e);
        }
    }

    private void doDelete() throws LDAPException, GeneralSecurityException {

        if (uid == null) {
            LOG.error("No uid attribute provided in the attributes");
            throw new IllegalArgumentException("No uid attribute provided in the attributes");
        }

        LOG.info("Calling server to delete {0}", uid.getUidValue());
        
        try {
            final String userDn = FreeIPAUserAccount.userDN(uid.getUidValue());
            final SearchResult sr = freeIPAConnection.lDAPConnection().search(userDn,
                    SearchScope.BASE, "uid=*", LDAPConstants.OBJECT_CLASS_STAR);
            if (ResultCode.SUCCESS.equals(sr.getResultCode())) {
                new FreeIPAIpaUsersGroup(freeIPAConnection).removeMember(userDn);
                freeIPAConnection.lDAPConnection().delete(userDn);
            }
        } catch (final LDAPSearchException e) {
            if (ResultCode.NO_SUCH_OBJECT.equals(e.getResultCode())) {
                throw new ConnectorException(String.format("User %s does not exists", uid.getUidValue()));
            }
        }        
    }
}
