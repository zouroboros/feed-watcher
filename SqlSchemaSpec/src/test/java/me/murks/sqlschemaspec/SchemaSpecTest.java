package me.murks.sqlschemaspec;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import me.murks.sqlschemaspec.templates.Column;
import me.murks.sqlschemaspec.templates.Schema;
import me.murks.sqlschemaspec.templates.Table;

/**
 * @author zouroboros
 */
public class SchemaSpecTest {

    private class TestSchema extends Schema {
        Table table2 = new Table() {
            public Column id = new Column(Type.Integer, false, true);
            public Column name = new Column(Type.String);
        };

        Table table1 = new Table() {
            Column fKey = new Column(Type.Integer, false, false).references(table2.c("id"));
        };
    }

    private SchemaSpec schemaSpec() {
        SchemaSpec spec = new SchemaSpec();

        TableSpec table1 = spec.createTable("table1");
        TableSpec table2 = spec.createTable("table2");

        ColumnSpec table2Id = table2.addColumn(new Column("id", Type.Integer, null, false, true));
        table2.addColumn(new Column("name", Type.String, null, false, false));

        table1.addColumn(new Column("fKey", Type.Integer, null, false, false))
                .setReferences(table2Id);
        return spec;
    }

    @Test
    public void innerClassToSchema() {
        SchemaSpec schema = new SchemaSpec();
        schema.fromSchema(new TestSchema());
        Assert.assertEquals("convert inner class to schema", schemaSpec(), schema);
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
