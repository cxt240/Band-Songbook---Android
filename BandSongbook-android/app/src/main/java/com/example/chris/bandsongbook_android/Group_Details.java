package com.example.chris.bandsongbook_android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ExpandedMenuView;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Group_Details extends AppCompatActivity {

    public String GroupName;
    public Boolean bandleader;
    public Client client;
    public ArrayList<String> songs;
    public ArrayList<String> members;
    public Members memberFrag;
    public Files fileFrag;
    public boolean check;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = SocketHolder.getClient();
        // get passed params
        Bundle field = getIntent().getExtras();
        GroupName = field.getString("Group Name");
        bandleader = field.getBoolean("Bandleader");
        check = true;
        if(bandleader) {
            songs = field.getStringArrayList("Songs");
            members = new ArrayList<String>();
            members.add("Bandleader");
        }
        else  {
            songs = new ArrayList<String>();
            members = new ArrayList<String>();
            members.add("Bandleader");
            members.add(field.getString("Name"));
        }
        setContentView(R.layout.activity_group__details);

        getSupportActionBar().setTitle("Group " + GroupName + " Details");
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                if(check) {
                    JSONObject recv = null;
                    while (recv == null && check) {
                        try {
                            recv = client.receiveJson();
                            Thread.sleep(1);
                            packetHandler(recv);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        };
        handler.postDelayed(r, 10);
    }


    public JSONObject begin() {
        try {
            JSONObject session = new JSONObject();
            session.put("request", "begin session");
            String[] songList = new String[songs.size()];
            for(int i = 0; i < songs.size(); i++) {
                String song = songs.get(i);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                String xmlString = sharedPref.getString(song, "Not available");
                songList[i] = xmlString;
            }
            session.put("songs", songList);
            return session;
        }
        catch (Exception e) {e.printStackTrace();}
        return null;
    }

    public void packetHandler(JSONObject packet) {
        try {
            if (packet.has("group members")) {
                JSONArray output = packet.getJSONArray("group members");
                String[] memberList = new String[output.length()];
                for(int i = 0; i < output.length(); i++) {
                    memberList[i] = output.getString(i);
                }
                memberFrag.updateMembers(memberList);
            }
            else if(packet.has("session")) {
                String outcome = packet.getString("session");
                if(outcome.equals("start")) {
                    JSONArray output = packet.getJSONArray("songs");
                    for(int i = 0; i < output.length(); i++) {
                        String XMLString = output.getString(i);
                        MusicXmlParser parser = new MusicXmlParser();
                        parser.parser(XMLString);
                        if (!songs.contains(parser.title)) {
                            fileFrag.add(XMLString);
                        }
                    }
                    check = false;
                    Intent play = new Intent(this, Play.class);
                    play.putExtra("Songs", songs);
                    play.putExtra("Bandleader", bandleader);
                    play.putExtra("Play", songs.get(0));
                    startActivity(play);
                }
                else if (outcome.equals("end")) {
                    client.close();
                    finish();
                }
            }
        }
        catch (Exception e) {e.printStackTrace();}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group__details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to leave the group?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Client current = SocketHolder.getClient();
                        current.close();
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    memberFrag = new Members();
                    return memberFrag;
                case 1:
                    fileFrag = new Files();
                    return fileFrag;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Group Members";
                case 1:
                    return "Group Files";
            }
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        check = true;
        return;
    }
}
