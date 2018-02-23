package com.wael.elmahask.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private TextInputLayout mStatusInput;
    private Button mStatusSaveBtn;
    //FireBase
    private FirebaseUser mCurrentUser;
    private DatabaseReference mStatusData;
    //Progress
    private ProgressDialog mRegProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        //firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = mCurrentUser.getUid();
        mStatusData = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUid);

        //views
        mToolBar = (Toolbar) findViewById(R.id.status_appBar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //button and input
        String statusValue = getIntent().getStringExtra("KeyStatus");
        mStatusInput = findViewById(R.id.status_input);
        mStatusInput.getEditText().setText(statusValue);//To Catch the Status
        mStatusSaveBtn = findViewById(R.id.status_save_btn);

        mStatusSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //progress
                mRegProgress = new ProgressDialog(StatusActivity.this);
                mRegProgress.setTitle("Saving Status");
                mRegProgress.setMessage("PLZ Wait to Saving Change");
                mRegProgress.setCanceledOnTouchOutside(false);
                mRegProgress.show();

                String status = mStatusInput.getEditText().getText().toString();
                mStatusData.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //progress dismis if success
                            mRegProgress.dismiss();
                            startActivity(new Intent(StatusActivity.this,SettingActivity.class));
                        } else {
                            Toast.makeText(StatusActivity.this, "Getting Some Error in Saving Changes", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

}