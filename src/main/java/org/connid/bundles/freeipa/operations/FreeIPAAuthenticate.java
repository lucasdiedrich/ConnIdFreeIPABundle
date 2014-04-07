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

import org.connid.bundles.freeipa.FreeIPAConfiguration;
import org.connid.bundles.freeipa.FreeIPAConnection;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.Uid;

public class FreeIPAAuthenticate {

    private static final Log LOG = Log.getLog(FreeIPACheckAlive.class);

    private final ObjectClass objectClass;

    private final String username;

    private final GuardedString password;

    private final OperationOptions options;

    private final FreeIPAConnection freeIPAConnection;

    public FreeIPAAuthenticate(final ObjectClass objectClass,
            final String username,
            final GuardedString password,
            final OperationOptions options,
            final FreeIPAConfiguration freeIPAConfiguration) {
        this.objectClass = objectClass;
        this.username = username;
        this.password = password;
        this.options = options;
        freeIPAConnection = new FreeIPAConnection(freeIPAConfiguration);
    }

    public final Uid authenticate() {
        try {
            return doAuthenticate();
        } catch (Exception e) {
            LOG.error(e, "error during authentication");
            throw new ConnectorException(e);
        }
    }

    private Uid doAuthenticate() {
        freeIPAConnection.checkAlive();
        return null;
    }
}
