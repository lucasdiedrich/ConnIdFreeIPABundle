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

import java.util.Set;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.Schema;
import org.identityconnectors.framework.common.objects.SyncResultsHandler;
import org.identityconnectors.framework.common.objects.SyncToken;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.PoolableConnector;
import org.identityconnectors.framework.spi.operations.AuthenticateOp;
import org.identityconnectors.framework.spi.operations.CreateOp;
import org.identityconnectors.framework.spi.operations.DeleteOp;
import org.identityconnectors.framework.spi.operations.ResolveUsernameOp;
import org.identityconnectors.framework.spi.operations.SchemaOp;
import org.identityconnectors.framework.spi.operations.SyncOp;
import org.identityconnectors.framework.spi.operations.TestOp;
import org.identityconnectors.framework.spi.operations.UpdateAttributeValuesOp;

@ConnectorClass(configurationClass = FreeIPAConfiguration.class, displayNameKey = "freeipa.connector.display")
public class FreeIPAConnector implements PoolableConnector, TestOp, SchemaOp,
        AuthenticateOp, ResolveUsernameOp, CreateOp, DeleteOp,
        UpdateAttributeValuesOp, SyncOp {

    @Override
    public void checkAlive() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Configuration getConfiguration() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void init(final Configuration cfg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dispose() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void test() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Uid authenticate(final ObjectClass objectClass, final String username,
            final GuardedString password, final OperationOptions options) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Schema schema() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Uid create(final ObjectClass oclass, final Set<Attribute> attrs, final OperationOptions options) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(final ObjectClass objClass, final Uid uid, final OperationOptions options) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Uid addAttributeValues(final ObjectClass objclass, final Uid uid,
            final Set<Attribute> valuesToAdd, final OperationOptions options) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Uid removeAttributeValues(final ObjectClass objclass, final Uid uid,
            final Set<Attribute> valuesToRemove, final OperationOptions options) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Uid update(final ObjectClass objclass, final Uid uid,
            final Set<Attribute> replaceAttributes, final OperationOptions options) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Uid resolveUsername(final ObjectClass objectClass, final String username, final OperationOptions options) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void sync(final ObjectClass objClass, final SyncToken token,
            final SyncResultsHandler handler, final OperationOptions options) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SyncToken getLatestSyncToken(final ObjectClass objClass) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
