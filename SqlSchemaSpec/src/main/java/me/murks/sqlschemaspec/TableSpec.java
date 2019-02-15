package me.murks.sqlschemaspec;

import java.util.LinkedList;
import java.util.List;

import me.murks.sqlschemaspec.templates.Column;

/**
 * Specification of a table.
 * @author zouroboros
 */
public class TableSpec {
    private final String tableName;
    private final SchemaSpec schema;
    private final List<ColumnSpec> columns;

    public TableSpec(SchemaSpec nSchema, String name) {
        schema = nSchema;
        tableName = name;
        columns = new LinkedList<>();
    }

    public String getTableName() {
        return tableName;
    }

    public List<ColumnSpec> columnSpecs() {
        return columns;
    }

    public ColumnSpec addColumn(Column attributes) {
        ColumnSpec spec = new ColumnSpec(this, attributes);
        columns.add(spec);
        return spec;
    }

    public String createStatement() {
        StringBuilder builder = new StringBuilder();
        builder.append("create table ")
                .append(getTableName())
                .append(" (");

        for (ColumnSpec column: columnSpecs()) {
            builder.append(column.createStatement());
            builder.append(", ");
        }

        if(!columnSpecs().isEmpty()) {
            builder.delete(builder.length() - 2, builder.length());
        }

        builder.append(")");

        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {

        if(obj != null && obj instanceof TableSpec) {
            TableSpec other = (TableSpec) obj;
            return createStatement().equals(other.createStatement());
        }

        return false;
    }

    @Override
    public String toString() {
        return createStatement();
    }
}
