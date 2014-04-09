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
package org.connid.bundles.freeipa.commons;

import java.util.Set;
import org.connid.bundles.freeipa.beans.server.FreeIPAGroupAccount;
import org.connid.bundles.freeipa.beans.server.FreeIPAUserAccount;
import org.identityconnectors.common.CollectionUtil;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.Name;

public class SampleAttributesFactory {

    public static Set<Attribute> sampleUserSetAttributes(final Name name) {
        final Set attributes = CollectionUtil.newSet(AttributeBuilder.buildEnabled(true));
        attributes.add(AttributeBuilder.build(FreeIPAUserAccount.DefaultAttributes.MAIL.ldapValue(),
                CollectionUtil.newSet(UserAttributesTestValue.mail)));
        attributes.add(AttributeBuilder.build(FreeIPAUserAccount.DefaultAttributes.SN.ldapValue(),
                CollectionUtil.newSet(UserAttributesTestValue.sn)));
        attributes.add(AttributeBuilder.build(FreeIPAUserAccount.DefaultAttributes.GIVEN_NAME.ldapValue(),
                CollectionUtil.newSet(UserAttributesTestValue.givenName)));
        attributes.add(AttributeBuilder.build(FreeIPAUserAccount.DefaultAttributes.INITIALS.ldapValue(),
                CollectionUtil.newSet(UserAttributesTestValue.initials)));
        attributes.add(AttributeBuilder.buildPassword(UserAttributesTestValue.userPassword));
        attributes.add(name);
        return attributes;
    }

    public static Set<Attribute> sampleGroupAttributes(final Name name) {
        final Set attributes = CollectionUtil.newSet(
                AttributeBuilder.build(FreeIPAGroupAccount.DefaultAttributes.DESCRIPTION.ldapValue(),
                        CollectionUtil.newSet(GroupAttributesTestValue.description)));
        attributes.add(name);
        return attributes;
    }

}
