package net.donnypz.mccore.utils.slime;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public interface SlimeHandler<T, P> {

    WrappedSlimeLoader<P> createFileLoader(@NotNull File file);

    WrappedSlimeLoader<P> createMongoLoader(@NotNull String databaseName, @NotNull String collectionName, @Nullable String username, @Nullable String password, @Nullable String authSource, @Nullable String host, @Nullable Integer port, @Nullable String uri);

    WrappedSlimeLoader<P> createMongoLoader(@NotNull String databaseName, @NotNull String collectionName, @NotNull String uri);

    WrappedSlimeWorld<T> createEmptyWorld(@NotNull String worldName, boolean readOnly);

    WrappedSlimeWorld<T> generateEmptyArenaWorld(@NotNull String emptyWorldName, @Nullable World.Environment environment);

    boolean isSlimeWorld(@NotNull String worldName);

    WrappedSlimeWorld<T> getSlimeWorld(@NotNull String worldName);

    WrappedSlimeWorld<T> getCloneFromLoader(@NotNull String worldName, @NotNull String cloneWorldName, boolean autoLoad);

}
