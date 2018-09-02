package com.example.android.wechatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChatsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    FirebaseAuth mAuth;
    private ViewPager myviewpager;
    private TabLayout tablayout;
    private TabsPagerAdapter tabsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);


        mAuth = FirebaseAuth.getInstance();
        myviewpager = (ViewPager) findViewById(R.id.main_tabs_pager);
        mToolbar = (Toolbar) findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("My Chat");
        tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        myviewpager.setAdapter(tabsPagerAdapter);
        tablayout = (TabLayout) findViewById(R.id.main_tabs);
        tablayout.setupWithViewPager(myviewpager);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if(user == null){

            LogoutUser();
        }
    }

    private void LogoutUser() {
        Intent intent = new Intent(ChatsActivity.this, LoginOptions.class);
        startActivity(intent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.main_logout_btn)
        {
            mAuth.signOut();
            LogoutUser();

        }

        if(item.getItemId() == R.id.account_settings_btn)
        {
            Intent intent = new Intent(ChatsActivity.this,SettingsActivity.class);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.all_users_btn)
        {
            Intent intent = new Intent(ChatsActivity.this,AllUsersActivity.class);
            startActivity(intent);
        }
        return true;
    }
}
