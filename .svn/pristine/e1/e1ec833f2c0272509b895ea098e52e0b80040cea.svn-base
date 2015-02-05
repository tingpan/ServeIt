package com.example.tingpan.grpproject;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChooseGame extends ActionBarActivity {
    private static String[] DATA_STORE = {
            "FlappyBird.js",
            "SuperMario.js",
            "CubeGame.js",
            "TestGame.js",
            "FruitNinja.js",
            "MentalGears.js",
            "LegalOfLegends.js"
    };
    private GameListAdapter gameListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_game);

        ListView gameList = (ListView) findViewById(R.id.gameList);
        //ArrayAdapter<String> adapter = new ArrayAdapter (this,R.layout.gamelist,DATA_STORE);
        //gameList.setAdapter(adapter);
        List<Map<String,Object>> gameItems = getGameItems();
        gameListAdapter = new GameListAdapter(this,gameItems);
        gameList.setAdapter(gameListAdapter);
        gameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               ListView gameList = (ListView) parent;
               Map<String,Object> game = (Map<String,Object>) gameList.getAdapter().getItem(position);
               String gameTitle = game.get("title").toString();
               Intent intent = new Intent(getApplicationContext(), ReadyToPort.class);
               intent.putExtra("title",gameTitle);
               startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_game, menu);
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

    public List<Map<String,Object>> getGameItems (){

        List<Map<String,Object>> gameItems = new ArrayList<>();

        for(int i = 0 ; i < DATA_STORE.length; i++){
            Map<String,Object> game = new HashMap<>();
            game.put("title",DATA_STORE[i]);
            game.put("date", "Date not implemented");
            gameItems.add(game);
        }

        return gameItems;
    }

}
