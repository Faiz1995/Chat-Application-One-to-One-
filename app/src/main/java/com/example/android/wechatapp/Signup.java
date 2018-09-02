package com.example.android.wechatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Signup extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText signup_name;
    private EditText signup_password;
    private EditText signup_email;
    private Button signup_btn;
    private ProgressDialog Loading_bar;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signup_name = (EditText) findViewById(R.id.sign_up_name);
        signup_email = (EditText) findViewById(R.id.sign_up_email);
        signup_password = (EditText) findViewById(R.id.sign_up_password);
        signup_btn = (Button) findViewById(R.id.sign_up_btn);
        Loading_bar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar)findViewById(R.id.signup_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String user_name = signup_name.getText().toString();
                String user_email = signup_email.getText().toString();
                String user_password = signup_password.getText().toString();

                Register_User(user_name, user_email, user_password);
            }
        });


    }

    private void Register_User(final String user_name, final String user_email, final String user_password) {
        if ((TextUtils.isEmpty(user_name)) && (TextUtils.isEmpty(user_email)) && TextUtils.isEmpty(user_password)) {
            Toast.makeText(Signup.this, "Please fill the required fields", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(user_name)) {
            Toast.makeText(Signup.this, "Please fill the name field", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(user_email)) {
            Toast.makeText(Signup.this, "Please fill the email field", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(user_password))

        {
            Toast.makeText(Signup.this, "Please fill the password field", Toast.LENGTH_LONG).show();
        } else {
            Loading_bar.setTitle("Creating New Account");
            Loading_bar.setMessage("Please wait. It will take few moments only...");
            Loading_bar.show();
            mAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        //  Log.e(TAG, "onComplete: Failed=" + task.getException().getMessage());

                        Toast.makeText(Signup.this, "User Added Successfully", Toast.LENGTH_LONG).show();

                        String current_Uid = mAuth.getCurrentUser().getUid();
                        String token = FirebaseInstanceId.getInstance().getToken();
                        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_Uid);
                        databaseReference.child("user_name").setValue(user_name);
                        databaseReference.child("user_status").setValue("Hey there,I'm using WeChat app");
                        databaseReference.child("user_token").setValue(token);
                        databaseReference.child("user_image").setValue("default profile");
                        databaseReference.child("user_thumb_image").setValue("default image")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){

                                            Intent intent = new Intent(Signup.this, ChatsActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                    }
                                });


                    } else {
                        // Log.e(TAG, "onComplete: Failed=" + task.getException().getMessage());
                        Toast.makeText(Signup.this, "Error, User not added successfully", Toast.LENGTH_LONG).show();
                    }
                    Loading_bar.dismiss();
                }
            });
        }


    }
}