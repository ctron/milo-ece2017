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

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseDirection;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseResultMask;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseResult;
import org.eclipse.milo.opcua.stack.core.types.structured.ReferenceDescription;

import de.dentrassi.ece2017.milo.step01.Connect;

public class Browse {

    public static void main(final String[] args) throws Exception {

        final OpcUaClient client = Connect.connect().get();

        try {
            System.out.format("%-60s %-15s %s%n", "Name", "Type", "NodeID");
            System.out.println(
                    "==========================================================================================");
            browse(client, Identifiers.RootFolder, "");

        } finally {
            client.disconnect().get();
        }
    }

    private static void browse(
            final OpcUaClient client,
            final NodeId browseRoot,
            final String indent) throws Exception {

        final BrowseDescription browse = new BrowseDescription(
                browseRoot,
                BrowseDirection.Forward,
                Identifiers.References,
                true,
                uint(NodeClass.Object.getValue() | NodeClass.Variable.getValue()),
                uint(BrowseResultMask.All.getValue()));

        BrowseResult browseResult = client.browse(browse).get();

        do {

            for (final ReferenceDescription ref : browseResult.getReferences()) {
                dumpRef(indent, ref);

                // browse children
                final NodeId childId = ref.getNodeId().local().orElse(null);
                if (childId != null) {
                    browse(client, childId, indent + "  ");
                }
            }

            if (browseResult.getContinuationPoint().isNotNull()) {
                browseResult = client.browseNext(true, browseResult.getContinuationPoint()).get();
            } else {
                browseResult = null;
            }

        } while (browseResult != null);
    }

    private static void dumpRef(final String indent, final ReferenceDescription ref) {
        System.out.format("%-60s %-15s %s%n",
                indent + ref.getBrowseName().getName(),
                ref.getNodeClass().toString(),
                ref.getNodeId().local().map(NodeId::toParseableString).orElse(""));
    }

}
