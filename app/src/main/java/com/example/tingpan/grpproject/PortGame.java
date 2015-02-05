package com.example.tingpan.grpproject;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.Map;

public class PortGame extends ActionBarActivity implements Animation.AnimationListener {
    private String gameName;
    private String ipAdd;
    private final String portNum = "23604";
    private int qrSize = 320;
    private TextView timeView;
    private int timeInSec = 3600;
    private Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    updateTime();
                    this.sendEmptyMessageDelayed(1, 1000);
                    break;
                case 0:
                    break;
            }
        }

    };
    private Animation starAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_port_game);

        starAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.star_animation);
        starAnimation.setAnimationListener(this);
        ImageView starView = (ImageView) findViewById(R.id.star);
        starView.startAnimation(starAnimation);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        qrSize = dm.widthPixels / 5 * 3;

        timeView = (TextView) findViewById(R.id.portTime);
        TextView ipView = (TextView) findViewById(R.id.ipAddress);
        TextView portView = (TextView) findViewById(R.id.portNumber);
        Intent intent = getIntent();
        gameName = intent.getStringExtra("title");
        ipAdd = getIpAddress();
        ipView.setText("IP : " + ipAdd);
        portView.setText("Port : " + portNum);

        timeHandler.sendEmptyMessageDelayed(1,1000);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_portgame, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void displayQR(View view) {
        Bitmap qrCode = createQRImage();
        QRDialog dialog = new QRDialog(this, R.style.QRDialog, qrCode);
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        // Processing logic code Note: This method applies only to the 2.0 or later version of the sdk
        return;
    }


    private String getIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses();
                     enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
                        String ipAddr = inetAddress.getHostAddress();
                        return ipAddr;
                    }
                }
            }
        } catch (SocketException e) {
            Log.e("Error", e.toString());
        }
        return "No Networks";
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
        }
        else {
            time = "" + hour;
        }
        if (min < 10){
            time = time + delimiter + "0" + min;
        }
        else {
            time = time + delimiter + min;
        }
        if (sec < 10){
            time = time + delimiter + "0" +sec;
        }
        else {
            time = time + delimiter + sec;
        }
        timeView.setText(time);
    }

    private Bitmap createQRImage() {
        String url = "http://" + ipAdd + ":" + portNum;
        Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
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
        this.finish();
    }


    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        starAnimation = AnimationUtils.loadAnimation(this,R.anim.star_loop);
        ImageView starView = (ImageView) findViewById(R.id.star);
        starView.startAnimation(starAnimation);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
