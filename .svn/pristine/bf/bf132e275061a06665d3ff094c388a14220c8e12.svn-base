package com.example.tingpan.nodeService.mockInstance;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.tingpan.nodeService.interfaces.MyBinder;
import com.example.tingpan.nodeService.NodeService;

import java.io.File;

/**
 * Created by TingPan on 3/24/15.
 */
public class MockNodeService extends Service {

    private static NodeService.NodeState nodeState = NodeService.NodeState.FAILED;
    private static String result = null;
    private static String ipAddress = "No Networks";
    private NodeBinder myBinder = new NodeBinder();
    public class NodeBinder extends MyBinder {
        @Override
        public String startNode(File hostFolder) {
            nodeState = NodeService.NodeState.RUNNING;
            return result;
        }

        @Override
        public void stopNode() {
            nodeState = NodeService.NodeState.READY;
        }

        @Override
        public NodeService.NodeState getNodeState() {
            return nodeState;
        }

        @Override
        public String getIpAddress() {
            return ipAddress;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public static void setNodeState(NodeService.NodeState nodeState){
        MockNodeService.nodeState = nodeState;
    }
    public static NodeService.NodeState getNodeState(){
        return nodeState;
    }
    public static void setResult(String result){
        MockNodeService.result = result;
    }

    public static void setIpAddress(String ipAddress){
        MockNodeService.ipAddress = ipAddress;
    }
}
