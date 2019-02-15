package me.murks.sqlschemaspec.templates;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * Templates class for new schemas. New schema should extends this class a declare tables as fields
 * of type {@link Table}.
 *
 * @author zouroboros
 */
public class Schema {

    /**
     * Returns all {@link Table}s that declared as fields in this class.
     * If a table is declared without a name the name of the table will be the defined by the field
     * that declares the table.
     * @return List of declared tables.
     */
    public List<Table> getTables() {
        LinkedList<Table> tables = new LinkedList<>();

        for (Field field: getClass().getDeclaredFields()) {
            if(Table.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                try {
                    Table table = (Table) field.get(this);
                    if(table.getName() == null) {
                        table.setName(field.getName());
                    }
                    tables.add(table);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return tables;
    }
}
