package com.example.chris.bandsongbook_android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;


/**
 * The main page of the Band Songbook application
 * @author Chris Tsuei
 */
public class Main extends Activity {

    private EditText name;
    private EditText groupCode;
    private boolean connected;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // overriding StrictMode to allow for network access without AsyncTask
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (EditText) findViewById(R.id.Name);
        groupCode = (EditText) findViewById(R.id.Code);

        Button create = (Button) findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent nextScreen = new Intent(getApplicationContext(), Create_Group.class);
                startActivity(nextScreen);
            }
        });

        Button group = (Button) findViewById(R.id.join);
        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String group = groupCode.getText().toString();
                String userName = name.getText().toString();
                if(isNetworkAvailable()) {

                    try {
                        // connecting to server
                        socket = new Socket("34.197.242.214", 54106);
                        String member = name.getText().toString();

                        // sending a JSON and receiving the response
                        JSONObject joiner = join(group, userName);
                        send(joiner);
                        JSONObject response = receiveJson();

                        String status = response.getString("response");
                        if(status.equals("ok")) {
                            Intent nextScreen = new Intent(getApplicationContext(), Group_Details.class);
                            nextScreen.putExtra("Group Name", group);
                            startActivity(nextScreen);
                            Log.v("Group Status", "Joined");
                        }
                        else {
                            Context context = getApplicationContext();
                            int duration = Toast.LENGTH_SHORT;
                            Toast.makeText(context, status, duration);
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    finally {
                        try {
                            socket.close();
                        }
                        catch (Exception e) {      }
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
     * Allows a user to join a group if it already exists
     * @param group name of the group that the member wants to join
     * @param name name of the member
     * @return JSON packet specifying the user wants to join a group
     */
    public static JSONObject join (String group, String name) {

        JSONObject joinGroup = new JSONObject();

        try  {
            joinGroup.put("request", "join group");
            joinGroup.put("group name", group);
            joinGroup.put("user name", name);
        }
        catch (Exception e) {
            System.out.println("failed to create join packet");
            e.printStackTrace();
        }
        return joinGroup;
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
