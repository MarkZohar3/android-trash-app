package com.example.trashtagchallenge.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.trashtagchallenge.R;
import com.example.trashtagchallenge.classes.Trash;
import com.example.trashtagchallenge.classes.TrashDate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddCleanedTrashFragment extends Fragment {


    private Button buttonSaveTrash;
    private ImageView imageViewAddTrash;
    private EditText editTextDescription;
    private DatabaseReference databaseReferenceTrash;
    private TextView textViewUsername;
    private TextView textViewType;
    private TextView textViewDescriptionMain;
    private TextView textViewAddImage;
    private TextView textViewDate;
    private TextView textViewDateMain;

    private static final int IMAGE_CAPTURE_REQUEST_CODE = 100;
    private Uri imageUri;
    private Trash trash = new Trash();

    public AddCleanedTrashFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_cleaned_trash, container, false);
        trash.setTrashID(getArguments().getString("markerIdKey"));
        initViews(view);

        imageViewAddTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!buttonSaveTrash.getText().toString().equals("Add Cleaned Record")){
                    takePicture();
                }
            }
        });




        buttonSaveTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(buttonSaveTrash.getText().toString().equals("Add Cleaned Record")) {
                    buttonSaveTrash.setText("Save");
                    imageViewAddTrash.setImageResource(R.drawable.trash_photo);
                    textViewDescriptionMain.setText("Add Description");
                    textViewDescriptionMain.setTextColor(ContextCompat.getColor(getContext(),R.color.colorAccent));
                    textViewAddImage.setVisibility(View.VISIBLE);
                    textViewAddImage.setTextColor(ContextCompat.getColor(getContext(),R.color.colorAccent));
                    editTextDescription.setText("");
                    textViewType.setText("Cleaned");
                    Date date = new Date();
                    SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy");
                    textViewDate.setText(formatter.format(date));
                    textViewUsername.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                }
                else{
                    if(imageUri==null ) {
                        Toast.makeText(getContext(),"You have to add image",Toast.LENGTH_SHORT).show();
                    }
                    else if(editTextDescription.getText().toString().trim() == null || editTextDescription.getText().toString().trim().equals("")){
                        Toast.makeText(getContext(),"You have to add description",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        addCleanedTrash();
                    }
                }
            }
        });


        databaseReferenceTrash = FirebaseDatabase.getInstance().getReference().child("trashes").child(trash.getTrashID());
        databaseReferenceTrash.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                trash = dataSnapshot.getValue(Trash.class);
                editTextDescription.setText(trash.getEntryDescription());
                textViewUsername.setText(trash.getEntryUsername());
                textViewType.setText("Recorded");
                String date = trash.getEntryDate().getDay() + "/" + trash.getEntryDate().getMonth() + "/" + trash.getEntryDate().getYear();
                textViewDate.setText(date);
                /*Calendar calendar = Calendar.getInstance();
                calendar.setTime(trash.getEntryDate());
                int j = calendar.get(Calendar.YEAR);*/

                imageUri = Uri.parse(trash.getEntryLink());
                if(imageUri!=null)
                    Glide.with(getContext()).load(imageUri).into(imageViewAddTrash);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }

    private void addCleanedTrash(){
        trash.setCleanedDescription(editTextDescription.getText().toString().trim());
        trash.setCleanedUsername(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        trash.setCleanedLink(imageUri.toString());
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        trash.setCleanedDate(new TrashDate(calendar.get(Calendar.DATE),calendar.get(Calendar.MONTH) + 1,calendar.get(Calendar.YEAR),calendar.get(Calendar.WEEK_OF_YEAR)));
        databaseReferenceTrash.setValue(trash).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(),"Trash added",Toast.LENGTH_SHORT).show();
                NavDirections navDirections = AddCleanedTrashFragmentDirections.actionAddCleanedTrashFragmentToMapFragment();
                Navigation.findNavController(getView()).navigate(navDirections);
            }
        });

    }

    private void initViews(View view){
        buttonSaveTrash = view.findViewById(R.id.button_saveTrash);
        editTextDescription = view.findViewById(R.id.text_trashDescription);
        imageViewAddTrash = view.findViewById(R.id.image_view_add_image);
        textViewUsername = view.findViewById(R.id.text_username);
        textViewType = view.findViewById(R.id.text_type);
        textViewDescriptionMain = view.findViewById(R.id.text_trashDescriptionMain);
        textViewAddImage = view.findViewById(R.id.text_add_image);
        textViewDate = view.findViewById(R.id.text_date);
        textViewDateMain =view.findViewById(R.id.text_dateMain);
        imageViewAddTrash.setBackground(null);
    }


    private void takePicture(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, IMAGE_CAPTURE_REQUEST_CODE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_CAPTURE_REQUEST_CODE && resultCode == RESULT_OK){
            Bitmap imageBitmap   = (Bitmap) data.getExtras().get("data");
            uploadImageAndSaveUri(imageBitmap);
        }
    }

    private void uploadImageAndSaveUri(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Date date = new Date(System.currentTimeMillis());
        String timestamp = date.toString();

        final StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("TrashTagPics/"+ FirebaseAuth.getInstance().getCurrentUser().getUid() + timestamp);
        bmp.compress(Bitmap.CompressFormat.JPEG, 90,baos);
        byte[] image = baos.toByteArray();
        final UploadTask uploadTask = storageRef.putBytes(image);



        uploadTask.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Upload Error: " +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                imageViewAddTrash.setBackground(null);
                while(!uri.isComplete());
                imageUri = uri.getResult();
                Toast.makeText(getActivity(), "Upload Success", Toast.LENGTH_SHORT).show();
                if(imageUri!=null)
                    Glide.with(getActivity()).load(imageUri).into(imageViewAddTrash);
            }
        });

    }



}
