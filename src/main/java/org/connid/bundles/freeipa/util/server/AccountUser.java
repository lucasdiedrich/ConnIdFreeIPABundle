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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AccountUser {

    private final String dn;

    private final List<String> objectClasses;

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

    private String mail;

    private String krbPrincipalName;

    private String krbPasswordExpiration;

    public AccountUser(final String uid, final String password,
            final String givenName, final String sn, final String posixIDsNumber,
            final List<String> memberOf) {
        dn = "uid=" + uid + ",cn=users,cn=accounts,dc=tirasa,dc=net";
        displayName = givenName + sn;
        cn = givenName + sn;
        objectClasses = addDefaultObjectClass();
        loginShell = "/bin/sh";
        userPassword = password;
        gecos = givenName + sn;
        gidNumber = posixIDsNumber;
        uidNumber = posixIDsNumber;
        homeDirectory = "/home/" + uid;
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
    }

    private List<String> addDefaultObjectClass() {
        List<String> defaultObjectClass = new ArrayList<String>();
        defaultObjectClass.add("top");
        defaultObjectClass.add("person");
        defaultObjectClass.add("organizationalperson");
        defaultObjectClass.add("inetorgperson");
        defaultObjectClass.add("inetuser");
        defaultObjectClass.add("posixAccount");
        defaultObjectClass.add("krbprincipalaux");
        defaultObjectClass.add("krbticketpolicyaux");
        defaultObjectClass.add("ipaobject");
        defaultObjectClass.add("ipasshuser");
        defaultObjectClass.add("ipaSshGroupOfPubKeys");
        defaultObjectClass.add("mepOriginEntry");
        return defaultObjectClass;
    }

    public AccountUser setMail(final String mail) {
        this.mail = mail;
        return this;
    }

    public AccountUser setKrbPasswordExpiration(final String krbPasswordExpiration) {
        this.krbPasswordExpiration = krbPasswordExpiration;
        return this;
    }

    public AccountUser setKrbPrincipalName(final String krbPrincipalName) {
        this.krbPrincipalName = krbPrincipalName;
        return this;
    }

    public String getDn() {
        return dn;
    }

    public List<String> getObjectClasses() {
        return objectClasses;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getCn() {
        return cn;
    }

    public String getLoginShell() {
        return loginShell;
    }

    public String getGecos() {
        return gecos;
    }

    public String getGidNumber() {
        return gidNumber;
    }

    public String getUidNumber() {
        return uidNumber;
    }

    public String getSn() {
        return sn;
    }

    public String getHomeDirectory() {
        return homeDirectory;
    }

    public String getUid() {
        return uid;
    }

    public String getMail() {
        return mail;
    }

    public String getKrbPrincipalName() {
        return krbPrincipalName;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getInitials() {
        return initials;
    }

    public List<String> getMemberOf() {
        return memberOf;
    }

    public String getKrbPasswordExpiration() {
        return krbPasswordExpiration;
    }

    public AddRequest toAddRequest() {

        final Attribute oc = new Attribute("objectClass", objectClasses);
        final Attribute userpassword = new Attribute("userPassword", this.userPassword);
        final Attribute commonName = new Attribute("cn", this.cn);
        final Attribute displayname = new Attribute("displayName", this.displayName);
        final Attribute uID = new Attribute("uid", this.uid);
        final Attribute gecOS = new Attribute("gecos", this.gecos);
        final Attribute mepmanagedentry = new Attribute("mepManagedEntry", this.mepManagedEntry);
        final Attribute uidnumber = new Attribute("uidNumber", this.uidNumber);
        final Attribute gidnumber = new Attribute("gidNumber", this.gidNumber);
        final Attribute loginshell = new Attribute("loginShell", this.loginShell);
        final Attribute homedirectory = new Attribute("homeDirectory", this.homeDirectory);
        final Attribute memberof = new Attribute("memberOf", this.memberOf);
        final Attribute surname = new Attribute("sn", this.sn);
        final Attribute email = new Attribute("mail", this.mail);
        final Attribute krbprincipalname = new Attribute("krbPrincipalName", this.krbPrincipalName);
        final Attribute givenname = new Attribute("givenName", this.givenName);
        final Attribute userInitials = new Attribute("initials", this.initials);
        final Attribute krbpasswordExpiration = new Attribute("krbPasswordExpiration", this.krbPasswordExpiration);

        final Collection<Attribute> attributes = new ArrayList();
        attributes.add(oc);
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
        attributes.add(email);
        attributes.add(krbprincipalname);
        attributes.add(givenname);
        attributes.add(userInitials);
        attributes.add(krbpasswordExpiration);

        return new AddRequest(dn, attributes);
    }

}
