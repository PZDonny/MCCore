package net.donnypz.mccore.minigame.arenaManager;

import javax.annotation.Nullable;

public record ArenaSettings(String minigameName, String mode, @Nullable String mapName, int minPlayers, int maxPlayers, @Nullable String privateSettings) {}