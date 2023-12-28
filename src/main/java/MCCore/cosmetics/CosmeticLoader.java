package MCCore.cosmetics;

import MCCore.MongoUtils;

import java.util.*;

public abstract class CosmeticLoader {

    static final Set<CosmeticLoader> loaders = new HashSet<>();
    private final LinkedHashMap<String, Cosmetic> cosmeticStorage = new LinkedHashMap<>();

    public CosmeticLoader(){
        loaders.add(this);

    }

    public abstract void loadCosmetics();

    public static void loadAllLoaders(){
        for (CosmeticLoader loader : loaders){
            loader.loadCosmetics();
        }
    }

    public Cosmetic registerCosmetic(Cosmetic cosmetic){
        if (!cosmeticStorage.containsKey(cosmetic.getCosmeticName())){
            cosmeticStorage.put(cosmetic.getCosmeticName(), cosmetic);
        }
        return cosmetic;
    }

    public void autoSetMongoSelectValues(){
        ArrayList<Cosmetic> cosmetics = new ArrayList<>(cosmeticStorage.values());
        for (int i = 0; i < cosmetics.size(); i++){
            cosmetics.get(i).setMongoSelectValue(i+1);
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
            if (mongoSelectValue.equals(cosmetic.getMongoSelectValue())){
                return cosmetic;
            }
        }
        return null;
    }

    public Collection<Cosmetic> getCosmeticsOfType(Cosmetic.CosmeticType cosmeticType){
        Collection<Cosmetic> cosmetics = new ArrayList<>();
        for (Cosmetic cosmetic : cosmeticStorage.values()){
            if (cosmetic.getCosmeticType() == cosmeticType) cosmetics.add(cosmetic);
        }
        return cosmetics;
    }

    public Collection<Cosmetic> getCosmeticsOfCurrency(MongoUtils.CurrencyType currencyType){
        Collection<Cosmetic> cosmetics = new ArrayList<>();
        for (Cosmetic cosmetic : cosmeticStorage.values()){
            if (cosmetic.getCosmeticType() == Cosmetic.CosmeticType.CURRENCY){
                if (cosmetic.getCurrencyType() == currencyType){
                    cosmetics.add(cosmetic);
                }
            }
        }
        return cosmetics;
    }

    public <T> ArrayList<T> getCosmetics(Class<T> cosmeticClass){
        try{
            ArrayList<T> list = new ArrayList<>();
            for (Cosmetic cosmetic : cosmeticStorage.values()){
                list.add(cosmeticClass.cast(cosmetic));
            }
            //return cosmeticStorage.values();
            return list;
        }
        catch(ClassCastException e){
            throw new ClassCastException("Invalid cosmetic class provided");
        }
    }

    public ArrayList<Cosmetic> getCosmetics(){
        return new ArrayList<>(cosmeticStorage.values());
    }
}
