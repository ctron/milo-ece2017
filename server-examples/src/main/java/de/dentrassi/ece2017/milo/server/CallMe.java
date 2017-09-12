/*******************************************************************************
 * Copyright (c) 2017 Red Hat Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jens Reimann - initial API and implementation
 *******************************************************************************/
package de.dentrassi.ece2017.milo.server;

import org.eclipse.milo.opcua.sdk.server.annotations.UaInputArgument;
import org.eclipse.milo.opcua.sdk.server.annotations.UaMethod;
import org.eclipse.milo.opcua.sdk.server.annotations.UaOutputArgument;
import org.eclipse.milo.opcua.sdk.server.api.ServerNodeMap;
import org.eclipse.milo.opcua.sdk.server.nodes.UaMethodNode;
import org.eclipse.milo.opcua.sdk.server.util.AnnotationBasedInvocationHandler;
import org.eclipse.milo.opcua.sdk.server.util.AnnotationBasedInvocationHandler.InvocationContext;
import org.eclipse.milo.opcua.sdk.server.util.AnnotationBasedInvocationHandler.Out;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;

public class CallMe {

    @UaMethod
    public void call(
            final InvocationContext context,
            @UaInputArgument(name = "me") final String me,
            @UaOutputArgument(name = "result") final Out<String> result) {

        if ("Al".equals(me)) {
            result.set("You did it!");
        } else {
            result.set("Try again");
        }
    }

    public static UaMethodNode createCallMeNode(final UShort index, final ServerNodeMap nodeMap) {
        final UaMethodNode method = new UaMethodNode(
                nodeMap,
                new NodeId(index, "call-me-al"),
                new QualifiedName(index, "Al"),
                LocalizedText.english("Al"),
                LocalizedText.english("Call me Al"),
                UInteger.MIN, UInteger.MIN, true, true);

        try {
            final AnnotationBasedInvocationHandler handler = AnnotationBasedInvocationHandler
                    .fromAnnotatedObject(nodeMap, new CallMe());
            method.setInputArguments(handler.getInputArguments());
            method.setOutputArguments(handler.getOutputArguments());
            method.setInvocationHandler(handler);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return method;
    }
}
