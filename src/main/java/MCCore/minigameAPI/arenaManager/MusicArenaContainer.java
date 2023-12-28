package MCCore.minigameAPI.arenaManager;

import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.Fade;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import org.bukkit.entity.Player;

public abstract class MusicArenaContainer extends ArenaContainer{

    protected RadioSongPlayer radio = null;
    protected Playlist playlist;
    private Fade fade;

    public MusicArenaContainer(Arena arena, int gameDurationInSeconds) {
        super(arena, gameDurationInSeconds);
    }


    public void setRadioPlaying(boolean play){
        if (fade == null){
            radio.setPlaying(play);
        }
        else{
            radio.setPlaying(play, fade);
        }

    }

    public void playRadio(Song song, int volume){
        radio = new RadioSongPlayer(song);
        playRadio(volume, null);
    }

    public void playRadio(Song song, int volume, Fade fade){
        radio = new RadioSongPlayer(song);
        playRadio(volume, fade);
    }

    public void playRadioWithPlaylist(int volume){
        if (playlist == null){
            return;
        }
        radio = new RadioSongPlayer(playlist);
        playRadio(volume, null);
    }

    public void playRadioWithPlaylist(int volume, Fade fade){
        if (playlist == null){
            return;
        }
        radio = new RadioSongPlayer(playlist);
        playRadio(volume, fade);
    }

    private void playRadio(int volume, Fade fade){
        radio.setCategory(com.xxmicloxx.NoteBlockAPI.model.SoundCategory.MASTER);
        radio.setVolume((byte) volume);

        for (Player p : arena.getArenaPlayers()){
            if (!p.isOnline()) continue;
            radio.addPlayer(p);
        }
        setRadioPlaying(true);

        radio.setRepeatMode(RepeatMode.ALL);
    }

    public void addSongToPlaylist(Song song){
        if (playlist == null){
            playlist = new Playlist(song);
        }
        else{
            playlist.add(song);
        }
    }

    public void addPlayerToRadio(Player p){
        if (!radio.getPlayerUUIDs().contains(p.getUniqueId())){
            radio.addPlayer(p);
        }

    }

    public void removePlayerFromRadio(Player p){
        if (radio == null) return;
        if (radio.getPlayerUUIDs().contains(p.getUniqueId())){
            radio.removePlayer(p);
        }
    }

    @Override
    public void removeArena(){
        allContainedArenas.remove(arena);
        destroyRadio();
        deleteArena();
    }

    public void destroyRadio() {
        if (radio == null) return;
        radio.destroy();
    }
}
