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
import org.connid.bundles.freeipa.util.server.AccountUser;
import org.identityconnectors.common.CollectionUtil;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.Name;

public class SampleAttributesFactory {
    
    public static Set<Attribute> sampleSetAttributes(final Name name) {
        final Set attributes = CollectionUtil.newSet(AttributeBuilder.buildEnabled(true));
        attributes.add(AttributeBuilder.build(AccountUser.DefaultAttributes.MAIL.ldapValue(),
                CollectionUtil.newSet(AttributesTestValue.mail)));
        attributes.add(AttributeBuilder.build(AccountUser.DefaultAttributes.SN.ldapValue(),
                CollectionUtil.newSet(AttributesTestValue.sn)));
        attributes.add(AttributeBuilder.build(AccountUser.DefaultAttributes.GIVEN_NAME.ldapValue(),
                CollectionUtil.newSet(AttributesTestValue.givenName)));
        attributes.add(AttributeBuilder.build(AccountUser.DefaultAttributes.INITIALS.ldapValue(),
                CollectionUtil.newSet(AttributesTestValue.initials)));
        attributes.add(AttributeBuilder.buildPassword(AttributesTestValue.userPassword));
        attributes.add(name);
        return attributes;
    }

}
