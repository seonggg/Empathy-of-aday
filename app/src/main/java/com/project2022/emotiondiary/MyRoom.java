package com.project2022.emotiondiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Calendar;
import java.util.Random;

public class MyRoom extends AppCompatActivity {

    long backKeyPressingTime = 0;
    ImageButton writeBtn;
    ImageButton settingBtn;
    ImageButton calenderBtn;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    //테스트
    String email;

    private RecyclerViewAdapter adapter;

//    Calendar myCalendar = Calendar.getInstance();
//
//    DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
//        @Override
//        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//            myCalendar.set(Calendar.YEAR, year);
//            myCalendar.set(Calendar.MONTH, month);
//            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//            //updateLabel();
//        }
//    };
    private CalendarDialog calendarDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_room);

        Log.d("myroom", "oncreat");

        mAuth= FirebaseAuth.getInstance();
        String user_uid = mAuth.getCurrentUser().getUid();
        ((Info)this.getApplication()).setId(user_uid);
        ((Info)this.getApplication()).setNick(user_uid);

        Intent intent_e = getIntent();
        email = intent_e.getStringExtra("email");

        //일기 구슬 보여주기(구슬 전시)
        init();
        getData();

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

        //다이얼로그 밖의 화면은 흐리게 만들어줌
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        calenderBtn=findViewById(R.id.calenderBtn);
        calenderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarDialog = new CalendarDialog(MyRoom.this);
                calendarDialog.show();
            }
        });

//        calenderBtn=findViewById(R.id.calenderBtn);
//        calenderBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new DatePickerDialog(MyRoom.this, myDatePicker, myCalendar.get(Calendar.YEAR),
//                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
//            }
//        });

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

    //구슬 전시 리사이클러뷰 설정
    private void init(){
        Log.d("myroom", "init");
        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        GridLayoutManager gridmanager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(gridmanager);

        adapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
    }

    //최근 일기 데이터 불러오기
    private void getData() {
        db.collection("diary")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .whereEqualTo("writer_id", email)
                .limit(24)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                            String docid = ds.getId();
                            String beads = ds.get("beads").toString();
                            int img = printBead(beads.split(",").length, beads);
                            DataBead data = new DataBead(img, docid);
                            adapter.addItem(data);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MyRoomBeads","fail");
                    }
                });
    }

    //구슬 이미지 불러오기
    int printBead(Integer beads_count, String topArray) {
        // 감정 1개만 추출됐을 때
        if (beads_count == 1) {
            if (topArray.contains("angry")) {
                return R.drawable.angry;
            } else if (topArray.contains("anxiety")) {
                return R.drawable.anxiety;
            } else if (topArray.contains("emb")) {
                return R.drawable.emb;
            } else if (topArray.contains("happy")) {
                return R.drawable.happy;
            } else if (topArray.contains("hurt")) {
                return R.drawable.hurt;
            } else if (topArray.contains("sad")) {
                return R.drawable.sad;
            }
        }
        // 감정 2개 추출됐을 때
        else if (beads_count == 2) {
            if (topArray.contains("angry")) {
                if (topArray.contains("anxiety"))
                    return R.drawable.angry_anxiety;
                else if (topArray.contains("emb"))
                    return R.drawable.angry_emb;
                else if (topArray.contains("happy"))
                    return R.drawable.angry_happy;
                else if (topArray.contains("hurt"))
                    return R.drawable.angry_hurt;
                else if (topArray.contains("sad"))
                    return R.drawable.angry_sad;
            } else if (topArray.contains("anxiety")) {
                if (topArray.contains("emb"))
                    return R.drawable.anxiety_emb;
                else if (topArray.contains("happy"))
                    return R.drawable.anxiety_happy;
                else if (topArray.contains("hurt"))
                    return R.drawable.anxiety_hurt;
                else if (topArray.contains("sad"))
                    return R.drawable.sad_anxiety;
            } else if (topArray.contains("emb")) {
                if (topArray.contains("happy"))
                    return R.drawable.emb_happy;
                else if (topArray.contains("hurt"))
                    return R.drawable.hurt_emb;
                else if (topArray.contains("sad"))
                    return R.drawable.sad_emb;
            } else if (topArray.contains("happy")) {
                if (topArray.contains("hurt"))
                    return R.drawable.hurt_happy;
                else if (topArray.contains("sad"))
                    return R.drawable.sad_happy;
            } else if (topArray.contains("hurt") && topArray.contains("sad")) {
                return R.drawable.sad_hurt;
            }
        }
        // 감정 3개 추출됐을 때
        else if (beads_count == 3) {
            if (topArray.contains("angry")) {
                if (topArray.contains("anxiety")) {
                    if (topArray.contains("emb"))
                        return R.drawable.emb_angry_anxiety; //분노+불안+당황
                    else if (topArray.contains("happy"))
                        return R.drawable.happy_angry_anxiety; //분노+불안+기쁨
                    else if (topArray.contains("hurt"))
                        return R.drawable.angry_anxiety_hurt; //분노+불안+상처
                    else if (topArray.contains("sad"))
                        return R.drawable.angery_sad_anxiety; //분노+불안+슬픔
                } else if (topArray.contains("emb")) {
                    if (topArray.contains("happy"))
                        return R.drawable.happy_emb_anxiety; //분노+당황+기쁨
                    else if (topArray.contains("hurt"))
                        return R.drawable.emb_angry_hurt; //분노+당황+상처
                    else if (topArray.contains("sad"))
                        return R.drawable.angry_sad_emb; //분노+당황+슬픔
                } else if (topArray.contains("happy")) {
                    if (topArray.contains("hurt"))
                        return R.drawable.happy_angry_hurt; //분노+기쁨+상처
                    else if (topArray.contains("sad"))
                        return R.drawable.angry_sad_happy; //분노+기쁨+슬픔
                } else if (topArray.contains("hurt"))
                    if (topArray.contains("sad"))
                        return R.drawable.angry_hurt_sad; //분노+상처+슬픔
            } else if (topArray.contains("anxiety")) {
                if (topArray.contains("emb")) {
                    if (topArray.contains("happy"))
                        return R.drawable.happy_emb_anxiety; //불안+당황+기쁨
                    else if (topArray.contains("hurt"))
                        return R.drawable.emb_anxiety_hurt; // 불안+당황+상처
                    else if (topArray.contains("sad"))
                        return R.drawable.emb_anxiety_sad; //불안+당황+슬픔
                } else if (topArray.contains("happy")) {
                    if (topArray.contains("hurt"))
                        return R.drawable.happy_anxiety_hurt; // 불안+기쁨+상처
                    else if (topArray.contains("sad"))
                        return R.drawable.happy_anxiety_sad; // 불안+기쁨+슬픔
                } else if (topArray.contains("hurt") && topArray.contains("sad"))
                    return R.drawable.anxiety_hurt_sad; //불안+상처+슬픔
            } else if (topArray.contains("emb")) {
                if (topArray.contains("happy")) {
                    if (topArray.contains("hurt"))
                        return R.drawable.happy_emb_hurt; //당황+기쁨+상처
                    else if (topArray.contains("sad"))
                        return R.drawable.happy_emb_sad; //당황+기쁨+슬픔
                } else if (topArray.contains("hurt") && topArray.contains("sad"))
                    return R.drawable.emb_hurt_sad; //당황+상처+슬픔
            } else if (topArray.contains("happy")) {
                if (topArray.contains("hurt") && topArray.contains("sad"))
                    return R.drawable.happy_hurt_sad; //기쁨+상처+슬픔
            }
        }
        return 0;
    }
}