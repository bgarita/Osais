/*
 * Características de una columna de una tabla de base de datos.
 */
package logica;

/**
 *
 * @author bgarita
 */
public class Column {
    private String columnName;
    private String columnType;
    private double columnLength;
    private String columnDefault;
    private boolean nullable;
    private int characterMaximumLength;
    private int numericPrecision;
    private int numericScale;
    private String columnKey;   // PRI, SEC
    private String columnComment;
    private int ordinalPosition;

    public Column(String columnName, String columnType, double columnLength, String columnDefault, boolean nullable, int characterMaximumLength, int numericPrecision, int numericScale, String columnKey, String columnComment, int ordinalPosition) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.columnLength = columnLength;
        this.columnDefault = columnDefault;
        this.nullable = nullable;
        this.characterMaximumLength = characterMaximumLength;
        this.numericPrecision = numericPrecision;
        this.numericScale = numericScale;
        this.columnKey = columnKey;
        this.columnComment = columnComment;
        this.ordinalPosition = ordinalPosition;
    } // end full constructor
    
    public Column() {
        this.columnName = "";
        this.columnType = "";
        this.columnLength = 0;
        this.columnDefault = "";
        this.nullable = false;
        this.characterMaximumLength = 0;
        this.numericPrecision = 0;
        this.numericScale = 0;
        this.columnKey = "";
        this.columnComment = "";
        this.ordinalPosition = 1;
    } // end empty constructor

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public double getColumnLength() {
        return columnLength;
    }

    public void setColumnLength(double columnLength) {
        this.columnLength = columnLength;
    }

    public String getColumnDefault() {
        return columnDefault;
    }

    public void setColumnDefault(String columnDefault) {
        this.columnDefault = columnDefault;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public int getCharacterMaximumLength() {
        return characterMaximumLength;
    }

    public void setCharacterMaximumLength(int characterMaximumLength) {
        this.characterMaximumLength = characterMaximumLength;
    }

    public int getNumericPrecision() {
        return numericPrecision;
    }

    public void setNumericPrecision(int numericPrecision) {
        this.numericPrecision = numericPrecision;
    }

    public int getNumericScale() {
        return numericScale;
    }

    public void setNumericScale(int numericScale) {
        this.numericScale = numericScale;
    }

    public String getColumnKey() {
        return columnKey;
    }

    public void setColumnKey(String columnKey) {
        this.columnKey = columnKey;
    }

    public String getColumnComment() {
        return columnComment;
    }

    public void setColumnComment(String columnComment) {
        this.columnComment = columnComment;
    }

    public int getOrdinalPosition() {
        return ordinalPosition;
    }

    public void setOrdinalPosition(int ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }
    
    
} // end Column
