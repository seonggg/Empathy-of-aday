package com.project2022.emotiondiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

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

    String TAG = "token";

    //사용자 푸시 토큰 생성해서 저장
    public void registerPushToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Failed to get token");
                            return;
                        }
                        String token = task.getResult();
                        Log.d("sys", token);
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Map<String, String> map = new HashMap<>();
                        map.put("pushToken", token);

                        db.getInstance().collection("user").document(uid).set(map, SetOptions.merge());
                    }
                });
    }

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

        //Toast.makeText(getApplicationContext(), "로그인 되었습니다", Toast.LENGTH_SHORT).show();

        email = mAuth.getCurrentUser().getEmail();
        Log.d("cur_email: ",email);

        //알림을 위한 푸시토큰 생성
        registerPushToken();

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
            Intent intent = new Intent(MyRoom.this,SettingActivity.class);
            startActivity(intent);
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
                calendarDialog = new CalendarDialog(MyRoom.this,email);
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
            } else if (topArray.contains("happy")) {
                return R.drawable.happy;
            } else if (topArray.contains("sad")) {
                return R.drawable.sad;
            }
        }
        // 감정 2개 추출됐을 때
        else if (beads_count == 2) {
            if (topArray.contains("angry")) {
                if (topArray.contains("anxiety"))
                    return R.drawable.angry_anxiety; // 분노,불안
                else if (topArray.contains("happy"))
                    return R.drawable.angry_happy; // 분노,기쁨
                else if (topArray.contains("sad"))
                    return R.drawable.angry_sad; // 분노,슬픔
            } else if (topArray.contains("anxiety")) {
                if (topArray.contains("happy"))
                    return R.drawable.anxiety_happy; // 불안,기쁨
                else if (topArray.contains("sad"))
                    return R.drawable.sad_anxiety; // 불안,슬픔
            } else if (topArray.contains("happy")) {
                if (topArray.contains("sad"))
                    return R.drawable.sad_happy; // 슬픔,기쁨
            }
        }
        // 감정 3개 추출됐을 때
        else if (beads_count == 3) {
            if (topArray.contains("angry")) {
                if (topArray.contains("anxiety")) {
                    if (topArray.contains("happy"))
                        return R.drawable.happy_angry_anxiety; // 분노,불안,기쁨
                    else if (topArray.contains("sad"))
                        return R.drawable.angry_anxiety_sad; //분노,불안,슬픔
                } else if (topArray.contains("happy")) {
                    if (topArray.contains("sad"))
                        return R.drawable.angry_sad_happy; // 분노,기쁨,슬픔
                }
            } else if (topArray.contains("happy")) {
                if (topArray.contains("anxiety") && topArray.contains("sad"))
                    return R.drawable.happy_anxiety_sad; // 기쁨,불안,슬픔
            }
        }
        return 0;
    }
}