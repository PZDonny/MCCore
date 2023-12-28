package MCCore.cosmetics;

import MCCore.MongoUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public abstract class Cosmetic{
    private final String cosmeticName;
    private Object mongoSelectValue = null;
    private String cosmeticDisplayName;
    private int price;
    private CosmeticType cosmeticType;
    private MongoUtils.CurrencyType currencyType;

    private Material displayMaterial;


    public Cosmetic(String cosmeticName){
        this.cosmeticName = cosmeticName;
        this.setMongoSelectValue(cosmeticName);
    }

    public Material getDisplayMaterial() {
        return displayMaterial;
    }

    public Cosmetic setDisplayMaterial(Material displayMaterial) {
        this.displayMaterial = displayMaterial;
        return this;
    }

    public String getCosmeticDisplayName(){
        if (cosmeticDisplayName != null) return cosmeticDisplayName;
        else return cosmeticName;
    }

    public Cosmetic setCosmeticDisplayName(String cosmeticDisplayName) {
        this.cosmeticDisplayName = cosmeticDisplayName;
        return this;
    }

    public int getPrice() {
        return price;
    }

    public Cosmetic setPrice(int price) {
        this.price = price;
        return this;
    }

    public Cosmetic setMongoSelectValue(Object mongoSelectValue){
        this.mongoSelectValue = mongoSelectValue;
        return this;
    }

    public Object getMongoSelectValue(){
        return this.mongoSelectValue;
    }

    public CosmeticType getCosmeticType() {
        return cosmeticType;
    }

    public Cosmetic setCosmeticType(CosmeticType cosmeticType) {
        this.cosmeticType = cosmeticType;
        return this;
    }

    public MongoUtils.CurrencyType getCurrencyType() {
        return currencyType;
    }

    public Cosmetic setCurrencyType(MongoUtils.CurrencyType currencyType) {
        this.currencyType = currencyType;
        return this;
    }

    public String getCosmeticName() {
        return cosmeticName;
    }

    public enum CosmeticType {
        CURRENCY(null, null, "Currency"),
        WINS(null, "wins", "Wins"),
        LEVEL(null, "level", "Level"),
        PRESTIGE(null, "prestige", "Prestige"),
        KILLS(null, "kills", "Kills"),

        IRONRANKED("mc.iron", null, ChatColor.GRAY+"Iron"),
        GOLDRANKED("mc.gold", null, ChatColor.GOLD+"Gold"),
        TNTRANKED("mc.tnt", null, ChatColor.RED+"T"+ChatColor.WHITE+"N"+ChatColor.RED+"T"),
        INFLUENCER("mc.influencer", null, ""),
        STAFF("mc.staff", null, "");

        private final String permission;
        private final String mongoKey;
        private final String displayName;
        CosmeticType(String permission, String mongoKey, String displayName){
            this.permission = permission;
            this.mongoKey = mongoKey;
            this.displayName = displayName;
        }

        public boolean isRanked(){
            return permission != null;
        }

        public String getPermission() {
            return permission;
        }

        public String getMongoKey(){
            return mongoKey;
        }

        public String getMongoKeyParenthesized(){
            if (mongoKey.charAt(mongoKey.length()-1) != 's'){
                return mongoKey;
            }
            String parenthesized = mongoKey.substring(0, mongoKey.length()-1)+"(";
            return parenthesized+mongoKey.charAt(mongoKey.length()-1)+")";
        }

        public String getDisplayName(){
            return displayName;
        }

        public boolean hasMongoKey(){
            return mongoKey != null;
        }
    }

}
