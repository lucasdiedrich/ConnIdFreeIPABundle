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
package org.connid.bundles.freeipa.exceptions;

import org.connid.bundles.freeipa.util.AuthResults;

public class FreeIPAException extends RuntimeException {

    private static final long serialVersionUID = 8453757915892769284L;

    private AuthResults exceptionCause;

    public FreeIPAException(AuthResults exceptionCause) {
        this.exceptionCause = exceptionCause;
    }

    public FreeIPAException(AuthResults exceptionCause, Throwable cause) {
        super(cause);
        this.exceptionCause = exceptionCause;
    }

    public FreeIPAException(final Throwable cause) {
        super(cause);
    }

    public FreeIPAException(final String message) {
        super(message);
    }

    public FreeIPAException(final String message, final Exception e) {
        super(message, e);
    }

    public AuthResults getExceptionCause() {
        return exceptionCause;
    }

}
