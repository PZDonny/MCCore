package MCCore.minigameAPI.arenaManager;

import MCCore.Core;
import MCCore.events.ArenaWorldGeneratedEvent;
import MCCore.events.GameStateChangeEvent;
import MCCore.events.PlayerRemovedFromArenaEvent;
import MCCore.minigameAPI.ConnectedParty;
import MCCore.minigameAPI.GameState;
import MCCore.sockets.Messages;
import MCCore.utils.AbilityHandler;
import MCCore.utils.Items;
import MCCore.utils.Scoreboard.PlayerScoreboard;
import MCCore.utils.Scoreboard.ScoreboardUtils;
import MCCore.utils.SlimeTools;
import MCCore.utils.WorldTools;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Arena {
    public static final ItemStack leaveTool = Items.makeItem(Material.HOPPER_MINECART, 1, ChatColor.RED+"Return To Lobby"+ChatColor.YELLOW+" (Click)");
    //public static final ItemStack requeueTool = Items.makeItem(Material.EMERALD, 1, ChatColor.GREEN+"Play Again"+ChatColor.YELLOW+" (Click)");

    int minPlayers = 0;
    int countdownDuration = 10;
    CountdownStyle countdownStyle = CountdownStyle.BOSSBAR;
    String minigameName = "";
    String mode = "";
    SlimeWorld arenaWorld;

    String templateWorldName;
    GameState gameState = GameState.CONNECTING;
    private final Set<Player> startPlayers = new HashSet<>();

    private final List<Player> playingPlayers = new ArrayList<>();

    private final Set<Player> spectators = new HashSet<>();
    private final ArenaCountdown countdown = new ArenaCountdown(this);

    private int queueID;

    private OfflinePlayer host;

    private String privateSettings;
    private ArrayList<ConnectedParty> connectedParties = new ArrayList<>();

    private boolean isInEndingProcess = false;

    private boolean allowSpectatorInteraction = false;

    private boolean isUsuable = true;
    private boolean isManualWorld = false;

//Constructor
    public Arena(int queueID, OfflinePlayer host, String privateSettings) {
        this.queueID = queueID;
        this.host = host;
        this.privateSettings = privateSettings;
    }

//Setters

    public void setAllowSpectatorInteraction(boolean status){
        this.allowSpectatorInteraction = status;
    }
    public void setMinimumPlayers(int minPlayers){
        this.minPlayers = minPlayers;
    }

    public void setCountdownDuration(int duration){
        this.countdownDuration = duration;
    }

    public void isManualWorld(boolean isManualWorld){
        this.isManualWorld = isManualWorld;
    }

    public void setMinigame(String minigameName, String mode){
        this.minigameName = minigameName;
        this.mode = mode;
    }

    public void setCountdownStyle(CountdownStyle countdownStyle){
        this.countdownStyle = countdownStyle;
    }

    protected void setStateToPlaying(){
        GameState pastGameState = this.gameState;
        this.gameState = GameState.PLAYING;
        String[] out = new String[]{Messages.MINIGAMEAPI_STARTARENA.getID()
                , String.valueOf(queueID)};
        Core.getClient().sendMessage(out);
        for (Player p : startPlayers){
            if (p.isOnline()){
                ArenaManager.refreshPlayer(p, GameMode.ADVENTURE);
                playingPlayers.add(p);
            }
        }

        Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(this, GameState.PLAYING, pastGameState));
    }

    public void endGame(int endingStateDelay, int arenaDeletionDelayTicks){
        if (this.gameState == GameState.ENDING || isInEndingProcess){
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Failed to end game while in \"ENDING\" Gamestate! "+ChatColor.WHITE+"("+queueID+")");
            return;
        }
        isInEndingProcess = true;

        Arena arena = this;

        new BukkitRunnable(){
            public void run(){
                GameState pastGameState = arena.gameState;
                arena.gameState = GameState.ENDING;
                Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(arena, GameState.ENDING, pastGameState));
            }
        }.runTaskLater(Core.getInstance(), endingStateDelay);


        new BukkitRunnable(){
            public void run(){
                ArenaManager.deleteArena(arena, null);
                Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(arena, GameState.STOPPED, GameState.ENDING));
            }
        }.runTaskLater(Core.getInstance(), arenaDeletionDelayTicks);

    }

    public void setWorldDifficulty(Difficulty difficulty){
        if (arenaWorld == null) return;
        World bukkitWorld = Bukkit.getWorld(arenaWorld.getName());
        if (bukkitWorld != null){
            bukkitWorld.setDifficulty(difficulty);
        }

    }

    public void generateArenaWorld(String worldName, boolean autoGameRules){
        Arena arena = this;
        new BukkitRunnable(){
            @Override
            public void run() {
                SlimeWorld worldCloned = SlimeTools.getCloneFromDataSource(worldName, worldName+"_"+queueID, true);
                ArenaManager.addArena(worldCloned, arena);
                arena.arenaWorld = worldCloned;
                arena.templateWorldName = worldName;

                new BukkitRunnable(){
                    final int maxAttempts = 50;
                    int attempt = 0;
                    public void run() {
                        World bukkitWorld = Bukkit.getWorld(worldCloned.getName());
                        if (attempt >= maxAttempts) {
                            cancel();
                            return;
                        }
                        if (bukkitWorld != null) {
                            //Call WorldLoadEvent to prevent WorldGuard "Region data for WorldGuard failed to load for this world" error
                            new WorldLoadEvent(bukkitWorld).callEvent();
                            new ArenaWorldGeneratedEvent(arena).callEvent();
                            bukkitWorld.setAutoSave(false);
                            bukkitWorld.setKeepSpawnInMemory(false);
                            for (Chunk chunk : bukkitWorld.getLoadedChunks()){
                                chunk.setForceLoaded(false);
                            }
                            if (autoGameRules) {
                                bukkitWorld.setGameRule(GameRule.DISABLE_RAIDS, true);
                                bukkitWorld.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
                                bukkitWorld.setGameRule(GameRule.DO_INSOMNIA, false);
                                bukkitWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                                bukkitWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
                                bukkitWorld.setGameRule(GameRule.MOB_GRIEFING, false);
                                bukkitWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
                                bukkitWorld.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
                                bukkitWorld.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
                                bukkitWorld.setGameRule(GameRule.SPAWN_RADIUS, 0);
                                bukkitWorld.setGameRule(GameRule.DO_FIRE_TICK, false);
                                bukkitWorld.setGameRule(GameRule.FORGIVE_DEAD_PLAYERS, true);
                                bukkitWorld.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
                                bukkitWorld.setDifficulty(Difficulty.NORMAL);
                            }
                            cancel();
                        }
                        attempt++;
                    }
                }.runTaskTimer(Core.getInstance(), 15, 2);
            }
        }.runTaskAsynchronously(Core.getInstance());

    }


