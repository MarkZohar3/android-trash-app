package com.example.trashtagchallenge.fragment;


import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trashtagchallenge.classes.MyTrashList;
import com.example.trashtagchallenge.R;
import com.example.trashtagchallenge.classes.Trash;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {



    private EditText textEmail;
    private EditText textUsername;
    private ListView listViewMyTrashlist;
    private TextView textContributionsMain;
    private TextView textEmailVerified;
    private String username;
    private DatabaseReference databaseReferenceTrash;


    private List<Trash> trashList;



    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initViews(view);


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            textEmail.setText(user.getEmail());
        }
        username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        textUsername.setText(username);


        if(!user.isEmailVerified()){
            textEmailVerified.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!user.isEmailVerified()){
                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getActivity(),"Verification email sent.",Toast.LENGTH_SHORT).show();
                                    }

                                });
                            }
                            else {
                                textEmailVerified.setText("Email verified");
                                textEmail.setTextColor(Color.BLACK);
                            }
                        }
                    });
                }
            });
        }
        else {
            textEmailVerified.setText("Email verified");
            textEmail.setTextColor(Color.BLACK);
        }



        trashList = new ArrayList<>();
        databaseReferenceTrash = FirebaseDatabase.getInstance().getReference();
        databaseReferenceTrash.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                trashList.clear();
                for(DataSnapshot trashSnapshot : dataSnapshot.child("trashes").getChildren()){
                    Trash trash = trashSnapshot.getValue(Trash.class);
                    if(trash.getEntryUsername().trim().equals(username)) {
                        trashList.add(trash);
                    }
                    else if(trash.getCleanedUsername()!=null)
                    {
                        if(trash.getCleanedUsername().equals(username))
                            trashList.add(trash);
                    }
                }
                if(!trashList.isEmpty()){
                    textContributionsMain.setText("Contributions");
                }

                MyTrashList adapter = new MyTrashList(getActivity(),trashList);
                listViewMyTrashlist.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }



    private void initViews(View view){
        textEmail = view.findViewById(R.id.text_email);
        textUsername = view.findViewById(R.id.edit_text_name);
        textEmailVerified = view.findViewById(R.id.text_emailMain);
        listViewMyTrashlist = view.findViewById(R.id.listViewMyTrashlist);

        textContributionsMain = view.findViewById(R.id.text_contributionsMain);
    }

}
