package net.donnypz.mccore.utils;

import java.time.LocalDate;
import java.time.Month;

public enum GameSeason {
    NORMAL, //No Holiday
    VALENTINES, //Valentines
    SPOOKY, //Halloween
    HOLIDAY, //Christmas + New Years
    ST_PATRICKS; //Saint Patrick's Day


    public boolean isCurrentSeason(){
        if (this == SPOOKY){
            return isSpookySeason();
        }
        else if (this == HOLIDAY){
            return isHolidaySeason();
        }
        else if (this == VALENTINES){
            return isValentines();
        }
        else if (this == ST_PATRICKS){
            return isStPatricks();
        }
        return true;
    }

    public static boolean isValentines(){
        LocalDate currentDate = LocalDate.now();
        return currentDate.getMonth() == Month.FEBRUARY && currentDate.getDayOfMonth() == 14;
    }

    public static boolean isHolidaySeason(){
        LocalDate currentDate = LocalDate.now();
        return currentDate.getMonth() == Month.DECEMBER || (currentDate.getMonth() == Month.JANUARY && currentDate.getDayOfMonth() < 10);
    }

    public static boolean isSpookySeason(){
        LocalDate currentDate = LocalDate.now();
        return currentDate.getMonth() == Month.OCTOBER && currentDate.getDayOfMonth() >= 10;
    }

    public static boolean isStPatricks(){
        LocalDate currentDate = LocalDate.now();
        return currentDate.getMonth() == Month.MARCH && currentDate.getDayOfMonth() == 17;
    }
}
