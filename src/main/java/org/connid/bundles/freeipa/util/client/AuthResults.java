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
package org.connid.bundles.freeipa.util.client;

public enum AuthResults {

    AUTH_SUCCESS("SUCCESS"),
    AUTH_GENERIC_ERROR("NEVER"),
    AUTH_PASSWORD_EXPIRED("[LDAP: error code 49 - Invalid Credentials]"),
    AUTH_NO_SUCH_OBJECT("[LDAP: error code 32 - No Such Object]"),
    AUTH_INVALID_CREDENTIALS("[LDAP: error code 49 - Invalid Credentials]");

    private final String cause;

    private AuthResults(final String cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {
        return cause;
    }

    public static AuthResults fromValue(final String cause) {
        AuthResults ex = AUTH_GENERIC_ERROR;
        for (final AuthResults exceptionTranslator : values()) {
            if (cause.equalsIgnoreCase(exceptionTranslator.toString())) {
                ex = exceptionTranslator;
            }
        }
        return ex;
    }
}
