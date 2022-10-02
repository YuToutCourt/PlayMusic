package com.example.playmusic;

import java.util.ArrayList;

public class SongsListSingleton {

    private static ArrayList<AudioModel> songsList = null;
    
    public static ArrayList<AudioModel> getInstance(){
        if (songsList ==  null){
            songsList = new ArrayList<>();
        }
        return songsList;
    }
}
