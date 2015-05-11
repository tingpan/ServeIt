package com.example.tingpan.application;


import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.tingpan.displayUnit.MyMaterialDialog;
import com.example.tingpan.fileStore.IFileStore;
import com.example.tingpan.fileStore.MockFileStore;
import com.example.tingpan.nodeService.interfaces.INodePreference;
import com.example.tingpan.nodeService.mockInstance.MockNodePreference;
import com.example.tingpan.nodeService.mockInstance.MockNodeService;
import com.example.tingpan.nodeService.NodeService;



/**
 * Created by TingPan on 3/24/15.
 */
public class LaunchTest extends ActivityUnitTestCase<Launch> {
    private Intent testIntent;
    private MockFileStore fileStore;
    private Launch launchActivity;

    public LaunchTest() {
        super(Launch.class);
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();
        testIntent = new Intent(Intent.ACTION_MAIN);
        testIntent.putExtra("RUN_ON_TEST_MODE",true);
        fileStore = MockFileStore.getInstance();
    }

    @Override
    protected void tearDown() throws Exception{
        if (launchActivity != null) {
            launchActivity.onDestroy();
        }
        super.tearDown();
    }

    public void testPreConditions() throws Exception{
        startActivity(testIntent,null,null);
        launchActivity = getActivity();
        assertNotNull("Activity is null",launchActivity);
        Button startButton =(Button) launchActivity.findViewById(R.id.startButton);
        assertNotNull("Start Button is null",startButton);
        Button settingButton =(Button) launchActivity.findViewById(R.id.settingButton);
        assertNotNull("Start Button is null",settingButton);
        Intent bindIntent = launchActivity.getBindIntent();
        assertEquals("Not calling the correct mock service","RUN_ON_TEST_MODE",bindIntent.getAction());
        IFileStore fileStore = launchActivity.getFileStore();
        assertEquals("Not calling the correct mock file store","Mock",fileStore.getFileStoreType());
        INodePreference mockNodePreference = launchActivity.getNodePreference();
        assertEquals("Not calling the correct mock preference file","Mock",mockNodePreference.getType());
    }

    public void testFileAndServiceCorrect() throws Throwable {
        fileStore.setFileStoreStates(IFileStore.FileStoreStates.READY);
        MockNodeService.setNodeState(NodeService.NodeState.READY);
        startLaunchActivity();
        assertNull("Dialog displays when everything is correct",launchActivity.getLastDialog());
    }

    public void testFileNotCorrect() throws Throwable {
        fileStore.setFileStoreStates(IFileStore.FileStoreStates.FAILED);
        MockNodeService.setNodeState(NodeService.NodeState.READY);
        startLaunchActivity();
        MyMaterialDialog lastDialog = launchActivity.getLastDialog();
        assertNotNull("Dialog did not display when file is not correct", lastDialog);
        assertEquals("Dialog text is not correct",lastDialog.getMessage()
                ,launchActivity.getString(R.string.fileError));
    }

    public void testServerNotCorrect() throws Throwable {
        fileStore.setFileStoreStates(IFileStore.FileStoreStates.READY);
        MockNodeService.setNodeState(NodeService.NodeState.FAILED);
        startLaunchActivity();
        MyMaterialDialog lastDialog = launchActivity.getLastDialog();
        assertNotNull("Dialog did not display when file is not correct", lastDialog);
        assertEquals("Dialog text is not correct",lastDialog.getMessage()
                ,launchActivity.getString(R.string.nodeError));
    }

    public void testSettingHint() throws Throwable {
        fileStore.setFileStoreStates(IFileStore.FileStoreStates.READY);
        MockNodeService.setNodeState(NodeService.NodeState.READY);
        MockNodePreference preference = MockNodePreference.getInstance();
        startLaunchActivity();
        Button settingButton =(Button) launchActivity.findViewById(R.id.settingButton);
        settingButton.performClick();
        MyMaterialDialog lastDialog = launchActivity.getLastDialog();
        assertNotNull("Setting Dialog is not correct",lastDialog);
        assertEquals("Dialog is not correct",lastDialog.getTitle(),"Settings");
        View convertView = lastDialog.getConvertView();
        EditText portEdit = (EditText) convertView.findViewById(R.id.portNum);
        EditText maxEdit = (EditText) convertView.findViewById(R.id.maxConnection);
        assertNotNull(portEdit);
        assertNotNull(maxEdit);
        assertEquals(portEdit.getHint(),String.valueOf(preference.getPortNumber()));
        assertEquals(maxEdit.getHint(),String.valueOf(preference.getMaxConnection()));
    }

    public void testSettingWithValid () throws Throwable {
        int portNumber = 23000;
        int maxConnection = 30;
        MockNodePreference preference = MockNodePreference.getInstance();
        preference.setMaxConnection(maxConnection);
        preference.setPortNumber(portNumber);
        assertEquals(portNumber,preference.getPortNumber());
        assertEquals(maxConnection,preference.getMaxConnection());
    }

    public void testSettingWithInValid () throws Throwable {
        int portNumber = 80;
        int maxConnection = 10000;
        MockNodePreference preference = MockNodePreference.getInstance();
        preference.setMaxConnection(maxConnection);
        assertEquals(false,preference.setPortNumber(portNumber));
        assertEquals(false,preference.setMaxConnection(maxConnection));
    }

    public void testStartButton () throws  Throwable {
        fileStore.setFileStoreStates(IFileStore.FileStoreStates.READY);
        MockNodeService.setNodeState(NodeService.NodeState.READY);
        startLaunchActivity();
        Button startButton =(Button) launchActivity.findViewById(R.id.startButton);
        startButton.performClick();
        Intent startIntent = getStartedActivityIntent();
        assertNotNull(startIntent);
    }

    public void testLifeCycle () throws  Throwable {
        fileStore.setFileStoreStates(IFileStore.FileStoreStates.READY);
        MockNodeService.setNodeState(NodeService.NodeState.READY);
        startLaunchActivity();
        getInstrumentation().callActivityOnStart(launchActivity);
        getInstrumentation().callActivityOnPause(launchActivity);
        getInstrumentation().callActivityOnResume(launchActivity);
        getInstrumentation().callActivityOnDestroy(launchActivity);
    }

    private void startLaunchActivity(){
        startActivity(testIntent,null,null);
        launchActivity = getActivity();
        while (!launchActivity.isChecked()){
            try {
                Thread.sleep(5);
            } catch (InterruptedException ignored) {
            }
        }
    }
}
