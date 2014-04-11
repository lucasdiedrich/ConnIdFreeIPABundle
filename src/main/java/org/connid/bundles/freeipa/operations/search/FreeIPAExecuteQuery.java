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
package org.connid.bundles.freeipa.operations.search;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import java.security.GeneralSecurityException;
import org.connid.bundles.freeipa.FreeIPAConfiguration;
import org.connid.bundles.freeipa.FreeIPAConnection;
import org.connid.bundles.freeipa.beans.server.FreeIPAUserAccount;
import org.connid.bundles.freeipa.util.client.LDAPConstants;
import org.connid.bundles.ldap.search.LdapFilter;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ConnectorObjectBuilder;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.ResultsHandler;

public class FreeIPAExecuteQuery {

    private static final Log LOG = Log.getLog(FreeIPAExecuteQuery.class);

    private final ObjectClass objectClass;

    private final LdapFilter ldapFilter;

    private final ResultsHandler resultsHandler;

    final FreeIPAConfiguration freeIPAConfiguration;

    final FreeIPAConnection freeIPAConnection;

    public FreeIPAExecuteQuery(final ObjectClass objectClass, final LdapFilter query, final ResultsHandler handler,
            final FreeIPAConfiguration freeIPAConfiguration) {
        this.objectClass = objectClass;
        ldapFilter = query;
        resultsHandler = handler;
        this.freeIPAConfiguration = freeIPAConfiguration;
        freeIPAConnection = new FreeIPAConnection(freeIPAConfiguration);
    }

    public void executeQuery() {
        try {
            doExecuteQuery();
        } catch (final LDAPException ex) {
            throw new ConnectorException(ex);
        } catch (final GeneralSecurityException ex) {
            throw new ConnectorException(ex);
        }
    }

    public void doExecuteQuery() throws LDAPException, GeneralSecurityException, ConnectorException {
        if (resultsHandler == null) {
            throw new IllegalArgumentException("Invalid handler");
        }

        if (ldapFilter == null) {
            for (final String baseContext : freeIPAConfiguration.getBaseContextsToSynchronize()) {
                fillUserHandler(freeIPAConnection.lDAPConnection().search(
                        baseContext,
                        SearchScope.SUB,
                        freeIPAConfiguration.getAccountSearchFilter()));
            }
        } else {
            final Filter filter = Filter.create(ldapFilter.getNativeFilter());
            LOG.info("Ldap search filter {0}", filter.toNormalizedString());
            fillUserHandler(freeIPAConnection.lDAPConnection().search(
                    LDAPConstants.USERS_DN_BASE_SUFFIX + "," + freeIPAConfiguration.getRootSuffix(),
                    SearchScope.SUB,
                    filter));
        }
    }

    private void fillUserHandler(final SearchResult results) throws LDAPException, GeneralSecurityException {
        if (results == null) {
            throw new ConnectorException("No results found");
        }

        final ConnectorObjectBuilder bld = new ConnectorObjectBuilder();

        String uid = "";
        for (final SearchResultEntry searchResultEntry : results.getSearchEntries()) {
            LOG.info("Adding {0} to results", searchResultEntry.getDN());

            for (final Attribute attribute : searchResultEntry.getAttributes()) {
                if (freeIPAConfiguration.getUidAttribute().equalsIgnoreCase(attribute.getName())) {
                    bld.setName(attribute.getValue());
                    bld.setUid(attribute.getValue());
                    uid = attribute.getValue();
                } else if (freeIPAConfiguration.getPasswordAttribute().equalsIgnoreCase(attribute.getName())) {
                    //DO NOTHING
                } else {
                    bld.addAttribute(attribute.getName(), attribute.getValue());
                }
            }

            if (StringUtil.isNotBlank(uid) && objectClass.equals(ObjectClass.ACCOUNT)) {
                bld.addAttribute(AttributeBuilder.buildEnabled(FreeIPAUserAccount.isEnabled(uid, freeIPAConfiguration)));
            }

            bld.setObjectClass(objectClass);
            resultsHandler.handle(bld.build());
        }
    }

}
