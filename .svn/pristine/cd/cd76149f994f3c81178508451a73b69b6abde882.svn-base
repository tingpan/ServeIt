package com.example.tingpan.application;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.widget.Button;
import android.widget.TextView;
import com.example.tingpan.displayUnit.MyMaterialDialog;
import com.example.tingpan.fileStore.IFileStore;
import com.example.tingpan.nodeService.NodeService;
import com.example.tingpan.nodeService.mockInstance.MockNodeService;
import java.util.concurrent.CountDownLatch;

/**
 * Created by TingPan on 3/24/15.
 */
public class ReadyToPortTest extends ActivityUnitTestCase<ReadyToPort>{
    private Intent testIntent;
    private ReadyToPort readyActivity;

    public ReadyToPortTest() {
        super(ReadyToPort.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        testIntent = new Intent(Intent.ACTION_MAIN);
        testIntent.putExtra("RUN_ON_TEST_MODE",true);
        testIntent.putExtra("title","Test File");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testPreconditions() throws Exception {
        startActivity(testIntent,null,null);
        readyActivity = getActivity();
        assertNotNull("Activity is null", readyActivity);
        TextView gameName = (TextView) readyActivity.findViewById(R.id.portGame);
        assertNotNull("Game name field is null", gameName);
        Button startButton = (Button) readyActivity.findViewById(R.id.comfirmButton);
        assertNotNull("Start button is null", startButton);
        IFileStore iFileStore = readyActivity.getFileStore();
        assertEquals("Not calling the correct mock file store","Mock",iFileStore.getFileStoreType());
        Intent bindIntent = readyActivity.getBindIntent();
        assertEquals("Not calling the correct mock service","RUN_ON_TEST_MODE",bindIntent.getAction());
    }

    public void testNameIsCorrect() throws Exception {
        startActivity(testIntent,null,null);
        readyActivity = getActivity();
        TextView gameName = (TextView) readyActivity.findViewById(R.id.portGame);
        assertEquals("Game name is not correct","Test File", gameName.getText());
    }

    public void testStartNodeWhenServerBroken() throws Exception {
        MockNodeService.setNodeState(NodeService.NodeState.FAILED);
        startReadyActivity();
        Button startButton = (Button) readyActivity.findViewById(R.id.comfirmButton);
        startButton.performClick();
        MyMaterialDialog lastDialog = readyActivity.getLastDialog();
        assertNotNull(lastDialog);
        assertEquals("Dialog is not correct",readyActivity.getString(R.string.nodeError),lastDialog.getMessage());
        assertEquals("server shall not running",false,readyActivity.isRunning());
    }

    public void testStartNodeWhenServerNotReady() throws Exception {
        MockNodeService.setNodeState(NodeService.NodeState.READY);
        startActivity(testIntent, null, null);
        readyActivity = getActivity();
        Button startButton = (Button) readyActivity.findViewById(R.id.comfirmButton);
        startButton.performClick();
        MyMaterialDialog lastDialog = readyActivity.getLastDialog();
        assertNotNull(lastDialog);
        assertEquals("Dialog is not correct",readyActivity.getString(R.string.notReady),lastDialog.getMessage());
        assertEquals("server shall not running",false,readyActivity.isRunning());
    }

    public void testReactWithNodeFailure1() throws Exception {
        MockNodeService.setNodeState(NodeService.NodeState.READY);
        MockNodeService.setResult("Failure");
        startReadyActivity();
        Button startButton = (Button) readyActivity.findViewById(R.id.comfirmButton);
        startButton.performClick();
        MyMaterialDialog lastDialog = readyActivity.getLastDialog();
        assertNotNull(lastDialog);
        assertEquals("Dialog is not correct",readyActivity.getString(R.string.pleaseWait),lastDialog.getMessage());
        assertEquals("server is not running",true,readyActivity.isRunning());
        while(readyActivity.isRunning()){
            try {
                Thread.sleep(5);
            } catch (InterruptedException ignored) {
            }
        }
        lastDialog = readyActivity.getLastDialog();
        assertEquals("Dialog is not correct",readyActivity.getString(R.string.nodeStartError),lastDialog.getMessage());
        assertNull("New activity shall not start",getStartedActivityIntent());
    }

    public void testReactWithNodeFailure2() throws Exception {
        MockNodeService.setNodeState(NodeService.NodeState.READY);
        MockNodeService.setResult(null);
        startReadyActivity();
        Button startButton = (Button) readyActivity.findViewById(R.id.comfirmButton);
        startButton.performClick();
        MyMaterialDialog lastDialog = readyActivity.getLastDialog();
        assertNotNull(lastDialog);
        assertEquals("Dialog is not correct",readyActivity.getString(R.string.pleaseWait),lastDialog.getMessage());
        assertEquals("server is not running",true,readyActivity.isRunning());
        while(readyActivity.isRunning()){
            try {
                Thread.sleep(5);
            } catch (InterruptedException ignored) {
            }
        }
        lastDialog = readyActivity.getLastDialog();
        assertEquals("Dialog is not correct",readyActivity.getString(R.string.nodeStartError),lastDialog.getMessage());
        assertNull("New activity shall not start",getStartedActivityIntent());
        assertEquals("Node service state is not correct",NodeService.NodeState.READY,MockNodeService.getNodeState());
    }

    public void testReactWithNodeSuccess() throws Exception {
        MockNodeService.setNodeState(NodeService.NodeState.READY);
        MockNodeService.setResult("Success");
        startReadyActivity();
        Button startButton = (Button) readyActivity.findViewById(R.id.comfirmButton);
        startButton.performClick();
        MyMaterialDialog lastDialog = readyActivity.getLastDialog();
        assertNotNull(lastDialog);
        assertEquals("Dialog is not correct",readyActivity.getString(R.string.pleaseWait),lastDialog.getMessage());
        assertEquals("server is not running",true,readyActivity.isRunning());
        while(readyActivity.isRunning()){
            try {
                Thread.sleep(5);
            } catch (InterruptedException ignored) {
            }
        }
        assertNotNull("New activity does not start", getStartedActivityIntent());
        assertEquals("Node service state is not correct",NodeService.NodeState.RUNNING,MockNodeService.getNodeState());
    }

    public void testLifeCycle() throws Exception {
        MockNodeService.setNodeState(NodeService.NodeState.READY);
        startReadyActivity();
        getInstrumentation().callActivityOnStart(readyActivity);
        getInstrumentation().callActivityOnPause(readyActivity);
        getInstrumentation().callActivityOnResume(readyActivity);
        getInstrumentation().callActivityOnDestroy(readyActivity);
    }

    private void startReadyActivity() throws InterruptedException {
        startActivity(testIntent, null, null);
        readyActivity = getActivity();
        final CountDownLatch latch = new CountDownLatch(1);
        while (readyActivity.getMyBinder()==null){
            try {
                Thread.sleep(5);
            } catch (InterruptedException ignored) {
            }
        }
        latch.countDown();
        latch.await();
    }
}
