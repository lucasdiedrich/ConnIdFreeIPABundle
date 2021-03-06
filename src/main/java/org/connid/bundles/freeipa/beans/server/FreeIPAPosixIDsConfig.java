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
import com.unboundid.ldap.sdk.SearchScope;
import java.security.GeneralSecurityException;
import org.connid.bundles.freeipa.FreeIPAConnection;
import org.connid.bundles.freeipa.util.client.LDAPConstants;

public class FreeIPAPosixIDsConfig {

    private static final String POSIXIDS_DN
            = "cn=Posix IDs,cn=Distributed Numeric Assignment Plugin,cn=plugins,cn=config";

    private static final String NEXT_VALUE_ATTRIBUTE = "dnaNextValue";

    private final FreeIPAConnection freeIPAConnection;

    public FreeIPAPosixIDsConfig(final FreeIPAConnection freeIPAConnection) {
        this.freeIPAConnection = freeIPAConnection;
    }

    public String nextPosixIDs() throws GeneralSecurityException, LDAPException {

        final LDAPConnection lDAPConnection = freeIPAConnection.lDAPConnection();
        final String nextPosixIDs = lDAPConnection.search(POSIXIDS_DN, SearchScope.BASE,
                LDAPConstants.OBJECT_CLASS_STAR, NEXT_VALUE_ATTRIBUTE).getSearchEntry(POSIXIDS_DN).getAttributeValue(
                        NEXT_VALUE_ATTRIBUTE);
        lDAPConnection.close();
        return nextPosixIDs;
    }

    public void updatePosixIDs(final String posixIDs)
            throws LDAPException, GeneralSecurityException {
        final LDAPConnection lDAPConnection = freeIPAConnection.lDAPConnection();
        lDAPConnection.modify(new ModifyRequest(POSIXIDS_DN, new Modification(
                ModificationType.REPLACE, NEXT_VALUE_ATTRIBUTE, String.valueOf(Integer.valueOf(posixIDs).intValue() + 1))));
        lDAPConnection.close();
    }
}
