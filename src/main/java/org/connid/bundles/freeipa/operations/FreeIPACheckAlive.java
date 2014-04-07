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
import org.connid.bundles.freeipa.exceptions.FreeIPAException;
import org.identityconnectors.common.logging.Log;

public class FreeIPACheckAlive {

    private static final Log LOG = Log.getLog(FreeIPACheckAlive.class);

    private final FreeIPAConnection freeIPAConnection;

    public FreeIPACheckAlive(final FreeIPAConfiguration freeIPAConfiguration) {
        freeIPAConnection = new FreeIPAConnection(freeIPAConfiguration);
    }

    public final void check() {
        try {
            doCheck();
        } catch (FreeIPAException e) {
            throw e;
        }
    }

    private void doCheck() {
        freeIPAConnection.checkAlive();
    }
}
