package com.example.tingpan.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.ActivityUnitTestCase;
import android.widget.TextView;
import com.example.tingpan.fileStore.FileStore;
import com.example.tingpan.nodeService.NodeService;
import java.util.concurrent.CountDownLatch;

/**
 * Created by TingPan on 3/13/15.
 */

public class SplashActivityTest extends ActivityUnitTestCase<SplashActivity> {
    private SplashActivity splashActivity;
    private TextView eventText;
    private Intent testActivity;
    public SplashActivity.NodeBroadcastReceiver nodeBroadcastReceiver;


    public SplashActivityTest() {
        super(SplashActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        testActivity = new Intent(Intent.ACTION_MAIN);
    }

    @Override
    protected void tearDown() throws Exception{
        super.tearDown();
        splashActivity.onDestroy();
    }

    public void testPreConditions() throws Exception {
        startSplashActivity();
        eventText = (TextView) splashActivity.findViewById(R.id.eventText);
        nodeBroadcastReceiver = splashActivity.getMyBroadcastReceiver();
        assertNotNull("Splash Activity is null", splashActivity);
        assertNotNull("Event text view is null", eventText);
        assertNotNull("Node receiver is null", nodeBroadcastReceiver);
    } // end of testPreConditions() method definition

    public void testReceiverWithNodeProgress() throws Exception{
        final String event = "testEvent";
        startSplashActivity();
        eventText = (TextView) splashActivity.findViewById(R.id.eventText);
        nodeBroadcastReceiver = splashActivity.getMyBroadcastReceiver();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(NodeService.PROGRESS_ACTION);
        broadcastIntent.putExtra("event", event);
        nodeBroadcastReceiver.onReceive(splashActivity, broadcastIntent);
        assertEquals("Receiver not handle the message correctly", event, eventText.getText());
    }

    public void testReceiverWithFileProgress() throws Exception{
        final String event = "testEvent";
        startSplashActivity();
        eventText = (TextView) splashActivity.findViewById(R.id.eventText);
        nodeBroadcastReceiver = splashActivity.getMyBroadcastReceiver();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(FileStore.PROGRESS_ACTION);
        broadcastIntent.putExtra("event",event);
        nodeBroadcastReceiver.onReceive(splashActivity,broadcastIntent);
        assertEquals("Receiver not handle the message correctly", event, eventText.getText());
    }

    public void testReceiverWithFileStoreEvent() throws Exception {
        startSplashActivity();
        eventText = (TextView) splashActivity.findViewById(R.id.eventText);
        nodeBroadcastReceiver = splashActivity.getMyBroadcastReceiver();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(FileStore.PROGRESS_ACTION);
        nodeBroadcastReceiver.onReceive(splashActivity,broadcastIntent);
    }

    public void testReceiverWithNodeEvent() throws Exception {
        startSplashActivity();
        eventText = (TextView) splashActivity.findViewById(R.id.eventText);
        nodeBroadcastReceiver = splashActivity.getMyBroadcastReceiver();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(NodeService.PROGRESS_ACTION);
        nodeBroadcastReceiver.onReceive(splashActivity,broadcastIntent);
        assertNotNull("New Activity is null", getStartedActivityIntent());
        assertTrue("Activity did not finished", isFinishCalled());
    }

    public void testLifeCycle() throws Exception {
        startSplashActivity();
        getInstrumentation().callActivityOnStart(splashActivity);
        getInstrumentation().callActivityOnPause(splashActivity);
        getInstrumentation().callActivityOnResume(splashActivity);
        getInstrumentation().callActivityOnDestroy(splashActivity);
    }

    public void testBackPress() throws Exception {
        startSplashActivity();
        splashActivity.onBackPressed();
        assertNotNull(getStartedActivityIntent());
        assertEquals("Back button did not performs like home button",Intent.FLAG_ACTIVITY_NEW_TASK,getStartedActivityIntent().getFlags());
    }

    private void startSplashActivity() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                latch.countDown();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(NodeService.EVENT_ACTION);
        startActivity(testActivity, null, null);
        splashActivity = getActivity();
        splashActivity.registerReceiver(myBroadcastReceiver, filter);
        latch.await();
    }
}
