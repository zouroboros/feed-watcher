package me.murks.sqlschemaspec.templates;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * Templates for classes that declare a table. The column of a table are defined by all fields of
 * this class that are of type {@link Column}.
 *
 * @author zouroboros
 */
public abstract class Table {

    private String name;

    /**
     * Constructs a new table without a name.
     */
    public Table() { }

    /**
     * Constructs a new table with the given name.
     * @param name The name of the table
     */
    public Table(String name) {
        this.name = name;
    }

    /**
     * Returns all columns that are declared as fields of type {@link Column}.
     * If a column is declared with out a name the name of the column is defined by the name of field
     * that declares it.
     * @return List of columns
     */
    public List<Column> columns() {
        LinkedList<Column> columnFields = new LinkedList<>();
        for (Field field: getClass().getDeclaredFields()) {
            if(Column.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                try {
                    Column column = (Column) field.get(this);
                    if(column.getName() == null) {
                        column.setName(field.getName());
                    }
                    columnFields.add(column);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return columnFields;
    }

    public Column c(String name) {
        for (Column col: columns()) {
            if(col.getName().equals(name)) {
                return col;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
