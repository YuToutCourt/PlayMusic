package com.example.playmusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    TextView noMusicTextView;
    //ArrayList<AudioModel> songsList;
    ArrayList<AudioModel> songsList = new ArrayList<>();

    static final String SINGLE_SONG_LIST = "songsList";
    static final String DATA_MAIN = "DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        noMusicTextView = findViewById(R.id.no_songs_text);

        if (!checkPermission()) {
            requestPermission();
            return;
        }

        /*
        if(savedInstanceState != null){
            songsList = savedInstanceState.getParcelable(SINGLE_SONG_LIST);
            Log.d("aled1", String.valueOf(songsList));
        }
        else if (getSharedPreferences(DATA_MAIN, MODE_PRIVATE).contains(SINGLE_SONG_LIST)) {
            String stringSongsList = getSharedPreferences(DATA_MAIN, MODE_PRIVATE).getString(SINGLE_SONG_LIST,"");
            Log.d("aled2", stringSongsList);
            ArrayList<String> test = new ArrayList<>(Arrays.asList(stringSongsList.split(",")));
            for(int i=0; i < test.size(); i++){
                songsList.add(test.get(i));
            }
        }
        else {
            songsList = SongsListSingleton.getInstance();
            Log.d("aled3", String.valueOf(songsList));
        }

         */


        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC +" != 0";

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,null);

        while(cursor.moveToNext()){
            AudioModel songData = new AudioModel(cursor.getString(0), cursor.getString(1), cursor.getString(2));
            if(new File(songData.getPath()).exists())
                songsList.add(songData);
        }

        if(songsList.size() == 0){
            noMusicTextView.setVisibility(View.VISIBLE);
        }
        else{
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new MusicListAdapter(songsList, getApplicationContext()));
        }

    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(MainActivity.this,"READ PERMISSION IS REQUIRED,PLEASE ALLOW FROM SETTINGS", Toast.LENGTH_SHORT).show();
        }
        else
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},123);
    }

    public void shuffleButton(View v){
        if(songsList.size() == 0) return;

        Collections.shuffle(songsList);

        MyMediaPlayerSingleton.getInstance().reset();
        MyMediaPlayerSingleton.currentIndex = 0;
        Intent intent = new Intent(this, MusicPlayerActivity.class);
        intent.putExtra("LIST",songsList);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(recyclerView != null){
            recyclerView.setAdapter(new MusicListAdapter(songsList, getApplicationContext()));
        }
    }

    public void onSaveInstanceState(Bundle saveInstanceState) {
        saveInstanceState.putString(SINGLE_SONG_LIST, String.valueOf(songsList));
        super.onSaveInstanceState(saveInstanceState);
    }


    @Override
    public void onStop(){
        super.onStop();
        SharedPreferences sharedPreferences = getSharedPreferences(DATA_MAIN,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SINGLE_SONG_LIST, String.valueOf(songsList));
        editor.apply();
    }

}