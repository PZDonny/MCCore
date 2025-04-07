package net.donnypz.mccore.minigame;

public enum GameState {
    CONNECTING, //Server is connecting players
    PLAYING, //Game has started
    ENDING, //Game is ending and will be deleted soon
    DELETED; //Game is over and world has been deleted

}
