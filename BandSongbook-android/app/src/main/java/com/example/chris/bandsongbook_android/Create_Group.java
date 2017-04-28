package com.example.chris.bandsongbook_android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class Create_Group extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 42;
    private EditText groupName;
    private ArrayList<String> songs;
    public Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // overriding StrictMode to allow for network access without AsyncTask
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__group);

        songs = new ArrayList<String>();
        groupName = (EditText) findViewById(R.id.group_name);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String group = groupName.getText().toString();

                if(isNetworkAvailable()) {
                    try {
                        // connecting to server
                        client = new Client();
                        SocketHolder.setClient(client);

                        // Protocol for creating a new group
                        JSONObject create = start(group);
                        // send packet
                        client.send(create);
                        JSONObject recv = null;
                        double waitTime = 0;
                        while(recv == null)
                        {
                            recv = client.receiveJson();
                            waitTime += 0.001;
                            Thread.sleep(1);
                        }
                        String status = recv.getString("response");
                        if(status.equals("ok")) {

                            // create intent and add params to bundle
                            Intent nextScreen = new Intent(getApplicationContext(), Group_Details.class);
                            nextScreen.putExtra("Group Name", group);
                            nextScreen.putExtra("Songs", songs);
                            nextScreen.putExtra("Bandleader", true);

                            // Start a the groupDetail activity
                            startActivity(nextScreen);
                            Log.v("Group Status", "Created");
                        }
                        else {
                            Log.v("Group", "Failed Join");
                            Context context = getApplicationContext();
                            int duration = Toast.LENGTH_SHORT;
                            Toast.makeText(context, status, duration);
                            client.close();
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Context context = getApplicationContext();
                    CharSequence text = "No Internet";
                    int duration = Toast.LENGTH_SHORT;
                    Toast.makeText(context, text, duration);
                }
            }
        });

        Button addFile = (Button) findViewById(R.id.addFile);
        addFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, READ_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        Log.v(TAG, requestCode + " " + resultCode + " " + resultData.getData().toString());
        if(requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if(resultData!= null) {
                try {
                    String result = readTextFromUri(resultData.getData());
                    Log.v(TAG, result);

                    MusicXmlParser parser = new MusicXmlParser();
                    parser.parser(result);
                    if(!songs.contains(parser.title)) {
                        songs.add(parser.title);

                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(parser.title, result);
                        editor.commit();
                    }
                }
                catch (Exception e) {e.printStackTrace();}
            }
        }
    }

    private String readTextFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        reader.close();
        inputStream.close();
        return stringBuilder.toString();
    }

    /**
     * checks if the device is connected
     * source: http://www.vogella.com/tutorials/AndroidNetworking/article.html
     * @return true if yes, else no
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * JSON object that contains the start details
     * @param name the name of the group
     * @return json object formatted correctly
     */
    public static JSONObject start (String name) {

        JSONObject startGroup = new JSONObject();

        try {
            startGroup.put("request", "create group");
            startGroup.put("group name", name);
            startGroup.put("user name", "bandleader");
        }
        catch (JSONException e) {
            System.out.println("Failed to make start packet");
        }

        return startGroup;
    }

}
