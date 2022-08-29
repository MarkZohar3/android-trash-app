package com.example.trashtagchallenge.classes;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.trashtagchallenge.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MyTrashList extends ArrayAdapter<Trash> {
    private Activity context;
    private List<Trash> trashList;

    public MyTrashList(Activity context, List<Trash> trashList){
        super(context, R.layout.list_layout,trashList);
        this.context = context;
        this.trashList = trashList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);

        TextView textViewName = listViewItem.findViewById(R.id.textViewName);
        TextView textViewGenre = listViewItem.findViewById(R.id.textViewGenre);
        textViewGenre.setText("");
        Trash trash = trashList.get(position);
        textViewName.setText(trash.getEntryDescription());
        if (trash.getEntryUsername().trim().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())){
            textViewGenre.setText("Recorded");
        }
        if(trash.getCleanedUsername() != null) {
            if (trash.getCleanedUsername().trim().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
                if(textViewGenre.getText().toString().trim().equals("Recorded"))
                    textViewGenre.setText(textViewGenre.getText() + " and Cleaned");
                else
                    textViewGenre.setText("Cleaned");
            }
        }



        return listViewItem;
    }

}
