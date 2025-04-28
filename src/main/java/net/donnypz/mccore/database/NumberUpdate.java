package net.donnypz.mccore.database;

public class NumberUpdate{
    Number incrementAmount;
    private final NumberType numberType;

    NumberUpdate(Number incrementAmount, NumberType numberType){
        this.incrementAmount = incrementAmount;
        this.numberType = numberType;
    }


    public int intValue() {
        return incrementAmount.intValue();
    }

    public long longValue() {
        return incrementAmount.longValue();
    }

    public float floatValue() {
        return incrementAmount.floatValue();
    }

    public double doubleValue() {
        return incrementAmount.doubleValue();
    }

    public NumberType getNumberType() {
        return numberType;
    }

    public String getNumberAsString() {
        switch (numberType) {
            case INT -> {
                return String.valueOf(intValue());
            }
            case LONG -> {
                return String.valueOf(longValue());
            }
            case FLOAT -> {
                return String.valueOf(floatValue());
            }
            case DOUBLE -> {
                return String.valueOf(doubleValue());
            }
            default -> {
                return "NaN";
            }
        }
    }

    public enum NumberType {
        INT,
        LONG,
        DOUBLE,
        FLOAT,
    }
}
