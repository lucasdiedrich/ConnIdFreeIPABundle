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
package org.connid.bundles.freeipa.it.search;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.connid.bundles.freeipa.FreeIPAConnection;
import org.connid.bundles.freeipa.FreeIPAConnector;
import org.connid.bundles.freeipa.beans.server.FreeIPAUserAccount;
import org.connid.bundles.freeipa.commons.SampleConfigurationFactory;
import org.connid.bundles.freeipa.commons.UserAttributesTestValue;
import org.connid.bundles.ldap.schema.LdapSchemaMapping;
import org.connid.bundles.ldap.search.LdapFilter;
import org.connid.bundles.ldap.search.LdapFilterTranslator;
import org.identityconnectors.common.CollectionUtil;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.common.objects.filter.FilterBuilder;
import org.identityconnectors.test.common.ToListResultsHandler;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class FreeIPAExecuteQueryTest {

    private static final String MAIL_TEST_VALUE = "search.mail@example.com";

    private static final String SN_TEST_VALUE = "mail";

    private static final String GIVENNAME_TEST_VALUE = "search";

    private static final String INITIALS_TEST_VALUE = "sm";

    private static FreeIPAConnector freeIPAConnector;

    private final static List<String> usersCreated = new ArrayList<String>();

    @Before
    public void before() {
        freeIPAConnector = new FreeIPAConnector();
        freeIPAConnector.init(SampleConfigurationFactory.configurationWithRightUsernameAndPassword());
    }

    @Test
    public void testEquals() {
        final Name name = new Name(UserAttributesTestValue.uid + (int) (Math.random() * 100000));
        final Uid uid = freeIPAConnector.create(
                ObjectClass.ACCOUNT, sampleUserSetAttributes(name), null);
        assertEquals(name.getNameValue(), uid.getUidValue());
        usersCreated.add(name.getNameValue());

        final EqualsFilter filter = (EqualsFilter) FilterBuilder.
                equalTo(AttributeBuilder.build("mail", MAIL_TEST_VALUE));

        final ToListResultsHandler handler = new ToListResultsHandler();

        freeIPAConnector.executeQuery(
                ObjectClass.ACCOUNT,
                LdapFilter.forNativeFilter(newTranslator().createEqualsExpression(filter, false).getNativeFilter()),
                handler, null);

        assertEquals(1, handler.getObjects().size());
    }

    public static Set<Attribute> sampleUserSetAttributes(final Name name) {
        final Set attributes = CollectionUtil.newSet(AttributeBuilder.buildEnabled(true));
        attributes.add(AttributeBuilder.build(FreeIPAUserAccount.DefaultAttributes.MAIL.ldapValue(),
                CollectionUtil.newSet(MAIL_TEST_VALUE)));
        attributes.add(AttributeBuilder.build(FreeIPAUserAccount.DefaultAttributes.SN.ldapValue(),
                CollectionUtil.newSet(SN_TEST_VALUE)));
        attributes.add(AttributeBuilder.build(FreeIPAUserAccount.DefaultAttributes.GIVEN_NAME.ldapValue(),
                CollectionUtil.newSet(GIVENNAME_TEST_VALUE)));
        attributes.add(AttributeBuilder.build(FreeIPAUserAccount.DefaultAttributes.INITIALS.ldapValue(),
                CollectionUtil.newSet(INITIALS_TEST_VALUE)));
        attributes.add(AttributeBuilder.buildPassword(UserAttributesTestValue.userPassword));
        attributes.add(name);
        return attributes;
    }

    private static LdapFilterTranslator newTranslator() {
        return new LdapFilterTranslator(new LdapSchemaMapping(new FreeIPAConnection(
                SampleConfigurationFactory.configurationWithRightUsernameAndPassword())), ObjectClass.ACCOUNT);
    }

    @AfterClass
    public static void deleteCreatedUser() {
        final FreeIPAConnector fipac = new FreeIPAConnector();
        fipac.init(SampleConfigurationFactory.configurationWithRightUsernameAndPassword());
        for (final String uid : usersCreated) {
            fipac.delete(ObjectClass.ACCOUNT, new Uid(uid), null);
        }
        freeIPAConnector.dispose();
    }

}
