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

public class CompatUser {

    private final String dn;

    private final  List<String> objectClass;

    private final String gecos;

    private final String cn;

    private final String uidNumber;

    private final String gidNumber;

    private final String loginShell;

    private final String homeDirectory;

    private final String uid;

    public CompatUser(final String uid, final String cn, final String posixIDs) {
        this.dn = "uid=" + uid + ",cn=users,cn=compat,dc=tirasa,dc=net";
        objectClass = new ArrayList<String>();
        objectClass.add("top");
        objectClass.add("posixAccount");

        this.cn = cn;
        this.gecos = cn;
        this.gidNumber = posixIDs;
        this.uidNumber = posixIDs;
        this.loginShell = "/bin/sh";
        this.homeDirectory = "home" + uid;
        this.uid = uid;
    }

    public String getDn() {
        return dn;
    }

    public List<String> getObjectClass() {
        return objectClass;
    }

    public String getGecos() {
        return gecos;
    }

    public String getCn() {
        return cn;
    }

    public String getUidNumber() {
        return uidNumber;
    }

    public String getGidNumber() {
        return gidNumber;
    }

    public String getLoginShell() {
        return loginShell;
    }

    public String getHomeDirectory() {
        return homeDirectory;
    }

    public String getUid() {
        return uid;
    }

    public AddRequest toAddRequest() {
        
        final Attribute oc = new Attribute("objectClass", this.objectClass);
        final Attribute commonName = new Attribute("cn", this.cn);
        final Attribute uID = new Attribute("uid", this.uid);
        final Attribute gecOS = new Attribute("gecos", this.gecos);
        final Attribute uidnumber = new Attribute("uidNumber", this.uidNumber);
        final Attribute gidnumber = new Attribute("gidNumber", this.gidNumber);
        final Attribute loginshell = new Attribute("loginShell", this.loginShell);
        final Attribute homedirectory = new Attribute("homeDirectory", this.homeDirectory);

        final Collection<Attribute> attributes = new ArrayList();
        attributes.add(oc);
        attributes.add(commonName);
        attributes.add(uID);
        attributes.add(gecOS);
        attributes.add(uidnumber);
        attributes.add(gidnumber);
        attributes.add(loginshell);
        attributes.add(homedirectory);

        return new AddRequest(dn, attributes);
    }

}
