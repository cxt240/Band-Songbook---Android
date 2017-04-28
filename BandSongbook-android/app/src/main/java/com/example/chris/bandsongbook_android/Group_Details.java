package com.example.chris.bandsongbook_android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONObject;

import java.util.ArrayList;

public class Group_Details extends AppCompatActivity {

    public String GroupName;
    public Boolean bandleader;
    public ArrayList<String> songs;
    public ArrayList<String> members;

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

        // get passed params
        Bundle field = getIntent().getExtras();
        GroupName = field.getString("Group Name");
        bandleader = field.getBoolean("Bandleader");

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
                    Members mem_fragment = new Members();
                    return mem_fragment;
                case 1:
                    Files file_fragment = new Files();
                    return file_fragment;
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
}
