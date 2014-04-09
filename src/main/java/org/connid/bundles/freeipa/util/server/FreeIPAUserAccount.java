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
package org.connid.bundles.freeipa.util.server;

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
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.Uid;

public class FreeIPAUserAccount {

    private static final Log LOG = Log.getLog(FreeIPAUserAccount.class);

    private final String dn;

    private final List<String> objectClasses;

    private final Boolean nsaccountlock;

    private final String userPassword;

    private final String cn;

    private final String displayName;

    private final String loginShell;

    private final String gecos;

    private final String gidNumber;

    private final String uidNumber;

    private final String homeDirectory;

    private final String uid;

    private final List<String> memberOf;

    private final String sn;

    private final String mepManagedEntry;

    private final String givenName;

    private final String initials;

    private final String krbPrincipalName;

    public FreeIPAUserAccount(final String uid, final String password, final Boolean enabled,
            final String givenName, final String sn, final String posixIDsNumber,
            final List<String> memberOf, final FreeIPAConfiguration freeIPAConfiguration) {
        dn = "uid=" + uid + ",cn=users,cn=accounts,dc=tirasa,dc=net";
        nsaccountlock = !enabled;
        displayName = givenName + sn;
        cn = givenName + sn;
        objectClasses = DefaultObjectClasses.toList();
        loginShell = "/bin/sh";
        userPassword = password;
        gecos = givenName + sn;
        gidNumber = posixIDsNumber;
        uidNumber = posixIDsNumber;
        homeDirectory = freeIPAConfiguration.getServerBaseHomeDirectory() + "/" + uid;
        this.uid = uid;
        this.givenName = givenName;
        this.sn = sn;
        initials = givenName.substring(0, 1) + sn.substring(0, 1);
        mepManagedEntry = "cn=" + uid + ",cn=groups,cn=accounts,dc=tirasa,dc=net";
        this.memberOf = new ArrayList<String>();
        this.memberOf.add("cn=ipausers,cn=groups,cn=accounts,dc=tirasa,dc=net");
        if (memberOf != null) {
            this.memberOf.addAll(memberOf);
        }
        krbPrincipalName = uid + "@" + freeIPAConfiguration.getKerberosRealm();
    }

    private enum DefaultObjectClasses {

        TOP("top"),
        PERSON("person"),
        ORGANIZATIONAL_PERSON("organizationalperson"),
        INET_ORG_PERSON("inetorgperson"),
        INET_USER("inetuser"),
        POSIX_ACCOUNT("posixAccount"),
        KRB_PRINCIPAL_AUX("krbprincipalaux"),
        KRB_TICKET_POLICY_AUX("krbticketpolicyaux"),
        IPAOBJECT("ipaobject"),
        IPA_SSH_USER("ipasshuser"),
        IPA_SSH_GROUP_OF_PUB_KEYS("ipaSshGroupOfPubKeys"),
        MEP_ORIGIN_ENTRY("mepOriginEntry");

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
        NS_ACCOUNT_LOCK("nsaccountlock"),
        USER_PASSWORD("userPassword"),
        CN("cn"),
        DISPLAY_NAME("displayName"),
        UID("uid"),
        GECOS("gecos"),
        MEP_MANAGED_ENTRY("mepManagedEntry"),
        UID_NUMBER("uidNumber"),
        GID_NUMBER("gidNumber"),
        LOGIN_SHELL("loginShell"),
        HOME_DIRECTORY("homeDirectory"),
        MEMBER_OF("memberOf"),
        SN("sn"),
        MAIL("mail"),
        KRB_PRINCIPAL_NAME("krbPrincipalName"),
        GIVEN_NAME("givenName"),
        INITIALS("initials");

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
        final Attribute userpassword = new Attribute(DefaultAttributes.USER_PASSWORD.ldapValue(), this.userPassword);
        final Attribute nsAccountLock = new Attribute(DefaultAttributes.NS_ACCOUNT_LOCK.ldapValue(),
                String.valueOf(this.nsaccountlock));
        final Attribute commonName = new Attribute(DefaultAttributes.CN.ldapValue(), this.cn);
        final Attribute displayname = new Attribute(DefaultAttributes.DISPLAY_NAME.ldapValue(), this.displayName);
        final Attribute uID = new Attribute(DefaultAttributes.UID.ldapValue(), this.uid);
        final Attribute gecOS = new Attribute(DefaultAttributes.GECOS.ldapValue(), this.gecos);
        final Attribute mepmanagedentry = new Attribute(DefaultAttributes.MEP_MANAGED_ENTRY.ldapValue(),
                this.mepManagedEntry);
        final Attribute uidnumber = new Attribute(DefaultAttributes.UID_NUMBER.ldapValue(), this.uidNumber);
        final Attribute gidnumber = new Attribute(DefaultAttributes.GID_NUMBER.ldapValue(), this.gidNumber);
        final Attribute loginshell = new Attribute(DefaultAttributes.LOGIN_SHELL.ldapValue(), this.loginShell);
        final Attribute homedirectory = new Attribute(DefaultAttributes.HOME_DIRECTORY.ldapValue(), this.homeDirectory);
        final Attribute memberof = new Attribute(DefaultAttributes.MEMBER_OF.ldapValue(), this.memberOf);
        final Attribute surname = new Attribute(DefaultAttributes.SN.ldapValue(), this.sn);
        final Attribute krbprincipalname = new Attribute(DefaultAttributes.KRB_PRINCIPAL_NAME.ldapValue(),
                this.krbPrincipalName);
        final Attribute givenname = new Attribute(DefaultAttributes.GIVEN_NAME.ldapValue(), this.givenName);
        final Attribute userInitials = new Attribute(DefaultAttributes.INITIALS.ldapValue(), this.initials);