//Getters

    public boolean isSpectatorInteractionAllowed() {
        return allowSpectatorInteraction;
    }

    public String getTemplateWorldName() {
        return templateWorldName;
    }

    public boolean isManualWorld() {
        return isManualWorld;
    }

    public int getMinPlayers(){
        return minPlayers;
    }

    public int getCountdownDuration(){
        return countdownDuration;
    }

    public String getMinigameName() {
        return minigameName;
    }

    public String getMode(){
        return mode;
    }

    public CountdownStyle getCountdownStyle(){
        return countdownStyle;
    }

    public SlimeWorld getArenaWorld(){
        return arenaWorld;
    }

    public World getBukkitWorld(){
        if (arenaWorld == null){
            return null;
        }
        return Bukkit.getWorld(arenaWorld.getName());
    }

    public GameState getGameState(){
        return gameState;
    }

    public Set<Player> getStartPlayers() {
        return new HashSet<>(startPlayers);
    }

    public Set<Player> getOnlineStartPlayers(){
        Set<Player> players = new HashSet<>();
        for (Player p : startPlayers){
            if (p.isOnline()){
                players.add( p);
            }
        }
        return players;
    }

    public List<Player> getPlayingPlayers() {
        return new ArrayList<>(playingPlayers);
    }

    public Set<Player> getSpectatingPlayers() {
        return new HashSet<>(spectators);
    }

    public Set<Player> getArenaPlayers(){
        Set<Player> players = new HashSet<>();
        players.addAll(spectators);
        players.addAll(playingPlayers);
        return players;
    }


    public boolean isPlayerSpectating(Player p){
        if (spectators.contains(p)) return true;
        else{
            ArenaContainer container = ArenaContainer.getArenaContainer(this);
            if (container == null){
                return false;
            }
            MinigamePlayerProfile profile = container.getPlayerProfile(p);
            if (profile != null){
                return profile.isRespawning;
            }
        }
        return false;
    }

    public void makePlayerSpectate(Player p){
        playingPlayers.remove(p);
        p.closeInventory();
        p.setShoulderEntityLeft(null);
        p.setShoulderEntityRight(null);
        p.setWalkSpeed(0.2f);
        p.setFlySpeed(0.1f);
        p.setGameMode(GameMode.ADVENTURE);
        p.setInvulnerable(true);
        p.setAllowFlight(true);
        p.setFlying(true);
        p.getInventory().clear();
        p.setCanPickupItems(false);
        p.getInventory().setItem(4, leaveTool);
        p.getInventory().setHeldItemSlot(0);
        Collection<PotionEffect> potions = p.getActivePotionEffects();
        for (PotionEffect effect : potions){
            p.removePotionEffect(effect.getType());
        }
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0, false, false, false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, PotionEffect.INFINITE_DURATION, 0, false, false, false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, PotionEffect.INFINITE_DURATION, 0, false, false, false));
    //Hide Player from Playing Players
        for (Player player : playingPlayers){
            player.hidePlayer(Core.getInstance(), p);
        }
    //Reveal Spectators to Player
        for (Player spectator : spectators){
            if (spectator.isOnline()) p.showPlayer(Core.getInstance(), spectator);
        }
        spectators.add(p);
        for (Player o : getArenaPlayers()){
            PlayerScoreboard board = ScoreboardUtils.getPlayerScoreboard(o);
            PlayerScoreboard.UpdatingValue.ARENA_PLAYINGPLAYERS.updateValue(board, this);
            PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS.updateValue(board, this);
        }
    }

    public void makePlayerTempSpectate(Player p){
        ArenaContainer container = ArenaContainer.getArenaContainer(this);
        if (container == null){
            return;
        }
        MinigamePlayerProfile profile = container.getPlayerProfile(p);
        if (profile.isRespawning()) return;
        p.setShoulderEntityLeft(null);
        p.setShoulderEntityRight(null);
        p.closeInventory();
        p.setWalkSpeed(0.2f);
        p.setFlySpeed(0.1f);
        p.setInvulnerable(true);
        p.setGameMode(GameMode.ADVENTURE);
        p.setCanPickupItems(false);
        profile.setRespawningState(true);
        Collection<PotionEffect> potions = p.getActivePotionEffects();
        for (PotionEffect effect : potions){
            p.removePotionEffect(effect.getType());
        }
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0));
        p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, PotionEffect.INFINITE_DURATION, 0));
        for (Player player : playingPlayers){
            if (player != p) player.hidePlayer(Core.getInstance(), p);
        }
    }

    public void revivePlayer(Player p, GameMode gameMode){
        ArenaContainer container = ArenaContainer.getArenaContainer(this);
        if (container == null){
            return;
        }
        MinigamePlayerProfile profile = container.getPlayerProfile(p);
        if (!profile.isRespawning()) return;
        p.closeInventory();
        p.setInvulnerable(false);
        p.setGameMode(gameMode);
        p.setCanPickupItems(true);
        profile.setRespawningState(true);
        Collection<PotionEffect> potions = p.getActivePotionEffects();
        for (PotionEffect effect : potions){
            p.removePotionEffect(effect.getType());
        }
        for (Player player : playingPlayers){
            if (player != p) player.showPlayer(Core.getInstance(), p);
        }
    }

    public void addConnectedParty(ConnectedParty party){
        for (ConnectedParty connectedParty : new ArrayList<>(connectedParties)){
        //Don't add identical parties (if a player leaves and rejoins)
            if (connectedParty.equals(party)){
                return;
            }
        //Replace parties if the leader is the same, but the parties aren't identical
            if (connectedParty.getMembers().get(0).equals(party.getMembers().get(0))){
                connectedParties.remove(connectedParty);
                break;
            }
        }
        connectedParties.add(party);
    }

    public void addPlayer(Player p){
        if (startPlayers.contains(p)){
            return;
        }
        startPlayers.add(p);

        for (Player o : Bukkit.getOnlinePlayers()){
            if (startPlayers.contains(o)){
                p.showPlayer(Core.getInstance(), o);
                o.showPlayer(Core.getInstance(), p);
            }
            else{
                p.hidePlayer(Core.getInstance(), o);
                o.hidePlayer(Core.getInstance(), p);
            }
        }
        p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
        p.getInventory().setHeldItemSlot(0);
        p.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, PotionEffect.INFINITE_DURATION, 255, false, false, false));

        Location minigameTP = Core.getInstance().getMinigameWaitingWorld().getSpawnLocation().clone();
        minigameTP.setPitch(-90);
        p.teleport(minigameTP);
        ArenaManager.refreshPlayer(p, GameMode.SPECTATOR);
        new BukkitRunnable(){
            @Override
            public void run() {
                if (!p.isOnline()) return;
                ArmorStand stand = null;
                for (ArmorStand as : minigameTP.getNearbyEntitiesByType(ArmorStand.class, 8)){
                    stand = as;
                    break;
                }
                if (stand != null){
                    p.setSpectatorTarget(stand);
                }
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        if (!p.isOnline()) return;
                        if (p.getSpectatorTarget() == null && p.getWorld().equals(Core.getInstance().getMinigameWaitingWorld())){
                            p.setGameMode(GameMode.ADVENTURE);

                            p.teleport(minigameTP);
                        }
                    }
                }.runTaskLater(Core.getInstance(), 1);
            }
        }.runTaskLater(Core.getInstance(), 2);
    }

    public void removePlayer(Player p, PlayerRemovedFromArenaEvent.RemoveCause cause){
        if (gameState == GameState.CONNECTING){
            startPlayers.remove(p);
        }
        playingPlayers.remove(p);
        spectators.remove(p);
        if (cause == PlayerRemovedFromArenaEvent.RemoveCause.JOINEDNEW && p.isOnline()){
            List<Entity> passengers = new ArrayList<>(p.getPassengers());
            for (Entity passenger : passengers){
                p.removePassenger(passenger);
            }
            Entity vehicle = p.getVehicle();
            p.leaveVehicle();
            new PlayerRemovedFromArenaEvent(this, p, cause, passengers, vehicle).callEvent();
        }
        else{
            new PlayerRemovedFromArenaEvent(this, p, cause).callEvent();
        }
        ArenaManager.refreshPlayer(p, GameMode.SPECTATOR);
        for (Player o : getArenaPlayers()){
            PlayerScoreboard board = ScoreboardUtils.getPlayerScoreboard(o);
            PlayerScoreboard.UpdatingValue.ARENA_PLAYINGPLAYERS.updateValue(board, this);
            PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS.updateValue(board, this);
        }

    }

    public ArenaCountdown getCountdown() {
        return countdown;
    }

    public int getQueueID() {
        return queueID;
    }

    public boolean isInEndingProcess() {
        return isInEndingProcess;
    }

    public OfflinePlayer getHost(){
        return host;
    }

    public String getPrivateSettings(){
        return privateSettings;
    }

    public ArrayList<ConnectedParty> getConnectedParties(){
        return connectedParties;
    }

    public boolean isPrivate(){
        return host != null;
    }

    void deleteArena(){
        queueID = 0;

    //Delete Stats

        AbilityHandler.removePlayerData(startPlayers);
        playingPlayers.clear();
        startPlayers.clear();
        spectators.clear();
        connectedParties.clear();
        host = null;
        isUsuable = false;

        ArenaContainer container = ArenaContainer.getArenaContainer(this);
        if (container != null){
            container.profiles.clear();
        }

        if (!isManualWorld){
            if (arenaWorld != null){
                Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD+"Removed slime world "+ ChatColor.AQUA+arenaWorld.getName()+ChatColor.GOLD+" from cache!");
                String arenaWorldName = arenaWorld.getName();
                arenaWorld = null;
                WorldTools.destroyWorld(Bukkit.getWorld(arenaWorldName));
            }
        }
    }

    public boolean isUsuable(){
        return isUsuable;
    }

    public boolean isEndingOrNotUsable(){
        return isInEndingProcess() || !isUsuable();
    }

    protected void doCountdown(){
        countdown.start(countdownStyle, countdownDuration);
    }
}
