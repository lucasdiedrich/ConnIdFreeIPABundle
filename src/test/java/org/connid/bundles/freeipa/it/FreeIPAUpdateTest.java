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

import java.util.Set;
import org.connid.bundles.freeipa.FreeIPAConnector;
import org.connid.bundles.freeipa.commons.SampleConfigurationFactory;
import org.connid.bundles.freeipa.util.server.FreeIPAUserAccount;
import org.identityconnectors.common.CollectionUtil;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.Uid;
import org.junit.Before;
import org.junit.Test;

public class FreeIPAUpdateTest {

    private FreeIPAConnector freeIPAConnector;

    @Before
    public void before() {
        freeIPAConnector = new FreeIPAConnector();
        freeIPAConnector.init(SampleConfigurationFactory.configurationWithRightUsernameAndPassword());
    }

    @Test
    public void freeIPAUpdateTest() {
        final Uid uid = new Uid("utente.test28462");
        freeIPAConnector.update(ObjectClass.ACCOUNT, uid, sampleSetAttributes(), null);
    }
    
    public static Set<Attribute> sampleSetAttributes() {
        final Set attributes = CollectionUtil.newSet(AttributeBuilder.buildEnabled(false));
        attributes.add(AttributeBuilder.build(FreeIPAUserAccount.DefaultAttributes.INITIALS.ldapValue(),
                CollectionUtil.newSet("ANDREA")));
        return attributes;
    }
}
