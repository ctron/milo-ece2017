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

import java.io.PrintStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.scada.utils.str.Tables;

public final class Values {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    private Values() {
    }

    public static void dumpValues(final PrintStream out, final List<NodeId> nodeIds, final List<DataValue> values) {
        final int len = Integer.max(nodeIds.size(), values.size());

        final List<List<String>> data = new ArrayList<>(len);

        for (int i = 0; i < Integer.min(nodeIds.size(), values.size()); i++) {

            final List<String> row = new ArrayList<>(5);
            data.add(row);

            final DataValue value = values.get(i);

            row.add(nodeIds.get(i).toParseableString());
            row.add(toString(value.getValue()));
            row.add(toString(value.getStatusCode()));
            row.add(TIMESTAMP_FORMATTER.format(value.getServerTime().getJavaDate().toInstant()));
            row.add(TIMESTAMP_FORMATTER.format(value.getSourceTime().getJavaDate().toInstant()));
        }

        Tables.showTable(out,
                Arrays.asList("Node Id", "Value", "State", "Timestamp(Server)", "Timestamp(Source)"),
                data,
                2);

        System.out.println();
    }

    public static String toString(final Variant value) {

        return String.format("%s : %s",
                value.getDataType() // get data type
                        .map(id -> NodeIds.lookup(id).orElse(id.toParseableString())) // map to ID or use node id
                        .orElse("<unknown>"), // default to "unknown"
                value.getValue());
    }

    public static String toString(final StatusCode statusCode) {
        return StatusCodes
                .lookup(statusCode.getValue()) // lookup
                .map(s -> s[0]) // pick name
                .orElse(statusCode.toString()); // or default to "toString"
    }

}
