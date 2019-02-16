package me.murks.sqlschemaspec;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Specification of a table.
 * @author zouroboros
 */
public class TableSpec {
    private String name;
    private SchemaSpec schema;
    private final List<ColumnSpec> columns;

    public TableSpec() {
        columns = new LinkedList<>();
    }

    public TableSpec(SchemaSpec nSchema, String nName) {
        schema = nSchema;
        name = nName;
        columns = new LinkedList<>();
    }

    public SchemaSpec getSchema() {
        return schema;
    }

    public void setSchema(SchemaSpec schema) {
        this.schema = schema;
    }

    public String getName() {
        return name;
    }

    public void setName(String nName) {
        name = nName;
    }

    public List<ColumnSpec> columnSpecs() {
        return columns;
    }

    public ColumnSpec column(String name, Type type, ColumnSpec references, Boolean nullable, Boolean primaryKey) {
        ColumnSpec column = new ColumnSpec(this, name, type, references, nullable, primaryKey);
        columns.add(column);
        return column;
    }

    public ColumnSpec column(Type type, ColumnSpec references, Boolean nullable, Boolean primaryKey) {
        ColumnSpec column = new ColumnSpec(this, null, type, references, nullable, primaryKey);
        columns.add(column);
        return column;
    }

    public ColumnSpec column(Type type, Boolean nullable) {
        ColumnSpec column = new ColumnSpec(this, null, type, null, nullable, false);
        columns.add(column);
        return column;
    }

    public ColumnSpec column(Type type) {
        ColumnSpec column = new ColumnSpec(this, null, type, null, false, false);
        columns.add(column);
        return column;
    }

    public ColumnSpec primaryKey(Type type) {
        ColumnSpec column = new ColumnSpec(this, null, type, null, false, true);
        columns.add(column);
        return column;
    }

    public ColumnSpec foreignKey(ColumnSpec references) {
        ColumnSpec column = new ColumnSpec(this, null, references.getType(), references, false, false);
        columns.add(column);
        return column;
    }

    public String createStatement() {
        StringBuilder builder = new StringBuilder();
        builder.append("create table ")
                .append(getName())
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

    public ColumnSpec getColumn(String name) {
        for (ColumnSpec column: columnSpecs()) {
            if (column.getName().equals(name)) {
                return column;
            }
        }
        return null;
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
        return String.format("\"%1$s\"", getName());
    }

    /**
     * Returns a (inner) join expression between the given columns.
     * @param referencedColumn The column from the current table
     * @param foreignKey The column from the foreign table
     * @return SQL join expression
     */
    public String join(ColumnSpec referencedColumn, ColumnSpec foreignKey) {
        if(referencedColumn.getTable() != this) {
            throw new IllegalArgumentException("Invalid column");
        }

        return String.format("%2$s on %1$s = %3$s", referencedColumn.sqlName(),
                foreignKey.getTable().sqlName(), foreignKey.sqlName());
    }

    /**
     * Returns a (inner) join expression between the given current table and the given table.
     * The join is based on a column that declares a foreign key to a column in the referenced table.
     * @param referencedTable The table to join
     * @return SQL join expression
     */
    public String join(TableSpec referencedTable) {
        for (ColumnSpec foreignKey: referencedTable.columnSpecs()) {
            if(foreignKey.getReferences() != null &&
                    foreignKey.getReferences().getTable().equals(this)) {
                return join(foreignKey.getReferences(), foreignKey);
            }
        }
        throw new IllegalArgumentException(
                String.format("No foreign key referencing %1$s in %2$s found!",
                        getName(), referencedTable.getName()));
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
