package me.murks.sqlschemaspec;

/**
 * Enum of the supported sql data types
 */
public enum Type {
    String("text"),
    Integer("integer"),
    Boolean("boolean"),
    Float("double");

    private final String type;

    Type(String nType) {
        type = nType;
    }

    public String toString() {
        return type;
    }
}
