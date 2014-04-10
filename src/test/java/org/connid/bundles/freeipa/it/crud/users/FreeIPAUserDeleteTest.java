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
import org.connid.bundles.freeipa.FreeIPAConnector;
import org.connid.bundles.freeipa.commons.SampleAttributesFactory;
import org.connid.bundles.freeipa.commons.SampleConfigurationFactory;
import org.connid.bundles.freeipa.commons.UserAttributesTestValue;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.Uid;
import org.junit.Before;
import org.junit.Test;

public class FreeIPAUserDeleteTest {

    private FreeIPAConnector freeIPAConnector;

    @Before
    public void before() {
        freeIPAConnector = new FreeIPAConnector();
        freeIPAConnector.init(SampleConfigurationFactory.configurationWithRightUsernameAndPassword());
    }

    @Test
    public void sampleDeleteTest() {
        final Name name = new Name(UserAttributesTestValue.uid + (int) (Math.random() * 100000));
        final Uid uid = freeIPAConnector.create(
                ObjectClass.ACCOUNT, SampleAttributesFactory.sampleUserSetAttributes(name), null);
        assertEquals(name.getNameValue(), uid.getUidValue());
        freeIPAConnector.delete(ObjectClass.ACCOUNT, uid, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void deleteWithNullUidTest() {
        freeIPAConnector.delete(ObjectClass.ACCOUNT, null, null);
    }

    @Test
    public void deleteWithNullUidCatchTest() {
        try {
            freeIPAConnector.delete(ObjectClass.ACCOUNT, null, null);
        } catch (final IllegalArgumentException e) {
            assertEquals("No uid attribute provided in the attributes", e.getMessage());
        }
    }
    
    @Test(expected = ConnectorException.class)
    public void deleteNullObjectClassTest() {
        final Name name = new Name(UserAttributesTestValue.uid + (int) (Math.random() * 100000));
        freeIPAConnector.create(null, SampleAttributesFactory.sampleGroupAttributes(name), null);
    }

    @Test
    public void deleteNullObjectClassCatchTest() {
        final Name name = new Name(UserAttributesTestValue.uid + (int) (Math.random() * 100000));
        try {
            freeIPAConnector.create(null, SampleAttributesFactory.sampleGroupAttributes(name), null);
        } catch (final ConnectorException e) {
            assertEquals("Object class not valid", e.getMessage());
        }
    }
    
    @Test(expected = ConnectorException.class)
    public void deleteNotExistsUserTest() {
        freeIPAConnector.delete(
                ObjectClass.ACCOUNT, new Uid("NOTEXISTS"), null);
    }

    @Test
    public void deleteNotExistsUserCatchTest() {
        try {
            freeIPAConnector.delete(
                ObjectClass.ACCOUNT, new Uid("NOTEXISTS"), null);
        } catch (final ConnectorException e) {
            assertEquals(String.format("User %s does not exists", "NOTEXISTS"), e.getMessage());
        }
    }
}
