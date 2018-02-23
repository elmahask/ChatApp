package com.wael.elmahask.chatapp;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingActivity extends AppCompatActivity {
    private static final int GALLERY_PICK = 1;
    //Firebase Auth and DataBase
    private FirebaseUser mCurrentUser;
    private DatabaseReference mRefDatabase;
    //Firebase Storage
    private StorageReference mStorageRef;
    //layout
    private CircleImageView mProfileImage;
    private TextView mDisplayName;
    private TextView mDisplayStatus;
    private Button mChangeStatus, mChangeImage;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //Views & Buttons
        mProfileImage = findViewById(R.id.profile_image);
        mDisplayName = findViewById(R.id.mDisplay_profile_name);
        mDisplayStatus = findViewById(R.id.mDisplay_status);
        mChangeStatus = findViewById(R.id.change_status_btn);
        mChangeImage = findViewById(R.id.chang_profile_image);

        //Firebase Connect
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();//Auth
        String currentUser = mCurrentUser.getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference();//Storage
        mRefDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);//DataBase
        mRefDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();

                mDisplayName.setText(name);
                mDisplayStatus.setText(status);
//                Picasso.with(SettingActivity.this).load(image).placeholder(R.drawable.ic_launcher_foreground).into(mProfileImage);
                if (!image.equals("default")) {
//                    Picasso.with(SettingActivity.this).load(image).into(mProfileImage);
                      Picasso.with(SettingActivity.this).load(image).placeholder(R.mipmap.ic_launcher_round).into(mProfileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String statusValue = mDisplayStatus.getText().toString();
                Intent intentStatus = new Intent(SettingActivity.this, StatusActivity.class);
                intentStatus.putExtra("KeyStatus", statusValue);
                startActivity(intentStatus);
            }
        });
        mChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentGallary = new Intent();
                intentGallary.setType("image/*");
                intentGallary.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intentGallary, "Select Image"), GALLERY_PICK);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            // start cropping activity for pre-acquired image saved on teh device
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(SettingActivity.this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgress = new ProgressDialog(SettingActivity.this);
                mProgress.setTitle("Uploading Image...");
                mProgress.setMessage("Plz Wait While Uploading Image");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();

                Uri resultUri = result.getUri();

                final File thum_filePath = new File(resultUri.getPath());
                Bitmap thumb_bitmap = null;
                String currentUserId = mCurrentUser.getUid();
                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thum_filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                StorageReference filePath = mStorageRef.child("profile_images").child(currentUserId + ".jpg");
//                StorageReference filePath = mStorageRef.child("profile_images").child(random()+".jpg");
                final StorageReference thumb_filePath = mStorageRef.child("profile_images").child("thumbs").child(currentUserId + "jpg");


                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            final String download_url = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filePath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                    String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();
                                    HashMap updateHashMap = new HashMap();
                                    updateHashMap.put("image",download_url);
                                    updateHashMap.put("thumb_image",thumb_downloadUrl);

                                    if (thumb_task.isSuccessful()) {
                                        mRefDatabase.updateChildren(updateHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    mProgress.dismiss();
                                                    Toast.makeText(SettingActivity.this, "Upload Done", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }else{
                                        mProgress.hide();
                                        Toast.makeText(SettingActivity.this, "Not Uploaded Thumbnail.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            mProgress.hide();
                            Toast.makeText(SettingActivity.this, "Not Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}