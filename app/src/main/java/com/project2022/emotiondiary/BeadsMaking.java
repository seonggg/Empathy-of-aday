package com.project2022.emotiondiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BeadsMaking extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beads_making);

        Intent get_intent = getIntent();
        String docid = get_intent.getStringExtra("docid");

        // 3초 뒤 자동 화면 전환
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(BeadsMaking.this,BeadsMakingFinish.class);
            intent.putExtra("docid",docid);
            startActivity(intent);
        },3000);
    }

    //뒤로가기 막기
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}