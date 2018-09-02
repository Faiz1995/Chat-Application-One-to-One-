package com.example.android.wechatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread thread = new Thread(){
            @Override
            public void run() {
                try{
                    sleep(5000);
                     }catch (InterruptedException e){
                    e.printStackTrace();

                }finally {
                    Intent intent = new Intent(MainActivity.this,ChatsActivity.class);
                    startActivity(intent);
                    finish();

                }

            }
        };
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
