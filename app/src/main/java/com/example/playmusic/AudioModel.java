package com.example.playmusic;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class AudioModel implements Serializable {

    private String title;
    private String path;
    private String duration;
    private int numberOfListens;
    private String artist;
    private String album;


    public AudioModel(String title, String path, String duration) {
        this.title = title;
        this.path = path;
        this.duration = duration;
        this.numberOfListens = 0;
        this.artist = "Artiste inconnu";
        this.album = "Album inconnu";
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public String getDuration() {
        return duration;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getNumberOfListens() {
        return numberOfListens;
    }

    public void setNumberOfListens(int numberOfListens) {
        this.numberOfListens = numberOfListens;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }


    public void updateNbOfListens(){
        this.numberOfListens++;
    }

    public String dataToString(){
        String listenString = "écoute";
        if(this.getNumberOfListens() > 1) listenString += "s";
        return this.getArtist() + " | " + this.getAlbum() + " | " + this.getNumberOfListens() + " " + listenString;
    }

    @NonNull
    @Override
    public String toString() {
        return "AudioModel{" +
                "title='" + title + '\'' +
                ", path='" + path + '\'' +
                ", duration='" + duration + '\'' +
                ", numberOfListens=" + numberOfListens +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                '}';
    }
}
