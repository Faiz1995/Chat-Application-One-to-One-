package com.example.android.wechatapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private Button profile_send_request_btn,profile_decline_request_btn;
    private TextView profile_name,profile_status;
    private ImageView profile_dp;
    private DatabaseReference users_reference;
    private DatabaseReference friend_request_reference;
    private DatabaseReference friends_reference;
    private DatabaseReference Notification_reference;
    private String current_state;
    private FirebaseAuth mAuth;
    public String sender_user_id,receiver_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profile_decline_request_btn = (Button) findViewById(R.id.profile_visit_decline_request);
        profile_send_request_btn = (Button) findViewById(R.id.profile_visit_send_request);
        profile_name = (TextView) findViewById(R.id.profile_visit_user_name);
        profile_status = (TextView) findViewById(R.id.profile_visit_user_status);
        profile_dp = (ImageView) findViewById(R.id.profile_visit_user_image);

        current_state = "not_friends";

        //NOTIFICATION REFERENCE
        Notification_reference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        Notification_reference.keepSynced(true);

        //FRIEND_REQUEST_REFERENCE
        friend_request_reference = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");
        friend_request_reference.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        sender_user_id = mAuth.getCurrentUser().getUid();

        //ADDED FRIENDS REFERENCE
        friends_reference = FirebaseDatabase.getInstance().getReference().child("Friends");
        friends_reference.keepSynced(true);

        //ALL USERS REFERENCE
        users_reference = FirebaseDatabase.getInstance().getReference().child("Users");
        receiver_user_id = getIntent().getExtras().getString("profile_user_id").toString();


        users_reference.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                String image = dataSnapshot.child("user_image").getValue().toString();

                profile_name.setText(name);
                profile_status.setText(status);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.user).into(profile_dp);

                friend_request_reference.child(sender_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            if (dataSnapshot.hasChild(receiver_user_id)) {
                                String request_type = dataSnapshot.child(receiver_user_id)
                                        .child("request_type").getValue().toString();

                                if (request_type.equals("sender")) {
                                    current_state = "request_sent";
                                    profile_send_request_btn.setText("Cancel Friend Request");

                                    profile_decline_request_btn.setVisibility(View.INVISIBLE);
                                    profile_decline_request_btn.setEnabled(false);

                                } else if (request_type.equals("receiver")) {
                                    current_state = "receive_request";
                                    profile_send_request_btn.setText("Accept Friend Request");

                                    profile_decline_request_btn.setVisibility(View.VISIBLE);
                                    profile_decline_request_btn.setEnabled(true);
                                    
                                    profile_decline_request_btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Decline_Request_Method();
                                        }
                                    });
                                }
                            }

                         else {
                            friends_reference.child(sender_user_id)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            if (dataSnapshot.hasChild(receiver_user_id)) {
                                                current_state = "friend";
                                                profile_send_request_btn.setText("Unfriend this person ");

                                                profile_decline_request_btn.setVisibility(View.INVISIBLE);
                                                profile_decline_request_btn.setEnabled(false);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        profile_decline_request_btn.setVisibility(View.INVISIBLE);
        profile_decline_request_btn.setEnabled(false);

        if (!sender_user_id.equals(receiver_user_id)){
            profile_send_request_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    profile_send_request_btn.setEnabled(false);
                    if (current_state.equals("not_friends")) {
                        Send_Friend_Request_Method();
                    }

                    if (current_state.equals("request_sent")) {
                        Cancel_Friend_Request_Method();
                    }

                    if (current_state.equals("receive_request")) {
                        Accept_Friend_Request_Method();
                    }

                    if (current_state.equals("friend")) {
                        Unfriend_Method();
                    }
                }
            });
    }
    else
        {

            profile_send_request_btn.setVisibility(View.INVISIBLE);
            profile_decline_request_btn.setVisibility(View.INVISIBLE);

        }

      //  Intent intent = new Intent(ProfileActivity.this,SettingsActivity.class);
      //  startActivity(intent);
    }

    private void Decline_Request_Method() {

        friend_request_reference.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            friend_request_reference.child(receiver_user_id).child(sender_user_id)
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        profile_send_request_btn.setEnabled(true);
                                        current_state = "not_friend";
                                        profile_send_request_btn.setText("Send Friend Request");

                                        profile_decline_request_btn.setVisibility(View.INVISIBLE);
                                        profile_decline_request_btn.setEnabled(false);
                                    }
                                }
                            });
                        }
                    }
                });

    }


    private void Unfriend_Method() {

    friends_reference.child(sender_user_id).child(receiver_user_id).removeValue()
            .addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful())
            {
                friends_reference.child(receiver_user_id).child(sender_user_id).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {

                                    profile_send_request_btn.setEnabled(true);
                                    current_state = "not_friend";
                                    profile_send_request_btn.setText("Send Friend Request");

                                    profile_decline_request_btn.setVisibility(View.INVISIBLE);
                                    profile_decline_request_btn.setEnabled(false);
                                }
                            }
                        });
            }
        }
    });






    }

    private void Accept_Friend_Request_Method()
        {

            Calendar calendar_date = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
            final String current_date = simpleDateFormat.format(calendar_date.getTime());

            friends_reference.child(sender_user_id).child(receiver_user_id)
                    .child("date")
                    .setValue(current_date)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            friends_reference.child(receiver_user_id).child(sender_user_id)
                                    .child("date")
                                    .setValue(current_date)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                            friend_request_reference.child(sender_user_id).child(receiver_user_id)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful())
                                {
                                    friend_request_reference.child(receiver_user_id).child(sender_user_id)
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                profile_send_request_btn.setEnabled(true);
                                                current_state = "friend";
                                                profile_send_request_btn.setText("Unfriend this user");

                                                profile_decline_request_btn.setVisibility(View.VISIBLE);
                                                profile_decline_request_btn.setEnabled(true);
                                            }
                                        }
                                    });
                                }
                            }
                        });

                                        }
                                    });
                        }
                    });
         }


    private void Cancel_Friend_Request_Method() {

        friend_request_reference.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            friend_request_reference.child(receiver_user_id).child(sender_user_id)
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        profile_send_request_btn.setEnabled(true);
                                       current_state = "not_friend";
                                       profile_send_request_btn.setText("Send Friend Request");

                                        profile_decline_request_btn.setVisibility(View.INVISIBLE);
                                        profile_decline_request_btn.setEnabled(false);
                                    }
                                }
                            });
                        }
                    }
                });
    }

    private void Send_Friend_Request_Method()
        {
            friend_request_reference.child(sender_user_id).child(receiver_user_id).child("request_type")
            .setValue("sender")
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> send_task) {

                    if(send_task.isSuccessful())
                    {
                        friend_request_reference.child(receiver_user_id).child(sender_user_id)
                                .child("request_type")
                                .setValue("receiver")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> recv_task) {

                                        if (recv_task.isSuccessful())
                                        {
                                            HashMap<String,String> notification_data = new HashMap<String, String>();

                                            notification_data.put("from",sender_user_id);
                                            notification_data.put("type","request");

                                            Notification_reference.child(receiver_user_id).push()
                                                    .setValue(notification_data)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful())
                                                            {

                                                                profile_send_request_btn.setEnabled(true);
                                                                current_state = "request_sent";
                                                                profile_send_request_btn.setText("Cancel Friend Request");

                                                                profile_decline_request_btn.setVisibility(View.INVISIBLE);
                                                                profile_decline_request_btn.setEnabled(false);

                                                            }
                                                        }
                                                    });

                                        }
                                    }
                                });
                    }
                }
            });

         }
}
