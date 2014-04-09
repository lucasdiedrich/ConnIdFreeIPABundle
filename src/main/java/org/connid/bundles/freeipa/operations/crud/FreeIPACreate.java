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
package org.connid.bundles.freeipa.operations.crud;

import com.unboundid.ldap.sdk.LDAPException;
import java.security.GeneralSecurityException;
import java.util.Set;
import org.connid.bundles.freeipa.FreeIPAConfiguration;
import org.connid.bundles.freeipa.FreeIPAConnection;
import org.connid.bundles.freeipa.operations.FreeIPAAuthenticate;
import org.connid.bundles.freeipa.util.client.ConnectorUtils;
import org.connid.bundles.freeipa.util.server.AccountUser;
import org.connid.bundles.freeipa.util.server.PosixIDs;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.Uid;

public class FreeIPACreate {

    private static final Log LOG = Log.getLog(FreeIPAAuthenticate.class);

    private final ObjectClass oclass;

    private final Set<Attribute> attrs;

    private final OperationOptions options;

    private final FreeIPAConfiguration freeIPAConfiguration;

    private final FreeIPAConnection freeIPAConnection;

    private final PosixIDs posixIDs;

    public FreeIPACreate(final ObjectClass oclass,
            final Set<Attribute> attrs,
            final OperationOptions options,
            final FreeIPAConfiguration freeIPAConfiguration) {

        this.oclass = oclass;
        this.attrs = attrs;
        this.options = options;
        this.freeIPAConfiguration = freeIPAConfiguration;
        this.freeIPAConnection = new FreeIPAConnection(freeIPAConfiguration);
        this.posixIDs = new PosixIDs(freeIPAConfiguration);
    }

    public final Uid create() {
        try {
            return doCreate();
        } catch (LDAPException e) {
            LOG.error(e, "error during create operation");
            throw new ConnectorException(e);
        } catch (GeneralSecurityException e) {
            LOG.error(e, "error during create operation");
            throw new ConnectorException(e);
        }
    }

    private Uid doCreate() throws LDAPException, GeneralSecurityException {
        final Name nameAttr = AttributeUtil.getNameFromAttributes(attrs);

        if (nameAttr == null) {
            LOG.error("No Name attribute provided in the attributes");
            throw new IllegalArgumentException("No Name attribute provided in the attributes");
        }

        LOG.info("Name found {0}", nameAttr.getNameValue());

        if (ObjectClass.ACCOUNT.equals(oclass)) {
            final String posixIDsNumber = posixIDs.nextPosixIDs(freeIPAConfiguration);
            LOG.info("Next posix IDs {0}", posixIDsNumber);
            for (Attribute attribute : attrs) {
                LOG.info("Attribute name {0}", attribute.getName());
                LOG.info("Attribute value {0}", attribute.getValue().get(0));
            }

            final AccountUser au = new AccountUser(nameAttr.getNameValue(),
                    ConnectorUtils.getPlainPassword(AttributeUtil.getPasswordValue(attrs)),
                    AttributeUtil.find("givenName", attrs).getValue().get(0).toString(),
                    AttributeUtil.find("sn", attrs).getValue().get(0).toString(),
                    posixIDsNumber, null).setKrbPasswordExpiration(
                            AttributeUtil.find("krbPasswordExpiration", attrs).getValue().get(0).toString())
                    .setMail(AttributeUtil.find("mail", attrs).getValue().get(0).toString())
                    .setKrbPrincipalName(nameAttr.getNameValue()
                            + "@"
                            + freeIPAConfiguration.getKerberosRealm());

            LOG.info("AccountUser {0}", au);

            freeIPAConnection.lDAPConnection().add(au.toAddRequest());
            posixIDs.updatePosixIDs(posixIDsNumber, freeIPAConfiguration);
        } else {

        }
        return new Uid(nameAttr.getNameValue());
    }
}
