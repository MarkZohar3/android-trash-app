package com.example.trashtagchallenge.fragment;


import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.trashtagchallenge.R;
import com.example.trashtagchallenge.classes.Trash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrashFragment extends Fragment {


    private ImageView imageViewEntry;
    private ImageView imageViewCleaned;
    private TextView textViewEntryDescription;
    private TextView textViewEntryUsername;
    private TextView textViewCleanedDescription;
    private TextView textViewCleanedUsername;
    private TextView textViewBefore;
    private TextView textViewAfter;
    private TextView textViewDateEntry;
    private TextView textViewDateCleaned;


    private DatabaseReference databaseReferenceTrash;
    private Trash trash = new Trash();
    private Uri imageUri;

    public TrashFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trash, container, false);
        initViews(view);


        trash.setTrashID(getArguments().getString("markerIdKey"));
        databaseReferenceTrash = FirebaseDatabase.getInstance().getReference().child("trashes").child(trash.getTrashID());
        databaseReferenceTrash.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                trash = dataSnapshot.getValue(Trash.class);

                textViewEntryDescription.setText(trash.getEntryDescription());
                textViewEntryUsername.setText(trash.getEntryUsername());
                imageUri = Uri.parse(trash.getEntryLink());
                if(imageUri!=null)
                    Glide.with(getContext()).load(imageUri).into(imageViewEntry);
                textViewBefore.setText("Before");
                textViewCleanedDescription.setText(trash.getCleanedDescription());
                textViewCleanedUsername.setText(trash.getCleanedUsername());
                imageUri = Uri.parse(trash.getCleanedLink());
                if(imageUri!=null)
                    Glide.with(getContext()).load(imageUri).into(imageViewCleaned);
                textViewAfter.setText("After");
                String dateEntry = trash.getEntryDate().getDay() + "/" + trash.getEntryDate().getMonth() + "/" + trash.getEntryDate().getYear();
                String dateCleaned = trash.getCleanedDate().getDay() + "/" + trash.getCleanedDate().getMonth() + "/" + trash.getCleanedDate().getYear();
                textViewDateEntry.setText(dateEntry);
                textViewDateCleaned.setText(dateCleaned);
                //textViewCleanedDescription.setLines(3);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void initViews(View view){
        imageViewEntry = view.findViewById(R.id.image_view1);
        imageViewCleaned = view.findViewById(R.id.image_view2);
        textViewEntryDescription = view.findViewById(R.id.text1_trashDescription);
        textViewCleanedDescription = view.findViewById(R.id.text2_trashDescription);
        textViewEntryUsername = view.findViewById(R.id.text1_username);
        textViewCleanedUsername = view.findViewById(R.id.text2_username);
        textViewBefore = view.findViewById(R.id.text_view_before);
        textViewAfter = view.findViewById(R.id.text_view_after);
        textViewDateEntry = view.findViewById(R.id.text1_date);
        textViewDateCleaned = view.findViewById(R.id.text2_date);
    }
}
