package com.example.trashtagchallenge.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.bumptech.glide.Glide;
import com.example.trashtagchallenge.R;
import com.example.trashtagchallenge.classes.Trash;
import com.example.trashtagchallenge.classes.TrashDate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddSpottedTrashFragment extends Fragment {

    private Button buttonSaveTrash;
    private ImageView imageViewAddTrash;
    private EditText editTextDescription;
    private TextView textViewType;
    private TextView textViewDate;

    private DatabaseReference databaseTrash;
    private static final int IMAGE_CAPTURE_REQUEST_CODE = 100;
    private Uri imageUri;
    private TrashDate trashDate;

    public AddSpottedTrashFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_trash, container, false);
        initViews(view);

        imageViewAddTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        databaseTrash = FirebaseDatabase.getInstance().getReference("trashes");
        buttonSaveTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUri==null ) {
                    Toast.makeText(getContext(),"You have to add image",Toast.LENGTH_SHORT).show();
                }
                else if(editTextDescription.getText().toString().trim() == null || editTextDescription.getText().toString().trim().equals("")){
                    Toast.makeText(getContext(),"You have to add description",Toast.LENGTH_SHORT).show();
                }
                else {
                    addSpottedTrash();
                    NavDirections navDirections = AddSpottedTrashFragmentDirections.actionAddTrashFragmentToMapFragment();
                    Navigation.findNavController(getView()).navigate(navDirections);
                }
            }
        });


        return view;
    }

    private void initViews(View view){
        buttonSaveTrash = view.findViewById(R.id.button_saveTrash);
        editTextDescription = view.findViewById(R.id.text_trashDescription);
        imageViewAddTrash = view.findViewById(R.id.image_view_add_image);
        textViewType = view.findViewById(R.id.text_type);
        textViewType.setText("Recorded");
        textViewDate = view.findViewById(R.id.text_date);
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        trashDate = new TrashDate(calendar.get(Calendar.DATE),calendar.get(Calendar.MONTH) + 1,calendar.get(Calendar.YEAR),calendar.get(Calendar.WEEK_OF_YEAR));
        String date = trashDate.getDay() + "/" + trashDate.getMonth() + "/" + trashDate.getYear();
        textViewDate.setText(date);
    }

    private void addSpottedTrash(){
        String entryDescription = editTextDescription.getText().toString().trim();
        String trashID = databaseTrash.push().getKey();

        double lat = getArguments().getDouble("lat");
        double lng = getArguments().getDouble("lng");
        String entryUsername = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String entryLink =imageUri.toString();


        Trash trash = new Trash(trashID,entryDescription,lat,lng,entryUsername,entryLink,trashDate);
        databaseTrash.child(trashID).setValue(trash);
        Toast.makeText(getContext(),"Trash added",Toast.LENGTH_SHORT).show();
    }



    private void takePicture(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, IMAGE_CAPTURE_REQUEST_CODE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
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

        final StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("TrashTagPics/"+ FirebaseAuth.getInstance().getCurrentUser().getUid() + timestamp);
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
                imageViewAddTrash.setBackground(null);
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while(!uri.isComplete());
                imageUri = uri.getResult();
                Toast.makeText(getActivity(), "Upload Success", Toast.LENGTH_SHORT).show();
                if(imageUri!=null)
                    Glide.with(getActivity()).load(imageUri).into(imageViewAddTrash);
            }
        });
    }

}
