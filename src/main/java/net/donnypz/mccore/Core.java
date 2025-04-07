package net.donnypz.mccore;

import net.donnypz.mccore.commands.*;
import net.donnypz.mccore.cosmetics.CosmeticRegistry;
import net.donnypz.mccore.listeners.*;
import net.donnypz.mccore.minigame.ArenaItemActionRegistry;
import net.donnypz.mccore.minigame.arenaManager.ArenaManager;
import net.donnypz.mccore.packets.CoreProtocolPacketListener;
import net.donnypz.mccore.utils.WorldUtils;
import net.donnypz.mccore.utils.inventory.gui.Listener_InventoryClick;
import net.donnypz.mccore.utils.inventory.gui.Listener_InventoryClose;
import net.donnypz.mccore.utils.inventory.item.Listener_Consume;
import net.donnypz.mccore.utils.inventory.item.Listener_ItemClick;
import net.donnypz.mccore.utils.inventory.item.Listener_ItemDrop;
import net.donnypz.mccore.utils.RankUtils;
import net.donnypz.mccore.utils.SlimeUtils;
import net.donnypz.mccore.utils.ui.BossBarUtils;
import net.donnypz.playerdbutils.database.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;


public final class Core extends JavaPlugin implements Listener {
    private FileConfiguration config = getConfig();
    static Core instance;
    static boolean projectileRandomness;

//Mongo
    static boolean connectToMongo;
    static String connectionString;

    static boolean hideConnectionMessage = true;
    static boolean isChatCooldownEnabled;


    static boolean isMinigameEnabled = false;
    static World waitingWorld;
    static boolean waitingWorldLimited;
    static boolean isSlimeInstalled = false;
    static boolean isNBAPIInstalled = true;
    static boolean isLPAPIInstalled = true;
    static boolean isProtocolLibInstalled = true;

    @Override
    public void onEnable() {
        instance = this;

        //Register Optional Dependencies
        isNBAPIInstalled = Bukkit.getPluginManager().isPluginEnabled("NoteBlockAPI");
        isProtocolLibInstalled = Bukkit.getPluginManager().isPluginEnabled("ProtocolLib");
        isSlimeInstalled = SlimeUtils.registerSlime();
        isLPAPIInstalled = Bukkit.getPluginManager().isPluginEnabled("LuckPerms");
        if (isLPAPIInstalled){
            RankUtils.registerLuckPerms();
        }

        //Load Config File
        config.options().copyDefaults(true);
        saveDefaultConfig();
        ConfigLoader.loadConfig();

        //Commands
        GamemodeCommand gmCommand = new GamemodeCommand();
        getCommand("gmc").setExecutor(gmCommand);
        getCommand("gms").setExecutor(gmCommand);
        getCommand("gma").setExecutor(gmCommand);
        getCommand("gmsp").setExecutor(gmCommand);

        DayCycle dcCommand = new DayCycle();
        getCommand("day").setExecutor(dcCommand);

        getCommand("noon").setExecutor(dcCommand);
        getCommand("night").setExecutor(dcCommand);
        getCommand("midnight").setExecutor(dcCommand);
        getCommand("fly").setExecutor(new Fly());
        getCommand("speed").setExecutor(new Speed());
        getCommand("cc").setExecutor(new ClearChat());
        getCommand("ci").setExecutor(new ClearInv());
        getCommand("previewborder").setExecutor(new PreviewBorder());
        getCommand("centerplayer").setExecutor(new CenterPlayer());
        getCommand("corereload").setExecutor(this);

        //Register Event Listeners
        getServer().getPluginManager().registerEvents(new MinigameListener(), this);
        getServer().getPluginManager().registerEvents(new DamageListener(), this);
        getServer().getPluginManager().registerEvents(new ProjectileRandomnessListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerSpectatorListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new Listener_InventoryClick(), this);
        getServer().getPluginManager().registerEvents(new Listener_InventoryClose(), this);
        getServer().getPluginManager().registerEvents(new Listener_ItemClick(), this);
        getServer().getPluginManager().registerEvents(new Listener_ItemDrop(), this);
        getServer().getPluginManager().registerEvents(new Listener_Consume(), this);
        getServer().getPluginManager().registerEvents(this, this);

        //Register Item Actions
        ArenaItemActionRegistry.register();

        getServer().getConsoleSender().sendMessage(Component.text("MCCore Enabled", NamedTextColor.GREEN));
    }

