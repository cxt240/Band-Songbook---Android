package com.example.chris.bandsongbook_android;

/**
 * Created by Chris on 4/27/2017.
 * client holder class
 */

public class SocketHolder {

    private static Client client;

    public static synchronized Client getClient() {
        return client;
    }

    public static synchronized void setClient(Client newClient) {
        SocketHolder.client = newClient;
    }
}
