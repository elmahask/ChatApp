package com.wael.elmahask.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    private TextInputLayout
            mLoginEmail,
            mLoginPass;
    private Button mLoginAccountBtn;
    private Toolbar mToolBar;
    private ProgressDialog mRegProgress;

    //Declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initialize the FirebaseAuth instance.
        mAuth = FirebaseAuth.getInstance();

        //views
        mToolBar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");

        //progressDialog
        mRegProgress = new ProgressDialog(this);

        mLoginEmail = (TextInputLayout) findViewById(R.id.regGetEmail);
        mLoginPass = (TextInputLayout) findViewById(R.id.regGetPass);
        //Buttons
        mLoginAccountBtn = (Button) findViewById(R.id.btnLoginAccount);
        mLoginAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mLoginEmail.getEditText().getText().toString();
                String pass = mLoginPass.getEditText().getText().toString();
                if( !TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass) ){
                    mRegProgress.setTitle("Logging In");
                    mRegProgress.setMessage("PLZ Wait While Login Your Account");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    //Calling function
                    loginUser( email, pass );
                }
            }
        });
    }

    private void loginUser(String email, String pass) {
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //progress dismis before go to the new activity
                            mRegProgress.dismiss();
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "createUserWithEmail:success");
                            Intent main_intent =new Intent(LoginActivity.this, MainActivity.class);
                            main_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(main_intent);
                            finish();
                        } else {
                            //progress hide when occurs error
                            mRegProgress.hide();
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Cannot Sign in. Plz Check Your Connection and Try Again",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