    @Override
    public void onDisable() {
        MongoUtils.disconnect();
    }


    public static Core getInstance(){
        return instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player && !(sender.hasPermission("net.donnypz.mccore.reload"))) {
            sender.sendMessage(Component.text("You do not have permission to do this command!", NamedTextColor.RED));
            return true;
        }
        if (args.length < 1){
            sender.sendMessage(Component.text("Incorrect Usage! /corereload <mongo  | config>", NamedTextColor.RED));
            return true;
        }
        String arg = args[0];
        if (arg.equals("mongo")){
            if (Core.isMongoEnabled()){
                sender.sendMessage(Component.text("Attempting to reconnect to MongoDB Database", NamedTextColor.YELLOW));
                MongoUtils.createConnection(connectionString);
            }
            else{
                sender.sendMessage(Component.text("MongoDB has not been enabled in the config! Enable it then run \"/corereload config\"", NamedTextColor.RED));
            }
        }
        else if (arg.equals("config")){
            reloadConfig();
            config = getConfig();
            ConfigLoader.loadConfig();
            sender.sendMessage(Component.text("MCCore Config successfully reloaded!", NamedTextColor.GREEN));
            if (isMongoEnabled()){
                MongoUtils.disconnect();
            }
        }
        else{
            sender.sendMessage(Component.text("Incorrect Usage! /corereload <mongo | config>", NamedTextColor.RED));
        }
        return true;
    }

    //Getters
    public static boolean isMinigameEnabled() {
        return isMinigameEnabled;
    }

    public World getMinigameWaitingWorld(){
        return waitingWorld;
    }

    public static boolean isWaitingWorldLimited(){
        return waitingWorldLimited;
    }
    public static boolean isConnectionMessageHidden(){
        return hideConnectionMessage;
    }

    public static boolean isProjectileRandomness() {
        return projectileRandomness;
    }

    public static boolean isSlimeInstalled() {
        return isSlimeInstalled;
    }
    public static boolean isNBAPIInstalled() {
        return isNBAPIInstalled;
    }
    public static boolean isLuckPermsInstalled() {
        return isLPAPIInstalled;
    }
    public static boolean isProtocolLibInstalled(){
        return isProtocolLibInstalled;
    }

    public static boolean isMongoEnabled() {
        return connectToMongo;
    }

    private static void templateWorldSetup(World w){
        if (Core.isMinigameEnabled()){
            if (w.equals(getInstance().getMinigameWaitingWorld())){
                if (!Core.isWaitingWorldLimited()){
                    return;
                }
                WorldUtils.useMinigameGamerules(w);
            }
            else if (SlimeUtils.isSlimeWorld(w.getName())){
                WorldUtils.useMinigameGamerules(w);
            }
            else if (ArenaManager.getArena(w.getName()) != null){
                WorldUtils.useMinigameGamerules(w);
            }
        }
    }

    @EventHandler
    void onServerStart(ServerLoadEvent event){
        if (event.getType() == ServerLoadEvent.LoadType.RELOAD) return;

        //Connect to MongoDB
        if (Core.isMongoEnabled()){
            MongoUtils.createConnection(connectionString);
        }

        //Register Packet Listener
        if (isProtocolLibInstalled) CoreProtocolPacketListener.registerOutgoing();



        for (World w : Bukkit.getWorlds()) templateWorldSetup(w);

        //Reset Bossbars
        BossBarUtils.removeAllBossBars();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onServerStartFinished(ServerLoadEvent event){
        CosmeticRegistry.loadAllRegistries();
    }
}
