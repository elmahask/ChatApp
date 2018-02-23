package com.wael.elmahask.chatapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView mUsersList;
    private DatabaseReference mUsersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mToolbar = findViewById(R.id.users_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mUsersList = findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                        Users.class,
                        R.layout.users_layout,
                        UsersViewHolder.class,
                        mUsersDatabase
                ) {
                    @Override
                    protected void populateViewHolder(UsersViewHolder usersViewHolder, Users users, int position) {
                        usersViewHolder.setDisplayName(users.getUserName());
                        usersViewHolder.setDisplayStatus(users.getUserStatus());
                        usersViewHolder.setDisplayImage(users.getUserThumImage(),getApplicationContext());

                        final String userId = getRef(position).getKey();

                        usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent profileIntent = new Intent(UsersActivity.this,ProfileActivity.class);
                                profileIntent.putExtra("userId",userId);
                                startActivity(profileIntent);
                            }
                        });
                    }
                };
        mUsersList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDisplayName(String name) {
            TextView userNameView = mView.findViewById(R.id.users_name);
            userNameView.setText(name);
        }

        public void setDisplayStatus(String status) {
            TextView userStatusView = (TextView) mView.findViewById(R.id.users_status);
            userStatusView.setText(status);
        }

        public void setDisplayImage(String thumImage, Context context) {
            CircleImageView userImageProf = mView.findViewById(R.id.users_profile);
            Picasso.with(context).load(thumImage).placeholder(R.mipmap.ic_launcher_round).into(userImageProf);
        }
    }
}
