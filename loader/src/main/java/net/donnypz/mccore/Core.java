package net.donnypz.mccore;

import net.donnypz.mccore.command.*;
import net.donnypz.mccore.cosmetics.CosmeticRegistry;
import net.donnypz.mccore.database.MongoUtils;
import net.donnypz.mccore.listener.*;
import net.donnypz.mccore.minigame.arena.ArenaManager;
import net.donnypz.mccore.utils.misc.WorldUtils;
import net.donnypz.mccore.utils.misc.RankUtils;
import net.donnypz.mccore.utils.slime.SlimeUtils;
import net.donnypz.mccore.utils.ui.BossBarUtils;
import net.donnypz.mccore.version.CoreAPI;
import net.donnypz.mccore.version.VersionHandlerRegistry;
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

    static boolean isNBAPIInstalled;
    public static boolean isLPAPIInstalled;

    @Override
    public void onEnable() {
        isNBAPIInstalled = Bukkit.getPluginManager().isPluginEnabled("NoteBlockAPI");
        isLPAPIInstalled = Bukkit.getPluginManager().isPluginEnabled("LuckPerms");

        if (!VersionHandlerRegistry.init(this)){
            Bukkit.getLogger().severe("Failed to initiate MCCore!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        //Load Config
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        VersionHandlerRegistry.updateConfig();

        this.loadImplementations();

        //Register Commands
        this.registerCommands();

        //Register Event Listeners
        this.registerListeners();

        this.getServer().getConsoleSender().sendMessage(Component.text("MCCore Enabled", NamedTextColor.GREEN));
    }

    public void loadImplementations(){
        if (isLPAPIInstalled) RankUtils.registerLuckPerms();
    }

    @EventHandler
    void onServerLoad(ServerLoadEvent event){
        if (event.getType() == ServerLoadEvent.LoadType.RELOAD) return;

        //Connect to MongoDB using connectionString from config.yml
        if (CoreAPI.getConfigOptions().connectToMongo) {
            MongoUtils.createConnection(CoreAPI.getConfigOptions().connectionString);
        }

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
        if (CoreAPI.getConfigOptions().isMinigameEnabled){
            if (w.equals(CoreAPI.getConfigOptions().waitingWorld)){
                if (!CoreAPI.getConfigOptions().waitingWorldLimited){
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
