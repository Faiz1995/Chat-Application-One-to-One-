package com.example.android.wechatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView dp;
    private TextView Settings_name,Setting_status;
    private Button Settings_profile_image_btn,Settings_change_status_btn;
    private DatabaseReference getUserDataReference;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private static final int gallery_pick = 1;
    private StorageReference thumb_image_reference;
    private ProgressDialog loading_bar;
    Bitmap thumb_bitmap = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dp = (CircleImageView) findViewById(R.id.Settings_profile_image);
        Settings_name = (TextView) findViewById(R.id.Settings_username);
        Setting_status = (TextView) findViewById(R.id.Settings_user_Status);
        Settings_profile_image_btn = (Button) findViewById(R.id.Settings_change_profile_image_btn);
        Settings_change_status_btn = (Button) findViewById(R.id.Settings_change_status_btn);
        loading_bar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        String online_user_id = mAuth.getCurrentUser().getUid();
        getUserDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);
        getUserDataReference.keepSynced(true);
        storageReference = FirebaseStorage.getInstance().getReference().child("profile_images");
        thumb_image_reference = FirebaseStorage.getInstance().getReference().child("thumb_images");

        getUserDataReference.addValueEventListener(new ValueEventListener() {
            @Override


            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    String u_name = dataSnapshot.child("user_name").getValue().toString();
                    final String u_image = dataSnapshot.child("user_image").getValue().toString();
                    String u_thumb_image = dataSnapshot.child("user_thumb_image").getValue().toString();
                    String u_status = dataSnapshot.child("user_status").getValue().toString();

                    Settings_name.setText(u_name);
                    Setting_status.setText(u_status);

                    if(!u_image.equals("default profile"))
                    {
                        Picasso.with(SettingsActivity.this).load(u_image).networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.user).into(dp, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(SettingsActivity.this).load(u_image).placeholder(R.drawable.user).into(dp);

                            }
                        });

                    }
                }
                else
                {
                    Settings_name.setText("Data not fetch");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

         Settings_profile_image_btn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent();
                 intent.setAction(Intent.ACTION_GET_CONTENT);
                 intent.setType("image/*");
                 startActivityForResult(intent,gallery_pick);
             }
         });


         Settings_change_status_btn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 String old_status = Setting_status.getText().toString();

                 Intent intent = new Intent(SettingsActivity.this,StatusActivity.class);
                 intent.putExtra("user_status",old_status);
                 startActivity(intent);
             }
         });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==gallery_pick && resultCode==RESULT_OK && data!=null)
        {
            Uri uri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                loading_bar.setTitle("Updating Profile Image");
                loading_bar.setMessage("Please wait while we are updating your profile image");
                loading_bar.show();

                Uri resultUri = result.getUri();

                File thumb_file_path_Uri = new File(resultUri.getPath());

                String u_id = mAuth.getCurrentUser().getUid();

                try{
                    thumb_bitmap = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(60)
                            .compressToBitmap(thumb_file_path_Uri);
                }catch (IOException e)
                {
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,60,byteArrayOutputStream);
                final byte [] thumb_byte = byteArrayOutputStream.toByteArray();

                final StorageReference thumb_File_path = thumb_image_reference.child(u_id + "jpg");


                StorageReference filepath = storageReference.child(u_id + "jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful())
                        {
                           final String downloadUrl = task.getResult().getDownloadUrl().toString();

                           UploadTask uploadTask = thumb_File_path.putBytes(thumb_byte);

                           uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                               @Override
                               public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                  String thumb_download_Url = thumb_task.getResult().getDownloadUrl().toString();

                                  if (thumb_task.isSuccessful())
                                  {
                                      Map update_user_data = new HashMap();
                                      update_user_data.put("user_image", downloadUrl);
                                      update_user_data.put("user_thumb_image",thumb_download_Url);

                                      getUserDataReference.updateChildren(update_user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                          @Override
                                          public void onComplete(@NonNull Task<Void> task) {

                                              Toast.makeText(SettingsActivity.this,"Image updated Successfully",Toast.LENGTH_LONG).show();

                                              loading_bar.dismiss();
                                          }
                                      });
                                  }
                               }
                           });




                        }
                        else
                        {
                            Toast.makeText(SettingsActivity.this,"Error occur , Please try again ! ",Toast.LENGTH_SHORT).show();
                            loading_bar.dismiss();

                        }
                    }
                });
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
}
