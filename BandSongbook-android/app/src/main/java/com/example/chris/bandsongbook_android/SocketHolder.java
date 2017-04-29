package com.example.chris.bandsongbook_android;

/**
 * Created by Chris on 4/27/2017.
 * client holder class (singleton)
 */

public class SocketHolder {

    // client to be used by the application
    private static Client client;

    /**
     * getting the client
     * @return client to be used by the application
     */
    public static synchronized Client getClient() {
        return client;
    }

    /**
     * setting the client for the application
     * @param newClient the client to be used by the entire appliciation
     */
    public static synchronized void setClient(Client newClient) {
        SocketHolder.client = newClient;
    }
}
