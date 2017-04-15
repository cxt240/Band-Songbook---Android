package com.example.chris.bandsongbook_android;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class Play extends AppCompatActivity {

    public List<String> files;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Bundle extras = getIntent().getExtras();
        Bandleader = extras.getBoolean("Bandleader");
        files = extras.getStringArrayList("Songs");
        currentSong = extras.getString("Play");

        SongName = (TextView) findViewById(R.id.SongName);
        SongName.setText(currentSong);

        songs = (Button) findViewById(R.id.songs);
        songs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] fileList = new String[files.size()];
                fileList = files.toArray(fileList);


                new android.app.AlertDialog.Builder(getApplicationContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Choose New Song")
                        .setSingleChoiceItems(fileList, selected, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selected = which;
                            }
                        })
                        .setPositiveButton("Change", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SongName.setText(files.get(selected));
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
                //TODO exit activity
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
