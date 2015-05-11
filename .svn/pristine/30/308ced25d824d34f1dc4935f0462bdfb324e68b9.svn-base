package com.example.tingpan.application;

import android.content.Intent;
import android.graphics.Bitmap;
import android.test.ActivityUnitTestCase;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.tingpan.displayUnit.MyMaterialDialog;
import com.example.tingpan.displayUnit.QRDialog;
import com.example.tingpan.nodeService.NodeService;
import com.example.tingpan.nodeService.interfaces.INodePreference;
import com.example.tingpan.nodeService.mockInstance.MockNodePreference;
import com.example.tingpan.nodeService.mockInstance.MockNodeService;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;


/**
 * Created by TingPan on 3/25/15.
 */
public class PortGameTest extends ActivityUnitTestCase<PortGame> {
    private PortGame portActivity;
    private Intent testIntent;
    private MockNodePreference mockNodePreference;

    public PortGameTest() {
        super(PortGame.class);
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();
        testIntent = new Intent(Intent.ACTION_MAIN);
        testIntent.putExtra("RUN_ON_TEST_MODE",true);
        mockNodePreference = MockNodePreference.getInstance();
    }

    @Override
    protected void tearDown() throws Exception{
        super.tearDown();
    }

    public void testPreconditions(){
        startActivity(testIntent,null,null);
        portActivity = getActivity();
        assertNotNull("Activity is null",portActivity);
        TextView timeView = (TextView) portActivity.findViewById(R.id.portTime);
        assertNotNull("Time text view is empty", timeView);
        TextView portView = (TextView) portActivity.findViewById(R.id.portNumber);
        assertNotNull("Port number view is empty", portView);
        TextView ipView = (TextView) portActivity.findViewById(R.id.ipAddress);
        assertNotNull("Ip address view is empty", ipView);
        ImageView qrButton = (ImageView) portActivity.findViewById(R.id.qrButton);
        assertNotNull("QR button is empty", qrButton);
        Button termButton = (Button) portActivity.findViewById(R.id.termButton);
        assertNotNull("terminate button is empty", termButton);
        Intent bindIntent = portActivity.getBindIntent();
        assertEquals("Not calling the correct mock service","RUN_ON_TEST_MODE",bindIntent.getAction());
        INodePreference mockNodePreference = portActivity.getNodePreference();
        assertEquals("Not calling the correct mock preference file","Mock",mockNodePreference.getType());
    }

    public void testPortNumber(){
        MockNodeService.setNodeState(NodeService.NodeState.RUNNING);
        starPortActivity();
        TextView portView = (TextView) portActivity.findViewById(R.id.portNumber);
        assertEquals("Port : " + mockNodePreference.getPortNumber(),portView.getText());
    }

    public void testIpNoNetwork(){
        MockNodeService.setNodeState(NodeService.NodeState.RUNNING);
        MockNodeService.setIpAddress("No Networks");
        starPortActivity();
        TextView ipView = (TextView) portActivity.findViewById(R.id.ipAddress);
        assertEquals("IP : No Networks" ,ipView.getText());
        MyMaterialDialog lastDialog = portActivity.getLastDialog();
        assertNotNull(lastDialog);
        assertEquals("Dialog is not correct",portActivity.getString(R.string.noNetworkNotion),lastDialog.getMessage());
    }

    public void testIpAddr(){
        MockNodeService.setNodeState(NodeService.NodeState.RUNNING);
        MockNodeService.setIpAddress("196.168.0.0");
        starPortActivity();
        TextView ipView = (TextView) portActivity.findViewById(R.id.ipAddress);
        assertEquals("IP : 196.168.0.0" ,ipView.getText());
        MyMaterialDialog lastDialog = portActivity.getLastDialog();
        assertNull(lastDialog);
    }

