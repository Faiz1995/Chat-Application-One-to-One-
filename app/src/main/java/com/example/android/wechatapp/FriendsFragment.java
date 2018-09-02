package com.example.android.wechatapp;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView Friends_List;
    private FirebaseAuth mAuth;
    private DatabaseReference friends_ref;
    private DatabaseReference User_ref;
    private View mView;
    String online_user_id;

    public FriendsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        // Inflate the layout for this fragment

        mView = inflater.inflate(R.layout.fragment_friends, container, false);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();

        User_ref = FirebaseDatabase.getInstance().getReference().child("Users");

        friends_ref = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        friends_ref.keepSynced(true);

        Friends_List = (RecyclerView) mView.findViewById(R.id.friends_list);
        Friends_List.setLayoutManager(new LinearLayoutManager(getContext()));

        return mView;


    }
    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<All_Friends,Friends_Viewholder> friends_listview =
                new FirebaseRecyclerAdapter<All_Friends, Friends_Viewholder>
                        (
                                All_Friends.class,
                                R.layout.all_user_layout,
                                Friends_Viewholder.class,
                                friends_ref

                        ) {
                    @Override
                    protected void populateViewHolder(final Friends_Viewholder viewHolder, All_Friends model, int position) {

                        viewHolder.setDate(model.getDate());
                        String list_user_id = getRef(position).getKey();
                        User_ref.child(list_user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                String name = dataSnapshot.child("user_name").getValue().toString();
                                String user_thumb_image = dataSnapshot.child("user_thumb_image").getValue().toString();

                                viewHolder.set_Username(name);
                                viewHolder.set_user_thumb_image(user_thumb_image,getContext());


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                };
        Friends_List.setAdapter(friends_listview);
    }
    public static class Friends_Viewholder extends RecyclerView.ViewHolder {

        View mView;

        public Friends_Viewholder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDate(String date)
        {
            TextView since_friend_date = (TextView) mView.findViewById(R.id.all_user_status_field);
            since_friend_date.setText(date);
        }

        public  void set_Username(String name)
        {
            TextView friend_name = (TextView) mView.findViewById(R.id.all_users_name_field);
            friend_name.setText(name);
        }

        public  void set_user_thumb_image(final String user_thumb_image,final Context ctx) {

            final CircleImageView thumb_image =  (CircleImageView) mView.findViewById(R.id.all_user_dp);
            Picasso.with(ctx).load(user_thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.user).into(thumb_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                    Picasso.with(ctx).load(user_thumb_image).placeholder(R.drawable.user).into(thumb_image);
                }
            });
        }
    }

}
