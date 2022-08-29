package com.example.trashtagchallenge.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trashtagchallenge.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private EditText edittxtEmail;
    private TextView txtviewRegister;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

    }

    private void init(){
        btnLogin = findViewById(R.id.button_sign_in);
        txtviewRegister = findViewById(R.id.text_view_register);
        edittxtEmail = findViewById(R.id.text_email);
        final EditText edittxtPassword = findViewById(R.id.edit_text_password);
        mAuth = FirebaseAuth.getInstance();



        txtviewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edittxtEmail.getText().toString().trim();
                String password = edittxtPassword.getText().toString().trim();
                if(email.isEmpty()){
                    Toast.makeText(LoginActivity.this,"email required",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(LoginActivity.this,"email invalid",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.length()<8){
                    Toast.makeText(LoginActivity.this,"password must contain at least 8 characters",Toast.LENGTH_SHORT).show();
                    return;
                }

                loginUser(email,password);
            }
        });

    }




    private void loginUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this,new OnCompleteListener<AuthResult>(){
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(LoginActivity.this,"Login error: " + task.getException(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
