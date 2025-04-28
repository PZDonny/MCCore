package net.donnypz.mccore.minigame.arena;

import com.infernalsuite.aswm.api.world.properties.SlimeProperties;
import net.donnypz.mccore.Core;
import net.donnypz.mccore.events.*;
import net.donnypz.mccore.minigame.ArenaState;
import net.donnypz.mccore.utils.ability.AbilityHandler;
import net.donnypz.mccore.utils.entity.EntityUtils;
import net.donnypz.mccore.utils.item.ItemBuilder;
import net.donnypz.mccore.utils.misc.SlimeUtils;
import net.donnypz.mccore.utils.misc.WorldUtils;
import net.donnypz.mccore.utils.ui.scoreboard.PlayerScoreboard;
import net.donnypz.mccore.utils.ui.scoreboard.ScoreboardUtils;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import com.infernalsuite.aswm.api.world.properties.SlimePropertyMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

public class Arena {
    public static final ItemStack spectatorTool = new ItemBuilder(Material.RECOVERY_COMPASS)
            .setDisplayName(MiniMessage.miniMessage().deserialize("<aqua>Spectate Players <yellow>(Click)"))
            .setItemAction("core:spectate_players")
            .build();

    int countdownDuration = 10;
    CountdownStyle countdownStyle = CountdownStyle.BOSSBAR;
    ArenaSettings arenaSettings;
    SlimeWorld mainArenaWorld;
    World manualBukkitWorld;

    String templateWorldName;
    ArenaState arenaState = ArenaState.CONNECTING;
    boolean exitedConnectingPhase = false;

    private final Set<Player> startPlayers = new HashSet<>();
    private final List<Player> playingPlayers = new ArrayList<>();
    private final HashSet<Player> spectators = new HashSet<>();

    private final ArenaCountdown countdown = new ArenaCountdown(this);

    private UUID queueUUID;

    private UUID hostUUID;

    private boolean isInEndingProcess = false;

    private boolean allowSpectatorInteraction = false;
    private boolean tablistHidesSpectators = true;

    private boolean isUsable = true;
    private ArenaType arenaType;

    private boolean isForceDeleted = false;
    private boolean allowMultipleWorlds = false;

//Constructor
    /**
     * Creates an arena
     * @param queueUUID The arena's pre-determined queue-ID (typically done but data-proxy)
     * @param host The host if this arena is private, can be null
     * @param type The arena's type, can not be null
     */
    public Arena(UUID queueUUID, OfflinePlayer host, @NotNull ArenaType type, @NotNull ArenaSettings arenaSettings) {
        this.queueUUID = queueUUID;
        if (host != null){
            this.hostUUID = host.getUniqueId();
        }

        this.arenaType = type;
        this.arenaSettings = arenaSettings;
        if (type == ArenaType.BUKKITMANUAL){
            ArenaManager.addInactiveManualBukkitArena(this);
        }
        else if (type == ArenaType.SLIMEMANUAL){
            ArenaManager.addArena(this, null);
        }
    }

    /**
     * Creates an arena with a random queueID
     * @param host The host if this arena is private, can be null
     * @param type The arena's type, can not be null
     */
    public Arena(OfflinePlayer host, @NotNull ArenaType type, @NotNull ArenaSettings arenaSettings) {
        this.queueUUID = UUID.randomUUID();
        if (host != null){
            this.hostUUID = host.getUniqueId();
        }

        this.arenaType = type;
        this.arenaSettings = arenaSettings;
        if (type == ArenaType.BUKKITMANUAL){
            ArenaManager.addInactiveManualBukkitArena(this);
        }
        else if (type == ArenaType.SLIMEMANUAL){
            ArenaManager.addArena(this, null);
        }
    }

    public Arena allowMultipleWorlds(){
        this.allowMultipleWorlds = true;
        return this;
    }

    public boolean allowsMultipleWorlds() {
        return allowMultipleWorlds;
    }

    //Setters

    public Arena setArenaSettings(@NotNull ArenaSettings arenaSettings){
        this.arenaSettings = arenaSettings;
        return this;
    }

    public Arena setTablistHidesSpecators(boolean status){
        this.tablistHidesSpectators = status;
        return this;
    }

    public Arena setAllowSpectatorInteraction(boolean status){
        this.allowSpectatorInteraction = status;
        return this;
    }

