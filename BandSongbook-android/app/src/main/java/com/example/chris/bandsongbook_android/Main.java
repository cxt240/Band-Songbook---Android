package com.example.chris.bandsongbook_android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * The main page of the Band Songbook application
 * @author Chris Tsuei
 */
public class Main extends Activity {

    private EditText name;
    private EditText groupCode;
    private boolean connected;
    public Client client;

    /**
     * Initializing everything used in the Main activity
     * @param savedInstanceState current instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // overriding StrictMode to allow for network access without AsyncTask
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (EditText) findViewById(R.id.Name);
        groupCode = (EditText) findViewById(R.id.Code);

        // create group button
        Button create = (Button) findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent nextScreen = new Intent(getApplicationContext(), Create_Group.class);
                startActivity(nextScreen);
            }
        });

        // join group button
        Button group = (Button) findViewById(R.id.join);
        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // button clicked, initialize client and get textfields
                String group = groupCode.getText().toString();
                String userName = name.getText().toString();
                if(isNetworkAvailable()) {
                    try {
                        Client client = new Client();
                        SocketHolder.setClient(client);

                        // connecting to server and getting responses
                        JSONObject join = join(group, userName);
                        client.send(join);

                        JSONObject recv = null;
                        while(recv == null)
                        {
                            recv = client.receiveJson();
                            Thread.sleep(1);
                        }

                        // check response, if "ok", move to next activity
                        String status = recv.getString("response");
                        if(status.equals("ok")) {
                            Intent nextScreen = new Intent(getApplicationContext(), Group_Details.class);
                            nextScreen.putExtra("Group Name", group);
                            nextScreen.putExtra("Name", userName);
                            nextScreen.putExtra("Bandleader", false);
                            startActivity(nextScreen);
                            Log.v("Group Status", "Joined");
                        }
                        else {
                            // nonpositive response, close client, return message
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
     * test method used to test the receiver
     */
    public static void test()
    {
        try {

            Client client = new Client();
            // construct create group JSON
            JSONObject startGroup = new JSONObject();
            try {
                startGroup.put("request", "create group");
                startGroup.put("group name", "The Black Keys");
                startGroup.put("user name", "John Bandleader");
            }
            catch (JSONException e) {
                // fail("Should have been able to create json");

                Log.v("out","JSONERROR");
            }
            client.send(startGroup);
            // we sent the start group message.
            // it will take some time for the message to propagate.
            // we can use the non blocking recieveJson to wait for the response.
            JSONObject recv = null;
            double waitTime = 0;
            while(recv == null)
            {
                recv = client.receiveJson();
                waitTime += 0.001;
                Thread.sleep(1);
            }
            Log.v("out","Recvd message after" + waitTime + "seconds");
            Log.v("out",recv.getString("response"));
            client.close();

            //recv error now

        } catch (IOException e) {
            Log.v("out","IOERROR");
            Log.v("out",e.getMessage());
            e.printStackTrace();
            //fail("Client crashed- it should not have.");
        }
        catch (InterruptedException e) {
            Log.v("out","INTERRUPTERROR");
            //  fail("Wait was interrputed- this should not happen");
        }

        catch (JSONException e) {
        // fail("Server sent invalid JSON");
         }

        Log.v("out","finish");

    }

}
