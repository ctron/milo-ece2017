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

import static de.dentrassi.ece2017.milo.server.CallMe.createCallMeNode;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.AccessContext;
import org.eclipse.milo.opcua.sdk.server.api.DataItem;
import org.eclipse.milo.opcua.sdk.server.api.MethodInvocationHandler;
import org.eclipse.milo.opcua.sdk.server.api.MethodInvocationHandler.NotImplementedHandler;
import org.eclipse.milo.opcua.sdk.server.api.MonitoredItem;
import org.eclipse.milo.opcua.sdk.server.api.Namespace;
import org.eclipse.milo.opcua.sdk.server.api.ServerNodeMap;
import org.eclipse.milo.opcua.sdk.server.model.nodes.objects.FolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.AttributeContext;
import org.eclipse.milo.opcua.sdk.server.nodes.ServerNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaMethodNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.server.util.SubscriptionModel;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.eclipse.milo.opcua.stack.core.types.structured.WriteValue;

public class CustomNamespace implements Namespace {

    public static final String URI = "urn:my:custom:namespace";

    private final UShort index;

    private final ServerNodeMap nodeMap;
    private final SubscriptionModel subscriptionModel;

    public CustomNamespace(final UShort index, final OpcUaServer server) {
        this.index = index;
        this.nodeMap = server.getNodeMap();
        this.subscriptionModel = new SubscriptionModel(server, this);

        registerItems();
    }

    private void registerItems() {

        // create a folder

        final UaFolderNode folder = new UaFolderNode(
                this.nodeMap,
                new NodeId(this.index, 1),
                new QualifiedName(this.index, "FooBarFolder"),
                LocalizedText.english("Foo Bar Folder"));

        // add our folder to the objects folder

        this.nodeMap.getNode(Identifiers.ObjectsFolder).ifPresent(node -> {
            ((FolderNode) node).addComponent(folder);
        });

        // add single variable

        {
            final UaVariableNode variable = new UaVariableNode(
                    this.nodeMap,
                    new NodeId(this.index, "look-at-me-i-am-right-here-hey-watch-out-woooohoo-here-i-am"),
                    new QualifiedName(this.index, "FooBar"),
                    LocalizedText.english("Foo Bar")) {

                @Override
                public DataValue getValue() {
                    return new DataValue(new Variant(Math.sin(System.currentTimeMillis() / 1000)));
                }
            };

            variable.setDataType(Identifiers.Double);

            folder.addOrganizes(variable);
        }

        // add method call

        {
            final UaMethodNode method = createCallMeNode(
                    this.index,
                    this.nodeMap);
            folder.addComponent(method);
        }
    }

    // default method implementations follow

    @Override
    public void read(
            final ReadContext context,
            final Double maxAge,
            final TimestampsToReturn timestamps,
            final List<ReadValueId> readValueIds) {

        final List<DataValue> results = new ArrayList<>(readValueIds.size());

        for (final ReadValueId id : readValueIds) {
            final ServerNode node = this.nodeMap.get(id.getNodeId());

            final DataValue value = node != null
                    ? node.readAttribute(new AttributeContext(context), id.getAttributeId())
                    : new DataValue(StatusCodes.Bad_NodeIdUnknown);

            results.add(value);
        }

        // report back with result

        context.complete(results);
    }

    @Override
    public void write(
            final WriteContext context,
            final List<WriteValue> writeValues) {

        final List<StatusCode> results = writeValues.stream()
                .map(value -> {
                    if (this.nodeMap.containsKey(value.getNodeId())) {
                        return new StatusCode(StatusCodes.Bad_NotWritable);
                    } else {
                        return new StatusCode(StatusCodes.Bad_NodeIdUnknown);
                    }
                })
                .collect(toList());

        // report back with result

        context.complete(results);
    }

    @Override
    public CompletableFuture<List<Reference>> browse(final AccessContext context, final NodeId nodeId) {
        final ServerNode node = this.nodeMap.get(nodeId);

        if (node != null) {
            return CompletableFuture.completedFuture(node.getReferences());
        } else {
            final CompletableFuture<List<Reference>> f = new CompletableFuture<>();
            f.completeExceptionally(new UaException(StatusCodes.Bad_NodeIdUnknown));
            return f;
        }
    }

    @Override
    public Optional<MethodInvocationHandler> getInvocationHandler(final NodeId methodId) {
        return Optional
                .ofNullable(this.nodeMap.get(methodId))
                .filter(n -> n instanceof UaMethodNode)
                .map(n -> {
                    final UaMethodNode m = (UaMethodNode) n;
                    return m.getInvocationHandler()
                            .orElse(new NotImplementedHandler());
                });
    }

    @Override
    public void onDataItemsCreated(final List<DataItem> dataItems) {
        this.subscriptionModel.onDataItemsCreated(dataItems);
    }

    @Override
    public void onDataItemsModified(final List<DataItem> dataItems) {
        this.subscriptionModel.onDataItemsModified(dataItems);
    }

    @Override
    public void onDataItemsDeleted(final List<DataItem> dataItems) {
        this.subscriptionModel.onDataItemsDeleted(dataItems);
    }

    @Override
    public void onMonitoringModeChanged(final List<MonitoredItem> monitoredItems) {
        this.subscriptionModel.onMonitoringModeChanged(monitoredItems);
    }

    @Override
    public UShort getNamespaceIndex() {
        return this.index;
    }

    @Override
    public String getNamespaceUri() {
        return URI;
    }

}
