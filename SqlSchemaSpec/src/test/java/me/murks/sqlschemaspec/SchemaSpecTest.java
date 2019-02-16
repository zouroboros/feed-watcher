package me.murks.sqlschemaspec;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import me.murks.sqlschemaspec.templates.TemplateCompiler;

/**
 * @author zouroboros
 */
public class SchemaSpecTest {

    private class TestSchema extends SchemaSpec {

        class Table2 extends TableSpec {
            ColumnSpec id = primaryKey(Type.Integer);
            ColumnSpec name = column(Type.String);
        };

        Table2 table2 = new Table2();

        TableSpec table1 = new TableSpec() {
            ColumnSpec fKey = foreignKey(table2.id);
        };
    }

    private SchemaSpec schemaSpec() {
        SchemaSpec spec = new SchemaSpec();

        TableSpec table1 = spec.createTable("table1");
        TableSpec table2 = spec.createTable("table2");

        ColumnSpec table2Id = table2.column("id", Type.Integer, null, false, true);
        table2.column("name", Type.String, null, false, false);

        table1.column("fKey", Type.Integer, table2Id, false, false);
        return spec;
    }

    @Test
    public void templateCompiler() {
        SchemaSpec schema = new TestSchema();
        TemplateCompiler compiler = new TemplateCompiler();
        compiler.compileTemplate(schema, schema);
        Assert.assertEquals(schemaSpec(), schema);
    }

    @Test
    public void createStatement() {
        Assert.assertEquals("create sql", schemaSpec().createStatement(),
                Arrays.asList("create table table2 (\"id\" integer not null primary key, \"name\" text not null)",
                        "create table table1 (\"fKey\" integer not null references \"table2\"(\"id\"))"));
    }

    @Test
    public void prefixedColumns() {
        TableSpec spec = schemaSpec().getTable("table1");
        Assert.assertEquals("\"table1\".\"fKey\" as \"table1fKey\"", spec.prefixedColumns("table1"));
    }
}
