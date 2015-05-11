package com.example.tingpan.application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.tingpan.displayUnit.GameListAdapter;
import com.example.tingpan.displayUnit.MyMaterialDialog;
import com.example.tingpan.fileStore.FileStore;
import com.example.tingpan.fileStore.IFileStore;
import com.example.tingpan.fileStore.MockFileStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.drakeet.materialdialog.MaterialDialog;


public class ChooseGame extends Activity {

    private IFileStore fileStore;
    private boolean isTest = false;



    private MyMaterialDialog lastDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_game);
        fileStore = FileStore.getInstance(getApplicationContext());
        Intent intent = getIntent();
        if (intent.getBooleanExtra("RUN_ON_TEST_MODE",false)){
            isTest = true;
            fileStore = MockFileStore.getInstance();
        }
        if (fileStore.getFileStoreState() == FileStore.FileStoreStates.READY) {
            initList();
        } else {
            messageDialog(getString(R.string.fileError));
        }
    }

    private void initList() {
        ListView gameList = (ListView) findViewById(R.id.gameList);
        List<Map<String, Object>> gameItems = fileStore.readFile();
        GameListAdapter gameListAdapter = new GameListAdapter(getApplicationContext(), gameItems);
        gameList.setAdapter(gameListAdapter);
        gameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView gameList = (ListView) parent;
                Map<String, Object> game = (Map<String, Object>) gameList.getAdapter().getItem(position);
                String gameTitle = game.get("title").toString();
                fileStore.selectFile(gameTitle);
                Intent intent = new Intent(getApplicationContext(), ReadyToPort.class);
                intent.putExtra("title", gameTitle);
                startActivity(intent);
            }
        });
    }

    public IFileStore getFileStore() {
        return fileStore;
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

    public MyMaterialDialog getLastDialog() {
        return lastDialog;
    }

}
