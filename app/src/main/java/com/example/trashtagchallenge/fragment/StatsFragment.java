package com.example.trashtagchallenge.fragment;

import android.graphics.drawable.Icon;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.trashtagchallenge.R;
import com.example.trashtagchallenge.classes.StatsClass;
import com.example.trashtagchallenge.classes.StatsList;
import com.example.trashtagchallenge.classes.Trash;
import com.example.trashtagchallenge.classes.TrashDate;
import com.example.trashtagchallenge.classes.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class StatsFragment extends Fragment {


    private ListView listView;
    private TextView textViewEntriesAddedThisWeek;
    private TextView textViewEntriesAddedThisWeekMain;
    private TextView textViewEntriesAddedToday;
    private TextView textViewEntriesAddedTodayMain;
    private TextView textViewLeaderboard;
    private DatabaseReference databaseReferenceTrash;
    private List<StatsClass> statsClassList;
    private TextView textViewGlobalStatsMain;
    private TextView textViewCleanedAddedThisWeek;
    private TextView textViewCleanedAddedThisWeekMain;
    private TextView textViewCleanedAddedToday;
    private TextView textViewCleanedAddedTodayMain;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        listView = view.findViewById(R.id.listViewStats);
        textViewEntriesAddedThisWeek = view.findViewById(R.id.text_added_this_week_points);
        textViewEntriesAddedThisWeekMain = view.findViewById(R.id.text_added_this_week);
        textViewEntriesAddedThisWeekMain.setText("Trash spots added this week ");
        textViewEntriesAddedToday = view.findViewById(R.id.text_added_today_points);
        textViewEntriesAddedTodayMain = view.findViewById(R.id.text_added_today);
        textViewEntriesAddedTodayMain.setText("Trash spots added today ");

        textViewLeaderboard = view.findViewById(R.id.text_leaderboard);
        textViewLeaderboard.setText("Leaderboard");
        textViewGlobalStatsMain = view.findViewById(R.id.text_global_stats);
        textViewGlobalStatsMain.setText("Global Statistics");
        textViewCleanedAddedThisWeek = view.findViewById(R.id.text_cleaned_this_week_points);
        textViewCleanedAddedThisWeekMain = view.findViewById(R.id.text_cleaned_this_week);
        textViewCleanedAddedThisWeekMain.setText("Trash spots cleaned this week:");
        textViewCleanedAddedToday = view.findViewById(R.id.text_cleaned_this_day_points);
        textViewCleanedAddedTodayMain = view.findViewById(R.id.text_cleaned_this_day);
        textViewCleanedAddedTodayMain.setText("Trash spots cleaned today:");

        statsClassList = new ArrayList<>();
        databaseReferenceTrash = FirebaseDatabase.getInstance().getReference();
        databaseReferenceTrash.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Boolean trashStatsAcquired = false;
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                int day = calendar.get(Calendar.DATE);
                int month = calendar.get(Calendar.MONTH) + 1;
                int year = calendar.get(Calendar.YEAR);
                int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
                int trashEntriesThisWeek = 0;
                int trashCleanedThisWeek = 0;
                int trashEntriesToday = 0;
                int trashCleanedToday = 0;


                for(DataSnapshot userSnapshot : dataSnapshot.child("users").getChildren()){
                    User user = userSnapshot.getValue(User.class);
                    StatsClass statsClass = new StatsClass(user.getUsername(),0);
                    for(DataSnapshot trashSnapshot : dataSnapshot.child("trashes").getChildren()){
                        Trash trash = trashSnapshot.getValue(Trash.class);
                        //POINTS
                        //ADDED
                        if(user.getUsername().trim().equals(trash.getEntryUsername())){
                            statsClass.setPoints(statsClass.getPoints()+1);
                        }
                        //CLEANED
                        if(trash.getCleanedUsername()!=null){
                            if(user.getUsername().trim().equals(trash.getCleanedUsername())){
                                statsClass.setPoints(statsClass.getPoints()+3);
                            }
                        }
                        //GLOBAL STATS
                        if(!trashStatsAcquired){
                            //CLEANED
                            if(trash.getCleanedUsername()!=null) {
                                //THIS WEEK
                                if (trash.getCleanedDate().getWeek() == weekOfYear && trash.getCleanedDate().getYear() == year) {
                                    trashCleanedThisWeek++;
                                    //TODAY
                                    if(trash.getCleanedDate().getDay() == day){
                                        trashCleanedToday++;
                                    }
                                }
                            }
                            //ADDED
                            //THIS WEEK
                            if(trash.getEntryDate().getWeek() == weekOfYear && trash.getEntryDate().getYear() == year){
                                trashEntriesThisWeek++;
                                //TODAY
                                if(trash.getEntryDate().getDay() == day){
                                    trashEntriesToday++;
                                }
                            }
                        }

                    }
                    if(!trashStatsAcquired){
                        trashStatsAcquired = true;
                        textViewEntriesAddedThisWeek.setText(String.valueOf(trashEntriesThisWeek));
                        textViewEntriesAddedToday.setText(String.valueOf(trashEntriesToday));
                        textViewCleanedAddedThisWeek.setText(String.valueOf(trashCleanedThisWeek));
                        textViewCleanedAddedToday.setText(String.valueOf(trashCleanedToday));
                    }
                    statsClassList.add(statsClass);
                }

                Collections.sort(statsClassList, new Comparator<StatsClass>() {
                    @Override
                    public int compare(StatsClass s1, StatsClass s2) {
                        if (s1.getPoints() < s2.getPoints())
                            return 1;
                        if (s1.getPoints() > s2.getPoints())
                            return -1;
                        return 0;
                    }
                });


                StatsList adapter = new StatsList(getActivity(),statsClassList);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }


}
