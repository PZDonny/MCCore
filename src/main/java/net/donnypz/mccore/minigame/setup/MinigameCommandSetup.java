/*package net.donnypz.mccore.minigame.setup;

import net.donnypz.mccore.Core;
import net.donnypz.mccore.database.CoreMongoUtils;
import net.donnypz.mccore.utils.RegionUtils;
import net.donnypz.mccore.utils.SlimeUtils;
import net.donnypz.playerdbutils.database.MongoUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@ApiStatus.Experimental
public final class MinigameCommandSetup {

    private MinigameCommandSetup(){};

    public static void registerSlimeWorld(Player p, DatabaseDataset dataset, boolean ignoreSWM){
        registerSlimeWorld(p, dataset, null, ignoreSWM);
    }

    public static void registerSlimeWorld(Player p, DatabaseDataset dataset, Map<String, Object> additionalData, boolean ignoreSWM){
        if (!ignoreSWM){
            if (!SlimeUtils.isSlimeWorld(p.getWorld().getName())){
                p.sendMessage(Component.text(ChatColor.RED+"You can only do this in SWM worlds!"));
                return;
            }
        }
        String worldName = p.getWorld().getName();

        Document doc = isWorldRegistered(p, dataset, false, worldName);
        if (doc != null){
            p.sendMessage(Component.text(ChatColor.RED+"This SWM world is already registered"));
            return;
        }

        p.sendMessage(Component.text(ChatColor.GREEN+"World successfully registered! "+ChatColor.AQUA+"("+worldName+")"));
        new BukkitRunnable(){
            public void run(){
                Document newWorld = new Document();
                newWorld.append("world", worldName);
                newWorld.append("mapName", worldName);
                newWorld.append("mapCreator", worldName);
                newWorld.append("isPlaytestOnly", true);
                if (additionalData != null){
                    for (String s : additionalData.keySet()){
                        newWorld.append(s, additionalData.get(s));
                    }
                }


            //Save File
                dataset.getMongoCollection().insertOne(newWorld);
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+"Successfully generated minigame MongoDB document");
            }
        }.runTaskAsynchronously(Core.getInstance());
    }

    public static void unregisterSlimeWorld(Player p, DatabaseDataset dataset, boolean ignoreSWM){
        if (!ignoreSWM){
            if (!SlimeUtils.isSlimeWorld(p.getWorld().getName())){
                p.sendMessage(Component.text(ChatColor.RED+"You can only do this in SWM worlds!"));
                return;
            }
        }
        String worldName = p.getWorld().getName();
        Document doc = isWorldRegistered(p, dataset, true, worldName);
        if (doc == null){
            return;
        }

        p.sendMessage(Component.text(ChatColor.YELLOW+"World successfully unregistered! "+ChatColor.AQUA+"("+worldName+")"));
        new BukkitRunnable(){
            public void run(){
                dataset.getMongoCollection().deleteOne(doc);
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW+"Successfully deleted minigame MongoDB document");
            }
        }.runTaskAsynchronously(Core.getInstance());
    }

    public static void togglePlaytest(Player p, DatabaseDataset dataset, boolean ignoreSWM){
        Document doc = isWorldValid(p, dataset, ignoreSWM);
        if (doc == null) return;

        if (doc.getBoolean("isPlaytestOnly")){
            p.sendMessage(Component.text(ChatColor.YELLOW+"Changed world's playtest state to: "+ChatColor.GREEN+"LIVE"));
        }
        else{
            p.sendMessage(Component.text(ChatColor.YELLOW+"Changed world's playtest state to: "+ChatColor.GOLD+"PLAYTEST ONLY"));
        }


        //Mongo
        new BukkitRunnable() {
            public void run(){
                Document newValue = new Document("isPlaytestOnly", !doc.getBoolean("isPlaytestOnly")); //New Values
                Bson updateOperation = new Document ("$set", newValue);
                dataset.getMongoCollection().updateOne(new Document("world", doc.getString("world")), updateOperation); //Update Doc
            }
        }.runTaskAsynchronously(Core.getInstance());
    }


    public static void setWorldName(Player p, DatabaseDataset dataset, String mapName, boolean ignoreSWM){
        Document doc = isWorldValid(p, dataset, ignoreSWM);
        if (doc == null) return;

        p.sendMessage(Component.text(ChatColor.YELLOW+"Successfully set world's map name! "+ChatColor.AQUA+"("+mapName)+")");

        //Mongo
        new BukkitRunnable() {

            public void run(){
                Document newValue = new Document("mapName", mapName); //New Values
                Bson updateOperation = new Document ("$set", newValue);
                dataset.getMongoCollection().updateOne(new Document("world", doc.getString("world")), updateOperation); //Update Doc
            }
        }.runTaskAsynchronously(Core.getInstance());
    }

    public static void setWorldCreator(Player p, DatabaseDataset dataset, String mapCreator, boolean ignoreSWM){
        Document doc = isWorldValid(p, dataset, ignoreSWM);
        if (doc == null) return;

        p.sendMessage(Component.text(ChatColor.YELLOW+"Successfully set world's map creator! "+ChatColor.AQUA+"("+mapCreator)+")");

        //Mongo
        new BukkitRunnable() {

            public void run(){
                Document newValue = new Document("mapCreator", mapCreator); //New Values
                Bson updateOperation = new Document ("$set", newValue);
                dataset.getMongoCollection().updateOne(new Document("world", doc.getString("world")), updateOperation); //Update Doc
            }
        }.runTaskAsynchronously(Core.getInstance());
    }

    public static void set(Player p, DatabaseDataset dataset, String key, Object value, String successMessage, boolean ignoreSWM, boolean ignoreExisting){
        Document doc = isWorldValid(p, dataset, ignoreSWM);
        if (doc == null){
            return;
        }

        p.sendMessage(successMessage);

        //Mongo
        new BukkitRunnable() {
            public void run(){

                //Not Nested
                Document newValue;
                if (!key.contains(".")){
                    if (doc.containsKey(key) && doc.get(key).equals(value)){
                        return;
                    }
                    newValue = new Document(key, value);
                }
                else{
                    String[] split = key.split("\\.");
                    String nestedDocKey = split[0];
                    String nestedKey = split[split.length-1];
                    Document nestedDoc = doc.get(nestedDocKey, Document.class);
                    if (nestedDoc == null){
                        return;
                    }
                    if (nestedDoc.containsKey(nestedKey) && nestedDoc.get(nestedKey).equals(value)){
                        return;
                    }
                    nestedDoc.append(nestedKey, value);
                    newValue = new Document(nestedDocKey, nestedDoc);
                }

                Bson updateOperation = new Document ("$set", newValue);
                dataset.getMongoCollection().updateOne(new Document("world", doc.getString("world")), updateOperation); //Update Doc
            }
        }.runTaskAsynchronously(Core.getInstance());
    }

    public static void setMany(Player p, DatabaseDataset dataset, HashMap<String, Object> map, String successMessage, boolean ignoreSWM){
        Document doc = isWorldValid(p, dataset, ignoreSWM);
        if (doc == null){
            return;
        }
        if (map == null || map.isEmpty()){
            return;
        }

        p.sendMessage(successMessage);

        //Mongo
        new BukkitRunnable() {
            public void run(){

                Document updateOperation = new Document();

                Document setDocument = new Document();
                for (String key : map.keySet()){
                    //Not Nested
                    if (!key.contains(".")){
                        if (doc.containsKey(key) && doc.get(key).equals(map.get(key))){
                            continue;
                        }
                        setDocument.append(key, map.get(key));
                    }
                    //Nested
                    else{
                        String[] split = key.split("\\.");
                        String nestedDocKey = split[0];
                        String nestedKey = split[split.length-1];
                        Document nestedDoc = doc.get(nestedDocKey, Document.class);
                        if (nestedDoc == null){
                            continue;
                        }
                        if (nestedDoc.containsKey(nestedKey) && nestedDoc.get(nestedKey).equals(map.get(key))){
                            continue;
                        }
                        nestedDoc.append(nestedKey, map.get(key));
                        setDocument.append(nestedDocKey, nestedDoc);
                    }
                }

                updateOperation.append("$set", setDocument);
                dataset.getMongoCollection().updateOne(new Document("world", doc.getString("world")), updateOperation); //Update Doc
            }
        }.runTaskAsynchronously(Core.getInstance());
    }

    public static void unset(Player p, DatabaseDataset dataset, String key, String successMessage, boolean ignoreSWM){
        Document doc = isWorldValid(p, dataset, ignoreSWM);
        if (doc == null){
            return;
        }

        if (!doc.containsKey(key)) {
            p.sendMessage(ChatColor.RED+"Failed to remove value, it doesn't exist!");
            return;
        }

        p.sendMessage(successMessage);

        //Mongo
        new BukkitRunnable() {
            public void run(){
                Document updateDoc = new Document(doc);
                updateDoc.remove(key);
                Bson updateOperation = new Document ("$set", updateDoc);
                dataset.getMongoCollection().updateOne(doc, updateOperation); //Update Doc
            }
        }.runTaskAsynchronously(Core.getInstance());
    }

    public static void unsetMany(Player p, DatabaseDataset dataset, Collection<String> keys, String successMessage, boolean ignoreSWM){
        Document doc = isWorldValid(p, dataset, ignoreSWM);
        if (doc == null){
            return;
        }


        p.sendMessage(successMessage);

        //Mongo
        new BukkitRunnable() {
            public void run(){
                Document updateDoc = new Document(doc);
                for (String key : keys){
                    updateDoc.remove(key);
                }
                Bson updateOperation = new Document ("$set", updateDoc);
                dataset.getMongoCollection().updateOne(doc, updateOperation); //Update Doc
            }
        }.runTaskAsynchronously(Core.getInstance());
    }



    public static void addListValue(Player p, DatabaseDataset dataset, String key, Object value, boolean ignoreSWM){
        Document doc = isWorldValid(p, dataset, ignoreSWM);
        if (doc == null) return;

        if (!doc.containsKey(key)){
            p.sendMessage(ChatColor.RED+"A list with that name does not exist!");
            return;
        }

        final Location loc = p.getLocation();
        List<Object> list = new ArrayList<>(doc.getList(key, Object.class));
        list.add(value);
        p.sendMessage(Component.text(ChatColor.GREEN+"Successfully added value!"));

        //Mongo
        new BukkitRunnable() {

            public void run(){
                Document newValue = new Document(key, list); //New Values
                Bson updateOperation = new Document ("$set", newValue);
                dataset.getMongoCollection().updateOne(doc, updateOperation); //Update Doc
            }
        }.runTaskAsynchronously(Core.getInstance());
    }


    public static void removeListValue(Player p, DatabaseDataset dataset, int index, String key, boolean ignoreSWM){
        Document doc = isWorldValid(p, dataset, ignoreSWM);
        if (doc == null) return;

        if (!doc.containsKey(key)){
            p.sendMessage(ChatColor.RED+"A list with that name does not exist!");
            return;
        }

        if (index < 0){
            p.sendMessage(ChatColor.RED+"Value with that ID not found!");
            return;
        }
        List<Object> list = new ArrayList<>(doc.getList(key, Object.class));
        if (index > list.size()){
            p.sendMessage(ChatColor.RED+"Value with that ID not found!");
        }
        list.remove(index);
        p.sendMessage(Component.text(ChatColor.YELLOW+"Value successfully removed!"));

        //Mongo
        new BukkitRunnable() {
            public void run(){
                Document newValue = new Document(key, list); //New Values
                Bson updateOperation = new Document ("$set", newValue);
                dataset.getMongoCollection().updateOne(new Document("world", p.getWorld().getName()), updateOperation); //Update Doc

            }
        }.runTaskAsynchronously(Core.getInstance());
    }

    public static <T> void listValues(Player p, DatabaseDataset dataset, int page, String listName, String listDisplayName, Class<T> clazz, boolean ignoreSWM){
        Document doc = isWorldValid(p, dataset, ignoreSWM);
        if (doc == null) return;

        if (!doc.containsKey(listName)){
            p.sendMessage(ChatColor.RED+"A list with that name does not exist!");
            return;
        }

        List<T> list = doc.getList(listName, clazz);
        int iteration = 0;
        int endNumber = (7*page);
        int startNumber = endNumber-7;
        p.sendMessage(ChatColor.AQUA+"-------------="+listDisplayName+"=------------");

        for (T obj : list){
            if (iteration >= endNumber){
                return;
            }
            if (iteration >= startNumber){
                p.sendMessage(ChatColor.AQUA+"ID: "+list.indexOf(obj)+" "+ChatColor.YELLOW+obj.toString());

            }
            iteration++;
        }
        if (iteration == 0){
            p.sendMessage("- "+ChatColor.YELLOW+"There are not any values!");
        }
        p.sendMessage(ChatColor.GRAY+""+ChatColor.BOLD+"-----------="+ ChatColor.GOLD+"Page "+page+ChatColor.GRAY+""+ChatColor.BOLD+"=-----------");
    }



    public static void addLocation(Player p, DatabaseDataset dataset, String listName, boolean ignoreSWM){
        Document doc = isWorldValid(p, dataset, ignoreSWM);
        if (doc == null) return;

        if (!doc.containsKey(listName)){
            p.sendMessage(ChatColor.RED+"A list with that name does not exist!");
            return;
        }

        final Location loc = p.getLocation();
        List<ArrayList> list = new ArrayList<>(doc.getList(listName, ArrayList.class));
        list.add(CoreMongoUtils.locationToList(loc));
            p.sendMessage(Component.text(ChatColor.GREEN+"Location successfully added to Location Category! "+ChatColor.YELLOW+"("+listName+")"));

        //Mongo
        new BukkitRunnable() {

            public void run(){
                Document newValue = new Document(listName, list); //New Values
                Bson updateOperation = new Document ("$set", newValue);
                dataset.getMongoCollection().updateOne(doc, updateOperation); //Update Doc
            }
        }.runTaskAsynchronously(Core.getInstance());
    }

    public static void removeLocation(Player p, DatabaseDataset dataset, int index, String listName, boolean ignoreSWM){
        Document doc = isWorldValid(p, dataset, ignoreSWM);
        if (doc == null) return;

        if (!doc.containsKey(listName)){
            p.sendMessage(ChatColor.RED+"A list with that name does not exist!");
            return;
        }


        if (index < 0){
            p.sendMessage(ChatColor.RED+"Location/Location Bounds with that ID not found!");
            return;
        }
        List<ArrayList> list = new ArrayList<>(doc.getList(listName, ArrayList.class));
        if (index > list.size()){
            p.sendMessage(ChatColor.RED+"Location/Location Bounds with that ID not found!");
        }
        list.remove(index);
        p.sendMessage(Component.text(ChatColor.YELLOW+"Location/Location Bounds successfully removed!"));

        //Mongo
        new BukkitRunnable() {
            public void run(){
                Document newValue = new Document(listName, list); //New Values
                Bson updateOperation = new Document ("$set", newValue);
                dataset.getMongoCollection().updateOne(doc, updateOperation); //Update Doc

            }
        }.runTaskAsynchronously(Core.getInstance());
    }

    public static void addLocationToNestedDocument(Player p, DatabaseDataset dataset, String nestedDocumentKey, String listName, String failMessage, boolean ignoreSWM){
        Document doc = isWorldValid(p, dataset, ignoreSWM);
        if (doc == null) return;

        if (!doc.containsKey(nestedDocumentKey)){
            p.sendMessage(failMessage);
            return;
        }
        Document nestedDoc = doc.get(nestedDocumentKey, Document.class);

        final Location loc = p.getLocation();
        List<ArrayList> list = new ArrayList<>(nestedDoc.getList(listName, ArrayList.class));
        list.add(CoreMongoUtils.locationToList(loc));
        p.sendMessage(Component.text(ChatColor.GREEN+"Location successfully added! "));

        new BukkitRunnable() {
            public void run(){
                Document newValue = new Document(nestedDocumentKey+"."+listName, list); //New Values
                Bson updateOperation = new Document ("$set", newValue);
                dataset.getMongoCollection().updateOne(doc, updateOperation); //Update Doc
            }
        }.runTaskAsynchronously(Core.getInstance());
    }


    public static void removeLocationFromNestedDocument(Player p, DatabaseDataset dataset, int index, String nestedDocumentKey, String listName, boolean ignoreSWM){
        Document doc = isWorldValid(p, dataset, ignoreSWM);
        if (doc == null) return;

        if (!doc.containsKey(nestedDocumentKey)){
            p.sendMessage(ChatColor.RED+"A list with that name does not exist!");
            return;
        }

        Document nestedDoc = doc.get(nestedDocumentKey, Document.class);


        if (index < 0){
            p.sendMessage(ChatColor.RED+"Location/Location Bounds with that ID not found!");
            return;
        }
        List<ArrayList> list = new ArrayList<>(nestedDoc.getList(listName, ArrayList.class));
        if (index > list.size()){
            p.sendMessage(ChatColor.RED+"Location/Location Bounds with that ID not found!");
        }
        list.remove(index);
        p.sendMessage(Component.text(ChatColor.YELLOW+"Location/Location Bounds successfully removed!"));

        new BukkitRunnable() {
            public void run(){
                Document newValue = new Document(nestedDocumentKey+"."+listName, list); //New Values
                Bson updateOperation = new Document ("$set", newValue);
                dataset.getMongoCollection().updateOne(doc, updateOperation); //Update Doc
            }
        }.runTaskAsynchronously(Core.getInstance());
    }


    public static void addLocationBounds(Player p, DatabaseDataset dataset, String listName, boolean ignoreSWM){
        Document doc = isWorldValid(p, dataset, ignoreSWM);
        if (doc == null) return;

        if (!doc.containsKey(listName)){
            p.sendMessage(ChatColor.RED+"A list with that name does not exist!");
            return;
        }

        final Location[] bounds = RegionUtils.getPlayerSelection(p);
        if (bounds.length == 0){
            p.sendMessage(ChatColor.RED+"You do not have a valid WorldEdit Selection!");
            return;
        }
        List<ArrayList> list = new ArrayList<>(doc.getList(listName, ArrayList.class));
        list.add(CoreMongoUtils.locationBoundsToList(bounds[0], bounds[1]));
        p.sendMessage(Component.text(ChatColor.GREEN+"Location Bounds successfully added to Location Category! "+ChatColor.YELLOW+"("+listName+")"));

    //Mongo
        String finalListName = listName;
        new BukkitRunnable() {

            public void run(){
                Document newValue = new Document(finalListName, list); //New Values
                Bson updateOperation = new Document ("$set", newValue);
                dataset.getMongoCollection().updateOne(doc, updateOperation); //Update Doc
            }
        }.runTaskAsynchronously(Core.getInstance());
    }




    public static void listLocations(Player p, DatabaseDataset dataset, int page, String listName, String listDisplayName, LocationType locationType, boolean ignoreSWM){
        Document doc = isWorldValid(p, dataset, ignoreSWM);
        if (doc == null) return;

        if (!doc.containsKey(listName)){
            p.sendMessage(ChatColor.RED+"A location category with that name does not exist!");
            return;
        }

        List<ArrayList> list = doc.getList(listName, ArrayList.class);
        int iteration = 0;
        int endNumber = (7*page);
        int startNumber = endNumber-7;
        p.sendMessage(ChatColor.AQUA+"-------------="+listDisplayName+"=------------");

        for (ArrayList nestedList : list){
            if (iteration >= endNumber){
                return;
            }
            if (iteration >= startNumber){
                if (nestedList.size() == 6  && (locationType == LocationType.BOTH || locationType == LocationType.LOCATION)){
                    Location loc = CoreMongoUtils.listToLocation(nestedList);
                    Component coords = Component.text("ID: ", NamedTextColor.AQUA).append(Component.text(list.indexOf(nestedList)+" "+ChatColor.YELLOW+"X: "+loc.getX()+", Y: "+loc.getY()+", Z:"+loc.getZ(), NamedTextColor.YELLOW));
                    Component teleport = Component.text(" [TELEPORT]", NamedTextColor.GREEN).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/tp @p "+loc.x()+" "+loc.y()+" "+loc.z()));
                    p.sendMessage(coords.append(teleport));
                }
                else if (nestedList.size() == 12 && (locationType == LocationType.BOTH || locationType == LocationType.LOCATIONBOUNDS)){
                    Location[] bounds = CoreMongoUtils.listToLocationBounds(nestedList);
                    if (bounds == null){
                        continue;
                    }
                    p.sendMessage(ChatColor.AQUA+"ID: "+list.indexOf(nestedList));
                    Location loc1 = bounds[0];
                    Location loc2 = bounds[1];
                    p.sendMessage(ChatColor.GRAY+"  - Bound 1: "+ChatColor.YELLOW+"X: "+loc1.getX()+", Y: "+loc1.getY()+", Z:"+loc1.getZ());
                    p.sendMessage(ChatColor.GRAY+"  - Bound 2: "+ChatColor.YELLOW+"X: "+loc2.getX()+", Y: "+loc2.getY()+", Z:"+loc2.getZ());
                }

            }
            iteration++;
        }
        if (iteration == 0){
            p.sendMessage("- "+ChatColor.YELLOW+"There are not any locations!");
        }
        p.sendMessage(ChatColor.GRAY+""+ChatColor.BOLD+"-----------="+ ChatColor.GOLD+"Page "+page+ChatColor.GRAY+""+ChatColor.BOLD+"=-----------");
    }

    public static void listLocationsFromNestedDocument(Player p, DatabaseDataset dataset, int page, String nestedDocumentKey, String listName, String listDisplayName, String failMessage, LocationType locationType, boolean ignoreSWM){
        Document doc = isWorldValid(p, dataset, ignoreSWM);
        if (doc == null) return;

        if (!doc.containsKey(nestedDocumentKey)){
            p.sendMessage(failMessage);
            return;
        }

        Document nestedDoc = doc.get(nestedDocumentKey, Document.class);

        List<ArrayList> list = nestedDoc.getList(listName, ArrayList.class);
        int iteration = 0;
        int endNumber = (7*page);
        int startNumber = endNumber-7;
        p.sendMessage(ChatColor.AQUA+"-------------="+listDisplayName+"=------------");

        for (ArrayList nestedList : list){
            if (iteration >= endNumber){
                return;
            }
            if (iteration >= startNumber){
                if (nestedList.size() == 6  && (locationType == LocationType.BOTH || locationType == LocationType.LOCATION)){
                    Location loc = CoreMongoUtils.listToLocation(nestedList);
                    Component coords = Component.text("ID: ", NamedTextColor.AQUA).append(Component.text(list.indexOf(nestedList)+" "+ChatColor.YELLOW+"X: "+loc.getX()+", Y: "+loc.getY()+", Z:"+loc.getZ(), NamedTextColor.YELLOW));
                    Component teleport = Component.text(" [TELEPORT]", NamedTextColor.GREEN).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/tp @p "+loc.x()+" "+loc.y()+" "+loc.z()));
                    p.sendMessage(coords.append(teleport));
                }
                else if (nestedList.size() == 12 && (locationType == LocationType.BOTH || locationType == LocationType.LOCATIONBOUNDS)){
                    Location[] bounds = CoreMongoUtils.listToLocationBounds(nestedList);
                    if (bounds == null){
                        continue;
                    }
                    p.sendMessage(ChatColor.AQUA+"ID: "+list.indexOf(nestedList));
                    Location loc1 = bounds[0];
                    Location loc2 = bounds[1];
                    p.sendMessage(ChatColor.GRAY+"  - Bound 1: "+ChatColor.YELLOW+"X: "+loc1.getX()+", Y: "+loc1.getY()+", Z:"+loc1.getZ());
                    p.sendMessage(ChatColor.GRAY+"  - Bound 2: "+ChatColor.YELLOW+"X: "+loc2.getX()+", Y: "+loc2.getY()+", Z:"+loc2.getZ());
                }

            }
            iteration++;
        }
        if (iteration == 0){
            p.sendMessage("- "+ChatColor.YELLOW+"There are not any locations!");
        }
        p.sendMessage(ChatColor.GRAY+""+ChatColor.BOLD+"-----------="+ ChatColor.GOLD+"Page "+page+ChatColor.GRAY+""+ChatColor.BOLD+"=-----------");
    }



    public static void listPrefixedKeys(Player p, DatabaseDataset dataset, @NotNull String prefix, @NotNull String displayName, boolean ignoreSWM){
        Document doc = isWorldValid(p, dataset, ignoreSWM);
        if (doc == null) return;

        p.sendMessage(ChatColor.AQUA+"----------="+displayName+"=----------");
        boolean contains = false;
        for (String key : doc.keySet()){
            if (key.contains(prefix)){
                contains = true;
                p.sendMessage(ChatColor.GRAY+"- "+ChatColor.YELLOW+key.replace(prefix, ""));
            }
        }
        if (!contains){
            p.sendMessage(ChatColor.RED+"Failed to find any values!");
        }
    }


    public static void listWorlds(Player p, DatabaseDataset dataset, int page){
        int iteration = 0;
        int endNumber = (7*page);
        int startNumber = endNumber-7;
        p.sendMessage(Component.text("----------=Minigame World List=----------", NamedTextColor.AQUA));
        for (Document doc : dataset.getMongoCollection().find()){
            if (iteration >= endNumber){
                return;
            }
            if (iteration >= startNumber){
                p.sendMessage(Component.text("- "+doc.getString("world"), NamedTextColor.YELLOW));
            }
            iteration++;
        }
        if (iteration == 0){
            p.sendMessage(Component.text("- There are not any SWM worlds registered!", NamedTextColor.YELLOW));
        }
        p.sendMessage(ChatColor.GRAY+""+ChatColor.BOLD+"-----------="+ ChatColor.GOLD+"Page "+page+ChatColor.GRAY+""+ChatColor.BOLD+"=-----------");
    }

    public static void showInfo(Player p, DatabaseDataset dataset, boolean ignoreSWM){
        Document doc = isWorldValid(p, dataset, ignoreSWM);
        if (doc == null) {
            p.sendMessage(Component.text("This SWM world is not registered!", NamedTextColor.RED));
            p.sendMessage(Component.text("Do '/sgs world register' to register this SWM world", NamedTextColor.GRAY, TextDecoration.ITALIC));
            return;
        }

        String mapName = doc.getString("mapName");
        String mapCreator = doc.getString("mapCreator");

        p.sendMessage(ChatColor.AQUA+"----------=Minigame Setup=----------");
        p.sendMessage("World: "+ChatColor.YELLOW+p.getWorld().getName());
        p.sendMessage("Map Name: "+ChatColor.YELLOW+mapName);
        p.sendMessage("Map Creator: "+ChatColor.YELLOW+mapCreator);

        if (doc.getBoolean("isPlaytestOnly")){
            p.sendMessage("Playtest Status: "+ChatColor.YELLOW+"PLAYTEST ONLY");
        }
        else{
            p.sendMessage("Playtest Status: "+ChatColor.RED+"LIVE");
        }
    }

    private static Document isWorldValid(World world, DatabaseDataset dataset, boolean ignoreSWM){
        if (!ignoreSWM){
            if (!SlimeUtils.isSlimeWorld(world.getName())){
                return null;
            }
        }
        return MongoUtils.getDocument(dataset.getMongoCollection(), "world", world.getName());
    }

    public static Document isWorldValid(Player player, DatabaseDataset dataset, boolean ignoreSWM){
        String worldName = player.getWorld().getName();
        if (!ignoreSWM){
            if (!SlimeUtils.isSlimeWorld(worldName)){
                player.sendMessage(Component.text("You can only do this in SWM worlds!", NamedTextColor.RED));
                return null;
            }
        }

        return isWorldRegistered(player, dataset, true, worldName);
    }

    private static Document isWorldRegistered(Player player, DatabaseDataset dataset, boolean sendUnregisteredMessage){
        return isWorldRegistered(player, dataset, sendUnregisteredMessage, player.getWorld().getName());
    }

    private static Document isWorldRegistered(Player player, DatabaseDataset dataset, boolean sendUnregisteredMessage, String worldName){
        Document doc = MongoUtils.getDocument(dataset.getMongoCollection(), "world", worldName);
        if (doc == null && sendUnregisteredMessage) {
            player.sendMessage(Component.text("This world is not registered!", NamedTextColor.RED));
        }
        return doc;
    }



    public enum LocationType{
        BOTH,
        LOCATION,
        LOCATIONBOUNDS;
    }
}*/
