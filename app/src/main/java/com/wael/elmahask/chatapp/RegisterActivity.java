package com.wael.elmahask.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    private TextInputLayout mDiplayFname,
            mDiplayEmail,
            mDiplayPass;
    private Button mCreateAccountBtn;
    private Toolbar mToolBar;
    private ProgressDialog mRegProgress;

    //Declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //initialize the FirebaseAuth instance.
        mAuth = FirebaseAuth.getInstance();

        //views
        mToolBar = (android.support.v7.widget.Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle(R.string.toolBar_create_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //progressDialog
        mRegProgress = new ProgressDialog(this);

        mDiplayFname = (TextInputLayout) findViewById(R.id.regDispalyFName);
        mDiplayEmail = (TextInputLayout) findViewById(R.id.regDispalyEmail);
        mDiplayPass = (TextInputLayout) findViewById(R.id.regDisplayPass);

        //button
        mCreateAccountBtn = findViewById(R.id.btnCreateAccount);

        mCreateAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String diplay_fname = mDiplayFname.getEditText().getText().toString();
                String diplay_email = mDiplayEmail.getEditText().getText().toString();
                String diplay_pass = mDiplayPass.getEditText().getText().toString();

                if( !TextUtils.isEmpty(diplay_fname) || !TextUtils.isEmpty(diplay_email) || !TextUtils.isEmpty(diplay_pass)){
                    mRegProgress.setTitle("Registring User");
                    mRegProgress.setMessage("PLZ Wait While Create Your Account");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                //Calling function
                registerUser(diplay_fname, diplay_email, diplay_pass);
                }
            }
        });
    }

    private void registerUser(final String name, final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            String uId = current_user.getUid();

                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uId);

                            HashMap<String,String> userMap = new HashMap<>();
                            userMap.put("name",name);
                            userMap.put("status","Hi there, I'm here");
                            userMap.put("image","default");
                            userMap.put("thumb_image","ImageURL");
                            userMap.put("email",email);
                            userMap.put("password",password);
                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        //progress dismis before go to the new activity
                                        mRegProgress.dismiss();
                                        // Sign in success, update UI with the signed-in user's information
                                        Intent main_intent =new Intent(RegisterActivity.this, MainActivity.class);
                                        main_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(main_intent);
                                        finish();
//                                        Toast.makeText(RegisterActivity.this, "Sign in.>> Done",
//                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            //progress hide when occurs error
                            mRegProgress.hide();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "Cannot Sign in. Plz Check Your Connection and Try Again",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
