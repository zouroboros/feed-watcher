package me.murks.sqlschemaspec;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.murks.sqlschemaspec.templates.Column;
import me.murks.sqlschemaspec.templates.Schema;
import me.murks.sqlschemaspec.templates.Table;

/**
 * Specification of sql database schema.
 * @author zouroboros
 */
public class SchemaSpec {
    private final List<TableSpec> tables;

    public SchemaSpec() {
        tables = new LinkedList<>();
    }

    /**
     * Contructs a new specification from a given {@link Schema} subclass.
     * @param schema A instance of a subclass of schema.
     */
    public void fromSchema(Schema schema) {
        Map<Column, Table> tableByColumn = new HashMap<>();
        Map<Column, ColumnSpec> columnSpecsByColumn = new HashMap<>();

        for (Table table: schema.getTables()) {
            TableSpec tableSpec = new TableSpec(this, table.getName());

            for (Column column: table.columns()) {
                tableByColumn.put(column, table);
                ColumnSpec spec = tableSpec.addColumn(column);
                columnSpecsByColumn.put(column, spec);
            }

            tables.add(tableSpec);
        }

        for (Map.Entry<Column, ColumnSpec> columnAndSpec: columnSpecsByColumn.entrySet()) {
            if(columnAndSpec.getKey().getReferences() != null) {
                ColumnSpec spec = columnSpecsByColumn.get(columnAndSpec.getKey().getReferences());
                if(spec == null) {
                    throw new IllegalArgumentException("Invalid column reference");
                }
                columnAndSpec.getValue().setReferences(spec);
            }
        }
    }

    /**
     * Returns all table specifications in this schema in an order in which they can created.
     * @return ordered tables
     */
    public Collection<TableSpec> createOrder() {
        Set<TableSpec> remaining = new HashSet<>(tables);
        LinkedHashSet<TableSpec> createOrder = new LinkedHashSet<>();

        while(!remaining.isEmpty()) {
            remaining.removeAll(createOrder);

            for (TableSpec spec: remaining) {
                if(createOrder.containsAll(spec.referencedTables())) {
                    createOrder.add(spec);
                }
            }
        }

        return createOrder;
    }

    /**
     * Returns a list of create statements for the schema
     * @return create statements
     */
    public List<String> createStatement() {
        LinkedList<String> statements = new LinkedList<>();

        for (TableSpec table: createOrder()) {
            statements.add(table.createStatement());
        }

        return statements;
    }

    /**
     * Creates and adds a new table specification in this schema. The new specification is returned.
     * @param name Table name
     * @return The new table specification
     */
    public TableSpec createTable(String name) {
        TableSpec spec = new TableSpec(this, name);
        tables.add(spec);
        return spec;
    }


    @Override
    public boolean equals(Object obj) {
        if(obj != null && obj instanceof SchemaSpec) {
            SchemaSpec other = (SchemaSpec) obj;
            return other.createStatement().equals(createStatement());
        }

        return false;
    }

    @Override
    public String toString() {
        return "SchemaSpec{tables=" + Arrays.toString(tables.toArray()) + '}';
    }
}
