package me.murks.sqlschemaspec;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

    /**
     * Returns all tables that are referenced by this table via foreign keys.
     * @return Referenced tables
     */
    public Set<TableSpec> referencedTables() {

        Set<TableSpec> specs = new HashSet<>();
        for (ColumnSpec column: columnSpecs()) {
            if(column.getReferences() != null) {
                specs.add(column.getReferences().getTable());
            }
        }

        return specs;
    }
    
    public String prefixedColumns(String prefix) {
        StringBuilder builder = new StringBuilder();
        for (ColumnSpec spec : columnSpecs()) {
            builder.append(spec.rename(prefix + spec.getName()));
            builder.append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
        return builder.toString();
    }

    /**
     * Returns an expression that can be used to refer to this table in an SQL string
     * @return SQL expression
     */
    public String sqlName() {
        return String.format("\"%1$s\"", getTableName());
    }

    /**
     * Returns a (inner) join expression between the given columns.
     * @param foreignKey The column from the current table
     * @param referencedColumn The column from the referenced table
     * @return SQL join expression
     */
    public String join(ColumnSpec foreignKey, ColumnSpec referencedColumn) {
        if(foreignKey.getTable() != this) {
            throw new IllegalArgumentException("Invalid column");
        }

        return String.format("%2$s on %1$s = %3$s", foreignKey.sqlName(),
                referencedColumn.getTable().sqlName(), referencedColumn.sqlName());
    }

    /**
     * Returns a (inner) join expression between the given current table and the given table.
     * The join is based on a column that declares a foreign key to a column in the referenced table.
     * @param referencedTable The table to join
     * @return SQL join expression
     */
    public String join(TableSpec referencedTable) {
        for (ColumnSpec foreignKey: columnSpecs()) {
            if(foreignKey.getReferences().getTable().equals(referencedTable)) {
                return join(foreignKey, foreignKey.getReferences());
            }
        }
        throw new IllegalArgumentException(
                String.format("No foreign key referencing %2$s in %1$s found!",
                        getTableName(), referencedTable.getTableName()));
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
