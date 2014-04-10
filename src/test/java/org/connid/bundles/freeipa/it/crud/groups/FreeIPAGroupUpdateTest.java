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
package org.connid.bundles.freeipa.it.crud.groups;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.connid.bundles.freeipa.FreeIPAConnector;
import org.connid.bundles.freeipa.beans.server.FreeIPAGroupAccount;
import org.connid.bundles.freeipa.commons.GroupAttributesTestValue;
import org.connid.bundles.freeipa.commons.SampleAttributesFactory;
import org.connid.bundles.freeipa.commons.SampleConfigurationFactory;
import org.connid.bundles.freeipa.commons.UserAttributesTestValue;
import org.identityconnectors.common.CollectionUtil;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.Uid;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FreeIPAGroupUpdateTest {

    private static FreeIPAConnector freeIPAConnector;

    private final static List<String> groupsCreated = new ArrayList<String>();

    @Before
    public void before() {
        freeIPAConnector = new FreeIPAConnector();
        freeIPAConnector.init(SampleConfigurationFactory.configurationWithRightUsernameAndPassword());
    }

    @Test
    public void freeIPAUpdateTest() {
        final Name name = new Name(GroupAttributesTestValue.cn + (int) (Math.random() * 100000));
        final Uid uid = freeIPAConnector.create(ObjectClass.GROUP,
                SampleAttributesFactory.sampleGroupAttributes(name), null);
        groupsCreated.add(name.getNameValue());
        assertEquals(name.getNameValue(), uid.getUidValue());
        freeIPAConnector.update(ObjectClass.GROUP, uid, sampleSetAttributes(), null);
    }

    private static Set<Attribute> sampleSetAttributes() {
        final Set attributes = CollectionUtil.newSet(AttributeBuilder.build(
                FreeIPAGroupAccount.DefaultAttributes.DESCRIPTION.ldapValue(),
                CollectionUtil.newSet("NUOVA DESCRIZIONE")));
        return attributes;
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateWithNullUidTest() {
        freeIPAConnector.update(ObjectClass.GROUP, null, sampleSetAttributes(), null);
    }

    @Test
    public void updateWithNullUidCatchTest() {
        try {
            freeIPAConnector.update(ObjectClass.GROUP, null, sampleSetAttributes(), null);
        } catch (final IllegalArgumentException e) {
            assertEquals("No uid attribute provided in the attributes", e.getMessage());
        }
    }

    @Test
    public void updateWithNullAttrsTest() {
        final Name name = new Name(GroupAttributesTestValue.cn + (int) (Math.random() * 100000));
        freeIPAConnector.update(ObjectClass.GROUP, new Uid(name.getNameValue()), null, null);
    }

    @Test
    public void updateWithNullAttrsCatchTest() {
        try {
            final Name name = new Name(GroupAttributesTestValue.cn + (int) (Math.random() * 100000));
            freeIPAConnector.update(ObjectClass.GROUP, new Uid(name.getNameValue()), null, null);
        } catch (final IllegalArgumentException e) {
            Assert.fail();
        }

    }
    
    @Test(expected = ConnectorException.class)
    public void updateWithNullObjectClassTest() {
        final Name name = new Name(GroupAttributesTestValue.cn + (int) (Math.random() * 100000));
        freeIPAConnector.update(null, new Uid(name.getNameValue()), null, null);
    }

    @Test
    public void updateWithNullObjectClassCatchTest() {
        final Name name = new Name(GroupAttributesTestValue.cn + (int) (Math.random() * 100000));
        try {
            freeIPAConnector.update(null, new Uid(name.getNameValue()), null, null);
        } catch (final ConnectorException e) {
            assertEquals("Object class not valid", e.getMessage());
        }
    }
    
    @Test(expected = ConnectorException.class)
    public void updateNotExistsGroupTest() {
        freeIPAConnector.update(
                ObjectClass.GROUP, new Uid("NOTEXISTS"), 
                SampleAttributesFactory.sampleUserSetAttributes(new Name("NOTEXISTS")), null);
    }

    @Test
    public void updateNotExistsGroupCatchTest() {
        try {
            freeIPAConnector.update(
                ObjectClass.GROUP, new Uid("NOTEXISTS"), 
                SampleAttributesFactory.sampleUserSetAttributes(new Name("NOTEXISTS")), null);
        } catch (final ConnectorException e) {
            assertEquals(String.format("Group %s already exists", "NOTEXISTS"), e.getMessage());
        }
    }

    @AfterClass
    public static void deleteCreatedGroups() {
        final FreeIPAConnector fipac = new FreeIPAConnector();
        fipac.init(SampleConfigurationFactory.configurationWithRightUsernameAndPassword());
        for (final String uid : groupsCreated) {
            fipac.delete(ObjectClass.GROUP, new Uid(uid), null);
        }
        freeIPAConnector.dispose();
    }
}
