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
package de.dentrassi.ece2017.milo.step04;

import static de.dentrassi.ece2017.milo.Values.dumpValues;
import static de.dentrassi.ece2017.milo.step01.Connect.connect;
import static java.time.Duration.ofMinutes;
import static java.util.Collections.singletonList;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;

public class Subscribe {

    private static final NodeId LED_STATE = new NodeId(1, 114);
    private static final NodeId PERMIT_STATE = NodeId.parse("ns=1;i=117");

    private static final AtomicInteger clientHandles = new AtomicInteger();

    public static void main(final String[] args) throws Exception {

        final CompletableFuture<OpcUaClient> future = connect().thenCompose(client -> {

            return client.getSubscriptionManager().createSubscription(1_000.0)
                    .thenCompose(subscription -> {

                        return subscribeTo(
                                subscription,
                                AttributeId.Value,
                                LED_STATE, PERMIT_STATE)

                                        .thenAccept(result -> {
                                            System.out.println(result);
                                        });

                    })
                    .thenApply(v -> client);

        });

        final OpcUaClient client = future.get();

        Thread.sleep(ofMinutes(15).toMillis());

        client.disconnect().get();
    }

    private static CompletableFuture<UaMonitoredItem> subscribeTo(
            final UaSubscription subscription,
            final AttributeId attributeId,
            final NodeId... nodeIds) {

        // subscription request

        final List<MonitoredItemCreateRequest> requests = new ArrayList<>(nodeIds.length);

        for (final NodeId nodeId : nodeIds) {

            final MonitoringParameters parameters = new MonitoringParameters(
                    uint(clientHandles.getAndIncrement()), // must be set
                    1_000.0, // sampling interval
                    null,
                    uint(10),
                    true);

            final ReadValueId readValueId = new ReadValueId(
                    nodeId,
                    attributeId.uid(),
                    null,
                    null);

            final MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(
                    readValueId,
                    MonitoringMode.Reporting,
                    parameters);

            requests.add(request);

        }

        // callback setup

        final BiConsumer<UaMonitoredItem, DataValue> consumer = //
                (item, value) -> dumpValues(
                        System.out,
                        singletonList(item.getReadValueId().getNodeId()),
                        singletonList(value));

        final BiConsumer<UaMonitoredItem, Integer> onItemCreated = //
                (monitoredItem, id) -> monitoredItem.setValueConsumer(consumer);

        // subscribe

        return subscription
                .createMonitoredItems(
                        TimestampsToReturn.Both,
                        requests,
                        onItemCreated // callback setup -> setting the callback
                )
                .thenApply(result -> result.get(0));
    }

}
