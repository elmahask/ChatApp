package com.wael.elmahask.chatapp;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private TextView mDisplayProfileName, mDisplayProfileStatus, mDisplayProfileFriend;
    private ImageView mDisplayProfileImage;
    private Button mProfileSendReqBtn;

    private DatabaseReference mUsersDataBase;
    private DatabaseReference mFriendReqDataBase;
    private FirebaseUser mCurrentUser;

    private ProgressDialog mProgressDialog;
    private String mCurrentState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String userId = getIntent().getStringExtra("userId");

        mUsersDataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        mFriendReqDataBase = FirebaseDatabase.getInstance().getReference().child("Friend_request");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mDisplayProfileImage = findViewById(R.id.profile_image);
        mDisplayProfileImage = findViewById(R.id.pro);
        mDisplayProfileName = findViewById(R.id.display_profile_name);
        mDisplayProfileStatus = findViewById(R.id.display_profile_status);
        mDisplayProfileFriend = findViewById(R.id.display_total_friend);
        mProfileSendReqBtn = findViewById(R.id.profile_send_request_btn);

        mProgressDialog = new ProgressDialog(ProfileActivity.this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait while we load the user data.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mCurrentState = "not_friend";

        mUsersDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String displayName = dataSnapshot.child("name").getValue().toString();
                String displayStatus = dataSnapshot.child("status").getValue().toString();
                String displayImage = dataSnapshot.child("image").getValue().toString();
                String displayImage2 = dataSnapshot.child("image").getValue().toString();
//                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
//                String email = dataSnapshot.child("email").getValue().toString();

                mDisplayProfileName.setText(displayName);
                mDisplayProfileStatus.setText(displayStatus);
//                Picasso.with(SettingActivity.this).load(image).placeholder(R.drawable.ic_launcher_foreground).into(mProfileImage);
//                if (!displayImage.equals("default")) {
//                    Picasso.with(ProfileActivity.this).load(image).into(mProfileImage);
//                    Picasso.with(ProfileActivity.this).load(displayImage).placeholder(R.drawable.profileboyy).into(mDisplayProfileImage);
//                    mProgressDialog.dismiss();
//                }
                Picasso.with(ProfileActivity.this).load(displayImage).placeholder(R.drawable.profileboyy).into(mDisplayProfileImage);

                //____Friend List / Request Feature______
                mFriendReqDataBase.child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(userId)){
                            String req_type = dataSnapshot.child(userId).child("Request_type").getValue().toString();
                            if(req_type.equals("Received")){
                                mCurrentState = "req_received";
                                mProfileSendReqBtn.setText(R.string.accept_frienf_request);
                            }else if(req_type.equals("Sent")){
                                mCurrentState = "req_sent";
                                mProfileSendReqBtn.setText(R.string.cancel_friend_request);
                            }
                        }
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfileSendReqBtn.setEnabled(false);
                // _____________Not Friend Request________________________
                if(mCurrentState.equals("not_friend")){
                    mFriendReqDataBase.child(mCurrentUser.getUid()).child(userId).child("Request_type")
                            .setValue("Sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mFriendReqDataBase.child(userId).child(mCurrentUser.getUid()).child("Request_type")
                                        .setValue("Received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mProfileSendReqBtn.setEnabled(true);
                                        mCurrentState = "req_sent";
                                        mProfileSendReqBtn.setText(R.string.cancel_friend_request);
//                                        Toast.makeText(ProfileActivity.this,"Request Sent Successfully.",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else {
                                Toast.makeText(ProfileActivity.this,"Failed Sending Request.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                //______________Cancel Friend Request____________________
                if(mCurrentState.equals("req_sent")){
                    mFriendReqDataBase.child(mCurrentUser.getUid()).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendReqDataBase.child(userId).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mProfileSendReqBtn.setEnabled(true);
                                    mCurrentState = " not_friend";
                                    mProfileSendReqBtn.setText(R.string.send_friend_request);
                                }
                            });
                        }
                    });
                }
            }
        });
    }
}