    public void testQRdialogWithNetwork(){
        Result result;
        String decode = "";
        QRCodeReader reader = new QRCodeReader();
        MockNodeService.setNodeState(NodeService.NodeState.RUNNING);
        MockNodeService.setIpAddress("196.168.0.0");
        starPortActivity();
        ImageView qrButton = (ImageView) portActivity.findViewById(R.id.qrButton);
        qrButton.performClick();
        QRDialog qrDialog = portActivity.getQrDialog();
        assertNotNull(qrDialog);
        Bitmap qrCode = qrDialog.getQrCode();
        int[] pixels = new int[qrCode.getWidth()*qrCode.getHeight()];
        qrCode.getPixels(pixels, 0, qrCode.getWidth(), 0, 0, qrCode.getWidth(), qrCode.getHeight());
        LuminanceSource source = new RGBLuminanceSource(qrCode.getWidth(),qrCode.getHeight(),pixels);
        BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            result = reader.decode(bBitmap);
        } catch (NotFoundException|ChecksumException|FormatException e) {
            result = null;
        }
        if (result != null){
            decode = result.getText();
        }
        assertEquals("http://196.168.0.0" + ":" + mockNodePreference.getPortNumber(),decode);
    }

    public void testQrDialogWithoutNetwork(){
        Result result;
        String decode = "";
        QRCodeReader reader = new QRCodeReader();
        MockNodeService.setNodeState(NodeService.NodeState.RUNNING);
        MockNodeService.setIpAddress("No Networks");
        starPortActivity();
        ImageView qrButton = (ImageView) portActivity.findViewById(R.id.qrButton);
        qrButton.performClick();
        QRDialog qrDialog = portActivity.getQrDialog();
        assertNotNull(qrDialog);
        Bitmap qrCode = qrDialog.getQrCode();
        int[] pixels = new int[qrCode.getWidth()*qrCode.getHeight()];
        qrCode.getPixels(pixels, 0, qrCode.getWidth(), 0, 0, qrCode.getWidth(), qrCode.getHeight());
        LuminanceSource source = new RGBLuminanceSource(qrCode.getWidth(),qrCode.getHeight(),pixels);
        BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            result = reader.decode(bBitmap);
        } catch (NotFoundException|ChecksumException|FormatException e) {
            result = null;
        }
        if (result != null){
            decode = result.getText();
        }
        assertEquals("http://No Networks" + ":" + mockNodePreference.getPortNumber(),decode);
    }

    public void testTerminate(){
        MockNodeService.setNodeState(NodeService.NodeState.RUNNING);
        starPortActivity();
        Button termButton = (Button) portActivity.findViewById(R.id.termButton);
        termButton.performClick();
        assertEquals("Node service did not finished",NodeService.NodeState.READY,MockNodeService.getNodeState());
        assertEquals("Activity is not finished",true,isFinishCalled());
    }

    public void testBackButton(){
        MockNodeService.setNodeState(NodeService.NodeState.RUNNING);
        starPortActivity();
        portActivity.onBackPressed();
        assertEquals("Activity is finished",false,isFinishCalled());
        MyMaterialDialog lastDialog = portActivity.getLastDialog();
        assertNotNull(lastDialog);
        assertEquals("Dialog is not correct",portActivity.getString(R.string.terminateMsg),lastDialog.getMessage());
    }

    public void testLifeCycle(){
        MockNodeService.setNodeState(NodeService.NodeState.RUNNING);
        starPortActivity();
        getInstrumentation().callActivityOnStart(portActivity);
        getInstrumentation().callActivityOnPause(portActivity);
        getInstrumentation().callActivityOnResume(portActivity);
        getInstrumentation().callActivityOnDestroy(portActivity);
    }

    private void starPortActivity(){
        startActivity(testIntent,null,null);
        portActivity = getActivity();
        while (!portActivity.isLoadFinished()){
            try {
                Thread.sleep(5);
            } catch (InterruptedException ignored) {
            }
        }
    }

}
