package com.example.tingpan.grpproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Hashtable;


public class PortGame extends ActionBarActivity {
    private String gameName;
    private String ipAdd;
    private final String portNum = "23604";
    private static final int QR_HEIGHT = 320;
    private static final int QR_WIDTH = 320;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_port_game);

        TextView ipView = (TextView) findViewById(R.id.ipAddress);
        TextView portView = (TextView) findViewById(R.id.portNumber);
        Intent intent = getIntent();
        gameName = intent.getStringExtra("title");
        ipAdd = getIpAddress();
        ipView.setText("IP : "+ipAdd);
        portView.setText("Port : " + portNum);

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

    public void displayQR(View view){
        Bitmap qrCode = createQRImage();
        Toast showQR = Toast.makeText(getApplicationContext(),"QRCODFEDKJDHAKDHADALKSD",Toast.LENGTH_LONG);
        showQR.setGravity(Gravity.CENTER,0,0);
        LinearLayout toastView = (LinearLayout) showQR.getView();
        ImageView qrView = new ImageView(getApplicationContext());
        qrView.setImageBitmap(qrCode);
        toastView.addView(qrView,0);
        showQR.show();
    }

    private String getIpAddress(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements();) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses();
                     enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()&& InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
                        String ipAddr= inetAddress.getHostAddress();
                        return ipAddr;
                    }
                }
            }
        } catch (SocketException e) {
             Log.e("Error", e.toString());
        }
        return "去你妈的";
    }


    private Bitmap createQRImage() {
        String url = "http://" + ipAdd + ":" + portNum;
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(url,BarcodeFormat.QR_CODE,QR_WIDTH,QR_HEIGHT);
            int[] pixels = new int[QR_HEIGHT * QR_WIDTH];
            for (int y = 0; y < QR_HEIGHT; y++){
                for(int x = 0; x < QR_WIDTH; x++){
                    int index = y * QR_HEIGHT + x;
                    if (matrix.get(x,y)){
                        pixels[index] =  0xff000000;
                    }
                    else {
                        pixels[index] = 0xffffffff;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH,QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            return bitmap;
        } catch (WriterException e) {
            Log.e("Error",e.toString());
        }
        return null;
    }

 }
