package com.project2022.emotiondiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MyRoom extends AppCompatActivity {

    long backKeyPressingTime = 0;
    ImageButton writeBtn;
    ImageButton settingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_room);

        //일기작성 버튼 이벤트
        writeBtn = findViewById(R.id.write_btn);
        writeBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MyRoom.this,DiaryWrite.class);
            startActivity(intent);
        });

        // 임시 로그아웃(원래는 환경설정)
        settingBtn = findViewById(R.id.setting_btn);
        settingBtn.setOnClickListener(view -> {
            //FirebaseAuth.getInstance().signOut();
            //Toast.makeText(MyRoom.this,"로그아웃되었습니다",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MyRoom.this,SettingActivity.class);
            startActivity(intent);
        });

    }

    @Override
    public void onBackPressed() {
        //뒤로가기 막기
        //super.onBackPressed();

        // 뒤로가기 연속 두 번 클릭 시 종료
        if(System.currentTimeMillis()>backKeyPressingTime + 2500){
            backKeyPressingTime = System.currentTimeMillis();
            Toast toast = Toast.makeText(getApplicationContext(), "뒤로가기 두 번 누르면 종료",Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if(System.currentTimeMillis()<=backKeyPressingTime + 2500){
            finishAffinity();
        }
    }
}