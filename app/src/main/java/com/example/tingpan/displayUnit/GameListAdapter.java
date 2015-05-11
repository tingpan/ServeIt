package com.example.tingpan.displayUnit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tingpan.application.R;

import java.util.List;
import java.util.Map;

/**
 * Created by TingPan on 1/29/15.
 */

public class GameListAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String,Object>> gameItems;
    private LayoutInflater listContainer;

    class ViewHolder {
        TextView gameTitle;
        TextView gameDate;
    }

    public GameListAdapter(Context context, List<Map<String, Object>> gameItems){
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.gameItems = gameItems;
    }

    @Override
    public int getCount() {
        return gameItems.size();
    }

    @Override
    public Object getItem(int position) {
        return gameItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
         if (convertView == null){
             holder = new ViewHolder();
             convertView = listContainer.inflate(R.layout.game_item,null);
             holder.gameTitle = (TextView) convertView.findViewById(R.id.gameTile);
             holder.gameDate = (TextView) convertView.findViewById(R.id.gameDate);
             convertView.setTag(holder);
         } else {
             holder = (ViewHolder) convertView.getTag();
         }
        holder.gameTitle.setText(gameItems.get(position).get("title").toString());
        holder.gameDate.setText(gameItems.get(position).get("date").toString());

        return convertView;
    }
}
