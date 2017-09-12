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
package de.dentrassi.ece2017.milo;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

public final class NodeIds {

    private NodeIds() {
    }

    private static final Map<NodeId, String> IDS;

    static {
        final LinkedHashMap<NodeId, String> ids = new LinkedHashMap<>();

        for (final Field field : Identifiers.class.getDeclaredFields()) {

            try {
                final Object value = field.get(null);
                if (value instanceof NodeId) {
                    ids.put((NodeId) value, field.getName());
                }
            } catch (final Exception e) {
                continue;
            }
        }

        IDS = Collections.unmodifiableMap(ids);
    }

    public static Optional<String> lookup(final NodeId id) {
        return Optional.ofNullable(IDS.get(id));
    }

}
