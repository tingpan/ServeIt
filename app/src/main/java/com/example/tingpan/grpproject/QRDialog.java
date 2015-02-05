package com.example.tingpan.grpproject;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by TingPan on 1/31/15.
 */
public class QRDialog extends Dialog {

    private Context context;
    private Bitmap qrCode;

    public QRDialog(Context context) {
        super(context);
        this.context = context;
    }

    public QRDialog(Context context, int theme, Bitmap qrCode) {
        super(context, theme);
        this.context = context;
        this.qrCode = qrCode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode_dialog);
        ImageView qrContainer = (ImageView) findViewById(R.id.qrContainer);
        qrContainer.setImageBitmap(qrCode);
    }
}
