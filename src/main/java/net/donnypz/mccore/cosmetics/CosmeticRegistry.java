package net.donnypz.mccore.cosmetics;

import net.donnypz.mccore.utils.inventory.cosmetic.FieldMinimumCondition;
import org.bukkit.Bukkit;

import java.util.*;


public abstract class CosmeticRegistry {

    protected static final HashSet<CosmeticRegistry> registries = new HashSet<>();
    private final LinkedHashMap<String, Cosmetic> cosmeticStorage = new LinkedHashMap<>();

    public CosmeticRegistry(){
        registries.add(this);

    }

    protected abstract void registerCosmetics();

    public static void loadAllRegistries(){
        for (CosmeticRegistry registry : registries){
            try{
                registry.registerCosmetics();
            }
            catch(IllegalStateException e){
                Bukkit.getLogger().warning("Failed to register a cosmetic registry! Was a plugin disabled?");
            }

        }
    }

    Cosmetic registerCosmetic(Cosmetic cosmetic){
        if (!cosmeticStorage.containsKey(cosmetic.getCosmeticName())){
            cosmeticStorage.put(cosmetic.getCosmeticName(), cosmetic);
        }
        return cosmetic;
    }

    public void autoSetDatabaseSelectValues(){
        ArrayList<Cosmetic> cosmetics = new ArrayList<>(cosmeticStorage.values());
        for (int i = 0; i < cosmetics.size(); i++){
            cosmetics.get(i).setValue(i+1);
        }
    }

    public Cosmetic getCosmetic(String cosmeticName){
        return cosmeticStorage.get(cosmeticName);
    }

    public <T> T getCosmetic(String cosmeticName, Class<T> cosmeticClass){
        Cosmetic cosmetic = cosmeticStorage.get(cosmeticName);
        if (cosmetic == null){
            return null;
        }
        try{
            return cosmeticClass.cast(cosmetic);
        }
        catch(ClassCastException e){
            throw new ClassCastException("Invalid cosmetic class provided");
        }
    }

    public Cosmetic getCosmetic(Object mongoSelectValue){
        for (Cosmetic cosmetic : cosmeticStorage.values()){
            if (cosmetic.getValue().equals(mongoSelectValue)){
                return cosmetic;
            }
        }
        return null;
    }


    public List<Cosmetic> getCosmetics(FieldMinimumCondition fieldMinimumCondition){
        return getCosmetics(fieldMinimumCondition, Cosmetic.class);
    }

    public <T> List<T> getCosmetics(FieldMinimumCondition fieldMinimumCondition, Class<T> cosmeticClass){
        List<T> cosmetics = new ArrayList<>();

        for (Cosmetic cosmetic : cosmeticStorage.values()){
            if (cosmetic.hasCosmeticCondition(fieldMinimumCondition)) {
                cosmetics.add(cosmeticClass.cast(cosmetic));
            }
        }
        return cosmetics;
    }


    public List<Cosmetic> getCosmetics(FieldMinimumCondition... fieldMinimumConditions){
        return getCosmetics(Cosmetic.class, fieldMinimumConditions);
    }

    /**
     * Get cosmetics that contain at least one of the cosmetic conditions
     */
    public <T> List<T> getCosmetics(Class<T> cosmeticClass, FieldMinimumCondition... fieldMinimumConditions){
        List<T> cosmetics = new ArrayList<>();

        for (Cosmetic cosmetic : cosmeticStorage.values()){
            for (FieldMinimumCondition condition : fieldMinimumConditions){
                if (cosmetic.hasCosmeticCondition(condition)) {
                    cosmetics.add(cosmeticClass.cast(cosmetic));
                    break;
                }
            }

        }
        return cosmetics;
    }


    public List<Cosmetic> getCosmeticsWithConditionField(String mongoField){
        List<Cosmetic> cosmetics = new ArrayList<>();
        for (Cosmetic cosmetic : cosmeticStorage.values()){
            for (FieldMinimumCondition cond : cosmetic.getFieldMinimumConditions()){
                if (cond.hasField(mongoField)){
                    cosmetics.add(cosmetic);
                    break;
                }
            }
        }
        return cosmetics;
    }

    public List<Cosmetic> getCosmeticsWithConditionField(Collection<String> mongoFields){
        List<Cosmetic> cosmetics = new ArrayList<>();
        for (Cosmetic cosmetic : cosmeticStorage.values()){
            condition:
            for (FieldMinimumCondition cond : cosmetic.getFieldMinimumConditions()){
                for (String field : mongoFields){
                    if (cond.hasField(field)){
                        cosmetics.add(cosmetic);
                        break condition;
                    }
                }

            }
        }
        return cosmetics;
    }


    public <T> List<T> getCosmetics(Class<T> cosmeticClass){
        try{
            List<T> list = new ArrayList<>();
            for (Cosmetic cosmetic : cosmeticStorage.values()){
                list.add(cosmeticClass.cast(cosmetic));
            }
            return list;
        }
        catch(ClassCastException e){
            throw new ClassCastException("Invalid cosmetic class provided");
        }
    }

    public List<Cosmetic> getCosmetics(){
        return new ArrayList<>(cosmeticStorage.values());
    }

    public SequencedSet<String> getCosmeticNames(){
        return cosmeticStorage.sequencedKeySet();
    }
}
