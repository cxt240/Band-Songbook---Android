package com.example.chris.bandsongbook_android;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Play extends AppCompatActivity {

    public List<String> files;
    public ArrayList<String> songList;
    public boolean Bandleader = false;
    public int selected = 0;
    public int speed = 0;

    public FloatingActionButton previous;
    public FloatingActionButton rewind2;
    public FloatingActionButton rewind;
    public FloatingActionButton stop;
    public FloatingActionButton play;
    public FloatingActionButton foward;
    public FloatingActionButton foward2;

    public Button songs;
    public Button options;

    public TextView SongName;
    public String currentSong;

    public MusicPlayer reader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Bundle extras = getIntent().getExtras();
        Bandleader = extras.getBoolean("Bandleader");
        files = extras.getStringArrayList("Songs");
        currentSong = extras.getString("Play");
        songList = extras.getStringArrayList("XML");

        reader = (MusicPlayer) findViewById(R.id.musicPlayer);
        SongName = (TextView) findViewById(R.id.SongName);
        SongName.setText(currentSong);

        songs = (Button) findViewById(R.id.songs);
        songs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> titles = new ArrayList<String>();
                for(int i = 0; i < songList.size(); i++) {
                    MusicXmlParser parser = new MusicXmlParser();
                    parser.parser(songList.get(i));
                    titles.add(parser.title);
                }
                String[] fileList = (String[])titles.toArray();
                fileList = files.toArray(fileList);

                new android.app.AlertDialog.Builder(Play.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Choose New Song")
                        .setSingleChoiceItems(fileList, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Change", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SongName.setText(files.get(selected));
                                //MusicPlayer call
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        options = (Button) findViewById(R.id.options);

        previous = (FloatingActionButton) findViewById(R.id.previous);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speed = -3;
            }
        });
        rewind2 = (FloatingActionButton) findViewById(R.id.rewind2);
        rewind2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speed = -2;
            }
        });
        rewind = (FloatingActionButton) findViewById(R.id.rewind);
        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speed = -1;
            }
        });
        stop = (FloatingActionButton) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        play = (FloatingActionButton) findViewById(R.id.Play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(speed == 0) {speed = 1;}
                else {speed = 0;}
            }
        });
        foward = (FloatingActionButton) findViewById(R.id.fast_foward);
        foward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speed = 2;
            }
        });
        foward2 = (FloatingActionButton) findViewById(R.id.fast_foward2);
        foward2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speed = 3;
            }
        });

        if(!Bandleader) {
            previous.setVisibility(View.GONE);
            rewind.setVisibility(View.GONE);
            rewind2.setVisibility(View.GONE);
            stop.setVisibility(View.GONE);
            play.setVisibility(View.GONE);
            foward.setVisibility(View.GONE);
            foward2.setVisibility(View.GONE);
        }
    }
}
