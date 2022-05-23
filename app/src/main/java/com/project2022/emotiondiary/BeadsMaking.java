package com.project2022.emotiondiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class BeadsMaking extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beads_making);

        // 3초 뒤 자동 화면 전환
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(BeadsMaking.this,BeadsMakingFinish.class);
            startActivity(intent);
        },3000);
    }

    //뒤로가기 막기
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}