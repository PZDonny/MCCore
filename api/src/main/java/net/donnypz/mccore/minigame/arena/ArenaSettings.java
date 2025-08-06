package net.donnypz.mccore.minigame.arena;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public record ArenaSettings(@NotNull String minigameName, @NotNull String mode, @Nullable String mapName, int minPlayers, int maxPlayers, @Nullable String privateSettings) {}