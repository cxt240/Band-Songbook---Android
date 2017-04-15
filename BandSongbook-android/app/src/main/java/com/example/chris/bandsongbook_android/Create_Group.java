package com.example.chris.bandsongbook_android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Create_Group extends AppCompatActivity {

    private static final int FILE_SELECT_CODE = 0;
    private EditText groupName;
    private Socket socket;
    private ArrayList<String> files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // overriding StrictMode to allow for network access without AsyncTask
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__group);

        files = new ArrayList<String>();
        groupName = (EditText) findViewById(R.id.group_name);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String group = groupName.getText().toString();

                if(isNetworkAvailable()) {
                    try {
                        // connecting to server
//                        socket = new Socket("34.197.242.214", 54106);
//
//                        // Protocol for creating a new group
//                        JSONObject create = start(group);
//                        // send packet
//                        send(create);
//                        JSONObject response = receiveJson();
//
//                        if(response != null) {
//                            String status = response.getString("response");
//                            if(status.equals("ok")) {

                                // create intent and add params to bundle
                                Intent nextScreen = new Intent(getApplicationContext(), Group_Details.class);
                                nextScreen.putExtra("Group Name", group);
                                nextScreen.putExtra("Files", files);
                                nextScreen.putExtra("Bandleader", true);

                                // Start a the groupDetail activity
                                startActivity(nextScreen);
                                Log.v("Group Status", "Created");
//                            }
//                            else {
//                                Log.v("Group", "Failed Join");
//                                Context context = getApplicationContext();
//                                int duration = Toast.LENGTH_SHORT;
//                                Toast.makeText(context, status, duration);
//                            }
//                        }
//                        else {
//                            Context context = getApplicationContext();
//                            CharSequence text = "Unable to receive packet";
//                            int duration = Toast.LENGTH_SHORT;
//                            Toast.makeText(context, text, duration);
//                        }
//                        socket.close();
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
                addFile();
            }
        });
    }

    /**
     * fileChooser for the device. Starts an intent that opens the device's file manager
     */
    public void addFile() {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("*/*");
        startActivityForResult(Intent.createChooser(chooseFile, "Select file"), FILE_SELECT_CODE);
    }

    /**
     * gets the result for the filechooser
     * @param requestCode whether the request was to select a file
     * @param resultCode whether a file was chosen
     * @param resultData path to the file
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if(requestCode == FILE_SELECT_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = resultData.getData();
            if (uri.getLastPathSegment().endsWith("mxl")) {
                String path = uri.getPath();
                files.add(path);
            }
        }
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

    /**
     * sends a packet following protocols to the server
     * @param jsonObject create group packet to be sent
     * @throws IOException if there is no valid connection
     */
    public void send (JSONObject jsonObject) throws IOException {
        OutputStream out = socket.getOutputStream();

        // converting the packet to utf-8 bytes (adding a newline char to conform to protocol)
        JSONObject packet = jsonObject;
        String serializePacket = packet.toString();
        StringBuilder frankenstein = new StringBuilder(serializePacket);
        frankenstein.append('\n');

        byte[] pack2 = frankenstein.toString().getBytes(StandardCharsets.UTF_8);

        // sends packet to server
        out.write(pack2);
        out.flush();
    }

    /**
     * receive a JSON from the server
     * @return the read JSON packet
     * @throws IOException if invalid response
     */
    public JSONObject receiveJson() throws IOException {

        InputStreamReader receive = new InputStreamReader(socket.getInputStream());
        BufferedReader receiver = new BufferedReader(receive);
        JSONObject packet = null;

        try {
            packet = new JSONObject(receiver.readLine());
        }
        catch (Exception e) {

            e.printStackTrace();
        }

        return packet;
    }

}
