package com.project2022.emotiondiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MyRoom extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_room);

        //일기작성 버튼 이벤트
        ImageButton writeBtn = (ImageButton)findViewById(R.id.write_btn);

        writeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyRoom.this,DiaryWrite.class);
                startActivity(intent);
            }
        });

    }
}