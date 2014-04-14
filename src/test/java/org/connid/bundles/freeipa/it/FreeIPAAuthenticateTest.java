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
package org.connid.bundles.freeipa.it;

import static org.junit.Assert.assertEquals;

import com.unboundid.ldap.sdk.LDAPException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.connid.bundles.freeipa.FreeIPAConnector;
import org.connid.bundles.freeipa.beans.server.FreeIPAUserAccount;
import org.connid.bundles.freeipa.commons.SampleAttributesFactory;
import org.connid.bundles.freeipa.commons.SampleConfigurationFactory;
import org.connid.bundles.freeipa.commons.UserAttributesTestValue;
import org.identityconnectors.common.CollectionUtil;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.Uid;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FreeIPAAuthenticateTest {

    private static FreeIPAConnector freeIPAConnector;

    private final static List<String> usersCreated = new ArrayList<String>();

    @Before
    public void before() {
        freeIPAConnector = new FreeIPAConnector();
        freeIPAConnector.init(SampleConfigurationFactory.configurationWithRightUsernameAndPassword());
    }

    @Test
    public void authenticationTest() {
        final Name name = new Name(UserAttributesTestValue.uid + (int) (Math.random() * 100000));
        final Uid createdUid = freeIPAConnector.create(
                ObjectClass.ACCOUNT, SampleAttributesFactory.sampleUserSetAttributes(name), null);
        assertEquals(name.getNameValue(), createdUid.getUidValue());
        usersCreated.add(name.getNameValue());
        final Uid authUid = freeIPAConnector.authenticate(
                ObjectClass.ACCOUNT, createdUid.getUidValue(), UserAttributesTestValue.userPassword, null);
        Assert.assertEquals(authUid, createdUid);
    }

    @Test
    public void authenticateAfterPasswordUpdate() throws LDAPException, GeneralSecurityException {
        final Name name = new Name(UserAttributesTestValue.uid + (int) (Math.random() * 100000));
        final Uid createdUid = freeIPAConnector.create(
                ObjectClass.ACCOUNT, SampleAttributesFactory.sampleUserSetAttributes(name), null);
        assertEquals(name.getNameValue(), createdUid.getUidValue());
        usersCreated.add(name.getNameValue());
        final Uid authUid = freeIPAConnector.authenticate(
                ObjectClass.ACCOUNT, createdUid.getUidValue(), UserAttributesTestValue.userPassword, null);
        Assert.assertEquals(createdUid, authUid);
        final Set<Attribute> attributes = sampleSetAttributes();
        attributes.add(AttributeBuilder.buildPassword(UserAttributesTestValue.newUserPassword));
        final Uid updatedUid = freeIPAConnector.update(ObjectClass.ACCOUNT, authUid, attributes, null);
        Assert.assertEquals(authUid, updatedUid);
    }

    private Set<Attribute> sampleSetAttributes() {
        final Set attributes = CollectionUtil.newSet(AttributeBuilder.buildEnabled(true));
        attributes.add(AttributeBuilder.build(FreeIPAUserAccount.DefaultAttributes.INITIALS.ldapValue(),
                CollectionUtil.newSet(UserAttributesTestValue.newInitials)));
        return attributes;
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
