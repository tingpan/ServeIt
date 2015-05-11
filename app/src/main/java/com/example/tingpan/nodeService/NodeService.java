package com.example.tingpan.nodeService;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.os.StatFs;
import android.util.Log;

import com.example.tingpan.nodeService.interfaces.MyBinder;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by TingPan on 2/11/15.
 */
public class NodeService extends Service {
    public static final String PROGRESS_ACTION = "com.node.progress";
    public static final String EVENT_ACTION = "com.node.event";
    private static final String FOLDER_NAME = "nodeFiles";
    private static final String NODE_NAME = "nodeFiles/node";
    private static final String MODULE_NAME = "nodeFiles/node_modules";
    private static final String SERVER_NAME = "nodeFiles/socket.js";
    private static final String PACKAGE_NAME = "nodeFiles/package.json";
    private static final String TAG = "node-service";
    public enum NodeState {INIT, NEW, FAILED, READY, RUNNING, NO_MEMORY}
    private NodeState nodeState = NodeState.NEW;
    private Process nodeProcess;
    private NodeBinder myBinder = new NodeBinder();
    private NodePreference nodePreference;
    private File fileDir;
    private File nodeApp;
    private File serverScript;
    private File nodeModules;
    private File nodePackage;
    private Intent broadcastIntent = new Intent();

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public class NodeBinder extends MyBinder {

        @Override
        public String startNode(File hostFolder) {
            String result = null;
            if (nodeState == NodeState.READY) {
                Log.d(TAG, "node starting...");
                if (!hostFolder.exists()){
                    return result;
                }
                ProcessBuilder nodeServer = new ProcessBuilder(nodeApp.getAbsolutePath(), serverScript.getAbsolutePath(), hostFolder.getAbsolutePath(),
                        String.valueOf(nodePreference.getPortNumber()),String.valueOf(nodePreference.getMaxConnection()));
                nodeServer.directory(fileDir);
                nodeServer.redirectErrorStream(true); //put the error info to standard output.
                try {
                    nodeProcess = nodeServer.start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(nodeProcess.getInputStream(), "UTF-8"));
                    result = reader.readLine();
                    Log.d(TAG, "Got result: " + result);
                    nodeState = NodeState.RUNNING;
                } catch (Exception e) {
                    Log.w(TAG, "Error reading from node: " + e);
                }
            }
            return result;
        }

    @Override
        public void stopNode() {
        if (nodeProcess != null) {
            nodeProcess.destroy();
        }
        if (nodeState == NodeState.RUNNING) {
            nodeState = NodeState.READY;
        }


    }

    @Override
        public NodeState getNodeState() {
            return nodeState;
        }

        @Override
        public String getIpAddress() {
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                     en.hasMoreElements(); ) {
                    NetworkInterface networkInterface = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddress = networkInterface.getInetAddresses();
                         enumIpAddress.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddress.nextElement();
                        if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
            return "No Networks";
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fileDir = getFilesDir();
        nodeApp = new File(fileDir, NODE_NAME);
        serverScript = new File(fileDir, SERVER_NAME);
        nodeModules = new File(fileDir, MODULE_NAME);
        nodePackage = new File(fileDir, PACKAGE_NAME);
        nodePreference = NodePreference.getInstance(getSharedPreferences(NodePreference.TAG, MODE_PRIVATE));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        nodeState = NodeState.NEW;
        if (!nodeApp.exists() || !serverScript.exists() || !nodeModules.exists() || !nodePackage.exists()) {
            nodeState = NodeState.INIT;
        }

        PreLoading task = new PreLoading();
        task.execute();
        return START_NOT_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (nodeProcess != null){
            nodeProcess.destroy();
        }
    }

    private void checkNode() {
        sendMyBroadcast(PROGRESS_ACTION, "Checking Files...");
        if (nodeState == NodeState.NO_MEMORY){
            return;
        }
        if (!nodeApp.exists() || !serverScript.exists() || !nodeModules.exists()) {
            nodeState = NodeState.FAILED;
            Log.e(TAG, "File does not exist");
            sendMyBroadcast(PROGRESS_ACTION, "File is not complete");
            return;
        } else {
            if (!makeNodeExecutable()) {
                nodeState = NodeState.FAILED;
                sendMyBroadcast(PROGRESS_ACTION, "File Can not be executed");
                return;
            }
        }
        sendMyBroadcast(PROGRESS_ACTION, "Checking Versions");
        String version = getNodeVersion();
        if (!version.equals("v0.11.12")) {
            nodeState = NodeState.FAILED;
            Log.e(TAG, "Node file is not correct");
            sendMyBroadcast(PROGRESS_ACTION, "Wrong Version");
            return;
        }
        nodeState = NodeState.READY;
        sendMyBroadcast(PROGRESS_ACTION, "Checking Completed");
    }

