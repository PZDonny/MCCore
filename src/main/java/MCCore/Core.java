package MCCore;

import MCCore.commands.*;
import MCCore.listeners.*;
import MCCore.minigameAPI.SlimeTools;
import MCCore.sockets.Client;
import MCCore.tempMinigame.TempPlayCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.net.Socket;


public final class Core extends JavaPlugin {
    private FileConfiguration config = getConfig();

    private static Core instance;
    public static boolean projectileRandomness;
    public static boolean connectToMongo;
    public static String connectionString;

    public static String fallbackServer;

    public static boolean isPlaytest;

    private static Client client;

    public static int port;

    static World waitingWorld;

    public static String prefix = "["+ ChatColor.GREEN+"M"+ChatColor.WHITE+"C"+ChatColor.RED+" Core"+ChatColor.WHITE+"] ";

    @Override
    public void onEnable() {

        //SlimeWorldManager
        if (!Bukkit.getPluginManager().isPluginEnabled("SlimeWorldManager")) {
            getLogger().severe("MCCore cannot fully utilize the MinigameAPI!");
            getLogger().severe("*** SlimeWorldManager is not installed or not enabled. ***");
        }

        //WorldGuard
        if (!Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            getLogger().severe("MCCore cannot fully utilize the WorldTools Utility!");
            getLogger().severe("*** WorldGuard is not installed or not enabled. ***");
        }

        instance = this;
        new PluginMessage();

    //Slime World Variables
        SlimeTools.setSlimeVariables();

    //Configuartion
        config.options().copyDefaults(true);
        saveConfig();
        setConfigVariables();
        try {
            client = new Client(new Socket(Bukkit.getServer().getIp(), port));
        } catch (IOException e) {
            e.printStackTrace();
        }

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "M" + ChatColor.WHITE + "C" + ChatColor.DARK_RED + "CORE " + ChatColor.GREEN + "ENABLED");

        new BukkitRunnable() {//Connect to Mongo Asynchronously
            public void run() {
                if (connectToMongo) DBConnection.connectToMongo(connectionString);
            }
        }.runTaskAsynchronously(this);

        //Commands
        getCommand("gmc").setExecutor(new Gamemode());
        getCommand("gms").setExecutor(new Gamemode());
        getCommand("gma").setExecutor(new Gamemode());
        getCommand("gmsp").setExecutor(new Gamemode());
        getCommand("fly").setExecutor(new Fly());
        getCommand("speed").setExecutor(new Speed());
        getCommand("cc").setExecutor(new ClearChat());
        getCommand("ci").setExecutor(new ClearInv());
        getCommand("day").setExecutor(new DayCycle());
        getCommand("noon").setExecutor(new DayCycle());
        getCommand("night").setExecutor(new DayCycle());
        getCommand("midnight").setExecutor(new DayCycle());
        getCommand("corereload").setExecutor(this);
        getCommand("play").setExecutor(new TempPlayCommand());

        //Events
        getServer().getPluginManager().registerEvents(new ProjectileRandomness(), this);
        getServer().getPluginManager().registerEvents(new PlayerChangeWorld(), this);
        getServer().getPluginManager().registerEvents(new JoinQuit(), this);
        getServer().getPluginManager().registerEvents(new Chat(), this);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "mccore:connect");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "mccore:partychat");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "mccore:minigameapi");

        this.getServer().getMessenger().registerIncomingPluginChannel(this, "mccore:minigameapi", PluginMessage.getInstance());
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "mccore:changechat", PluginMessage.getInstance());


    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "M" + ChatColor.WHITE + "C" + ChatColor.DARK_RED + "CORE " + ChatColor.RED + "DISABLED");
        client.disconnect();
        // Plugin shutdown logic
    }

    public static Core getInstance(){
        return instance;
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
        projectileRandomness = getConfig().getBoolean("fixProjectileRandomness");
        connectToMongo = getConfig().getBoolean("mongoDB.enabled");
        isPlaytest = getConfig().getBoolean("mongoDB.usePlaytestDatabase");
        connectionString = getConfig().getString("mongoDB.connectionString");
        port = getConfig().getInt("port");
        fallbackServer = getConfig().getString("minigames.mainFallbackServer");
        String world = getConfig().getString("minigames.waitingWorld");
        if (world != null && Bukkit.getWorld(world) != null){
            waitingWorld = Bukkit.getWorld(world);
        }
        else{
            Bukkit.getConsoleSender().sendMessage(Component.text(ChatColor.RED+"There was an error using the specified minigame waiting world!"+ChatColor.GOLD+"("+world+")"));
        }
    }

    public static Client getClient() {
        return client;
    }

    public World getMinigameWaitingWorld(){
        return waitingWorld;
    }
}
