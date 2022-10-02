package com.example.playmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {

    private TextView titleTv;
    private TextView currentTimeTv;
    private TextView totalTimeTv;
    private SeekBar seekBar;
    private ImageView pausePlay,nextBtn,previousBtn,musicIcon;
    private ArrayList<AudioModel> songsList;
    private AudioModel currentSong;
    private MediaPlayer mediaPlayer = MyMediaPlayerSingleton.getInstance();

    private void bouncingImage(ImageView img){
        final Animation animation = AnimationUtils.loadAnimation(this,R.anim.bounce);
        img.startAnimation(animation);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        this.titleTv = findViewById(R.id.song_title);
        this.currentTimeTv = findViewById(R.id.current_time);
        this.totalTimeTv = findViewById(R.id.total_time);
        this.seekBar = findViewById(R.id.seek_bar);
        this.pausePlay = findViewById(R.id.pause_play);
        this.nextBtn = findViewById(R.id.next);
        this.previousBtn = findViewById(R.id.previous);
        this.musicIcon = findViewById(R.id.music_icon_big);

        this.titleTv.setSelected(true);

        this.songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");

        setResourcesWithMusic();

        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTv.setText(convertToMMSS(mediaPlayer.getCurrentPosition()+""));


                    Log.d("Current music", currentSong.dataToString());
                    Log.d("Current music", currentSong.getTitle());


                    if(mediaPlayer.isPlaying()){
                        pausePlay.setImageResource(R.drawable.pause);
                    }else{
                        pausePlay.setImageResource(R.drawable.play);
                    }

                    if(!mediaPlayer.isPlaying() && mediaPlayer.getCurrentPosition() >= mediaPlayer.getDuration()){
                        playNextSong();
                    }

                }
                new Handler().postDelayed(this,100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setResourcesWithMusic() {
        this.currentSong = songsList.get(MyMediaPlayerSingleton.currentIndex);
        this.currentSong.updateNbOfListens();

        this.titleTv.setText(this.currentSong.getTitle());

        this.totalTimeTv.setText(convertToMMSS(currentSong.getDuration()));

        pausePlay.setOnClickListener(v-> pausePlay());
        nextBtn.setOnClickListener(v-> playNextSong());
        previousBtn.setOnClickListener(v-> playPreviousSong());

        playMusic();
    }

    private void playMusic(){

        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void playNextSong(){

        if(MyMediaPlayerSingleton.currentIndex == this.songsList.size()-1)
            return;
        MyMediaPlayerSingleton.currentIndex +=1;
        mediaPlayer.reset();
        setResourcesWithMusic();
        this.bouncingImage(nextBtn);

    }

    private void playPreviousSong(){
        if(MyMediaPlayerSingleton.currentIndex == 0)
            return;
        MyMediaPlayerSingleton.currentIndex -= 1;
        mediaPlayer.reset();
        setResourcesWithMusic();
        this.bouncingImage(previousBtn);
    }

    private void pausePlay(){
        if(mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();
        this.bouncingImage(pausePlay);
    }

    @SuppressLint("DefaultLocale")
    public static String convertToMMSS(String duration){
        long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
}