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
package de.dentrassi.ece2017.milo.step02;

import static de.dentrassi.ece2017.milo.step01.Connect.connect;
import static java.util.Arrays.asList;
import static java.util.Collections.nCopies;
import static java.util.Collections.singletonList;
import static org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn.Both;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;

import de.dentrassi.ece2017.milo.Values;

public class Read {

    private static final NodeId NODE_TO_READ = new NodeId(0, 2261);

    public static CompletableFuture<DataValue> read(
            final OpcUaClient client,
            final NodeId nodeId) {

        return client.readValue(0, TimestampsToReturn.Both, nodeId);
    }

    public static CompletableFuture<List<DataValue>> read(
            final OpcUaClient client,
            final AttributeId attributeId,
            final NodeId... nodeIds) {

        return client
                .read(
                        0,
                        Both,
                        asList(nodeIds),
                        nCopies(nodeIds.length, attributeId.uid()));
    }

    public static void main(final String[] args) throws Exception {

        // first example

        connect()

                .thenCompose(client -> {
                    return read(client, NODE_TO_READ)
                            .thenAccept(value -> {
                                Values.dumpValues(
                                        System.out,
                                        singletonList(NODE_TO_READ),
                                        singletonList(value));
                            })
                            .thenCompose(v -> client.disconnect());
                })

                .get();

        // second example

        final OpcUaClient client = connect().get();

        // nodes to read

        final NodeId[] moreIds = new NodeId[] {
                Identifiers.Server_ServerStatus_BuildInfo_ManufacturerName,
                Identifiers.Server_ServerStatus_BuildInfo_ProductName,
                Identifiers.Server_ServerStatus_CurrentTime
        };

        // read values

        final CompletableFuture<List<DataValue>> future = read(
                client,
                AttributeId.Value,
                moreIds);

        final List<DataValue> values = future.get();
        Values.dumpValues(System.out, Arrays.asList(moreIds), values);

        // read browse name

        final CompletableFuture<List<DataValue>> future2 = read(
                client,
                AttributeId.BrowseName,
                moreIds);

        final List<DataValue> values2 = future2.get();
        Values.dumpValues(System.out, Arrays.asList(moreIds), values2);

        // disconnect

        client.disconnect().get();
    }
}
