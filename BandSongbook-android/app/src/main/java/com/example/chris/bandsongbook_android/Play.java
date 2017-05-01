package com.example.chris.bandsongbook_android;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Activity to display the MusicXMl file
 * @author Chris Tsuei
 */
public class Play extends AppCompatActivity {

    public List<String> files;
    public ArrayList<String> partNames;
    public boolean Bandleader = false;
    public boolean receive;
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
    public Client client;

    /**
     * initializing all fields for this instance of the activity
     * @param savedInstanceState current instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        // overriding StrictMode to allow for network access without AsyncTask
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // getting all initial fields and setting global variables
        client = SocketHolder.getClient();
        Bundle extras = getIntent().getExtras();
        Bandleader = extras.getBoolean("Bandleader");
        files = extras.getStringArrayList("Songs");
        currentSong = extras.getString("Play");
        receive = true;
        reader = (MusicPlayer) findViewById(R.id.musicPlayer);
        SongName = (TextView) findViewById(R.id.SongName);
        SongName.setText(currentSong);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String xmlString = sharedPref.getString(currentSong, "Not available");
        MusicXmlParser read = new MusicXmlParser();
        read.parser(xmlString);
        reader.songChanged(currentSong, 0, getApplicationContext());
        partNames = read.partNames;

        // handler to take care of the bottom row of buttons (specifically their responses)
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                if(speed == 1) { // play
                    reader.current += reader.divSeconds * 0.01;
                    reader.current_end += reader.divSeconds * 0.01;
                }
                else if (speed == -3){  // back to start
                    reader.current_end = reader.divisions * 2;
                    reader.current = reader.divisions * -1;
                }
                else if (speed == -2) { // skip back 4
                    int current_meas = (int)(reader.current / reader.divisions);
                    reader.current = reader.divisions * (current_meas - 4);
                    reader.current_end = reader.divisions * (current_meas - 1);
                    speed = 0;
                }
                else if (speed == -1) { // back 2 measures
                    int current_meas = (int)(reader.current / reader.divisions);
                    reader.current = reader.divisions * (current_meas - 2);
                    reader.current_end = reader.divisions * (current_meas + 1);
                    speed = 0;
                }
                else if (speed == 2) { // forward 2
                    int current_meas = (int)(reader.current / reader.divisions);
                    reader.current = reader.divisions * (current_meas + 2);
                    reader.current_end = reader.divisions * (current_meas + 5);
                    speed = 0;
                }
                else if (speed == 3) { // forward 4
                    int current_meas = (int)(reader.current / reader.divisions);
                    reader.current = reader.divisions * (current_meas + 4);
                    reader.current_end = reader.divisions * (current_meas + 7);
                    speed = 0;
                }
                reader.invalidate();
                handler.postDelayed(this, 100);
            }
        };
        handler.postDelayed(r, 100);

        // song button (change song)
        songs = (Button) findViewById(R.id.songs);
        songs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> titles = new ArrayList<String>();
                for(int i = 0; i < files.size(); i++) {
                    files.get(i);
                }
                String[] fileList = titles.toArray(new String[titles.size()]);
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
                                try {
                                    receive = false;
                                    JSONObject packet = nextSong(selected);
                                    client.send(packet);
                                    JSONObject recv = null;
                                    while (recv == null &&receive) {
                                        try {
                                            recv = client.receiveJson();
                                            Thread.sleep(1);
                                            packetHandler(recv);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    try {
                                        if (recv.has("response") && recv.getString("response").equals("ok")) {
                                            reader.songChanged(files.get(selected), 0, getApplicationContext());
                                            SongName.setText(files.get(selected));
                                            speed = 0;
                                            reader.invalidate();
                                        }
                                    } catch (Exception e) {e.printStackTrace();}
                                    receive = true;
                                }
                                catch (Exception e) {e.printStackTrace();}
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        // change part button
        options = (Button) findViewById(R.id.options);
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] partList = new String[partNames.size()];
                partList = partNames.toArray(partList);
                final boolean select[] = new boolean[partNames.size()];
                new android.app.AlertDialog.Builder(Play.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Choose Parts")
                        .setMultiChoiceItems(partList, select, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                select[which] = isChecked;
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int part = 0;
                                for(int i = 0; i < select.length;i++) {
                                    if(select[i]) {part = i;};
                                }
                                speed = 0;
                                reader.songChanged(currentSong, part, getApplicationContext());

                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();

            }
        });

        // bottom row of buttons
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
            public void onClick(View v) { // stop button, closes activity if the response is "ok"
                try {
                    receive = false;
                    client.send(stop());
                    JSONObject recv = null;
                    while (recv == null) {
                        try {
                            recv = client.receiveJson();
                            Thread.sleep(1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if(recv.has("response") && recv.getString("response").equals("ok")) {
                        finish();
                    }
                }
                catch (Exception e) {e.printStackTrace();}
            }
        });

        // play button for if the bandleader wishes to start the session playback
        play = (FloatingActionButton) findViewById(R.id.Play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(speed == 0) {
                    receive = false;
                    JSONObject starter = playbackStart((int) (reader.current / reader.divisions),1, 40);
                    try {
                        client.send(starter);
                        JSONObject recv = null;
                        while (recv == null) {
                            try {
                                recv = client.receiveJson();
                                Thread.sleep(1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if(recv.has("response") && recv.getString("response").equals("ok")) {
                            speed = 1;
                            receive = true;
                        }
                    }
                    catch (Exception e) {e.printStackTrace();}
                }
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

        // no access for buttons if the user isn't the bandleader
        if(!Bandleader) {
            previous.setVisibility(View.GONE);
            rewind.setVisibility(View.GONE);
            rewind2.setVisibility(View.GONE);
            stop.setVisibility(View.GONE);
            play.setVisibility(View.GONE);
            foward.setVisibility(View.GONE);
            foward2.setVisibility(View.GONE);
        }

        // initializes the recevier for this activity
       // receiver();
    }

    /**
     * handles all packets that need to be received (outside of the packets where we expect reponses)
     */
    public void receiver() {
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                JSONObject recv = null;
                while (recv == null &&receive) {
                    try {
                        recv = client.receiveJson();
                        Thread.sleep(1);
                        packetHandler(recv);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        handler.postDelayed(r, 10);
    }

    /**
     * handles any packets received by the receiver method
     * @param packet the packet to be handled
     */
    public void packetHandler(JSONObject packet) {
        if(packet.has("session")) {
            try { // S4 packet
                String response = packet.getString("session");
                if(response.equals("switch")) { // S4 packet
                    int number = packet.getInt("song id");
                    reader.songChanged(files.get(number), 0, getApplicationContext());
                    SongName.setText(files.get(selected));
                    speed = 0;
                    reader.invalidate();
                }
                else if (response.equals("begin playback")) { // S5 packet
                    speed = 1;
                }
                else if (response.equals("stop playback")) { // S6 packet
                    finish();
                }
            }
            catch (Exception e) {e.printStackTrace();}
        }
    }

    /**
     * C4 packet generator
     * @param number the song index of the song to be played
     * @return the C4 packet
     */
    public JSONObject nextSong(int number) {
        try {
            JSONObject next = new JSONObject();
            next.put("request", "switch song");
            next.put("song id", number);
            return next;
        }
        catch (Exception e) {e.printStackTrace();}
        return null;
    }

    /**
     * C5 packet generator
     * @param number the measure number
     * @param tempo tempo to be played back
     * @param time until playback starts
     * @return C5 packet
     */
    public JSONObject playbackStart(int number, int tempo, int time) {
        try {
            JSONObject playback = new JSONObject();
            playback.put("request", "begin playback");
            playback.put("measure", number);
            playback.put("tempo", tempo);
            playback.put("time", time);
            return playback;
        }
        catch (Exception e) {e.printStackTrace();}
        return null;
    }

    /**
     * C6 packet generator
     * @return C6 packet
     */
    public JSONObject stop() {
        try {
            JSONObject stop = new JSONObject();
            stop.put("request", "stop playback");
            return stop;
        }
        catch (Exception e) {e.printStackTrace();}
        return null;
    }
}
