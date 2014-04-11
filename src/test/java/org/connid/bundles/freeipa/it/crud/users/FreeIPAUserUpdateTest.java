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
package org.connid.bundles.freeipa.it.crud.users;

import static org.junit.Assert.assertEquals;

import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.extensions.PasswordModifyExtendedRequest;
import com.unboundid.ldap.sdk.extensions.PasswordModifyExtendedResult;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.connid.bundles.freeipa.FreeIPAConnection;
import org.connid.bundles.freeipa.FreeIPAConnector;
import org.connid.bundles.freeipa.commons.SampleConfigurationFactory;
import org.connid.bundles.freeipa.beans.server.FreeIPAUserAccount;
import org.connid.bundles.freeipa.commons.SampleAttributesFactory;
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

public class FreeIPAUserUpdateTest {

    private static FreeIPAConnector freeIPAConnector;

    private final static List<String> usersCreated = new ArrayList<String>();

    @Before
    public void before() {
        freeIPAConnector = new FreeIPAConnector();
        freeIPAConnector.init(SampleConfigurationFactory.configurationWithRightUsernameAndPassword());
    }

    @Test
    public void sampleUpdateTest() {
        final Name name = new Name(UserAttributesTestValue.uid + (int) (Math.random() * 100000));
        final Uid uid = freeIPAConnector.create(
                ObjectClass.ACCOUNT, SampleAttributesFactory.sampleUserSetAttributes(name), null);
        assertEquals(name.getNameValue(), uid.getUidValue());
        usersCreated.add(name.getNameValue());
        freeIPAConnector.update(ObjectClass.ACCOUNT, uid, sampleSetAttributes(), null);
    }
    
    @Test
    public void passwordUpdateTest() throws GeneralSecurityException, LDAPException {
        final Name name = new Name(UserAttributesTestValue.uid + (int) (Math.random() * 100000));
        final Uid uid = freeIPAConnector.create(
                ObjectClass.ACCOUNT, SampleAttributesFactory.sampleUserSetAttributes(name), null);
        assertEquals(name.getNameValue(), uid.getUidValue());
        usersCreated.add(name.getNameValue());
        Set<Attribute> attributes = sampleSetAttributes();
        attributes.add(AttributeBuilder.buildPassword(UserAttributesTestValue.newUserPassword));
//        
//        PasswordModifyExtendedRequest r = new PasswordModifyExtendedRequest("newpassword");
//        FreeIPAConnection freeIPAConnection =
//                new FreeIPAConnection(SampleConfigurationFactory.configurationWithRightUsernameAndPassword());
//        freeIPAConnection.lDAPConnection().processExtendedOperation(r);
        freeIPAConnector.update(ObjectClass.ACCOUNT, uid, attributes, null);
    }

    private Set<Attribute> sampleSetAttributes() {
        final Set attributes = CollectionUtil.newSet(AttributeBuilder.buildEnabled(false));
        attributes.add(AttributeBuilder.build(FreeIPAUserAccount.DefaultAttributes.INITIALS.ldapValue(),
                CollectionUtil.newSet(UserAttributesTestValue.newInitials)));
        return attributes;
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateWithNullUidTest() {
        freeIPAConnector.update(ObjectClass.ACCOUNT, null, sampleSetAttributes(), null);
    }

    @Test
    public void updateWithNullUidCatchTest() {
        try {
            freeIPAConnector.update(ObjectClass.ACCOUNT, null, sampleSetAttributes(), null);
        } catch (final IllegalArgumentException e) {
            assertEquals("No uid attribute provided in the attributes", e.getMessage());
        }
    }
    
    @Test
    public void updateWithNullAttrsTest() {
        final Name name = new Name(UserAttributesTestValue.uid + (int) (Math.random() * 100000));
        freeIPAConnector.update(ObjectClass.ACCOUNT, new Uid(name.getNameValue()), null, null);
    }

    @Test
    public void updateWithNullAttrsCatchTest() {
        try {
            final Name name = new Name(UserAttributesTestValue.uid + (int) (Math.random() * 100000));
            freeIPAConnector.update(ObjectClass.ACCOUNT, new Uid(name.getNameValue()), null, null);
        } catch (final IllegalArgumentException e) {
            Assert.fail();
        }

    }

    @Test(expected = ConnectorException.class)
    public void updateWithNullObjectClassTest() {
        final Name name = new Name(UserAttributesTestValue.uid + (int) (Math.random() * 100000));
        freeIPAConnector.update(null, new Uid(name.getNameValue()), null, null);
    }

    @Test
    public void updateWithNullObjectClassCatchTest() {
        final Name name = new Name(UserAttributesTestValue.uid + (int) (Math.random() * 100000));
        try {
            freeIPAConnector.update(null, new Uid(name.getNameValue()), null, null);
        } catch (final ConnectorException e) {
            assertEquals("Object class not valid", e.getMessage());
        }
    }
    
    @Test(expected = ConnectorException.class)
    public void updateNotExistsUserTest() {
        freeIPAConnector.update(
                ObjectClass.ACCOUNT, new Uid(UserAttributesTestValue.not_exists), 
                SampleAttributesFactory.sampleUserSetAttributes(new Name(UserAttributesTestValue.not_exists)), null);
    }

    @Test
    public void updateNotExistsUserCatchTest() {
        try {
            freeIPAConnector.update(
                ObjectClass.ACCOUNT, new Uid(UserAttributesTestValue.not_exists), 
                SampleAttributesFactory.sampleUserSetAttributes(new Name(UserAttributesTestValue.not_exists)), null);
        } catch (final ConnectorException e) {
            assertEquals(String.format("User %s already exists", UserAttributesTestValue.not_exists), e.getMessage());
        }
    }

//    @AfterClass
//    public static void deleteCreatedUser() {
//        final FreeIPAConnector fipac = new FreeIPAConnector();
//        fipac.init(SampleConfigurationFactory.configurationWithRightUsernameAndPassword());
//        for (final String uid : usersCreated) {
//            fipac.delete(ObjectClass.ACCOUNT, new Uid(uid), null);
//        }
//        freeIPAConnector.dispose();
//    }
}
