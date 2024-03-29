package com.project2022.emotiondiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.inputmethod.InputMethodManager;

import com.project2022.emotiondiary.applock.HomePage;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((SettingActivity)SettingActivity.context_main).path = "start";

        // 2초 뒤 자동 화면 전환
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, HomePage.class);
            startActivity(intent);
        },2000);
    }
}