        final Collection<Attribute> attributes = new ArrayList();
        attributes.add(oc);
        attributes.add(nsAccountLock);
        attributes.add(commonName);
        attributes.add(displayname);
        attributes.add(mepmanagedentry);
        attributes.add(uID);
        attributes.add(gecOS);
        attributes.add(uidnumber);
        attributes.add(gidnumber);
        attributes.add(loginshell);
        attributes.add(homedirectory);
        attributes.add(userpassword);
        attributes.add(memberof);
        attributes.add(surname);
        attributes.add(krbprincipalname);
        attributes.add(givenname);
        attributes.add(userInitials);

        return new AddRequest(dn, attributes);
    }
    
    public static String userDN(final String uid) {
        return "uid=" + uid + ",cn=users,cn=accounts,dc=tirasa,dc=net";
    }

    public void fillOtherAttributesToAddRequest(final Map<String, List<Object>> otherAttributes,
            final AddRequest addRequest) {
        Attribute attribute;
        List<String> stringAttributes;
        for (final Map.Entry<String, List<Object>> attr : otherAttributes.entrySet()) {
            stringAttributes = new ArrayList<String>();
            for (final Object object : attr.getValue()) {
                stringAttributes.add(object.toString());
            }
            attribute = new Attribute(attr.getKey(), stringAttributes);
            addRequest.addAttribute(attribute);
        }
    }

    public static ModifyRequest createModifyRequest(final Uid uid, final String password, final Boolean enabled,
            final Map<String, List<Object>> otherAttributes, final FreeIPAConfiguration freeIPAConfiguration) {
        LOG.info("Updating user {0} with status {1} and attributes {2}", uid, enabled, otherAttributes);
        final List<Modification> modifications = new ArrayList<Modification>();
        final String dn = "uid=" + uid.getUidValue() + ",cn=users,cn=accounts,dc=tirasa,dc=net";
        if (StringUtil.isNotBlank(password)) {
            modifications.add(new Modification(
                    ModificationType.REPLACE, DefaultAttributes.USER_PASSWORD.ldapValue(), password));
        }
        if (enabled != null) {
            modifications.add(new Modification(
                    ModificationType.REPLACE, DefaultAttributes.NS_ACCOUNT_LOCK.ldapValue(), String.valueOf(!enabled)));
        }
        String[] stringAttributes;
        for (final Map.Entry<String, List<Object>> attr : otherAttributes.entrySet()) {
            stringAttributes = new String[attr.getValue().size()];
            for (int i = 0; i < attr.getValue().size(); i++) {
                stringAttributes[i] = attr.getValue().get(i).toString();
            }
            LOG.info("Creating new modification for {0} with value {1}", attr.getKey(), stringAttributes);
            modifications.add(new Modification(ModificationType.REPLACE, attr.getKey(), stringAttributes));
        }
        return new ModifyRequest(dn, modifications);
    }
}
