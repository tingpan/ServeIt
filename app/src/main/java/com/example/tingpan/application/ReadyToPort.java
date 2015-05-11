package com.example.tingpan.application;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;
import com.example.tingpan.displayUnit.MyMaterialDialog;
import com.example.tingpan.fileStore.FileStore;
import com.example.tingpan.fileStore.IFileStore;
import com.example.tingpan.fileStore.MockFileStore;
import com.example.tingpan.nodeService.interfaces.MyBinder;
import com.example.tingpan.nodeService.NodeService;
import com.example.tingpan.nodeService.mockInstance.MockNodeService;


public class ReadyToPort extends Activity {
    private String gameName;

    public MyBinder getMyBinder() {
        return myBinder;
    }

    private MyBinder myBinder;
    private IFileStore fileStore;
    private Intent bindIntent;
    private boolean isRunning = false;
    private boolean isTest = false;
    private MyMaterialDialog lastDialog;
    protected ServiceConnection myConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myBinder = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready_to_port);
        fileStore = FileStore.getInstance(getApplicationContext());
        Intent intent = getIntent();
        gameName = intent.getStringExtra("title");
        TextView textView = (TextView) findViewById(R.id.portGame);
        textView.setText(gameName);
        bindIntent = new Intent(this, NodeService.class);
        if (intent.getBooleanExtra("RUN_ON_TEST_MODE",false)){
               setTestEnvironment();
        }
        bindService(bindIntent, myConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unbindService(myConnection);
        }catch (IllegalArgumentException ignored){

        }
    }

    public void startPort(View view) {
        if (myBinder == null){
            messageDialog(getString(R.string.notReady));
            return;
        }
        if (myBinder.getNodeState() != NodeService.NodeState.READY) {
            messageDialog(getString(R.string.nodeError));
            return;
        }
        if (!isRunning) {
            messageDialog(getString(R.string.pleaseWait));
            RunNodeTask runNodeTask = new RunNodeTask();
            runNodeTask.execute();
            isRunning = true;
        } else {
            messageDialog(getString(R.string.pleaseWait));
        }
    }

    private void messageDialog(String msg) {
        final MyMaterialDialog mMaterialDialog = new MyMaterialDialog(this);
        mMaterialDialog.setMessage(msg);
        lastDialog = mMaterialDialog;
        mMaterialDialog.setPositiveButton("Get it", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
            }
        });
        if (!isTest) {
            mMaterialDialog.show();
        }
    }

    class RunNodeTask extends AsyncTask<String, Integer, Boolean>{
        @Override
        protected Boolean doInBackground(String... params) {
            String result = myBinder.startNode(fileStore.getPortDir());
            if (result!=null && result.equals("Success")){
                return true;
            } else {
                return false;
            }
        }
        public void onPostExecute(Boolean result){
            super.onPostExecute(result);
            isRunning = false;
            if (!isTest && lastDialog!=null){
                lastDialog.dismiss();
            }
            if (!result){
                messageDialog(getString(R.string.nodeStartError));
                myBinder.stopNode();
                return;
            }
            Intent intent = new Intent(getApplicationContext(), PortGame.class);
            intent.putExtra("title", gameName);
            startActivity(intent);
        }
    }

    private void setTestEnvironment(){
        isTest = true;
        fileStore = MockFileStore.getInstance();
        bindIntent = new Intent(this, MockNodeService.class);
        bindIntent.setAction("RUN_ON_TEST_MODE");
    }

    public MyMaterialDialog getLastDialog() {
        return lastDialog;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public Intent getBindIntent() {
        return bindIntent;
    }

    public IFileStore getFileStore() {
        return fileStore;
    }
}
