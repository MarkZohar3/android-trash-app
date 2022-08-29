package com.example.trashtagchallenge.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trashtagchallenge.R;
import com.example.trashtagchallenge.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference databaseUser;
    private EditText editTextUsername;
    private TextView txtviewLogin;
    private Button btnRegister;
    private EditText edittxtEmail;
    private EditText edittxtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
        mAuth = FirebaseAuth.getInstance();
        databaseUser = FirebaseDatabase.getInstance().getReference("users");
        txtviewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edittxtEmail.getText().toString().trim();
                String password = edittxtPassword.getText().toString().trim();
                if(email.isEmpty()){
                    Toast.makeText(RegisterActivity.this,"email required",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(RegisterActivity.this,"email invalid",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.length()<8){
                    Toast.makeText(RegisterActivity.this,"password must contain at least 8 characters",Toast.LENGTH_SHORT).show();
                    return;
                }
                registerUser(email,password);
            }
        });

    }



    private void initViews(){
        txtviewLogin = findViewById(R.id.text_view_login);
        btnRegister = findViewById(R.id.button_register);
        editTextUsername = findViewById(R.id.text_username);
        edittxtEmail = findViewById(R.id.text_email);
        edittxtPassword = findViewById(R.id.edit_text_password);
    }

    private void registerUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String username = editTextUsername.getText().toString();
                    User user = new User(FirebaseAuth.getInstance().getUid(),username);
                    databaseUser.child(FirebaseAuth.getInstance().getUid()).setValue(user);
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build();
                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(RegisterActivity.this,"Registration successfull" + task.getException(),Toast.LENGTH_SHORT);
                        }
                    });

                    Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(RegisterActivity.this,"Registration failed" + task.getException(),Toast.LENGTH_SHORT);
                }
            }
        });
    }
    @Override protected void onStart(){
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
