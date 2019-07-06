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

import static java.util.Collections.singleton;
import static org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText.english;

import java.security.cert.X509Certificate;
import java.util.List;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig;
import org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfigBuilder;
import org.eclipse.milo.opcua.sdk.server.identity.AnonymousIdentityValidator;
import org.eclipse.milo.opcua.sdk.server.identity.CompositeValidator;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.CertificateValidator;
import org.eclipse.milo.opcua.stack.core.security.DefaultCertificateManager;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.server.EndpointConfiguration;

public class Server {
    public static void main(final String[] args) throws Exception {

        final OpcUaServerConfigBuilder builder = new OpcUaServerConfigBuilder();

        builder.setIdentityValidator(new CompositeValidator(
                AnonymousIdentityValidator.INSTANCE // You should better ask who knocked, right?
        ));

        final EndpointConfiguration.Builder endpointBuilder = new EndpointConfiguration.Builder();

        endpointBuilder.addTokenPolicies(
                OpcUaServerConfig.USER_TOKEN_POLICY_ANONYMOUS // You wouldn't leave you door open, would you?
        );

        endpointBuilder.setSecurityPolicy(SecurityPolicy.None); // ... or give everyone access to your fridge ...

        endpointBuilder.setBindPort(4840);
        builder.setEndpoints(singleton(endpointBuilder.build()));

        builder.setApplicationName(english("Foo Bar Server"));
        builder.setApplicationUri("urn:my:example");

        builder.setCertificateManager(new DefaultCertificateManager()); // ... don't to this at home! ...

        builder.setCertificateValidator(new CertificateValidator() {

            @Override
            public void validate(final X509Certificate certificate) throws UaException {
                // ... ever! ...
            }

            @Override
            public void verifyTrustChain(final List<X509Certificate> certificateChain) throws UaException {
                // ... I mean it!
            }
        });

        final OpcUaServer server = new OpcUaServer(builder.build());

        // register namespace

        server.getAddressSpaceManager().register(new CustomNamespace(server, CustomNamespace.URI));

        // start it up

        server.startup().get();

        // don't wait for me

        Thread.sleep(Long.MAX_VALUE);
    }
}