    public Arena setCountdownDuration(int duration){
        this.countdownDuration = duration;
        return this;
    }

    public Arena setCountdownMessage(Component message){
        this.countdown.setMessage(message);
        return this;
    }

    public Arena setCountdownMessage(String message){
        this.countdown.setMessage(message);
        return this;
    }

    public boolean setManualBukkitWorld(World world){
        if (!ArenaManager.addManualBukkitArena(this, world)){
            return false;
        }
        this.arenaType = ArenaType.BUKKITMANUAL;
        this.manualBukkitWorld = world;
        return true;
    }


    public Arena setCountdownStyle(CountdownStyle countdownStyle){
        this.countdownStyle = countdownStyle;
        return this;
    }

    protected void setStateToPlaying(){
        ArenaState pastArenaState = this.arenaState;
        this.arenaState = ArenaState.PLAYING;
        if (startPlayers.isEmpty()){
            endGame(0, 0);
            return;
        }

        exitedConnectingPhase = true;

        for (Player p : startPlayers){
            if (p.isOnline()){
                playingPlayers.add(p);
            }
        }

        new ArenaStateChangeEvent(this, ArenaState.PLAYING, pastArenaState).callEvent();
        ArenaContainer container = ArenaContainer.getArenaContainer(this);
        container.onPlayersSentToArena();
    }

    public void endGame(int endingStateDelay, int arenaDeletionDelayTicks){
        if (this.arenaState == ArenaState.ENDING || isInEndingProcess){
            Bukkit.getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize("<red>Failed to end game while in \"ENDING\" Gamestate! <white>("+queueUUID+")"));
            return;
        }
        if (arenaDeletionDelayTicks < endingStateDelay){
            endingStateDelay = arenaDeletionDelayTicks;
        }
        isInEndingProcess = true;

        Arena arena = this;


        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
            ArenaState pastArenaState = arena.arenaState;
            arena.arenaState = ArenaState.ENDING;
            new ArenaStateChangeEvent(arena, ArenaState.ENDING, pastArenaState).callEvent();
        }, endingStateDelay);

        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
            ArenaState pastArenaState = arena.arenaState;
            arena.arenaState = ArenaState.DELETED;
            new ArenaStateChangeEvent(arena, ArenaState.DELETED, pastArenaState).callEvent();
            ArenaManager.deleteArena(arena, null);
        }, arenaDeletionDelayTicks);
    }

    public void forceDelete(){
        if (this.arenaState == ArenaState.ENDING || isInEndingProcess){
            Bukkit.getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize("<red>Failed to force deletion of already force deleted arena! <white>("+queueUUID+")"));
            return;
        }

        ArenaState pastArenaState = arenaState;
        arenaState = ArenaState.DELETED;
        isInEndingProcess = true;
        isForceDeleted = true;
        new ArenaStateChangeEvent(this, ArenaState.DELETED, pastArenaState).callEvent();
        ArenaManager.deleteArena(this, null);

    }

    public void setWorldDifficulty(Difficulty difficulty){
        if (mainArenaWorld == null){
            return;
        }
        World bukkitWorld = Bukkit.getWorld(mainArenaWorld.getName());
        if (bukkitWorld != null){
            bukkitWorld.setDifficulty(difficulty);
        }

    }

    public void generateArenaWorld(String slimeWorldName, boolean autoGameRules){
        Arena arena = this;
        Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(), () -> {
            SlimeWorld worldCloned = SlimeUtils.getCloneFromLoader(slimeWorldName, slimeWorldName+"_"+queueUUID, true);
            arena.mainArenaWorld = worldCloned;
            arena.templateWorldName = slimeWorldName;

            //Could probably be moved to world loaded event
            new BukkitRunnable(){
                final int maxAttempts = 100;
                int attempt = 0;
                public void run() {
                    World bukkitWorld = Bukkit.getWorld(worldCloned.getName());
                    if (attempt >= maxAttempts) {
                        cancel();
                        return;
                    }
                    if (bukkitWorld != null) {
                        ArenaManager.registerArena(arena);
                        new ArenaWorldGeneratedEvent(arena).callEvent();

                        if (autoGameRules) {
                            WorldUtils.useMinigameGamerules(bukkitWorld);
                        }
                        cancel();
                    }
                    attempt++;
                }
            }.runTaskTimer(Core.getInstance(), 5, 2);
        });

    }

    public void generateEmptyArenaWorld(boolean autoGameRules){
        generateEmptyArenaWorld(autoGameRules, null);
    }

    public void generateEmptyArenaWorld(boolean autoGameRules, @Nullable World.Environment environment){
        String emptyWorldName = "empty_"+queueUUID;
        Arena arena = this;
        Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(), () -> {
            SlimeWorld emptyWorld;
            SlimePropertyMap properties = new SlimePropertyMap();
            if (environment != null){
                properties.setValue(SlimeProperties.ENVIRONMENT, environment.name());
            }

            emptyWorld = SlimeUtils.getSlimeAPI().createEmptyWorld(emptyWorldName, true, properties, null);
            if (emptyWorld == null){
                return;
            }
            arena.mainArenaWorld = emptyWorld;
            arena.templateWorldName = emptyWorldName;

            Bukkit.getScheduler().runTask(Core.getInstance(), () -> SlimeUtils.getSlimeAPI().loadWorld(emptyWorld, true));

            new BukkitRunnable(){
                final int maxAttempts = 100;
                int attempt = 0;
                public void run() {
                    World bukkitWorld = Bukkit.getWorld(emptyWorld.getName());
                    if (attempt >= maxAttempts) {
                        cancel();
                        return;
                    }
                    if (bukkitWorld != null) {
                        ArenaManager.registerArena(arena);
                        new WorldLoadEvent(bukkitWorld).callEvent();
                        new ArenaWorldGeneratedEvent(arena).callEvent();

                        if (autoGameRules) {
                            WorldUtils.useMinigameGamerules(bukkitWorld);
                        }
                        cancel();
                        return;
                    }
                    attempt++;
                }
            }.runTaskTimer(Core.getInstance(), 5, 2);
        });
    }


