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
package de.dentrassi.ece2017.milo.step03;

import static de.dentrassi.ece2017.milo.step01.Connect.connect;
import static java.util.Collections.singletonList;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowsePath;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowsePathResult;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowsePathTarget;
import org.eclipse.milo.opcua.stack.core.types.structured.RelativePath;
import org.eclipse.milo.opcua.stack.core.types.structured.RelativePathElement;

public class Lookup {

    public static CompletableFuture<BrowsePathResult> translate(
            final OpcUaClient client,
            final NodeId startingNode,
            final String... path) {

        Objects.requireNonNull(startingNode);
        Objects.requireNonNull(path);

        // convert to elements

        final RelativePathElement[] elements = new RelativePathElement[path.length];
        for (int i = 0; i < path.length; i++) {
            elements[i] = new RelativePathElement(
                    Identifiers.HierarchicalReferences,
                    false, true,
                    QualifiedName.parse(path[i]));
        }

        // translate

        final BrowsePath request = new BrowsePath(startingNode, new RelativePath(elements));
        return client.translateBrowsePaths(singletonList(request)).thenApply(response -> response.getResults()[0]);
    }

    public static void main(final String[] args) throws Exception {

        connect()

                .thenCompose(client -> {

                    return translate(client, Identifiers.ObjectsFolder, "1:LedState", "1:E_SR", "1:Q")

                            .thenAccept(Lookup::dumpResult)
                            .thenCompose(c -> client.disconnect());

                }) // .thenCompose

                .get();

    }

    public static void dumpResult(final BrowsePathResult result) {
        if (result.getStatusCode().isGood()) {

            for (final BrowsePathTarget target : result.getTargets()) {
                System.out.println(target.getTargetId().local().get());
            }

        } else {

            System.out.println(result.getStatusCode());

        }
    }

}