    private class PreLoading extends AsyncTask<String, Integer, Long> {
        private AssetManager assetManager = getAssets();
        private int fileNum = 0;
        private final int TOTAL_FILE = 864;

        @Override
        //copy the file to the internal memory (the app folder).
        protected Long doInBackground(String... folderNames) {
            if (nodeState == NodeState.INIT) {
                long requiredSpace = 16627997;
                if (availableSpace()< requiredSpace) {
                    nodeState = NodeState.NO_MEMORY;
                    return null;
                }
                extractFolder(FOLDER_NAME);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            checkNode();
            Log.d(TAG, "node service pre-loading is finish, status: " + nodeState);
            sendMyBroadcast(EVENT_ACTION, "");
        }

        private void extractFolder(String folderName) {
            try {
                String[] filesInside = assetManager.list(folderName);
                if (filesInside.length == 0) {
                    Log.d(TAG, "find file" + folderName);
                    fileNum++;
                    extractFile(folderName);
                } else {
                    File filePath = new File(fileDir, folderName);
                    if (!filePath.exists() || !filePath.isDirectory()) {
                        if (!filePath.mkdir()) {
                            Log.e(TAG, "Error with make new folder " + folderName);
                            sendMyBroadcast(PROGRESS_ACTION, "Error with make new folder " + folderName);
                            return;
                        }
                        Log.d(TAG, "Make new folder " + folderName);
                    } else {
                        Log.d(TAG, "Folder exists " + folderName);
                    }
                    for (String eachFile : filesInside) {
                        extractFolder(folderName + "/" + eachFile);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void extractFile(String fileName)                                                                                                                                                                                                                                                                                                        {
            sendMyBroadcast(PROGRESS_ACTION, "Extracting: files...[" + fileNum + "/" + TOTAL_FILE + "]");
            Log.d(TAG, "Extracting " + fileName + "...");
            try {
                InputStream in = assetManager.open(fileName);
                int total = in.available();
                OutputStream out = new FileOutputStream(new File(fileDir, fileName));
                byte buffer[] = new byte[100000];
                int len = 0;
                while (true) {
                    int count = in.read(buffer);
                    if (count < 0)
                        break;
                    out.write(buffer, 0, count);
                    len += count;
                    int progress = (int) ((len * 1.0 / total) * 100);
                    Log.d(TAG, "extract:" + progress);
                }
                in.close();
                out.close();
                Log.d(TAG, "Extracted " + len + " bytes to " + fileName);
            } catch (IOException e) {
                sendMyBroadcast(PROGRESS_ACTION, "can not Extracting: " + fileName);
                Log.e(TAG, "Error to copy " + fileName + ":" + e);
            }
        }
    }

    private String getNodeVersion() {
        String version;
        ProcessBuilder versionCheck = new ProcessBuilder(nodeApp.getAbsolutePath(), "--version");
        versionCheck.directory(fileDir);
        versionCheck.redirectErrorStream(true); //put the error info to standard output.
        try {
            Process nodeProcess = versionCheck.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(nodeProcess.getInputStream(), "UTF-8"));
            version = reader.readLine();
            Log.d(TAG, "Got version: " + version);
        } catch (Exception e) {
            Log.w(TAG, "Error reading from node: " + e);
            version = "Error reading from node: " + e;
        }
        return version;
    }

    private boolean makeNodeExecutable() {
        boolean result = true;
        if (!nodeApp.canExecute()) {
            if (!nodeApp.setExecutable(true, false)) {
                result = false;
                Log.e(TAG, "Fail to set executable");
            } else {
                Log.d(TAG, "Set executable to " + nodeApp);
            }
        }
        return result;
    }

    private void sendMyBroadcast(String action, String event) {
        if (!action.equals(EVENT_ACTION)) {
            broadcastIntent.setAction(action);
            broadcastIntent.putExtra("event", event);
        } else {
            broadcastIntent.setAction(action);
        }
        sendBroadcast(broadcastIntent);
    }

    private long availableSpace(){
        File path = Environment.getDataDirectory();
        StatFs m_stat = new StatFs(path.getPath());
        long m_blockSize = m_stat.getBlockSize();
        long m_availableBlocks = m_stat.getAvailableBlocks();
        return (m_availableBlocks * m_blockSize);
    }

}