package me.murks.sqlschemaspec;

/**
 * Container for holding information about columns.
 * @author zouroboros
 */
public class ColumnAttributes {
    private String name;
    private Type type;
    private Boolean nullable;
    private Boolean primaryKey;

    public ColumnAttributes(String nName, Type nType, Boolean nNullable, Boolean nPrimaryKey){
        name = nName;
        type = nType;
        nullable = nNullable;
        primaryKey = nPrimaryKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Boolean isNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public Boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Boolean primaryKey) {
        this.primaryKey = primaryKey;
    }
}
