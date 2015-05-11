package com.example.tingpan.displayUnit;

import android.content.Context;
import android.view.View;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by TingPan on 3/24/15.
 */
public class MyMaterialDialog extends MaterialDialog {
    private String message;
    private String title;
    private View convertView;

    public MyMaterialDialog(Context context) {
       super(context);
    }

    public void setMessage(String message){
        super.setMessage(message);
        this.message = message;
    }

    public void setTitle(String title){
        super.setTitle(title);
        this.title = title;
    }

    public void setContentTag(View convertView){
        super.setContentView(convertView);
        this.convertView = convertView;
    }


    public View getConvertView() {
        return convertView;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }



}
