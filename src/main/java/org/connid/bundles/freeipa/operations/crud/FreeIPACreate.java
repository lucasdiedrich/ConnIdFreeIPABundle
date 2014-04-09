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

import com.unboundid.ldap.sdk.AddRequest;
import com.unboundid.ldap.sdk.LDAPException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.connid.bundles.freeipa.FreeIPAConfiguration;
import org.connid.bundles.freeipa.FreeIPAConnection;
import org.connid.bundles.freeipa.util.client.ConnectorUtils;
import org.connid.bundles.freeipa.util.server.FreeIPAUserAccount;
import org.connid.bundles.freeipa.util.server.PosixIDs;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.OperationalAttributes;
import org.identityconnectors.framework.common.objects.Uid;

public class FreeIPACreate {

    private static final Log LOG = Log.getLog(FreeIPACreate.class);

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

            final Map<String, List<Object>> otherAttributes = new HashMap<String, List<Object>>();
            String password = "";
            Boolean attrStatus = Boolean.TRUE;
            String givenName = "";
            String sn = "";
            for (final Attribute attribute : attrs) {
                if (FreeIPAUserAccount.DefaultAttributes.GIVEN_NAME.ldapValue().equalsIgnoreCase(attribute.getName())) {
                    givenName = attribute.getValue().get(0).toString();
                } else if (FreeIPAUserAccount.DefaultAttributes.SN.ldapValue().equalsIgnoreCase(attribute.getName())) {
                    sn = attribute.getValue().get(0).toString();
                } else if (attribute.is(OperationalAttributes.PASSWORD_NAME)) {
                    password = ConnectorUtils.getPlainPassword((GuardedString) attribute.getValue().get(0));
                } else if (attribute.is(OperationalAttributes.ENABLE_NAME)) {
                    if (attribute.getValue() != null && !attribute.getValue().isEmpty()) {
                        attrStatus = Boolean.parseBoolean(attribute.getValue().get(0).toString());
                    }
                } else if (attribute.is(Name.NAME)) {
                    //DO NOTHING
                } else {
                    otherAttributes.put(attribute.getName(), attribute.getValue());
                }
            }

            if (StringUtil.isBlank(sn)
                    || (StringUtil.isBlank(password))
                    || (StringUtil.isBlank(givenName))) {
                throw new IllegalArgumentException("GivenName and SN attributes are required");
            }

            final FreeIPAUserAccount baseUserAccount
                    = new FreeIPAUserAccount(nameAttr.getNameValue(), password, attrStatus, givenName, sn,
                            posixIDsNumber, null, freeIPAConfiguration);

            LOG.info("Base AccountUser {0}", baseUserAccount);

            final AddRequest addRequest = baseUserAccount.toAddRequest();

            if (!otherAttributes.isEmpty()) {
                baseUserAccount.fillOtherAttributesToAddRequest(otherAttributes, addRequest);
            }

            LOG.info("Complete AccountUser {0}", baseUserAccount);

            freeIPAConnection.lDAPConnection().add(addRequest);
            posixIDs.updatePosixIDs(posixIDsNumber, freeIPAConfiguration);
        } else {

        }
        return new Uid(nameAttr.getNameValue());
    }
}
