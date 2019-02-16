package me.murks.sqlschemaspec;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

    public void addTable(TableSpec table) {
        table.setSchema(this);
        tables.add(table);
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

    /**
     * Returns all tables in this schema.
     * @return List of tables
     */
    public List<TableSpec> getTables() {
        return Collections.unmodifiableList(tables);
    }

    /**
     * Find a table with the given name in the schema and returns it. If the table is not found then
     * null is returned.
     * @param name The table name
     * @return The table specification
     */
    public TableSpec getTable(String name) {
        for (TableSpec spec : getTables()) {
            if(spec.getName().equals(name)) {
                return spec;
            }
        }
        return null;
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
