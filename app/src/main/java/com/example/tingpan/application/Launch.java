package com.example.tingpan.application;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.tingpan.displayUnit.MyMaterialDialog;
import com.example.tingpan.fileStore.FileStore;
import com.example.tingpan.fileStore.IFileStore;
import com.example.tingpan.fileStore.MockFileStore;
import com.example.tingpan.nodeService.interfaces.INodePreference;
import com.example.tingpan.nodeService.mockInstance.MockNodePreference;
import com.example.tingpan.nodeService.mockInstance.MockNodeService;
import com.example.tingpan.nodeService.interfaces.MyBinder;
import com.example.tingpan.nodeService.NodePreference;
import com.example.tingpan.nodeService.NodeService;



public class Launch extends Activity {

    private INodePreference nodePreference;
    private MyMaterialDialog lastDialog;
    private Intent bindIntent;
    private MyBinder myBinder;
    private IFileStore fileStore;
    private boolean isTest;
    private boolean isChecked = false;

    private ServiceConnection myConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MyBinder) service;
            nodeServiceStatus();
            isChecked = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            myBinder = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        nodePreference = NodePreference.getInstance(this.getSharedPreferences(NodePreference.TAG,MODE_PRIVATE));
        Intent intent = getIntent();
        bindIntent = new Intent(this, NodeService.class);
        fileStore = FileStore.getInstance(getApplicationContext());
        if(intent.getBooleanExtra("RUN_ON_TEST_MODE",false)){
            setTestEnvironment();
        }
        bindService(bindIntent, myConnection, BIND_AUTO_CREATE);
        if (fileStore.getFileStoreState() != IFileStore.FileStoreStates.READY) {
            messageDialog(getString(R.string.fileError));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unbindService(myConnection);
        } catch (IllegalArgumentException ignored){

        }
    }

    public void chooseGame(View view) {
        Intent intent = new Intent(this, ChooseGame.class);
        startActivity(intent);
    }

    public void setting(View view) {
        settingDialog();
    }

    private void settingDialog() {
        final MyMaterialDialog myMaterialDialog = new MyMaterialDialog(this);
        myMaterialDialog.setTitle("Settings");
        LayoutInflater listContainer = LayoutInflater.from(this);
        View convertView = listContainer.inflate(R.layout.setting_dialog, null);
        final EditText portEdit = (EditText) convertView.findViewById(R.id.portNum);
        final EditText maxEdit = (EditText) convertView.findViewById(R.id.maxConnection);
        portEdit.setHint(String.valueOf(nodePreference.getPortNumber()));
        maxEdit.setHint(String.valueOf(nodePreference.getMaxConnection()));
        myMaterialDialog.setContentView(convertView);
        myMaterialDialog.setContentTag(convertView);
        myMaterialDialog.setPositiveButton("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String portNumber = portEdit.getText().toString();
                String maxNumber = maxEdit.getText().toString();
                if (portNumber.length() > 0) {
                    int portNum = Integer.parseInt(portNumber);
                    if (!nodePreference.setPortNumber(portNum)) {
                        messageDialog("Port number is between 5000 - 65535, Please Input Again");
                        return;
                    }
                }
                if (maxNumber.length() > 0) {
                    int maxNum = Integer.parseInt(maxNumber);
                    if (!nodePreference.setMaxConnection(maxNum)) {
                        messageDialog("Max connection number is between 0 - 1000, please set again");
                        return;
                    }
                }
                myMaterialDialog.dismiss();
                Log.d("setting", nodePreference.getMaxConnection() + "," + nodePreference.getPortNumber());
            }
        });
        myMaterialDialog.setNegativeButton("CANCEL", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMaterialDialog.dismiss();
            }
        });
        lastDialog = myMaterialDialog;
        if (!isTest) {
            myMaterialDialog.show();
        }
    }

    private void nodeServiceStatus(){
        if (myBinder != null){
            if (myBinder.getNodeState() != NodeService.NodeState.READY) {
                if (myBinder.getNodeState() == NodeService.NodeState.NO_MEMORY) {
                    messageDialog(getString(R.string.noMemory));
                }else {
                    messageDialog(getString(R.string.nodeError));
                    Log.e("TAG","yes");
                }
            }
        }

    }

    private void messageDialog(String msg) {
        final MyMaterialDialog myMaterialDialog = new MyMaterialDialog(this);
        myMaterialDialog.setMessage(msg);
        myMaterialDialog.setPositiveButton("Get it", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMaterialDialog.dismiss();
            }
        });
        lastDialog = myMaterialDialog;
        if (!isTest) {
            myMaterialDialog.show();
        }
    }

    private void setTestEnvironment(){
        isTest = true;
        bindIntent = new Intent(this, MockNodeService.class);
        bindIntent.setAction("RUN_ON_TEST_MODE");
        fileStore = MockFileStore.getInstance();
        nodePreference = MockNodePreference.getInstance();
    }

    public Intent getBindIntent() {
        return bindIntent;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public MyMaterialDialog getLastDialog(){
        return lastDialog;
    }
    public INodePreference getNodePreference() { return nodePreference; }
    public IFileStore getFileStore() {
        return fileStore;
    }
}
