package com.example.chris.bandsongbook_android;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


/**
 * App transmit/receive class
 *
 * @author Chris Tsuei
 */
public class Client {

    private String host = "34.197.242.214";
    private int port = 54106;
    private Socket socket;
    private DataInputStream in;
    private OutputStream out;
    //can probably use a stringbuilder for better performace but this should be fine for now
    private String buffer = "";

    // will throw if connection failed. This should not happen, unless the server goes down.
    public Client() throws IOException
    {
        socket = new Socket(host, port);
        this.in = new DataInputStream(socket.getInputStream());
        this.out = socket.getOutputStream();
    }

    /**
     * receive a JSON from the server
     *
     * @return the read JSON packet
     * @throws IOException if invalid response
     */
    public JSONObject receiveJson() throws IOException {

        //first, zip any available bytes into the buffer
        int bytesAvailable = in.available();
        for(int i = bytesAvailable; i != 0; i--)
        {
            buffer += (char)(in.read());
        }

        Log.v("out",buffer);
        //if buffer contains a new line, then complete JSON was recv'd
        // In case two messages were split, make sure to save rest of buffer.
        if(buffer.contains("\n")) {
            int split = buffer.indexOf("\n");
            String message = buffer.substring(0,split);
            buffer = buffer.substring(split+1,buffer.length());
            JSONObject messageAsJSON = null;
            try {
                messageAsJSON = new JSONObject(message);
            } catch (Exception e) {
                // Server sent you a bad JSON- this should never happen!!
                e.printStackTrace();
            }
            return messageAsJSON;
        }

        //nothing new from server, yet.
        return null;
    }

    // this method is fine.
    public void send(JSONObject jsonObject) throws IOException {
        JSONObject packet = jsonObject;
        String serializePacket = packet.toString();
        StringBuilder frankenstein = new StringBuilder(serializePacket);
        frankenstein.append('\n');
        byte[] pack2 = frankenstein.toString().getBytes(StandardCharsets.UTF_8);
        out.write(pack2);
        out.flush();
        System.out.println("Sent to server: " + frankenstein.toString());
    }

    public void close() {
        try {
            socket.close();
        } catch (Exception e) {
            //we couldn't close the socket, quietly ignore.
        }
    }

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
            System.out.println("Recvd message after" + waitTime + "seconds");
            //assertEquals(recv.getString("response"),"ok");

        } catch (IOException e) {
            //fail("Client crashed- it should not have.");
        }
        catch (InterruptedException e) {
            //  fail("Wait was interrputed- this should not happen");
        }

        //    catch (JSONException e) {
        // fail("Server sent invalid JSON");
        //   }
    }


    /**

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



     * JSON object that contains the start details
     * @param name the name of the group
     * @return json object formatted correctly
     */
}


// example




