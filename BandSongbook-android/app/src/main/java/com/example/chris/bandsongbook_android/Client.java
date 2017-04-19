package com.example.chris.bandsongbook_android;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


/**
 * App transmit/receive class
 * @author Chris Tsuei
 */
public class Client {

    private String host;
    private int port;
    private Socket socket;
    private DataInputStream in;
    private OutputStream out;

    /**
     * Connects to the server
     * @param host server ip
     * @param port server port
     * @throws IOException if invalid host or port number
     */
    public void connect (String host, int port) throws IOException {

        this.host = host;
        this.port = port;
        socket = new Socket(host, port);
        System.out.println("connected");
        this.in = new DataInputStream(socket.getInputStream());
        this.out = socket.getOutputStream();
        System.out.println("Streams set up");
    }

    /**
     * receive a JSON from the server
     * @return the read JSON packet
     * @throws IOException if invalid response
     */
    public JSONObject receiveJson() throws IOException {

        InputStreamReader receive = new InputStreamReader(in);
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

    public void send (JSONObject jsonObject) throws IOException {

        JSONObject packet = jsonObject;
        String serializePacket = packet.toString();
        StringBuilder frankenstein = new StringBuilder(serializePacket);
        frankenstein.append('\n');

        byte[] pack2 = frankenstein.toString().getBytes(StandardCharsets.UTF_8);

        out.write(pack2);
        out.flush();

        System.out.println("Sent to server packet: " + frankenstein.toString());

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
     * Test method to print json objects
     * @param test the json object to print
     */
    public static void printJSON (JSONObject test) {
        String packet = test.toString();
        System.out.println(packet);
        try {
            String response = (String) test.get("response");
            System.out.println(response);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main (String args[]) {

        Client client = new Client();


        try {

            client.connect("34.197.242.214", 54106);

//            JSONObject test = start("Best group");
//            printJSON(test);
//            client.send(test);

            JSONObject join = join("Best Group", "Chris Tsuei");
            client.send(join);
            JSONObject response = client.receiveJson();
            printJSON(response);
            System.out.println("send finished");

            JSONObject close = new JSONObject();
            close.put("request", "end session");

            client.send(close);
            response = client.receiveJson();
            System.out.println("close");
            printJSON(response);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            try {
                client.socket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
