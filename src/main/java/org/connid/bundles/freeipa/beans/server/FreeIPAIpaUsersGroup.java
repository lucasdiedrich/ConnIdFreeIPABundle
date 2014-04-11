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
package org.connid.bundles.freeipa.beans.server;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.ModifyRequest;
import java.security.GeneralSecurityException;
import org.connid.bundles.freeipa.FreeIPAConfiguration;
import org.connid.bundles.freeipa.FreeIPAConnection;
import org.connid.bundles.freeipa.util.client.LDAPConstants;
import org.identityconnectors.common.logging.Log;

public class FreeIPAIpaUsersGroup {
    
    private static final Log LOG = Log.getLog(FreeIPAIpaUsersGroup.class);

    private final FreeIPAConfiguration freeIPAConfiguration;
    
    private final FreeIPAConnection freeIPAConnection;

    public FreeIPAIpaUsersGroup(final FreeIPAConfiguration freeIPAConfiguration) {
        this.freeIPAConfiguration = freeIPAConfiguration;
        this.freeIPAConnection = new FreeIPAConnection(freeIPAConfiguration);
    }
    
    public void addMember(final String newMemberDn) throws LDAPException, GeneralSecurityException {
        LOG.info("Adding member {0} to ipausers group", newMemberDn);
        final LDAPConnection lDAPConnection = freeIPAConnection.lDAPConnection();
        lDAPConnection.modify(new ModifyRequest(
                LDAPConstants.IPA_USERS_DN_BASE_SUFFIX + "," + freeIPAConfiguration.getRootSuffix(),
                new Modification(ModificationType.ADD, LDAPConstants.MEMBER_ATTRIBUTE, String.valueOf(newMemberDn))));
        lDAPConnection.close();
    }
    
    public void removeMember(final String existsMemberDn) throws LDAPException, GeneralSecurityException {
        LOG.info("Removing member {0} to ipausers group", existsMemberDn);
        final LDAPConnection lDAPConnection = freeIPAConnection.lDAPConnection();
        lDAPConnection.modify(new ModifyRequest(
                LDAPConstants.IPA_USERS_DN_BASE_SUFFIX + "," + freeIPAConfiguration.getRootSuffix(),
                new Modification(ModificationType.DELETE, LDAPConstants. MEMBER_ATTRIBUTE,
                        String.valueOf(existsMemberDn))));
        lDAPConnection.close();
    }

}
