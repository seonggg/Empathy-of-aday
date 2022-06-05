package com.project2022.emotiondiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ShareList extends AppCompatActivity {

    ArrayList<String> topArray;
    int size;
    String curEmail;
    ArrayList<String> UserArray; // 구슬이 같은 일기의 id를 담기 위한 배열

    TextView nickname1, nickname2, nickname3, nickname4, nickname5;
    ImageButton readBtn1,readBtn2,readBtn3,readBtn4, readBtn5;

    FirebaseFirestore db= FirebaseFirestore.getInstance();
    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_list);

        nickname1 = findViewById(R.id.name_view1);
        nickname2 = findViewById(R.id.name_view2);
        nickname3 = findViewById(R.id.name_view3);
        nickname4 = findViewById(R.id.name_view4);
        nickname5 = findViewById(R.id.name_view5);
        readBtn1 = findViewById(R.id.read_btn1);
        readBtn2 = findViewById(R.id.read_btn2);
        readBtn3 = findViewById(R.id.read_btn3);
        readBtn4 = findViewById(R.id.read_btn4);
        readBtn5 = findViewById(R.id.read_btn5);


        UserArray = new ArrayList<>();

        // 현재 로그인 이메일 받아오기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(TAG, "현재 사용자: " + user.getEmail());
            curEmail = user.getEmail();
        }
        else{
            Log.d(TAG, "현재 사용자를 찾을 수 없음");
        }

        // 현재 사용자의 구슬 조합 인텐트값 받기
        Intent intent = getIntent();
        topArray = new ArrayList<>();
        size = intent.getIntExtra("size",0);
        for(int i=1;i<size+1;i++){
            topArray.add(intent.getStringExtra("감정"+i));
        }


        // beads가 동일하고 share가 true인 사용자를 찾아 timestamp를 기준으로 내림차순 정렬
        CollectionReference colRef = db.collection("diary");
        colRef.whereEqualTo("beads",topArray).whereEqualTo("share",true)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // 닉네임 셋팅
                        if(!document.get("writer_id").toString().equals(curEmail)
                                && !UserArray.contains(document.get("nickname").toString())){
                            Log.d(TAG, "UserArray에 새로 추가됨: " + document.get("nickname"));
                            UserArray.add(document.get("nickname").toString());
                            if(UserArray.size()==1){
                                nickname1.setText(document.get("nickname").toString());
                            }
                            else if(UserArray.size()==2){
                                nickname2.setText(document.get("nickname").toString());
                            }
                            else if(UserArray.size()==3){
                                nickname3.setText(document.get("nickname").toString());
                            }
                            else if(UserArray.size()==4){
                                nickname4.setText(document.get("nickname").toString());
                            }
                            else if(UserArray.size()==5){
                                nickname5.setText(document.get("nickname").toString());
                                break;
                            }
                        }
                        else
                            continue;
                    }
                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        //일기 버튼
        readBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });
        readBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });
        readBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });
        readBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });
        readBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });
    }
}