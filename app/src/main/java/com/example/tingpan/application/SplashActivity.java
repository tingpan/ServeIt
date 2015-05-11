package com.example.tingpan.application;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import com.example.tingpan.fileStore.FileStore;
import com.example.tingpan.nodeService.NodeService;



public class SplashActivity extends Activity {


    private NodeBroadcastReceiver myBroadcastReceiver = new NodeBroadcastReceiver();
    private TextView eventText;
    public class NodeBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String event = intent.getStringExtra("event");
            eventText.setText(event);

            if (intent.getAction().equals(FileStore.EVENT_ACTION)) {
                Intent nodeService = new Intent(getApplicationContext(), NodeService.class);
                startService(nodeService);
            }

            if (intent.getAction().equals(NodeService.EVENT_ACTION)) {
                startMain();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        eventText = (TextView) findViewById(R.id.eventText);
        FileStore fileStore = FileStore.getInstance(getApplicationContext());
        registerReceiver();
        fileStore.checkFileStore();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(myBroadcastReceiver);
        } catch (IllegalArgumentException ignored){

        };
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }

    private void registerReceiver() {
        myBroadcastReceiver = new NodeBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(FileStore.EVENT_ACTION);
        filter.addAction(FileStore.PROGRESS_ACTION);
        filter.addAction(NodeService.EVENT_ACTION);
        filter.addAction(NodeService.PROGRESS_ACTION);
        registerReceiver(myBroadcastReceiver, filter);
    }


    private void startMain() {
        Intent i = new Intent(this, Launch.class);
        startActivity(i);
        SplashActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    public NodeBroadcastReceiver getMyBroadcastReceiver() {
        return myBroadcastReceiver;
    }
}
