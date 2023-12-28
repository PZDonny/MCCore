package MCCore;

import MCCore.commands.*;
import MCCore.commands.minigameCommands.CreatePrivateQueueCommand;
import MCCore.commands.minigameCommands.JoinPrivateQueueCommand;
import MCCore.commands.minigameCommands.PlayCommand;
import MCCore.listeners.*;
import MCCore.minigameAPI.RamThresholdManager;
import MCCore.minigameAPI.arenaManager.ArenaManager;
import MCCore.sockets.Client;
import MCCore.utils.InventoryUtils.GUIItem_InventoryClick;
import MCCore.utils.SlimeTools;
import dev.iiahmed.disguise.DisguiseManager;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URL;


public final class Core extends JavaPlugin {
    private FileConfiguration config = getConfig();

    private static Core instance;
    public static boolean projectileRandomness;
    private static boolean connectToMongo;
    private static String connectionString;

    private static boolean isMinigameEnabled = false;

    private static boolean isPlaytest;

    private static Client client = null;

    private static int port;

    private static String dataProxyIP;

    private static World waitingWorld;

    private static String serverID;
    private static boolean isDataProxyAllowed;

    private static boolean isLobbyServer;

    private static boolean isSlimeInstalled = true;
    private static boolean isNBAPIInstalled = true;

    private static String mainDatabaseName;
    private static String playtestDatabaseName;
    private static String minigameDatabaseName;

    private static int ramThreshold = -1;
    private static int allocatedOffset = 512;

    private static RamThresholdManager ramManager;

    public static final String prefix = "["+ ChatColor.GREEN+"M"+ChatColor.WHITE+"C"+ChatColor.RED+" Core"+ChatColor.WHITE+"] ";

    private static int networkPlayerCount = 0;

