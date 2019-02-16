package me.murks.sqlschemaspec.templates;

import java.lang.reflect.Field;

import me.murks.sqlschemaspec.ColumnSpec;
import me.murks.sqlschemaspec.SchemaSpec;
import me.murks.sqlschemaspec.TableSpec;

/**
 * Class that compiles a valid {@Link SchemaSpec} from classes.
 * @author zouroboros
 */
public class TemplateCompiler {

    /**
     * Compiles a valid {@Link SchemaSpec} out into a given {@link SchemaSpec}.
     *
     * All fields of the type {@Link TableSpec} in the template are compiled and added to the schema.
     *
     * @param template The template
     * @param spec The schema to which the tables from the template are added
     */
    public void compileTemplate(Object template, SchemaSpec spec) {
        for (Field field: template.getClass().getDeclaredFields()) {
            if(TableSpec.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                try {
                    TableSpec table = (TableSpec) field.get(spec);
                    if(table.getName() == null) {
                        table.setName(field.getName());
                    }
                    compileTable(table, table);
                    spec.addTable(table);
                } catch (IllegalAccessException ae) {
                    throw new RuntimeException(ae);
                }
            }
        }
    }

    /**
     * Compiles a template. All fields of type {@Link ColumnSpec} in template are added to the given
     * table.
     * @param template The template instance
     * @param table The table.
     * @throws IllegalAccessException
     */
    public void compileTable(Object template, TableSpec table) throws IllegalAccessException{
        for (Field field: table.getClass().getDeclaredFields()) {
            if(ColumnSpec.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                ColumnSpec column = (ColumnSpec) field.get(table);
                if(column.getName() == null) {
                    column.setName(field.getName());
                }
                column.setTable(table);
            }
        }
    }
}
