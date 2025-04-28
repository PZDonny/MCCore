package net.donnypz.mccore.minigame.arena;

import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.model.playmode.MonoStereoMode;
import com.xxmicloxx.NoteBlockAPI.songplayer.Fade;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class MusicArenaContainer extends ArenaContainer{

    protected RadioSongPlayer radio = null;
    protected Playlist playlist = null;
    private Fade fade;

    public MusicArenaContainer(Arena arena, int gameDurationInSeconds) {
        super(arena, gameDurationInSeconds);
    }

    public MusicArenaContainer(Arena arena, int gameDurationInSeconds, Fade fade) {
        super(arena, gameDurationInSeconds);
        this.fade = fade;
    }

    public void resumeRadio(){
        radio.setPlaying(true, fade);
    }

    public void pauseRadio(){
        radio.setPlaying(false, fade);
    }

    public void playRadio(@NotNull Song song, int volume, boolean loop){
        playRadio(song, volume, loop, null);
    }

    public void playRadio(@NotNull Song song, int volume, boolean loop, @Nullable Fade fade){
        playlist = new Playlist(song);
        radio = new RadioSongPlayer(playlist);
        this.fade = fade;
        playRadio(volume, loop, fade);
    }

    public void playRadio(@NotNull Playlist playlist, int volume, boolean loop){
        playRadio(playlist, volume, loop, null);
    }

    public void playRadio(@NotNull Playlist playlist, int volume, boolean loop, @Nullable Fade fade){
        this.playlist = playlist;
        radio = new RadioSongPlayer(playlist);
        playRadio(volume, loop, fade);
    }

    private void playRadio(int volume, boolean loop, Fade fade){
        if (radio.getPlaylist() != playlist){
            radio.setPlaylist(playlist);
        }
        radio.setPlaying(true, fade);
        radio.setCategory(com.xxmicloxx.NoteBlockAPI.model.SoundCategory.MASTER);
        radio.setVolume((byte) volume);
        radio.setChannelMode(new MonoStereoMode());

        for (Player p : arena.getArenaPlayers()){
            if (!p.isOnline()) continue;
            radio.addPlayer(p);
        }
        resumeRadio();

        if (!loop){
            radio.setRepeatMode(RepeatMode.NO);
        }
        else{
            radio.setRepeatMode(RepeatMode.ALL);
        }
    }

    public void addSong(Song song){
        if (playlist != null){
            playlist.add(song);
        }
        else{
            playlist = new Playlist(song);
        }

    }

    public void addPlayerToRadio(Player p){
        if (radio != null && !radio.getPlayerUUIDs().contains(p.getUniqueId()) && arena.getArenaPlayers().contains(p)){
            radio.addPlayer(p);
        }

    }

    public void removePlayerFromRadio(Player p){
        if (radio == null){
            return;
        }
        if (radio.getPlayerUUIDs().contains(p.getUniqueId())){
            radio.removePlayer(p);
        }
    }

    @Override
    public void delete(){
        super.delete();
        destroyRadio();
    }

    public void destroyRadio() {
        if (radio == null){
            return;
        }
        radio.destroy();
        radio = null;
        fade = null;
    }
}
