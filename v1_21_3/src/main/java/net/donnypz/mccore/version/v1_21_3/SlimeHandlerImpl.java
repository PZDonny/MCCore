package net.donnypz.mccore.version.v1_21_3;

import com.infernalsuite.aswm.api.AdvancedSlimePaperAPI;
import com.infernalsuite.aswm.api.exceptions.CorruptedWorldException;
import com.infernalsuite.aswm.api.exceptions.NewerFormatException;
import com.infernalsuite.aswm.api.exceptions.UnknownWorldException;
import com.infernalsuite.aswm.api.exceptions.WorldAlreadyExistsException;
import com.infernalsuite.aswm.api.loaders.SlimeLoader;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import com.infernalsuite.aswm.api.world.properties.SlimeProperties;
import com.infernalsuite.aswm.api.world.properties.SlimePropertyMap;
import com.infernalsuite.aswm.loaders.file.FileLoader;
import com.infernalsuite.aswm.loaders.mongo.MongoLoader;
import net.donnypz.mccore.utils.slime.SlimeHandler;
import net.donnypz.mccore.utils.slime.SlimeUtils;
import net.donnypz.mccore.utils.slime.WrappedSlimeWorld;
import net.donnypz.mccore.version.CoreAPI;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public class SlimeHandlerImpl implements SlimeHandler<SlimeWorld, SlimeLoader> {
    AdvancedSlimePaperAPI api = AdvancedSlimePaperAPI.instance();

    @Override
    public WrappedSlimeLoaderImpl createFileLoader(@NotNull File file) {
        return new WrappedSlimeLoaderImpl(new FileLoader(file));
    }

    @Override
    public WrappedSlimeLoaderImpl createMongoLoader(@NotNull String databaseName, @NotNull String collectionName, @Nullable String username, @Nullable String password, @Nullable String authSource, @Nullable String host, @Nullable Integer port, @Nullable String uri) {
        MongoLoader loader = new MongoLoader(databaseName, collectionName, username, password, authSource, host, port, uri);
        return new WrappedSlimeLoaderImpl(loader);
    }

    @Override
    public WrappedSlimeLoaderImpl createMongoLoader(@NotNull String databaseName, @NotNull String collectionName, @NotNull String uri){
        return createMongoLoader(databaseName, collectionName, null, null, null, null, null, uri);
    }

    @Override
    public WrappedSlimeWorldImpl createEmptyWorld(@NotNull String worldName, boolean readOnly) {
        AdvancedSlimePaperAPI.instance().createEmptyWorld(worldName, readOnly, new SlimePropertyMap(), null);
        return null;
    }

    @Override
    public WrappedSlimeWorld<SlimeWorld> generateEmptyArenaWorld(@NotNull String emptyWorldName, World.@Nullable Environment environment) {
        SlimePropertyMap properties = new SlimePropertyMap();
        if (environment != null){
            properties.setValue(SlimeProperties.ENVIRONMENT, environment.name());
        }

        SlimeWorld emptyWorld = api.createEmptyWorld(emptyWorldName, true, properties, null);
        if (emptyWorld == null){
            return null;
        }
        else{
            Bukkit.getScheduler().runTask(CoreAPI.getPlugin(), () -> api.loadWorld(emptyWorld, true));
            return new WrappedSlimeWorldImpl(emptyWorld);

        }
    }

    @Override
    public boolean isSlimeWorld(@NotNull String worldName) {
        return api.getLoadedWorld(worldName) != null;
    }

    @Override
    public WrappedSlimeWorldImpl getSlimeWorld(@NotNull String worldName) {
        SlimeWorld sw = api.getLoadedWorld(worldName);
        return sw == null ? null : new WrappedSlimeWorldImpl(sw);
    }

    @Override
    public WrappedSlimeWorldImpl getCloneFromLoader(@NotNull String worldName, @NotNull String cloneWorldName, boolean autoLoad) {
        try{
            SlimeLoader slimeLoader = (SlimeLoader) SlimeUtils.getDefaultLoader().getSlimeLoader();
            SlimeWorld world = api.readWorld(slimeLoader, worldName, true, new SlimePropertyMap());
            if (world == null){
                throw new RuntimeException("World does not exist: "+worldName);
            }

            try{
                SlimeWorld cloneWorld = world.clone(cloneWorldName, null);
                if (autoLoad){
                    Bukkit.getScheduler().runTask(CoreAPI.getPlugin(), () ->{
                        api.loadWorld(cloneWorld, true);
                    });
                }

                return new WrappedSlimeWorldImpl(cloneWorld);
            }
            catch (IllegalArgumentException | IOException | WorldAlreadyExistsException e) {
                throw new RuntimeException(e);
            }

        }
        catch(CorruptedWorldException | NewerFormatException | UnknownWorldException | IOException e){
            throw new RuntimeException(e);
        }
    }
}
