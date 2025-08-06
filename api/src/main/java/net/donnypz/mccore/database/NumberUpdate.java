package net.donnypz.mccore.database;

class NumberUpdate{
    Number incrementAmount;
    private final NumberType numberType;

    NumberUpdate(Number incrementAmount, NumberType numberType){
        this.incrementAmount = incrementAmount;
        this.numberType = numberType;
    }


    int intValue() {
        return incrementAmount.intValue();
    }

    long longValue() {
        return incrementAmount.longValue();
    }

    float floatValue() {
        return incrementAmount.floatValue();
    }

    double doubleValue() {
        return incrementAmount.doubleValue();
    }

    NumberType getNumberType() {
        return numberType;
    }

    String getNumberAsString() {
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

    enum NumberType {
        INT,
        LONG,
        DOUBLE,
        FLOAT,
    }
}
