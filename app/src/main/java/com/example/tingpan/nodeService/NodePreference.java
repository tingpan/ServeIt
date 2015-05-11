package com.example.tingpan.nodeService;

import android.content.SharedPreferences;

import com.example.tingpan.nodeService.interfaces.INodePreference;

/**
 * Created by TingPan on 3/10/15.
 */
public class NodePreference implements INodePreference {
    private static SharedPreferences sharedPref;
    private static NodePreference nodePreference;
    private int portNumber;
    private int maxConnection;
    public static final String TAG = "com.serveIt.app";
    private static final String PORT_KEY = "com.serveIt.app.portNum";
    private static final String MAX_KEY = "com.serveIt.app.maxNum";

    public synchronized static NodePreference getInstance(SharedPreferences sharedPref){
        NodePreference.sharedPref = sharedPref;
        if (nodePreference == null){
            nodePreference = new NodePreference();
        }
        return nodePreference;
    }

    private NodePreference() {
        portNumber = sharedPref.getInt(PORT_KEY, 8000);
        maxConnection = sharedPref.getInt(MAX_KEY, 10);
    }

    public int getPortNumber() {
        return portNumber;
    }

    public boolean setPortNumber(int portNumber) {
        boolean result = false;
        if (portNumber >= 5000 && portNumber <= 65535) {
            this.portNumber = portNumber;
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(PORT_KEY, portNumber);
            editor.apply();
            result = true;
        }
        return result;
    }

    public int getMaxConnection() {
        return maxConnection;
    }

    public boolean setMaxConnection(int maxConnection) {
        boolean result = false;
        if (maxConnection >= 1 && maxConnection <= 1000) {
            this.maxConnection = maxConnection;
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(MAX_KEY, maxConnection);
            editor.apply();
            result = true;
        }
        return result;
    }

    @Override
    public String getType() {
        return "Real";
    }
}
