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
package de.dentrassi.ece2017.milo.step05;

import static de.dentrassi.ece2017.milo.step01.Connect.connect;

import java.util.concurrent.CompletableFuture;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;

import de.dentrassi.ece2017.milo.step04.Subscribe;

public class Write {

    private static final NodeId NODE_TO_WRITE = Subscribe.PERMIT_STATE;

    public static CompletableFuture<StatusCode> write(
            final OpcUaClient client,
            final NodeId nodeId,
            final Object value) {

        return client.writeValue(nodeId, new DataValue(new Variant(value)));
    }

    public static void main(final String[] args) throws Exception {

        final boolean value = args.length > 0 ? Boolean.parseBoolean(args[0]) : false;

        // first example

        connect()

                .thenCompose(client -> {

                    return write(
                            client,
                            NODE_TO_WRITE,
                            value // value to write
                    )

                            .whenComplete((result, error) -> {
                                if (error == null) {
                                    System.out.format("Result: %s%n", result);
                                } else {
                                    error.printStackTrace();
                                }
                            })

                            .thenCompose(v -> client.disconnect());
                })

                .get(); // wait for everything to complete
    }
}
