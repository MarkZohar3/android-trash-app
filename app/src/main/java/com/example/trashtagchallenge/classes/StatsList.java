package com.example.trashtagchallenge.classes;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trashtagchallenge.R;

import java.util.List;

public class StatsList extends ArrayAdapter<StatsClass> {
    private Activity context;
    private List<StatsClass> statsClassList;

    public StatsList(Activity context, List<StatsClass> statsClassList){
        super(context, R.layout.list_layout,statsClassList);
        this.context = context;
        this.statsClassList = statsClassList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);

        TextView textViewName = listViewItem.findViewById(R.id.textViewName);
        TextView textViewGenre = listViewItem.findViewById(R.id.textViewGenre);
        ImageView imageView = listViewItem.findViewById(R.id.image_view_medal);
        StatsClass statsClass = statsClassList.get(position);
        if(position==0){
            imageView.setImageResource(R.mipmap.goldmedal);
        }
        else if(position==1){
            imageView.setImageResource(R.mipmap.silvermedal);
        }
        else if(position==2){
            imageView.setImageResource(R.mipmap.bronzemedal);
        }
        textViewName.setText(statsClass.getUsername());
        textViewGenre.setText("Points: " + String.valueOf(statsClass.getPoints()));

        return listViewItem;
    }

}

