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

import com.unboundid.ldap.sdk.AddRequest;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.ModifyRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.connid.bundles.freeipa.FreeIPAConfiguration;
import org.connid.bundles.freeipa.util.client.LDAPConstants;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.Uid;

public class FreeIPAGroupAccount {

    private static final Log LOG = Log.getLog(FreeIPAGroupAccount.class);

    private final String dn;

    private final List<String> objectClasses;

    private final String description;

    private final String gidNumber;

    private final String cn;

    public FreeIPAGroupAccount(final String uid, final String description,
            final String posixIDsNumber, final FreeIPAConfiguration freeIPAConfiguration) {
        dn = groupDN(uid, freeIPAConfiguration);
        objectClasses = DefaultObjectClasses.toList();
        this.description = description;
        cn = uid;
        gidNumber = posixIDsNumber;
    }

    public String getDn() {
        return dn;
    }

    public enum DefaultObjectClasses {

        TOP("top"),
        GROUP_OF_NAMES("groupofnames"),
        NESTED_GROUP("nestedgroup"),
        IPA_USER_GROUP("ipausergroup"),
        IPA_OBJECT("ipaobject"),
        POSIX_GROUP("posixgroup");

        private final String ldapName;

        private DefaultObjectClasses(final String ldapName) {
            this.ldapName = ldapName;
        }

        @Override
        public String toString() {
            return ldapName;
        }

        public static List<String> toList() {
            final List<String> defaultObjectClass = new ArrayList<String>();
            for (final DefaultObjectClasses objectClass : values()) {
                defaultObjectClass.add(objectClass.toString());
            }
            return defaultObjectClass;
        }
    }

    public enum DefaultAttributes {

        OBJECT_CLASS("objectClass"),
        DN("dn"),
        CN("cn"),
        DESCRIPTION("description"),
        GID_NUMBER("gidNumber");

        private final String ldapName;

        private DefaultAttributes(final String ldapName) {
            this.ldapName = ldapName;
        }

        public String ldapValue() {
            return ldapName;
        }
    }

    public AddRequest toAddRequest() {

        final Attribute oc = new Attribute(DefaultAttributes.OBJECT_CLASS.ldapValue(), objectClasses);
        final Attribute commonName = new Attribute(DefaultAttributes.CN.ldapValue(), this.cn);
        final Attribute gidnumber = new Attribute(DefaultAttributes.GID_NUMBER.ldapValue(), this.gidNumber);
        final Attribute groupDescription = new Attribute(DefaultAttributes.DESCRIPTION.ldapValue(), this.description);

        final Collection<Attribute> attributes = new ArrayList();
        attributes.add(oc);
        attributes.add(commonName);
        attributes.add(gidnumber);
        attributes.add(groupDescription);

        return new AddRequest(dn, attributes);
    }

    public static String groupDN(final String uid, final FreeIPAConfiguration freeIPAConfiguration) {
        final String groupDN = freeIPAConfiguration.getCnAttribute() + "=" + uid + ","
                + LDAPConstants.GROUPS_DN_BASE_SUFFIX + "," + freeIPAConfiguration.getRootSuffix();
        LOG.info("Generated userDN: {0}", groupDN);
        return groupDN;
    }

    public void fillOtherAttributesToAddRequest(final Map<String, List<Object>> otherAttributes,
            final AddRequest addRequest) {
        Attribute attribute;
        List<String> stringAttributes;
        for (final Map.Entry<String, List<Object>> attr : otherAttributes.entrySet()) {
            stringAttributes = new ArrayList<String>();
            if (attr.getValue() != null && !attr.getValue().isEmpty()) {
                for (final Object object : attr.getValue()) {
                    stringAttributes.add(object.toString());
                }
                attribute = new Attribute(attr.getKey(), stringAttributes);
                addRequest.addAttribute(attribute);
            }
        }
    }

    public static ModifyRequest createModifyRequest(final Uid uid,
            final Map<String, List<Object>> otherAttributes, final FreeIPAConfiguration freeIPAConfiguration) {
        LOG.info("Updating group {0} with  and attributes {1}", uid, otherAttributes);
        final List<Modification> modifications = new ArrayList<Modification>();
        final String dn = groupDN(uid.getUidValue(), freeIPAConfiguration);
        String[] stringAttributes;
        for (final Map.Entry<String, List<Object>> attr : otherAttributes.entrySet()) {
            if (attr.getValue() != null && !attr.getValue().isEmpty()) {
                stringAttributes = new String[attr.getValue().size()];
                for (int i = 0; i < attr.getValue().size(); i++) {
                    stringAttributes[i] = attr.getValue().get(i).toString();
                }
                LOG.info("Creating new modification for {0} with value {1}", attr.getKey(), stringAttributes);
                modifications.add(new Modification(ModificationType.REPLACE, attr.getKey(), stringAttributes));
            }
        }
        return new ModifyRequest(dn, modifications);
    }
}
