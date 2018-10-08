/*******************************************************************************
 * Copyright (c) 2018 Red Hat Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jens Reimann - initial API and implementation
 *******************************************************************************/

package de.dentrassi.heapcon2018.milo.camel;

import org.apache.camel.component.milo.server.MiloServerComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean("my-milo")
    public MiloServerComponent miloServerComponent() {
        final MiloServerComponent server = new MiloServerComponent();
        server.setEnableAnonymousAuthentication(true);
        return server;
    }

}
