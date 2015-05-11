package com.example.tingpan.application;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.tingpan.displayUnit.MyMaterialDialog;
import com.example.tingpan.displayUnit.QRDialog;
import com.example.tingpan.nodeService.NodePreference;
import com.example.tingpan.nodeService.NodeService;
import com.example.tingpan.nodeService.interfaces.INodePreference;
import com.example.tingpan.nodeService.interfaces.MyBinder;
import com.example.tingpan.nodeService.mockInstance.MockNodePreference;
import com.example.tingpan.nodeService.mockInstance.MockNodeService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;

public class PortGame extends Activity implements Animation.AnimationListener {
    private String ipAdd;
    private String portNum;
    private int qrSize = 320;
    private TextView timeView;
    private int timeInSec = 0;
    private MyBinder myBinder;
    private boolean isTest = false;
    private Intent bindIntent;
    private INodePreference nodePreference;
    private MyMaterialDialog lastDialog;
    private QRDialog qrDialog;
    private boolean loadFinished = false;
    private Handler timeHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    updateTime();
                    timeHandler.sendEmptyMessageDelayed(1, 1000);
                    break;
                case 0:
                    System.out.println("test");
                    break;
            }
            return true;
        }
    });

    protected ServiceConnection myConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MyBinder) service;
            TextView ipView = (TextView) findViewById(R.id.ipAddress);
            ipAdd = myBinder.getIpAddress();
            ipView.setText("IP : " + ipAdd);
            if (ipAdd.equals("No Networks")){
                messageDialog(getString(R.string.noNetworkNotion));
            }
            loadFinished = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myBinder.stopNode();
            myBinder = null;
        }
    };
    private Animation starAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_port_game);
        nodePreference = NodePreference.getInstance(this.getSharedPreferences(NodePreference.TAG, MODE_PRIVATE));
        bindIntent = new Intent(this, NodeService.class);
        Intent intent = getIntent();
        if (intent.getBooleanExtra("RUN_ON_TEST_MODE",false)){
            setTestEnvironment();
        }
        portNum = String.valueOf(nodePreference.getPortNumber());
        bindService(bindIntent, myConnection, BIND_AUTO_CREATE);

        starAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.star_animation);
        starAnimation.setAnimationListener(this);
        ImageView starView = (ImageView) findViewById(R.id.star);
        starView.startAnimation(starAnimation);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        qrSize = dm.widthPixels / 5 * 3;
        timeView = (TextView) findViewById(R.id.portTime);
        TextView portView = (TextView) findViewById(R.id.portNumber);
        portView.setText("Port : " + portNum);
        timeHandler.sendEmptyMessageDelayed(1, 1000);

    }

    private void setTestEnvironment() {
        isTest = true;
        bindIntent = new Intent(this, MockNodeService.class);
        bindIntent.setAction("RUN_ON_TEST_MODE");
        nodePreference = MockNodePreference.getInstance();
    }


    public void displayQR(View view) {
        Bitmap qrCode = createQRImage();
        qrDialog = new QRDialog(this, R.style.QRDialog, qrCode);
        if (!isTest) {
            qrDialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        messageDialog(getString(R.string.terminateMsg));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unbindService(myConnection);
        } catch (IllegalArgumentException ignored){

        }
    }


    private void updateTime() {
        int hour;
        int min;
        int sec;
        String delimiter = ":";
        String time;
        timeInSec++;
        hour = timeInSec / 3600;
        min = (timeInSec / 60) % 60;
        sec = timeInSec % 60;
        if (hour < 10) {
            time = "0" + hour;
        } else {
            time = "" + hour;
        }
        if (min < 10) {
            time = time + delimiter + "0" + min;
        } else {
            time = time + delimiter + min;
        }
        if (sec < 10) {
            time = time + delimiter + "0" + sec;
        } else {
            time = time + delimiter + sec;
        }
        timeView.setText(time);
    }

    public void openLink (View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://127.0.0.1:"+portNum));
        startActivity(browserIntent);
    }

    private Bitmap createQRImage() {
        String url = "http://" + ipAdd + ":" + portNum;
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1); /* default = 4 */
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, qrSize, qrSize, hints);
            int[] pixels = new int[qrSize * qrSize];
            for (int y = 0; y < qrSize; y++) {
                for (int x = 0; x < qrSize; x++) {
                    int index = y * qrSize + x;
                    if (matrix.get(x, y)) {
                        pixels[index] = 0xff000000;
                    } else {
                        pixels[index] = 0xffffffff;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(qrSize, qrSize, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, qrSize, 0, 0, qrSize, qrSize);
            return bitmap;
        } catch (WriterException e) {
            Log.e("Error", e.toString());
        }
        return null;
    }

    public void terminatePort(View view) {
        timeHandler.sendEmptyMessage(0);
        myBinder.stopNode();
        this.finish();
    }


    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        starAnimation = AnimationUtils.loadAnimation(this, R.anim.star_loop);
        ImageView starView = (ImageView) findViewById(R.id.star);
        starView.startAnimation(starAnimation);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    private void messageDialog(String msg) {
        final MyMaterialDialog mMaterialDialog = new MyMaterialDialog(this);
        mMaterialDialog.setMessage(msg);
        mMaterialDialog.setPositiveButton("Get it", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
            }
        });
        lastDialog = mMaterialDialog;
        if (!isTest) {
            mMaterialDialog.show();
        }
    }

    public INodePreference getNodePreference() {
        return nodePreference;
    }

    public Intent getBindIntent() {
        return bindIntent;
    }

    public MyBinder getMyBinder() {
        return myBinder;
    }

    public QRDialog getQrDialog() {
        return qrDialog;
    }

    public MyMaterialDialog getLastDialog() {
        return lastDialog;
    }

    public boolean isLoadFinished() {
        return loadFinished;
    }
}
