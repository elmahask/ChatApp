package com.wael.elmahask.chatapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    // [START declare_auth]
    private FirebaseAuth mAuth;
    private android.support.v7.widget.Toolbar mToolBar;
    private ViewPager mViewPager;
    private SectionPagerAdapter mSectionPagerAdapter;
    private android.support.design.widget.TabLayout mTablayout;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Tabs
        mViewPager = (ViewPager) findViewById(R.id.main_tabPager);
        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionPagerAdapter);

        mTablayout = findViewById(R.id.main_tabs);
        mTablayout.setupWithViewPager(mViewPager);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // Views
        mToolBar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_page_toolBar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("ChatApp");

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToStart();
        } else {
//            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.main_logout_btn) {
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        } else if (item.getItemId() == R.id.main_setting_btn) {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
//            finish();
        } else if (item.getItemId() == R.id.main_users_btn) {
            startActivity(new Intent(MainActivity.this, UsersActivity.class));
            //finish() will make the app closed
//            finish();
        }
        return true;
    }
}
