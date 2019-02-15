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
            foreignKey = String.format(" references \"%2$s\"(\"%3$s\")", getName(),
                    getReferences().getTable().getTableName(), getReferences().getName());
        }

        return String.format("\"%1$s\" %2$s%3$s%4$s%5$s", getName(),
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

    /**
     * Returns a string that can be used to refer to this column in an SQL statement
     * @return SQL expression
     */
    public String sqlName() {
        return String.format("%1$s.\"%2$s\"", getTable().sqlName(), getName());
    }

    /**
     * Returns a sql expression that renames this column to a the given name.
     * @param name The new name
     * @return SQL expression
     */
    public String rename(String name) {
        return "" + sqlName() + " as \"" + name + "\"";
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
