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
package de.dentrassi.ece2017.milo.step01;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.stack.client.UaTcpStackClient;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;

import de.dentrassi.ece2017.milo.Constants;

public class Connect {

    private static OpcUaClientConfig buildConfiguration(final EndpointDescription[] endpoints) {

        final OpcUaClientConfigBuilder cfg = new OpcUaClientConfigBuilder();

        cfg.setEndpoint(findBest(endpoints));

        return cfg.build();

    }

    public static EndpointDescription findBest(final EndpointDescription[] endpoints) {
        /*
         * We simply assume we have at least one and pick the first one. In a more
         * productive scenario you would actually evaluate things like ciphers and
         * security.
         */
        return endpoints[0];
    }

    // create client

    public static CompletableFuture<OpcUaClient> createClient() {
        final String endpoint = String.format("opc.tcp://%s:%s", Constants.HOST, Constants.PORT);

        return UaTcpStackClient
                .getEndpoints(endpoint) // look up endpoints from remote
                .thenApply(endpoints -> new OpcUaClient(buildConfiguration(endpoints)));
    }

    // connect

    public static CompletableFuture<OpcUaClient> connect() {
        return createClient()
                .thenCompose(OpcUaClient::connect) // trigger connect
                .thenApply(c -> (OpcUaClient) c); // cast result of connect from UaClient to OpcUaClient
    }

    // main entry point

    public static void main(final String[] args) throws InterruptedException, ExecutionException {

        final Semaphore s = new Semaphore(0);

        connect()
                .whenComplete((client, e) -> {
                    // called when the connect operation finished ... either way

                    if (e == null) {
                        System.out.println("Connected");
                    } else {
                        System.err.println("Failed to connect");
                        e.printStackTrace();
                    }
                })
                .thenCompose(OpcUaClient::disconnect)
                .thenRun(s::release); // wake up s.acquire() below

        System.out.println("Wait for completion");

        s.acquire(); // what could could wrong?

        System.out.println("Bye bye");
    }

    // synchronous way of doing things

    public static OpcUaClient createClientSync() throws InterruptedException, ExecutionException {
        final String endpoint = String.format("opc.tcp://%s:%s", Constants.HOST, Constants.PORT);

        final EndpointDescription[] endpoints = UaTcpStackClient.getEndpoints(endpoint)
                .get();

        return new OpcUaClient(buildConfiguration(endpoints));
    }

    public static OpcUaClient connectSync() throws InterruptedException, ExecutionException {
        final OpcUaClient client = createClientSync();

        client.connect()
                .get();

        return client;
    }

}
