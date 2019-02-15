package me.murks.sqlschemaspec.templates;

import me.murks.sqlschemaspec.ColumnAttributes;
import me.murks.sqlschemaspec.Type;

/**
 * Class for defining columns
 * @author zouroboros
 */
public class Column extends ColumnAttributes {

    private Column references;

    public Column(String name, Type nType, Column nReferences, Boolean nNullable, Boolean nPrimaryKey){
        super(name, nType, nNullable, nPrimaryKey);
        references = nReferences;
    }

    public Column(Type nType, Boolean nNullable, Boolean nPrimaryKey){
        this(null, nType, null, nNullable, nPrimaryKey);
    }

    public Column(Type nType) {
        this(null, nType, null, false, false);
    }

    /**
     * Make this column a foreign key referencing the given column.
     * @param otherColumn The column to be referenced
     * @return The current column
     */
    public Column references(Column otherColumn) {
        references = otherColumn;
        return this;
    }

    /**
     * Returns the referenced column.
     * @return The referenced column
     */
    public Column getReferences() {
        return references;
    }
}
