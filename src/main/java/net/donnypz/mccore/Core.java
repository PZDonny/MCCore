package net.donnypz.mccore;

import net.donnypz.mccore.commands.*;
import net.donnypz.mccore.cosmetics.CosmeticRegistry;
import net.donnypz.mccore.database.MongoUtils;
import net.donnypz.mccore.listeners.*;
import net.donnypz.mccore.minigame.arena.ArenaManager;
import net.donnypz.mccore.utils.misc.WorldUtils;
import net.donnypz.mccore.utils.inventory.gui.Listener_InventoryClick;
import net.donnypz.mccore.utils.inventory.gui.Listener_InventoryClose;
import net.donnypz.mccore.utils.item.Listener_Consume;
import net.donnypz.mccore.utils.item.Listener_ItemClick;
import net.donnypz.mccore.utils.item.Listener_ItemDrop;
import net.donnypz.mccore.utils.misc.RankUtils;
import net.donnypz.mccore.utils.misc.SlimeUtils;
import net.donnypz.mccore.utils.ui.BossBarUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Core extends JavaPlugin implements Listener {
    static Core instance;

    //Config Variables
    static boolean projectileRandomness;

    static boolean connectToMongo;
    static String connectionString;

    static boolean hideConnectionMessage;
    static boolean isChatCooldownEnabled;

    static boolean isMinigameEnabled;
    static World waitingWorld;
    static boolean waitingWorldLimited;

    //Dependencies
    static boolean isSlimeInstalled;
    static boolean isNBAPIInstalled;
    static boolean isLPAPIInstalled;

    @Override
    public void onEnable() {
        instance = this;

        //Register Optional Dependencies
        isNBAPIInstalled = Bukkit.getPluginManager().isPluginEnabled("NoteBlockAPI");
        isSlimeInstalled = SlimeUtils.registerSlime();
        isLPAPIInstalled = Bukkit.getPluginManager().isPluginEnabled("LuckPerms");
        if (isLPAPIInstalled) RankUtils.registerLuckPerms();

        //Load Config
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        ConfigLoader.loadConfig();

        //Register Commands
        this.registerCommands();

        //Register Event Listeners
        this.registerListeners();

        this.getServer().getConsoleSender().sendMessage(Component.text("MCCore Enabled", NamedTextColor.GREEN));
    }

    @EventHandler
    void onServerLoad(ServerLoadEvent event){
        if (event.getType() == ServerLoadEvent.LoadType.RELOAD) return;

        //Connect to MongoDB using connectionString from config.yml
        if (Core.isMongoEnabled()) MongoUtils.createConnection(connectionString);

        for (World w : Bukkit.getWorlds()) templateWorldSetup(w);

        //Reset Bossbars (UI)
        BossBarUtils.removeAllBossBars();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onServerLoadFinished(ServerLoadEvent event){
        CosmeticRegistry.loadRegistries();
    }

    @Override
    public void onDisable() { //Close MongoDB Connections
        MongoUtils.disconnect();
    }

    public static Core getInstance(){
        return instance;
    }

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

    public static boolean isMongoEnabled() {
        return connectToMongo;
    }

    private void registerListeners(){
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
    }

    private void registerCommands(){
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
        getCommand("corereload").setExecutor(new ReloadCommand());
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
}
