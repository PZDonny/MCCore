package net.donnypz.mccore.database;

import net.donnypz.mccore.cosmetics.Cosmetic;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class UnlockedCosmetics {

    private final HashSet<Cosmetic> cosmetics = new HashSet<>();


    void add(@NotNull Cosmetic cosmetic){
        cosmetics.add(cosmetic);
    }

    boolean contains(@NotNull Cosmetic cosmetic){
        return cosmetics.contains(cosmetic);
    }

    int getCount(){
        return cosmetics.size();
    }

    void clear(){
        cosmetics.clear();
    }

}
