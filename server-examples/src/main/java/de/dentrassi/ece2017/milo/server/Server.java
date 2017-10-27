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

import static java.util.Arrays.asList;
import static org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText.english;

import java.security.cert.X509Certificate;
import java.util.EnumSet;
import java.util.List;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig;
import org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfigBuilder;
import org.eclipse.milo.opcua.sdk.server.identity.AnonymousIdentityValidator;
import org.eclipse.milo.opcua.sdk.server.identity.CompositeValidator;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.application.CertificateValidator;
import org.eclipse.milo.opcua.stack.core.application.DefaultCertificateManager;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;

public class Server {
    public static void main(final String[] args) throws Exception {

        final OpcUaServerConfigBuilder builder = new OpcUaServerConfigBuilder();

        builder.setIdentityValidator(new CompositeValidator(
                AnonymousIdentityValidator.INSTANCE // You should better ask who knocked, right?
        ));

        builder.setBindPort(4840);

        builder.setApplicationName(english("Foo Bar Server"));
        builder.setApplicationUri("urn:my:example");

        builder.setUserTokenPolicies(
                asList(OpcUaServerConfig.USER_TOKEN_POLICY_ANONYMOUS) // You wouldn't leave you door open, would you?
        );

        builder.setSecurityPolicies(
                EnumSet.of(SecurityPolicy.None) // ... or give everyone access to your fridge ...
        );

        builder.setCertificateManager(new DefaultCertificateManager()); // ... don't to this at home! ...

        builder.setCertificateValidator(new CertificateValidator() {

            @Override
            public void validate(final X509Certificate certificate) throws UaException {
                // ... ever! ...
            }

            @Override
            public void verifyTrustChain(final X509Certificate certificate, final List<X509Certificate> chain)
                    throws UaException {
                // ... I mean it!
            }
        });

        final OpcUaServer server = new OpcUaServer(builder.build());

        // register namespace

        server.getNamespaceManager().registerAndAdd(
                CustomNamespace.URI, index -> new CustomNamespace(index, server));

        // start it up

        server.startup().get();

        // don't wait for me

        Thread.sleep(Long.MAX_VALUE);
    }
}
