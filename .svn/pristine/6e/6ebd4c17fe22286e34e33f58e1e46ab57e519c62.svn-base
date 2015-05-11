package com.example.tingpan.nodeService.mockInstance;

import com.example.tingpan.nodeService.interfaces.INodePreference;

/**
 * Created by TingPan on 3/24/15.
 */
public class MockNodePreference implements INodePreference {
    private static MockNodePreference mockNodePreference = new MockNodePreference();
    private int portNumber = 3000;
    private int maxConnection = 20;

    private MockNodePreference(){}

    public static MockNodePreference getInstance(){
        return mockNodePreference;
    }
    @Override
    public int getPortNumber() {
        return portNumber;
    }

    @Override
    public boolean setPortNumber(int portNumber) {
        boolean result = false;
        if (portNumber >= 5000 && portNumber <= 65535) {
            this.portNumber = portNumber;
            result = true;
        }
        return result;
    }

    @Override
    public int getMaxConnection() {
        return maxConnection;
    }

    @Override
    public boolean setMaxConnection(int maxConnection) {
        boolean result = false;
        if (maxConnection >= 1 && maxConnection <= 1000) {
            this.maxConnection = maxConnection;
            result = true;
        }
        return result;
    }

    @Override
    public String getType() {
        return "Mock";
    }
}
