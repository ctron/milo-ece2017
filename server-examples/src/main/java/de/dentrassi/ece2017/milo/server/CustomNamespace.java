/*******************************************************************************
 * Copyright (c) 2017, 2019 Red Hat Inc.
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

import java.util.List;
import java.util.Optional;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.DataItem;
import org.eclipse.milo.opcua.sdk.server.api.ManagedNamespace;
import org.eclipse.milo.opcua.sdk.server.api.MonitoredItem;
import org.eclipse.milo.opcua.sdk.server.model.nodes.objects.FolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaMethodNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNodeContext;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.server.util.SubscriptionModel;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;

public class CustomNamespace extends ManagedNamespace {

    public static final String URI = "urn:my:custom:namespace";

    private final SubscriptionModel subscriptionModel;

    public CustomNamespace(final OpcUaServer server, final String uri) {
        super(server, uri);
        this.subscriptionModel = new SubscriptionModel(server, this);
    }

    private void registerItems(final UaNodeContext context) {

        // create a folder

        final UaFolderNode folder = new UaFolderNode(
                context,
                newNodeId(1),
                newQualifiedName("FooBarFolder"),
                LocalizedText.english("Foo Bar Folder"));

        // add our folder to the objects folder

        final Optional<UaNode> objectsFolder = context.getServer()
                .getAddressSpaceManager()
                .getManagedNode(Identifiers.ObjectsFolder);

        objectsFolder.ifPresent(node -> {
            ((FolderNode) node).addComponent(folder);
        });

        // add single variable

        {
            final UaVariableNode variable = new UaVariableNode(
                    context,
                    newNodeId("look-at-me-i-am-right-here-hey-watch-out-woooohoo-here-i-am"),
                    newQualifiedName("FooBar"),
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
                    context,
                    newNodeId("call-me-al"),
                    newQualifiedName("Al"));
            folder.addComponent(method);
        }
    }

    @Override
    protected void onStartup() {
        super.onStartup();
        registerItems(getNodeContext());
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

}
