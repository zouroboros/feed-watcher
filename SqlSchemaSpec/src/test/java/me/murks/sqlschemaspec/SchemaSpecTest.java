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
        Table table1 = new Table() {
            public Column id = new Column(Type.Integer, false, true);
            public Column name = new Column(Type.String);
        };

        Table table2 = new Table() {
            Column fKey = new Column(Type.Integer, false, false).references(table1.c("id"));
        };
    }

    private SchemaSpec schemaSpec() {
        SchemaSpec spec = new SchemaSpec();
        TableSpec table1 = spec.createTable("table1");

        ColumnSpec table1Id = table1.addColumn(new Column("id", Type.Integer, null, false, true));
        table1.addColumn(new Column("name", Type.String, null, false, false));

        TableSpec table2 = spec.createTable("table2");
        table2.addColumn(new Column("fKey", Type.Integer, null, false, false))
                .setReferences(table1Id);
        return spec;
    }

    @Test
    public void testInnerClassToSchema() {
        SchemaSpec schema = new SchemaSpec();
        schema.fromSchema(new TestSchema());
        Assert.assertEquals("convert inner class to schema", schemaSpec(), schema);
    }

    @Test
    public void testCreateSql() {
        Assert.assertEquals("create sql", schemaSpec().createStatement(),
                Arrays.asList("create table table1 (id int not null primary key, name text not null)",
                        "create table table2 (fKey int not null, foreign key fKey references table1(id))"));
    }
}
