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

import static org.connid.bundles.ldap.commons.LdapUtil.nullAsEmpty;

import com.sun.jndi.ldap.ctl.PasswordExpiredResponseControl;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;
import java.security.GeneralSecurityException;
import java.util.Hashtable;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import org.connid.bundles.freeipa.exceptions.FreeIPAException;
import org.connid.bundles.freeipa.util.client.ConnectorUtils;
import org.connid.bundles.freeipa.util.client.AuthResults;
import org.connid.bundles.freeipa.util.client.TrustAllSocketFactory;
import org.connid.bundles.ldap.LdapConnection;
import org.identityconnectors.common.Pair;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

public class FreeIPAConnection extends LdapConnection {

    private static final Log LOG = Log.getLog(FreeIPAConnection.class);

    private static final String LDAP_CTX_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";

    private static final String LDAP_CTX_SOCKET_FACTORY = "java.naming.ldap.factory.socket";

    private LdapContext initCtx = null;

    private LDAPConnection lDAPConnection = null;

    private final FreeIPAConfiguration freeIPAConfiguration;

    public FreeIPAConnection(final FreeIPAConfiguration config) {
        super(config);
        this.freeIPAConfiguration = config;
    }

    @Override
    public LdapContext getInitialContext() throws FreeIPAException {
        if (initCtx == null) {
            initCtx = createInitialContex(authenticateWithConfiguration()).second;
        }
        return initCtx;
    }

    public AuthResults login(final String principal, final GuardedString password) throws FreeIPAException {
        return createInitialContex(authenticateWithUsernameAndPassword(principal, password)).first;
    }

    private Pair<AuthResults, LdapContext> createInitialContex(final Hashtable<String, Object> env) throws
            FreeIPAException {
        AuthResults authResult = AuthResults.AUTH_SUCCESS;
        InitialLdapContext context = null;
        try {
            context = new InitialLdapContext(env, null);

            if (freeIPAConfiguration.
                    isRespectResourcePasswordPolicyChangeAfterReset()) {
                if (hasPasswordExpiredControl(context.getResponseControls())) {
                    authResult = AuthResults.AUTH_PASSWORD_EXPIRED;
                }
            }
            // TODO: process Password Policy control.
        } catch (final AuthenticationException e) {
            LOG.error("Authentication error initializing ldap context {0}", e.getMessage());
            authResult = AuthResults.fromValue(e.getMessage());
            throw new FreeIPAException(authResult, e);
        } catch (final NamingException e) {
            System.out.println(">>>>>>>>>>>>>>>> NamingException: " + e.getMessage());
        }
        if (!authResult.equals(AuthResults.AUTH_SUCCESS)) {

        }
        return new Pair<AuthResults, LdapContext>(authResult, context);
    }

    private Hashtable<String, Object> authenticateWithUsernameAndPassword(
            final String principal, final GuardedString password) {
        final Hashtable<String, Object> ldapEnvironment = createEnvironment();
        ldapEnvironment.put(Context.SECURITY_PRINCIPAL, principal);
        ldapEnvironment.put(Context.SECURITY_CREDENTIALS, ConnectorUtils.getPlainPassword(password));
        return ldapEnvironment;
    }

    private Hashtable<String, Object> authenticateWithConfiguration() {
        final Hashtable<String, Object> ldapEnvironment = createEnvironment();
        ldapEnvironment.put(Context.SECURITY_PRINCIPAL, freeIPAConfiguration.getPrincipal());
        ldapEnvironment.put(Context.SECURITY_CREDENTIALS, ConnectorUtils.getPlainPassword(freeIPAConfiguration.
                getCredentials()));
        return ldapEnvironment;
    }

    private Hashtable<String, Object> createEnvironment() {
        final Hashtable<String, Object> ldapEnvironment = new Hashtable<String, Object>();
        ldapEnvironment.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_CTX_FACTORY);
        ldapEnvironment.put(Context.PROVIDER_URL, getLdapUrls());
        ldapEnvironment.put(Context.REFERRAL, "follow");
        ldapEnvironment.put(Context.SECURITY_AUTHENTICATION, "simple");
        if (freeIPAConfiguration.isSsl()) {
            ldapEnvironment.put(Context.SECURITY_PROTOCOL, "ssl");
            if (freeIPAConfiguration.isTrustAllCerts()) {
                ldapEnvironment.put(LDAP_CTX_SOCKET_FACTORY, TrustAllSocketFactory.class.getName());
            }
        }

        return ldapEnvironment;
    }

    private static boolean hasPasswordExpiredControl(Control[] controls) {
        if (controls != null) {
            for (Control control : controls) {
                if (control instanceof PasswordExpiredResponseControl) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getLdapUrls() {
        final StringBuilder ldapUrlBuilder = new StringBuilder();
        ldapUrlBuilder.append("ldap://").
                append(freeIPAConfiguration.getHost()).append(':').append(freeIPAConfiguration.getPort());
        for (final String failover : nullAsEmpty(freeIPAConfiguration.getFailover())) {
            ldapUrlBuilder.append(' ');
            ldapUrlBuilder.append(failover);
        }
        LOG.info("Connecting to {0} server", ldapUrlBuilder.toString());
        return ldapUrlBuilder.toString();
    }

    @Override
    public void test() {
        checkAlive();
    }

    @Override
    public void checkAlive() {
        try {
            final Attributes attrs = getInitialContext().getAttributes("", new String[]{"subschemaSubentry"});
            attrs.get("subschemaSubentry");
        } catch (NamingException e) {
            throw new ConnectorException(e);
        }
    }

    public LDAPConnection lDAPConnection() throws LDAPException, GeneralSecurityException {
        if (lDAPConnection == null || !lDAPConnection.isConnected()) {
            if (freeIPAConfiguration.isSsl()) {
                final SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
                lDAPConnection = new LDAPConnection(
                        sslUtil.createSSLSocketFactory(),
                        freeIPAConfiguration.getHost(),
                        freeIPAConfiguration.getPort(),
                        freeIPAConfiguration.getPrincipal(),
                        ConnectorUtils.getPlainPassword(freeIPAConfiguration.getCredentials()));
            } else {
                lDAPConnection = new LDAPConnection(
                        freeIPAConfiguration.getHost(),
                        freeIPAConfiguration.getPort(),
                        freeIPAConfiguration.getPrincipal(),
                        ConnectorUtils.getPlainPassword(freeIPAConfiguration.getCredentials()));
            }
        }
        return lDAPConnection;
    }

    public void disconnect() {
        lDAPConnection.close();
    }
}
