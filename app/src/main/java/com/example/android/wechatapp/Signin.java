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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Signin extends AppCompatActivity {

    private  Button login_btn;
    private EditText login_email,login_password;
    Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference user_reference;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mAuth = FirebaseAuth.getInstance();
        user_reference = FirebaseDatabase.getInstance().getReference().child("Users");
        login_btn = (Button) findViewById(R.id.sign_in_btn);
        login_email = (EditText) findViewById(R.id.sign_in_email);
        login_password = (EditText) findViewById(R.id.sign_in_password);
        loadingbar = new ProgressDialog(this);

        mToolbar = (Toolbar)findViewById(R.id.signin_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign In");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = login_email.getText().toString();
                String password = login_password.getText().toString();
                LoginUser(email,password);
            }
        });
    }

    private void LoginUser(String email, String password) {
        if ((TextUtils.isEmpty(email)) && (TextUtils.isEmpty(password))) {
            Toast.makeText(Signin.this, "Please fill the required fields", Toast.LENGTH_LONG).show();
                }
        else if (TextUtils.isEmpty(email)) {
            Toast.makeText(Signin.this, "Please fill the email field", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(password))

        {
            Toast.makeText(Signin.this, "Please fill the password field", Toast.LENGTH_LONG).show();
        }
        else
            {
                loadingbar.setTitle("Login Account");
                loadingbar.setMessage("Please wait, while we are verifying your credentials");
                loadingbar.show();

                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            String online_user_id = mAuth.getCurrentUser().getUid();
                            String token = FirebaseInstanceId.getInstance().getToken();

                            user_reference.child("user_token").setValue(token)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Intent intent = new Intent(Signin.this,ChatsActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });

                        }
                        else {
                            Toast.makeText(Signin.this,"Please try again ! ,Username & Password are not correct",Toast.LENGTH_LONG).show();
                        }
                        loadingbar.dismiss();
                    }

                });
            }
    }
}
