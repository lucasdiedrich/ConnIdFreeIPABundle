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
import org.connid.bundles.freeipa.operations.FreeIPAAuthenticate;
import org.connid.bundles.freeipa.operations.FreeIPACheckAlive;
import org.connid.bundles.freeipa.operations.FreeIPADispose;
import org.connid.bundles.freeipa.operations.FreeIPASchema;
import org.connid.bundles.freeipa.operations.crud.groups.FreeIPAGroupCreate;
import org.connid.bundles.freeipa.operations.crud.groups.FreeIPAGroupDelete;
import org.connid.bundles.freeipa.operations.crud.groups.FreeIPAGroupUpdate;
import org.connid.bundles.freeipa.operations.crud.users.FreeIPAUserCreate;
import org.connid.bundles.freeipa.operations.crud.users.FreeIPAUserDelete;
import org.connid.bundles.freeipa.operations.crud.users.FreeIPAUserUpdate;
import org.connid.bundles.freeipa.operations.search.FreeIPAExecuteQuery;
import org.connid.bundles.ldap.LdapConnector;
import org.connid.bundles.ldap.search.LdapFilter;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.Schema;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.ConnectorClass;

@ConnectorClass(configurationClass = FreeIPAConfiguration.class, displayNameKey = "freeipa.connector.display")
public class FreeIPAConnector extends LdapConnector {

    private static final Log LOG = Log.getLog(FreeIPAConnector.class);

    private FreeIPAConfiguration freeIPAConfiguration;

    @Override
    public void init(final Configuration cfg) {
        super.init(cfg);
        freeIPAConfiguration = (FreeIPAConfiguration) cfg;
    }

    @Override
    public Configuration getConfiguration() {
        return freeIPAConfiguration;
    }

    @Override
    public void test() {
        checkAlive();
    }

    @Override
    public void checkAlive() {
        LOG.info("Trying check alive operation");
        new FreeIPACheckAlive(freeIPAConfiguration).check();
    }

    @Override
    public Uid authenticate(final ObjectClass objectClass,
            final String username, final GuardedString password, final OperationOptions options) {
        return new FreeIPAAuthenticate(objectClass, username, password, options, freeIPAConfiguration).authenticate();
    }

    @Override
    public Uid create(final ObjectClass oclass, final Set<Attribute> attrs, final OperationOptions options) {
        Uid uid;
        if (ObjectClass.ACCOUNT.equals(oclass)) {
            uid = new FreeIPAUserCreate(attrs, freeIPAConfiguration).createUser();
        } else if (ObjectClass.GROUP.equals(oclass)) {
            uid = new FreeIPAGroupCreate(attrs, freeIPAConfiguration).createGroup();
        } else {
            throw new ConnectorException("Object class not valid");
        }
        dispose();
        return uid;
    }

    @Override
    public Uid update(final ObjectClass oclass, final Uid uid,
            final Set<Attribute> replaceAttributes, final OperationOptions options) {
        Uid finalUid;
        if (ObjectClass.ACCOUNT.equals(oclass)) {
            finalUid = new FreeIPAUserUpdate(uid, replaceAttributes, freeIPAConfiguration).updateUser();
        } else if (ObjectClass.GROUP.equals(oclass)) {
            finalUid = new FreeIPAGroupUpdate(uid, replaceAttributes, freeIPAConfiguration).updateGroup();
        } else {
            throw new ConnectorException("Object class not valid");
        }
        dispose();
        return finalUid;
    }

    @Override
    public void delete(final ObjectClass oclass, final Uid uid, final OperationOptions options) {
        if (ObjectClass.ACCOUNT.equals(oclass)) {
            new FreeIPAUserDelete(uid, freeIPAConfiguration).deleteUser();
        } else if (ObjectClass.GROUP.equals(oclass)) {
            new FreeIPAGroupDelete(uid, freeIPAConfiguration).deleteGroup();
        } else {
            throw new ConnectorException("Object class not valid");
        }
        dispose();
    }

    @Override
    public Schema schema() {
        final Schema freeIpaSchema = new FreeIPASchema(freeIPAConfiguration).schema();
        dispose();
        return freeIpaSchema;
    }

    @Override
    public void executeQuery(final ObjectClass oclass, final LdapFilter query,
            final ResultsHandler handler, final OperationOptions options) {
        new FreeIPAExecuteQuery(oclass, query, handler, freeIPAConfiguration).executeQuery();
        dispose();
    }
    
    @Override
    public void dispose() {
        new FreeIPADispose(freeIPAConfiguration).closeConnection();
    }
}
