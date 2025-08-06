package net.donnypz.mccore.version.v1_21_4;

import com.infernalsuite.asp.api.exceptions.WorldAlreadyExistsException;
import com.infernalsuite.asp.api.loaders.SlimeLoader;
import com.infernalsuite.asp.api.world.SlimeWorld;
import net.donnypz.mccore.utils.slime.WrappedSlimeLoader;
import net.donnypz.mccore.utils.slime.WrappedSlimeWorld;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class WrappedSlimeWorldImpl extends WrappedSlimeWorld<SlimeWorld> {
    public WrappedSlimeWorldImpl(SlimeWorld slimeWorld) {
        super(slimeWorld);
    }

    @Override
    public WrappedSlimeWorld<SlimeWorld> clone(@NotNull String cloneWorldName) {
        return new WrappedSlimeWorldImpl(slimeWorld.clone(cloneWorldName));
    }

    @Override
    public WrappedSlimeWorld<SlimeWorld> clone(@NotNull String cloneWorldName, WrappedSlimeLoader<?> loader) throws IOException {
        try{
            return new WrappedSlimeWorldImpl(slimeWorld.clone(cloneWorldName, (SlimeLoader) loader.getSlimeLoader()));
        }
        catch(WorldAlreadyExistsException e){
            throw new IOException(e);
        }
    }

    @Override
    public @NotNull String getName() {
        return slimeWorld.getName();
    }

    @Override
    public @NotNull WrappedSlimeLoader<?> getLoader() {
        return new WrappedSlimeLoaderImpl(slimeWorld.getLoader());
    }
}
