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

import org.connid.bundles.freeipa.FreeIPAConnector;
import org.connid.bundles.freeipa.commons.ConnectorObjectFactory;
import org.connid.bundles.freeipa.exceptions.FreeIPAException;
import org.connid.bundles.freeipa.util.AuthResults;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FreeIPACheckAliveTest {
    
    private final FreeIPAConnector freeIPAConnector = new FreeIPAConnector();
    
    @Before
    public void before() {
        freeIPAConnector.init(ConnectorObjectFactory.configurationWithRightUsernameAndPassword());
    }
    
    @Test
    public void freeIPACheckAliveTest() {
        freeIPAConnector.checkAlive();
    }
    
    @Test
    public void freeIPAAuthenticateWithWrongUsernameTest() {
        freeIPAConnector.init(ConnectorObjectFactory.configurationWithWrongUsername());
        try {
            freeIPAConnector.checkAlive();
        } catch (FreeIPAException e) {
            Assert.assertEquals(AuthResults.AUTH_NO_SUCH_OBJECT, e.getExceptionCause());
        }

    }

    @Test
    public void freeIPAAuthenticateWithWrongPasswordTest() {
        freeIPAConnector.init(ConnectorObjectFactory.configurationWithWrongPassword());
        try {
            freeIPAConnector.checkAlive();
        } catch (FreeIPAException e) {
            Assert.assertEquals(AuthResults.AUTH_INVALID_CREDENTIALS, e.getExceptionCause());
        }
    }
    
}
