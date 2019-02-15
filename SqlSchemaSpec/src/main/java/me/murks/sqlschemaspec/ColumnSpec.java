package me.murks.sqlschemaspec;

import me.murks.sqlschemaspec.templates.Column;

/**
 * Specification of a column
 * @author zouroboros
 */
public class ColumnSpec extends ColumnAttributes {

    private final TableSpec table;
    private ColumnSpec references;

    public ColumnSpec(TableSpec nTable, Column column) {
        super(column.getName(), column.getType(), column.isNullable(), column.isPrimaryKey());
        table = nTable;
        references = null;
    }


    public String createStatement() {
        String nullable = " not null";
        if(isNullable()) {
            nullable = " null";
        }

        String primaryKey = "";
        if(isPrimaryKey()) {
            primaryKey = " primary key";
        }

        String foreignKey = "";

        if(getReferences() != null) {
            foreignKey = String.format(", foreign key %1$s references %2$s(%3$s)", getName(),
                    getReferences().getTable().getTableName(), getReferences().getName());
        }

        return String.format("%1$s %2$s%3$s%4$s%5$s", getName(),
                getType().toString(), nullable, primaryKey, foreignKey);
    }

    public TableSpec getTable() {
        return table;
    }

    public ColumnSpec getReferences() {
        return references;
    }

    public void setReferences(ColumnSpec references) {
        this.references = references;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj != null && obj instanceof ColumnSpec) {
            ColumnSpec other = (ColumnSpec) obj;
            return createStatement().equals(other.createStatement());
        }

        return false;
    }

    @Override
    public String toString() {
        return createStatement();
    }
}
