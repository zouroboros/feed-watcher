package me.murks.feedwatcher.data;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import me.murks.sqlschemaspec.ColumnSpec;
import me.murks.sqlschemaspec.SchemaSpec;
import me.murks.sqlschemaspec.TableSpec;
import me.murks.sqlschemaspec.Type;
import me.murks.sqlschemaspec.templates.TemplateCompiler;

/**
 * Database schema of the FeedWatcherApp
 * @author zouroboros
 */
public class FeedWatcherSchema extends SchemaSpec {

    class Feeds extends TableSpec {
        ColumnSpec id = primaryKey(Type.Integer);
        ColumnSpec name = column(Type.String);
        ColumnSpec url = column(Type.String);
        ColumnSpec lastUpdated = column(Type.String, true);
        ColumnSpec deleted = column(Type.Boolean);
    }

    Feeds feeds = new Feeds();

    class Queries extends TableSpec {
        ColumnSpec id = primaryKey(Type.Integer);
        ColumnSpec name = column(Type.String);
        ColumnSpec deleted = column(Type.Boolean);
    }

    Queries queries = new Queries();

    class Filters extends TableSpec {
        ColumnSpec id = primaryKey(Type.Integer);
        ColumnSpec type = column(Type.String);
        ColumnSpec index = column(Type.Integer);
        ColumnSpec queryId = foreignKey(queries.id);
    }

    Filters filters = new Filters();

    class FilterParameters extends TableSpec {
        ColumnSpec id = primaryKey(Type.Integer);
        ColumnSpec name = column(Type.String);
        ColumnSpec stringValue = column(Type.String, true);
        ColumnSpec filterId = foreignKey(filters.id);
        ColumnSpec dateValue = column(Type.Integer);
    }

    FilterParameters filterParameters = new FilterParameters();

    class Results extends TableSpec {
        ColumnSpec id = primaryKey(Type.Integer);
        ColumnSpec feedId = foreignKey(feeds.id);
        ColumnSpec title = column(Type.String);
        ColumnSpec description = column(Type.String);
        ColumnSpec link = column(Type.String, true);
        ColumnSpec date = column(Type.Integer, true);
        ColumnSpec found = column(Type.Integer);
    }

    Results results = new Results();

    class ResultQueries extends TableSpec {
        ColumnSpec id = primaryKey(Type.Integer);
        ColumnSpec resultId = foreignKey(results.id);
        ColumnSpec queryId = foreignKey(queries.id);
    }

    ResultQueries resultQueries = new ResultQueries();

    public FeedWatcherSchema() {
        TemplateCompiler compiler = new TemplateCompiler();
        compiler.compileTemplate(this, this);
    }

    public void createSchema(SQLiteDatabase db) {
        db.beginTransaction();
        for (String statement: createStatement()) {
            Log.d(getClass().toString(), statement);
            db.execSQL(statement);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
