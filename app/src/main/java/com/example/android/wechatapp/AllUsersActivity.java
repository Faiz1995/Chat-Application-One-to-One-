package com.example.android.wechatapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private RecyclerView allUsersList;
    private DatabaseReference allDatabaseUserReference;
    private FirebaseDatabase database;
    myAdapter adapter;
    List<All_Users> list;
    All_Users allUsers;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        mtoolbar=findViewById(R.id.all_user_appbar_layout);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        allUsersList=findViewById(  R.id.recycler_view);
        allUsersList.setHasFixedSize(true);
        allUsersList.setLayoutManager(new LinearLayoutManager(this));
        allUsersList.setItemAnimator(new DefaultItemAnimator());
        allUsersList.addItemDecoration(new DividerItemDecoration(getApplicationContext(),LinearLayoutManager.VERTICAL));
        database= FirebaseDatabase.getInstance();
        allDatabaseUserReference= database.getReference().child("Users");
        allDatabaseUserReference.keepSynced(true);
        allUsers = new All_Users();
        list=new ArrayList<>();
        adapter=new myAdapter(list);

        usersPage();

        }


    public void usersPage(){
        Toast.makeText(this, "In on start", Toast.LENGTH_SHORT).show();
        allDatabaseUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {

                    String id = messageSnapshot.getRef().getKey().toString();
                    String name = (String) messageSnapshot.child("user_name").getValue().toString();
                    String status = (String) messageSnapshot.child("user_status").getValue().toString();
                    String image = (String) messageSnapshot.child("user_thumb_image").getValue().toString();

                    allUsers=new All_Users(id,name,status,image);
                    list.add(allUsers);
                    adapter = new myAdapter(list);
                    allUsersList.setAdapter(adapter);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

        }

    public class myAdapter extends RecyclerView.Adapter<myAdapter.myViewholder>{

        List <All_Users> list;
        View view;
        public myAdapter(List <All_Users> list){
            this.list=list;

        }
        @NonNull
        @Override
        public myViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            view= LayoutInflater.from(parent.getContext()).inflate(  R.layout.all_user_layout,parent,false);

            return new myViewholder(view);


        }

        @Override
        public void onBindViewHolder(@NonNull myViewholder holder, final int position) {
            final All_Users allUsers=list.get(position);
            holder.name.setText(allUsers.getUser_name());
            holder.setUserProfile(getApplicationContext(),allUsers.getUser_thumb_image());
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String visit_user_id=list.get(position).getId();
                    Toast.makeText(AllUsersActivity.this, visit_user_id, Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent(getApplicationContext(),ProfileActivity.class);

                    intent.putExtra("visit_user_id",visit_user_id);
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
        public class myViewholder extends RecyclerView.ViewHolder{
            TextView name,status;
            CircleImageView image;
            View mView;
            public myViewholder(View mView) {
                super(mView);
                this.mView=mView;

                name=mView.findViewById(R.id.all_users_name_field);
                status=mView.findViewById(R.id.all_user_status_field);
                image=mView.findViewById(R.id.all_user_dp);


            }
            public void setUserProfile(final Context ctx, final String userProfile) {
                Picasso.with(ctx).load(userProfile).placeholder(R.drawable.user).networkPolicy(NetworkPolicy.OFFLINE).into(image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }


                    @Override
                    public void onError() {
                        Picasso.with(ctx).load(userProfile).placeholder(R.drawable.user).into(image);

                    }
                });

            }
        }
    }
}