    @Override
    public void onEnable() {

        //LuckPerms
        if (!Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
            getLogger().severe("MCCore cannot utilize the LuckPerms Utilities!");
            getLogger().severe("*** LuckPerms is not installed or not enabled. ***");
        }

        //WorldGuard
        if (!Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            getLogger().severe("MCCore cannot utilize the WorldTools Utilities!");
            getLogger().severe("*** WorldGuard is not installed or not enabled. ***");
        }

        //NoteBlockAPI
        if (!Bukkit.getPluginManager().isPluginEnabled("NoteBlockAPI")) {
            getLogger().severe("MCCore cannot utilize the NoteBlockAPI Utilities!");
            getLogger().severe("*** NoteBlockAPI is not installed or not enabled. ***");
            isNBAPIInstalled = false;
        }

        //SlimeWorldManager
        if (!Bukkit.getPluginManager().isPluginEnabled("SlimeWorldManager")) {
            getLogger().severe("MCCore cannot fully utilize the MinigameAPI!");
            getLogger().severe("*** SlimeWorldManager is not installed or not enabled. ***");
            isSlimeInstalled = false;
        }
        else {
            if (!SlimeTools.setSlimeVariables()) isSlimeInstalled = false;
        }


        instance = this;
        new PluginMessage();


    //Slime World Variables

    //Configuartion
        config.options().copyDefaults(true);
        saveDefaultConfig();
        setConfigVariables();
        getServer().getConsoleSender().sendMessage(prefix + ChatColor.GREEN + "ENABLED");

        DisguiseManager.setPlugin(this);

        //Commands
        getCommand("gmc").setExecutor(new GamemodeCommand());
        getCommand("gms").setExecutor(new GamemodeCommand());
        getCommand("gma").setExecutor(new GamemodeCommand());
        getCommand("gmsp").setExecutor(new GamemodeCommand());
        getCommand("fly").setExecutor(new Fly());
        getCommand("speed").setExecutor(new Speed());
        getCommand("cc").setExecutor(new ClearChat());
        getCommand("ci").setExecutor(new ClearInv());
        getCommand("day").setExecutor(new DayCycle());
        getCommand("noon").setExecutor(new DayCycle());
        getCommand("night").setExecutor(new DayCycle());
        getCommand("midnight").setExecutor(new DayCycle());
        getCommand("corereload").setExecutor(this);
        getCommand("play").setExecutor(new PlayCommand());
        getCommand("privatequeue").setExecutor(new CreatePrivateQueueCommand());
        getCommand("joinprivate").setExecutor(new JoinPrivateQueueCommand());
        getCommand("previewborder").setExecutor(new PreviewBorder());
        getCommand("realname").setExecutor(new RealName());
        getCommand("centerplayer").setExecutor(new CenterPlayer());
        getCommand("lobby").setExecutor(new LobbyCommand());
        getCommand("help").setExecutor(new HelpCommand());

        //Events
        getServer().getPluginManager().registerEvents(new ProjectileRandomness(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteract(), this);
        getServer().getPluginManager().registerEvents(new JoinQuit(), this);
        getServer().getPluginManager().registerEvents(new Chat(), this);
        getServer().getPluginManager().registerEvents(new PlayerDropItem(), this);
        getServer().getPluginManager().registerEvents(new ServerStart(), this);
        getServer().getPluginManager().registerEvents(new ArrowPickup(), this);
        getServer().getPluginManager().registerEvents(new Damage(), this);
        getServer().getPluginManager().registerEvents(new WorldLoad(), this);
        getServer().getPluginManager().registerEvents(new GUIItem_InventoryClick(), this);
        getServer().getPluginManager().registerEvents(new InventoryClose(), this);
        getServer().getPluginManager().registerEvents(new ChunkUnload(), this);
        getServer().getPluginManager().registerEvents(new WorldChange(), this);
        getServer().getPluginManager().registerEvents(new Sneak(), this);
        getServer().getPluginManager().registerEvents(new MCCore.listeners.Command(), this);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "mccore:lobby");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "mccore:lobbydequeue");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "mccore:connect");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "mccore:partychat");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "mccore:minigameapi");

        this.getServer().getMessenger().registerIncomingPluginChannel(this, "mccore:minigameapi", PluginMessage.getInstance());
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "mccore:changechat", PluginMessage.getInstance());

        setGamerulesForSlime();

        if (isDataProxyAllowed){
            setClient(new Client(dataProxyIP, port));
        }
    }

    @Override
    public void onDisable() {
        if (client != null){
            client.disconnect();
        }
    }

    public URL getResourceURL(String resource){
        return getClassLoader().getResource(resource);
    }

    public static int getNetworkPlayerCount() {
        return networkPlayerCount;
    }

    public static void setNetworkPlayerCount(int networkPlayerCount) {
        Core.networkPlayerCount = networkPlayerCount;
    }

    public static Core getInstance(){
        return instance;
    }
    public static String getDataProxyIP(){
        return dataProxyIP;
    }

    public static int getPort() {
        return port;
    }

    public static void setClient(Client newClient){
        client = newClient;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player && !(sender.hasPermission("mccore.reload"))) {
            sender.sendMessage(ChatColor.RED+"You do not have permission to do this command!");
            return true;
        }
        reloadConfig();
        config = getConfig();
        setConfigVariables();
        sender.sendMessage(prefix+ChatColor.GREEN+" Config successfully reloaded!");
        return true;
    }

    private void setConfigVariables(){
        if (ramManager != null){
            ramManager.stop();
        }
        projectileRandomness = getConfig().getBoolean("fixProjectileRandomness");
        connectToMongo = getConfig().getBoolean("mongoDB.enabled");
        mainDatabaseName = getConfig().getString("mongoDB.mainDatabase");
        playtestDatabaseName = getConfig().getString("mongoDB.playtestDatabase");
        minigameDatabaseName = getConfig().getString("mongoDB.minigameDatabase");
        isPlaytest = getConfig().getBoolean("mongoDB.usePlaytestDatabase");
        connectionString = getConfig().getString("mongoDB.connectionString");
        port = getConfig().getInt("dataProxyConnection.port");
        dataProxyIP = getConfig().getString("dataProxyConnection.dataProxyIP");
        serverID = getConfig().getString("dataProxyConnection.spigotServerID");
        isDataProxyAllowed = getConfig().getBoolean("dataProxyConnection.enabled");
        isLobbyServer = getConfig().getBoolean("dataProxyConnection.isLobbyServer");

    //Minigames
        if (isDataProxyAllowed){
            isMinigameEnabled = getConfig().getBoolean("minigames.enabled");
            getServer().getConsoleSender().sendMessage(Component.text(ChatColor.GREEN+"Data proxy connection enabled!"));
        }
        else{
            getServer().getConsoleSender().sendMessage(Component.text(ChatColor.RED+"Data proxy connection disabled!"));
            getServer().getConsoleSender().sendMessage(Component.text(ChatColor.RED+"The MinigameAPI will not be functional due to data proxy connection being disabled!"));
            isMinigameEnabled = false;
        }

        //Enabled
        if (isMinigameEnabled){
            for (World w : Bukkit.getWorlds()){
                w.setKeepSpawnInMemory(false);
            }
            String world = getConfig().getString("minigames.waitingWorld");
            if (world != null && Bukkit.getWorld(world) != null){
                waitingWorld = Bukkit.getWorld(world);
            }
            else{
                waitingWorld = null;
                getServer().getConsoleSender().sendMessage(Component.text(ChatColor.RED+"There was an error using the specified minigame waiting world!"+ChatColor.GOLD+"("+world+")"));
            }

            ramThreshold = getConfig().getInt("minigames.ramThreshold");
            allocatedOffset = getConfig().getInt("minigames.allocatedOffset");
            if (ramThreshold > 0){
                new Thread(new RamThresholdManager(ramThreshold)).start();
            }
            else if (ramThreshold == 0){
                int allocated = (int) (Runtime.getRuntime().maxMemory() / (1024*1024));
                new Thread(new RamThresholdManager(allocated-allocatedOffset)).start();
            }

        }

        //Disabled
        else{
            waitingWorld = null;
        }

    }

    public static void setRamManager(RamThresholdManager manager){
        ramManager = manager;
    }

    public static RamThresholdManager getRamManager() {
        return ramManager;
    }

    //Getters
    public static Client getClient() {
        return client;
    }

    public static String getServerID() {
        return serverID;
    }

    public static boolean isMinigameEnabled() {
        return isMinigameEnabled;
    }

    public static boolean isPlaytest() {
        return isPlaytest;
    }

    public static boolean isLobbyServer(){
        return isLobbyServer;
    }

    public World getMinigameWaitingWorld(){
        return waitingWorld;
    }

    public static boolean isSlimeInstalled() {
        return isSlimeInstalled;
    }
    public static boolean isNBAPIInstalled() {
        return isSlimeInstalled;
    }

    public static boolean isDataProxyAllowed() {
        return isDataProxyAllowed;
    }

    public static boolean isMongoAllowed() {
        return connectToMongo;
    }

    public static String getConnectionString() {
        return connectionString;
    }

    public static String getMainDatabaseName() {
        return mainDatabaseName;
    }

    public static String getPlaytestDatabaseName() {
        return playtestDatabaseName;
    }

    public static String getMinigameDatabaseName() {
        return minigameDatabaseName;
    }


    private void setGamerulesForSlime(){
        for (World w : Bukkit.getWorlds()){
            templateWorldSetup(w);
        }
    }

    public static void templateWorldSetup(World w){
    //Only for Slime Worlds
        if (!SlimeTools.isSlimeWorld(w.getName())){
            return;
        }
    //Non Arena Worlds
        if (ArenaManager.getArena(w.getName()) != null){
            return;
        }

        w.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        w.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
        w.setGameRule(GameRule.DO_FIRE_TICK, false);
        w.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        w.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        w.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
    }
}