//Getters
    public boolean isSpectatorInteractionAllowed() {
        return allowSpectatorInteraction;
    }

    public String getTemplateWorldName() {
        return templateWorldName;
    }


    public ArenaType getArenaType(){
        return this.arenaType;
    }


    public ArenaSettings getArenaSettings() {
        return arenaSettings;
    }

    public boolean isForceDeleted() {
        return isForceDeleted;
    }

    public int getMinPlayers(){
        return arenaSettings.minPlayers();
    }
    public int getMaxPlayers(){
        return arenaSettings.maxPlayers();
    }

    public int getCountdownDuration(){
        return countdownDuration;
    }

    public String getMinigameName() {
        return arenaSettings.minigameName();
    }

    public String getMode(){
        return arenaSettings.mode();
    }

    public CountdownStyle getCountdownStyle(){
        return countdownStyle;
    }

    public boolean tablistHidesSpectators() {
        return tablistHidesSpectators;
    }

    SlimeWorld getMainArenaWorld(){
        return mainArenaWorld;
    }

    public World getManualBukkitWorld(){
        return manualBukkitWorld;
    }

    public World getArenaAsBukkitWorld(){
        if (mainArenaWorld == null){
            return null;
        }
        return Bukkit.getWorld(mainArenaWorld.getName());
    }

    public String getArenaWorldName(){
        return mainArenaWorld.getName();
    }

    public ArenaState getGameState(){
        return arenaState;
    }

    public Set<Player> getStartPlayers() {
        return new HashSet<>(startPlayers);
    }

    public Set<Player> getOnlineStartPlayers(){
        Set<Player> players = new HashSet<>();
        for (Player p : startPlayers){
            if (p.isConnected()){
                players.add( p);
            }
        }
        return players;
    }

    public List<Player> getPlayingPlayers() {
        return new ArrayList<>(playingPlayers);
    }



    public Set<Player> getSpectatingPlayers(SpectatorType type) {
        Set<Player> players = new HashSet<>();
        switch(type){
            case ALL -> {
                players.addAll(spectators);
            }

            case STARTING_PLAYER -> {
                for (Player p : spectators){
                    if (startPlayers.contains(p)){
                        players.add(p);
                    }
                }
            }

            case OUTSIDE_PLAYER -> {
                for (Player p : spectators){
                    if (!startPlayers.contains(p)){
                        players.add(p);
                    }
                }
            }

            case PROFILED_PLAYER -> {
                ArenaContainer container = ArenaContainer.getArenaContainer(this);
                if (container != null){
                    for (Player p : spectators){
                        if (container.getPlayerProfile(p.getUniqueId()) != null){
                            players.add(p);
                        }
                    }
                }
            }

            case PROFILE_ACTIVE_PLAYER -> {
                ArenaContainer container = ArenaContainer.getArenaContainer(this);
                if (container != null){
                    for (Player p : spectators){
                        MinigamePlayerProfile profile = container.getPlayerProfile(p.getUniqueId());
                        if (profile != null && profile.isActive()){
                            players.add(p);
                        }
                    }
                }
            }

            case PROFILE_INACTIVE_PLAYER -> {
                ArenaContainer container = ArenaContainer.getArenaContainer(this);
                if (container != null){
                    for (Player p : spectators){
                        MinigamePlayerProfile profile = container.getPlayerProfile(p.getUniqueId());
                        if (profile != null && !profile.isActive()){
                            players.add(p);
                        }
                    }
                }
            }

            case PROFILELESS_AND_INACTIVE_PLAYER -> {
                ArenaContainer container = ArenaContainer.getArenaContainer(this);
                if (container != null){
                    for (Player p : spectators){
                        MinigamePlayerProfile profile = container.getPlayerProfile(p.getUniqueId());
                        if (profile != null){
                            if (!profile.isActive()){
                                players.add(p);
                            }

                        }
                        else{
                            players.add(p);
                        }
                    }
                }
            }

            case PROFILELESS_PLAYER -> {
                ArenaContainer container = ArenaContainer.getArenaContainer(this);
                if (container != null){
                    for (Player p : spectators){
                        if (container.getPlayerProfile(p.getUniqueId()) == null){
                            players.add(p);
                        }
                    }
                }
            }
        }
        return players;
    }



    public boolean hasSpectators(){
        return !spectators.isEmpty();
    }

    public boolean containsPlayer(@NotNull Player player){
        World w = player.getWorld();
        if (arenaType == ArenaType.BUKKITMANUAL){
            if (w.equals(manualBukkitWorld)){
                return true;
            }
        }
        else{
            World arenaWorld = getArenaAsBukkitWorld();
            if (w.equals(arenaWorld)){
                return true;
            }
        }
        if (arenaState == ArenaState.CONNECTING && startPlayers.contains(player)){
            return true;
        }
        return spectators.contains(player) || playingPlayers.contains(player);
    }


    public Set<Player> getArenaPlayers(){
        Set<Player> players = new HashSet<>();
        players.addAll(spectators);
        players.addAll(playingPlayers);
        return players;
    }

    public boolean isStartingPlayer(Player player){
        return startPlayers.contains(player);
    }


    public boolean isPlayerSpectating(Player p, boolean mustBeStartingPlayer){
        if (spectators.contains(p)){
            if (mustBeStartingPlayer){
                return startPlayers.contains(p);
            }
            else{
                return true;
            }
        }
        return false;
    }



    public void makePlayerSpectate(Player p, boolean giveSpectateTool, boolean refreshPlayer){
        playingPlayers.remove(p);
        p.closeInventory();
        p.setGameMode(GameMode.ADVENTURE);
        p.setInvulnerable(true);
        p.setAllowFlight(true);
        p.setFlying(true);
        p.setCanPickupItems(false);
        p.setCollidable(false);
        if (refreshPlayer) {
            p.setShoulderEntityLeft(null);
            p.setShoulderEntityRight(null);
            p.setWalkSpeed(0.2f);
            p.setFlySpeed(0.1f);
            p.getInventory().clear();

            if (giveSpectateTool){
                p.getInventory().setItem(4, spectatorTool);
            }

            p.getInventory().setHeldItemSlot(0);
            Collection<PotionEffect> potions = p.getActivePotionEffects();
            for (PotionEffect effect : potions){
                p.removePotionEffect(effect.getType());
            }
            p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0, false, false, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, PotionEffect.INFINITE_DURATION, 0, false, false, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, PotionEffect.INFINITE_DURATION, 0, false, false, false));
        }
        else if (giveSpectateTool) {
            p.getInventory().addItem(spectatorTool);
        }


        spectators.add(p);
        new PlayerSpectateArenaEvent(this, p).callEvent();

    //Hide Player from Playing Players
        for (Player startPlayer : startPlayers){
            if (ArenaManager.getArenaOfPlayer(startPlayer) == this){
                startPlayer.hidePlayer(Core.getInstance(), p);
            }
        }

    //Reveal Spectators to Player
        for (Player spectator : spectators){
            if (spectator.isConnected()){
                p.showPlayer(Core.getInstance(), spectator);
            }
        }

        for (Player o : getArenaPlayers()){
            PlayerScoreboard board = ScoreboardUtils.getPlayerScoreboard(o);
            if (board != null){
                board.addSpectator(p);
                PlayerScoreboard.UpdatingValue.ARENA_PLAYINGPLAYERS.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_STARTING.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_OUTSIDE.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_PROFILED.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_PROFILE_ACTIVE.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_PROFILE_INACTIVE.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_PROFILELESS_AND_INACTIVE.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_PROFILELESS.updateValue(board, this);
            }
        }
    }

    private void updateSpectators(Player p, boolean addToPlayerTeam){
        for (Player o : getArenaPlayers()){
            if (o != p){
                if (playingPlayers.contains(o)){ //Reveal Player to Playing Players
                    o.showPlayer(Core.getInstance(), p);
                }

                if (spectators.contains(o)){ //Hide Spectators from Player
                    p.hidePlayer(Core.getInstance(), o);
                }
            }

            PlayerScoreboard board = ScoreboardUtils.getPlayerScoreboard(o);
            if (board != null){
                board.removeSpectator(p, addToPlayerTeam);
                PlayerScoreboard.UpdatingValue.ARENA_PLAYINGPLAYERS.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_STARTING.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_OUTSIDE.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_PROFILED.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_PROFILE_ACTIVE.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_PROFILE_INACTIVE.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_PROFILELESS_AND_INACTIVE.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_PROFILELESS.updateValue(board, this);
            }
        }
    }

    public void softRevivePlayer(Player p, GameMode gamemode){
        if (!playingPlayers.contains(p)){
            playingPlayers.add(p);
        }
        spectators.remove(p);
        p.setGameMode(gamemode);
        updateSpectators(p, false);
    }



    public void revivePlayer(Player p, GameMode gameMode){
        revivePlayer(p, gameMode, false);
    }

    public void revivePlayer(Player p, GameMode gameMode, boolean removeAttributeModifiers){
        if (!playingPlayers.contains(p)){
            playingPlayers.add(p);
        }
        spectators.remove(p);
        ArenaContainer container = ArenaContainer.getArenaContainer(this);
        if (container == null){
            return;
        }
        p.closeInventory();
        p.setInvulnerable(false);
        p.setAllowFlight(false);
        p.setFlying(false);
        p.setCanPickupItems(true);
        p.setCollidable(true);
        p.setGameMode(gameMode);
        p.setWalkSpeed(0.2f);
        p.setFlySpeed(0.1f);

        if (removeAttributeModifiers){
            EntityUtils.removeAttributeModifiers(p);
        }
        updateSpectators(p, false);
    }

    public void revivePlayerToPlayerTeam(Player p, GameMode gameMode, boolean removeAttributeModifiers){
        if (!playingPlayers.contains(p)){
            playingPlayers.add(p);
        }
        spectators.remove(p);
        ArenaContainer container = ArenaContainer.getArenaContainer(this);
        if (container == null){
            return;
        }
        p.closeInventory();
        p.setInvulnerable(false);
        p.setAllowFlight(false);
        p.setFlying(false);
        p.setCanPickupItems(true);
        p.setCollidable(true);
        p.setGameMode(gameMode);
        p.setWalkSpeed(0.2f);
        p.setFlySpeed(0.1f);

        if (removeAttributeModifiers){
            EntityUtils.removeAttributeModifiers(p);
        }

        updateSpectators(p, true);
    }


    public void revivePlayer(Player p, GameMode gameMode, boolean removeAttributeModifiers, @NotNull String otherPlayersScoreboardTeam, @NotNull String teammateTeam){
        revivePlayer(p, gameMode, removeAttributeModifiers, otherPlayersScoreboardTeam, teammateTeam, Set.of());
    }


    public void revivePlayer(Player p, GameMode gameMode, boolean removeAttributeModifiers, @NotNull String otherPlayersScoreboardTeam, @NotNull String teammateTeam, @NotNull Collection<Player> teammates){
        if (!playingPlayers.contains(p)){
            playingPlayers.add(p);
        }

        spectators.remove(p);
        ArenaContainer container = ArenaContainer.getArenaContainer(this);
        if (container == null){
            return;
        }
        p.closeInventory();
        p.setInvulnerable(false);
        p.setAllowFlight(false);
        p.setFlying(false);
        p.setCanPickupItems(true);
        p.setGameMode(gameMode);
        p.setCollidable(true);
        p.setWalkSpeed(0.2f);
        p.setFlySpeed(0.1f);

        if (removeAttributeModifiers){
            EntityUtils.removeAttributeModifiers(p);
        }

        for (Player o : getArenaPlayers()){
            PlayerScoreboard board = ScoreboardUtils.getPlayerScoreboard(o);
            if (board != null){
                if (o == p){
                    board.removeSpectator(p, teammateTeam);
                }
                else{
                    if (playingPlayers.contains(o)){ //Reveal Player to Playing Players
                        o.showPlayer(Core.getInstance(), p);
                    }

                    if (spectators.contains(o)){ //Hide Spectators from Player
                        p.hidePlayer(Core.getInstance(), o);
                    }
                    if (teammates.contains(o)){
                        board.removeSpectator(p, teammateTeam);
                    }
                    else{
                        board.removeSpectator(p, otherPlayersScoreboardTeam);
                    }
                }

                PlayerScoreboard.UpdatingValue.ARENA_PLAYINGPLAYERS.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_STARTING.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_OUTSIDE.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_PROFILED.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_PROFILE_ACTIVE.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_PROFILE_INACTIVE.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_PROFILELESS_AND_INACTIVE.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_PROFILELESS.updateValue(board, this);
            }
        }
    }

    public void revivePlayer(Player p, GameMode gameMode, boolean removeAttributeModifiers, @NotNull String otherPlayersScoreboardTeam, boolean joinPlayerDefaultTeam){
        revivePlayer(p, gameMode, removeAttributeModifiers, otherPlayersScoreboardTeam, joinPlayerDefaultTeam, Set.of());
    }

    public void revivePlayer(Player p, GameMode gameMode, boolean removeAttributeModifiers, @NotNull String otherPlayersScoreboardTeam, boolean joinPlayerDefaultTeam, @NotNull Collection<Player> teammates){
        if (!playingPlayers.contains(p)){
            playingPlayers.add(p);
        }
        spectators.remove(p);
        ArenaContainer container = ArenaContainer.getArenaContainer(this);
        if (container == null){
            return;
        }
        p.closeInventory();
        p.setInvulnerable(false);
        p.setAllowFlight(false);
        p.setFlying(false);
        p.setCanPickupItems(true);
        p.setGameMode(gameMode);
        p.setCollidable(true);
        p.setWalkSpeed(0.2f);
        p.setFlySpeed(0.1f);

        if (removeAttributeModifiers){
            EntityUtils.removeAttributeModifiers(p);
        }

        for (Player o : getArenaPlayers()){
            if (!o.equals(p) && playingPlayers.contains(o)){
                o.showPlayer(Core.getInstance(), p);
            }
            
            PlayerScoreboard board = ScoreboardUtils.getPlayerScoreboard(o);
            if (board != null){
                if (o == p){
                    if (joinPlayerDefaultTeam){
                        board.removeSpectator(p, true);
                    }
                    else{
                        board.removeSpectator(p, otherPlayersScoreboardTeam);
                    }
                }
                else{
                    if (playingPlayers.contains(o)){ //Reveal Player to Playing Players
                        o.showPlayer(Core.getInstance(), p);
                    }

                    if (spectators.contains(o)){ //Hide Spectators from Player
                        p.hidePlayer(Core.getInstance(), o);
                    }

                    if (teammates.contains(o)) {
                        board.removeSpectator(p, true);
                    }
                    else{
                        board.removeSpectator(p, otherPlayersScoreboardTeam);
                    }

                }

                PlayerScoreboard.UpdatingValue.ARENA_PLAYINGPLAYERS.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_STARTING.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_OUTSIDE.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_PROFILED.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_PROFILE_ACTIVE.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_PROFILE_INACTIVE.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_PROFILELESS_AND_INACTIVE.updateValue(board, this);
                PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_PROFILELESS.updateValue(board, this);
            }
        }
    }



    @ApiStatus.Internal
    public void addPlayingPlayer(Player player, boolean addStartingPlayer){
        if (isInEndingProcess){
            return;
        }
        if (addStartingPlayer){
            ArenaManager.setPlayerArena(player.getUniqueId(), this);
            startPlayers.add(player);
        }

        if (arenaState == ArenaState.PLAYING){
            if (!playingPlayers.contains(player)){
                ArenaManager.setPlayerArena(player.getUniqueId(), this);
                playingPlayers.add(player);
            }
        }
    }

    public void addPlayerManual(@NotNull Player p, boolean hideSpecators, boolean hideOutsidePlayers){
        if (startPlayers.contains(p)){
            return;
        }

        Arena currentArena = ArenaManager.getArenaOfPlayer(p);
        if (currentArena == this){
            return;
        }
        else if (currentArena != null){
            ArenaManager.removePlayerFromArena(p, PlayerRemovedFromArenaEvent.RemoveCause.JOINEDNEW);
        }

        ArenaManager.setPlayerArena(p.getUniqueId(), this);
        PlayerAddedToArenaEvent addedEvent = new PlayerAddedToArenaEvent(this, p,  PlayerAddedToArenaEvent.AddType.MANUAL);
        addedEvent.callEvent();

        //if (!addedEvent.isAddedAsPlayingPlayer()){
            startPlayers.add(p);
        //}
        ArenaContainer container = ArenaContainer.getArenaContainer(this);

        if (container != null && !isEndingOrNotUsable()){
            container.removeEarlyDisconnectPlayer(p.getUniqueId());
        }

        p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1, 2);

        for (Player o : Bukkit.getOnlinePlayers()){
            if (!hideSpecators && isPlayerSpectating(o, false)){
                continue;
            }
            if (startPlayers.contains(o)){
                p.showPlayer(Core.getInstance(), o);
                o.showPlayer(Core.getInstance(), p);
            }
            else if (hideOutsidePlayers){
                p.hidePlayer(Core.getInstance(), o);
                o.hidePlayer(Core.getInstance(), p);
            }
        }

    }

    boolean addPlayerAutomatic(@NotNull Player p, boolean hideOutsidePlayers){
        if (startPlayers.contains(p)){
            return false;
        }
        startPlayers.add(p);
        ArenaManager.setPlayerArena(p.getUniqueId(), this);

        if (hideOutsidePlayers){
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
        }

        p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
        p.getInventory().setHeldItemSlot(0);

        Location minigameTP = Core.getInstance().getMinigameWaitingWorld().getSpawnLocation().clone();
        minigameTP.setPitch(-90);
        p.leaveVehicle();
        p.setGameMode(GameMode.SPECTATOR);
        p.teleport(minigameTP);
        //if (gameMode == GameMode.SPECTATOR){
            Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
                if (!p.isConnected()){
                    return;
                }

                ArenaManager.refreshPlayer(p, GameMode.SPECTATOR, true, true);
                Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
                    if (!p.isConnected()){
                        return;
                    }
                    if (p.getSpectatorTarget() == null && p.getWorld().equals(Core.getInstance().getMinigameWaitingWorld())){
                        p.setGameMode(GameMode.ADVENTURE);
                    }
                }, 1);
            },2);
        //}

        new PlayerAddedToArenaEvent(this, p, PlayerAddedToArenaEvent.AddType.AUTOMATIC).callEvent();
        return true;

    }

    public void removePlayer(Player p, PlayerRemovedFromArenaEvent.RemoveCause cause){
        playingPlayers.remove(p);
        spectators.remove(p);
        ArenaManager.unsetPlayerArena(p.getUniqueId());
        ArenaContainer container = ArenaContainer.getArenaContainer(this);
        if (container != null && !isEndingOrNotUsable()){
            container.addEarlyDisconnectPlayer(p.getUniqueId());
        }

        PlayerRemovedFromArenaEvent removedEvent;
        if (cause == PlayerRemovedFromArenaEvent.RemoveCause.JOINEDNEW && p.isOnline()){
            List<Entity> passengers = new ArrayList<>(p.getPassengers());
            for (Entity passenger : passengers){
                p.removePassenger(passenger);
            }
            Entity vehicle = p.getVehicle();
            p.leaveVehicle();
            removedEvent = new PlayerRemovedFromArenaEvent(this, container, p, cause, passengers, vehicle);
        }
        else{
            removedEvent = new PlayerRemovedFromArenaEvent(this, container, p, cause);
        }
        removedEvent.callEvent();
        if (removedEvent.isRemoveStartingPlayer()){
            startPlayers.remove(p);
        }

        if (arenaState == ArenaState.CONNECTING){
            startPlayers.remove(p);
        }

        else if (getArenaPlayers().isEmpty() && !isEndingOrNotUsable()){
            endGame(0, 0);
        }

        if (arenaType == ArenaType.SLIME && cause != PlayerRemovedFromArenaEvent.RemoveCause.UNKNOWN){
            ArenaManager.refreshPlayer(p, GameMode.SPECTATOR, true, true);
        }
        for (Player o : getArenaPlayers()){
            PlayerScoreboard board = ScoreboardUtils.getPlayerScoreboard(o);
            PlayerScoreboard.UpdatingValue.ARENA_PLAYINGPLAYERS.updateValue(board, this);
            PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS.updateValue(board, this);
            PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_STARTING.updateValue(board, this);
            PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_OUTSIDE.updateValue(board, this);
            PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_PROFILED.updateValue(board, this);
            PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_PROFILE_ACTIVE.updateValue(board, this);
            PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_PROFILE_INACTIVE.updateValue(board, this);
            PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_PROFILELESS_AND_INACTIVE.updateValue(board, this);
            PlayerScoreboard.UpdatingValue.ARENA_SPECTATINGPLAYERS_PROFILELESS.updateValue(board, this);
        }
    }

    public void sendMessage(String message, boolean messageSpectators){
        sendMessage(Component.text(message), messageSpectators);
    }

    public void sendMessage(Component message, boolean messageSpecators){
        for (Player p : getArenaPlayers()){
            if (!messageSpecators && isPlayerSpectating(p, false)){
                continue;
            }
            p.sendMessage(message);
        }
    }

    public void sendTitle(Title title){
        for (Player p : getArenaPlayers()){
            p.showTitle(title);
        }
    }


    public ArenaCountdown getCountdown() {
        return countdown;
    }

    public UUID getQueueUUID() {
        return queueUUID;
    }

    public boolean isInEndingProcess() {
        return isInEndingProcess;
    }

    public UUID getHostUUID(){
        return hostUUID;
    }

    public OfflinePlayer getHost(){
        return Bukkit.getOfflinePlayer(hostUUID);
    }

    public Player getHostAsPlayer(){
        return Bukkit.getPlayer(hostUUID);
    }

    public String getPrivateSettings(){
        return arenaSettings.privateSettings();
    }

    public boolean isHost(UUID playerUUID){
        return playerUUID.equals(hostUUID);
    }

    public boolean isPrivate(){
        return hostUUID != null;
    }

    void deleteArena(){
        queueUUID = null;
        AbilityHandler.removePlayerData(playingPlayers);
        playingPlayers.clear();
        startPlayers.clear();
        spectators.clear();
        hostUUID = null;
        arenaSettings = null;
        isUsable = false;

        if (arenaType != ArenaType.BUKKITMANUAL){
            if (mainArenaWorld != null){
                Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD+"Unloaded slime world "+ ChatColor.AQUA+ mainArenaWorld.getName()+ChatColor.GOLD+"!");
                String arenaWorldName = mainArenaWorld.getName();
                mainArenaWorld = null;
                WorldUtils.destroyWorld(Bukkit.getWorld(arenaWorldName));
            }
        }
        else{
            Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD+"Manual arena world match has finalized! ("+ ChatColor.AQUA+manualBukkitWorld.getName()+ChatColor.GOLD+")");
        }
        manualBukkitWorld = null;
        mainArenaWorld = null;
    }

    public boolean isUsable(){
        return isUsable;
    }

    public boolean isEndingOrNotUsable(){
        return isInEndingProcess() || !isUsable();
    }

    protected void startCountdown(){
        countdown.start(countdownStyle, countdownDuration);
    }


    public ArenaCountdown getArenaCountdown(){
        return countdown;
    }

    public enum ArenaType{
        SLIME,
        SLIMEMANUAL,
        BUKKITMANUAL;
    }

    public enum SpectatorType{
        ALL,
        STARTING_PLAYER,
        OUTSIDE_PLAYER,
        PROFILED_PLAYER,
        PROFILE_ACTIVE_PLAYER,
        PROFILE_INACTIVE_PLAYER,
        PROFILELESS_AND_INACTIVE_PLAYER,
        PROFILELESS_PLAYER;
    }
}
