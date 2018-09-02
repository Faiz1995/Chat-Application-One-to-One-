package com.example.android.wechatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    Toolbar mToolbar;
    Button update_status_btn;
    EditText write_status_field;
    ProgressDialog progressDialog;
    DatabaseReference StatusReference;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        StatusReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        progressDialog = new ProgressDialog(this);

        mToolbar = (Toolbar) findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Update Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        update_status_btn = (Button) findViewById(R.id.update_status);
        write_status_field = (EditText) findViewById(R.id.write_status);
        String old_status = getIntent().getExtras().get("user_status").toString();
        write_status_field.setText(old_status);


        update_status_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String new_status = write_status_field.getText().toString();
                ChangeProfileStatus(new_status);
 }
        });
    }

        private void ChangeProfileStatus(String new_status) {
        if(TextUtils.isEmpty(new_status))
        {
            Toast.makeText(StatusActivity.this,"Please fill the status field",Toast.LENGTH_SHORT).show();
        }
        else
            {
                progressDialog.setTitle("Updating Status");
                progressDialog.setMessage("Please wait it will take few moments only");
                progressDialog.show();
                StatusReference.child("user_status").setValue(new_status ).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            Intent intent = new Intent(StatusActivity.this,SettingsActivity.class);
                            startActivity(intent);

                            Toast.makeText(StatusActivity.this,"Profile Status updated successfully",Toast.LENGTH_SHORT).show();


                        }
                        else
                        {
                           Toast.makeText(StatusActivity.this,"Error occured ! Please try again",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }


    }
}
