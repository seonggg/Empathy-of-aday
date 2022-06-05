package com.project2022.emotiondiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Random;

public class MyRoom extends AppCompatActivity {

    long backKeyPressingTime = 0;
    ImageButton writeBtn;
    ImageButton settingBtn;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    //테스트
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_room);

        mAuth= FirebaseAuth.getInstance();
        String user_uid = mAuth.getCurrentUser().getUid();
        ((Info)this.getApplication()).setId(user_uid);
        ((Info)this.getApplication()).setNick(user_uid);

        Intent intent_e = getIntent();
        email = intent_e.getStringExtra("email");

        Button showBtn = findViewById(R.id.show_btn);
        showBtn.setOnClickListener(view -> {

            //사용자의 가장 최신 일기 불러오기
            String id = ((Info)this.getApplication()).getId();

            db.collection("diary")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .whereEqualTo("writer_id",id)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                                String docid = ds.getId();

                                Intent intent = new Intent(MyRoom.this,ShowDiary.class);
                                intent.putExtra("id",id);
                                intent.putExtra("docid",docid);
                                startActivity(intent);
                            }
                        }
                    });
        });

        //일기작성 버튼
        writeBtn = findViewById(R.id.write_btn);
        writeBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MyRoom.this,DiaryWrite.class);
            intent.putExtra("email",email);
            startActivity(intent);
        });

        // 환경설정 버튼
        settingBtn = findViewById(R.id.setting_btn);
        settingBtn.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(MyRoom.this,"로그아웃되었습니다",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MyRoom.this,LoginActivity.class);
            startActivity(intent);
            /*
            Toast.makeText(MyRoom.this,"로그아웃되었습니다",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MyRoom.this,SettingActivity.class);
            intent.putExtra("email",email);
            startActivity(intent);*/
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