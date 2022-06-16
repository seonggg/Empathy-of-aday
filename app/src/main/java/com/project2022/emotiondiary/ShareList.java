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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ShareList extends AppCompatActivity {

    ArrayList<String> topArray;
    int size;
    String curEmail;
    ArrayList<String> UserArray; // 감정이 같은 일기의 이메일 저장
    ArrayList<String> saveArray; // 감정이 같은 일기 id 저장
    String sameEmoId;
    String docid;
    String nickname;
    String[] splitStr;

    TextView nickname1, nickname2, nickname3, nickname4, nickname5;
    ImageButton readBtn1,readBtn2,readBtn3,readBtn4, readBtn5;
    View divider1,divider2,divider3,divider4,divider5;
    Button homeBtn;

    FirebaseFirestore db= FirebaseFirestore.getInstance();
    private final String TAG = this.getClass().getSimpleName();
    String diaryId1,diaryId2, diaryId3, diaryId4, diaryId5;
    Boolean show;

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
        divider1 = findViewById(R.id.divider1);
        divider2 = findViewById(R.id.divider2);
        divider3 = findViewById(R.id.divider3);
        divider4 = findViewById(R.id.divider4);
        divider5 = findViewById(R.id.divider5);
        homeBtn = findViewById(R.id.home_btn);

        nickname1.setVisibility(View.GONE);
        nickname2.setVisibility(View.GONE);
        nickname3.setVisibility(View.GONE);
        nickname4.setVisibility(View.GONE);
        nickname5.setVisibility(View.GONE);
        readBtn1.setVisibility(View.GONE);
        readBtn1.setVisibility(View.GONE);
        readBtn2.setVisibility(View.GONE);
        readBtn3.setVisibility(View.GONE);
        readBtn4.setVisibility(View.GONE);
        readBtn5.setVisibility(View.GONE);
        divider1.setVisibility(View.GONE);
        divider2.setVisibility(View.GONE);
        divider3.setVisibility(View.GONE);
        divider4.setVisibility(View.GONE);
        divider5.setVisibility(View.GONE);
        homeBtn.setVisibility(View.GONE);


        UserArray = new ArrayList<>();
        saveArray = new ArrayList<>();

        // 현재 로그인 이메일 받아오기
        curEmail = ((Info)this.getApplication()).getId();

        // 현재 사용자의 구슬 조합 인텐트값 받기
        Intent intent = getIntent();
        topArray = new ArrayList<>();
        size = intent.getIntExtra("size",0);
        for(int i=1;i<size+1;i++){
            topArray.add(intent.getStringExtra("감정"+i));
        }

        // 저장된 리스트를 보는 것인지 여부 인텐트값 받기
        Intent showIntent = getIntent();
        show = showIntent.getBooleanExtra("show",false);

        Intent idIntent = getIntent();
        docid = idIntent.getStringExtra("docid");

        // 저장된 공유 리스트를 불러올 때
        if(show) {
            CollectionReference colRef = db.collection("diary");
            colRef.document(docid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                            if(document.get("same_emo_diary")!=null){
                                sameEmoId = document.get("same_emo_diary").toString();
                                sameEmoId = sameEmoId.replace("[", "");
                                sameEmoId = sameEmoId.replace("]", "");
                                sameEmoId = sameEmoId.replace(" ", "");
                                Log.d("TAG", "sameEmoId: " + sameEmoId);
                                splitStr = sameEmoId.split(",");
                                for(int i=0; i<splitStr.length;i++)
                                    Log.d("TAG", "splitStr: " + splitStr[i]);
                                UpdateView();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"저장된 다른 사람의 일기가 없습니다\n오늘의 일기를 작성해주세요"
                                        ,Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d("TAG", "No such document");
                        }
                    } else {
                        Log.d("TAG", "get failed with ", task.getException());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "최신 일기 불러오기 실패", Toast.LENGTH_SHORT).show();
                    Log.w("TAG", "get failed with ", e);
                }
            });
        }
        // 새로운 리스트를 볼 때
        else{
            homeBtn.setVisibility(View.VISIBLE);

            // beads가 동일하고 share가 true인 사용자를 찾아 timestamp를 기준으로 내림차순 정렬
            CollectionReference colRef = db.collection("diary");
            colRef.whereEqualTo("beads",topArray).whereEqualTo("share",true)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId()+"=>" + document.getData());
                        // 닉네임 셋팅
                        if(document.get("writer_id")!=null && !document.get("writer_id").toString().equals(curEmail)
                                && !UserArray.contains(document.get("nickname").toString())){
                            Log.d(TAG, "UserArray에 새로 추가됨: " + document.get("nickname"));
                            UserArray.add(Objects.requireNonNull(document.get("nickname")).toString());
                            if(UserArray.size()==1){
                                nickname1.setText(Objects.requireNonNull(document.get("nickname")).toString());
                                diaryId1 = document.getId();
                                saveArray.add(diaryId1);
                                nickname1.setVisibility(View.VISIBLE);
                                readBtn1.setVisibility(View.VISIBLE);
                                divider1.setVisibility(View.VISIBLE);
                            }
                            else if(UserArray.size()==2){
                                nickname2.setText(Objects.requireNonNull(document.get("nickname")).toString());
                                diaryId2=document.getId();
                                saveArray.add(diaryId2);
                                nickname2.setVisibility(View.VISIBLE);
                                readBtn2.setVisibility(View.VISIBLE);
                                divider2.setVisibility(View.VISIBLE);
                            }
                            else if(UserArray.size()==3){
                                nickname3.setText(Objects.requireNonNull(document.get("nickname")).toString());
                                diaryId3=document.getId();
                                saveArray.add(diaryId3);
                                nickname3.setVisibility(View.VISIBLE);
                                readBtn3.setVisibility(View.VISIBLE);
                                divider3.setVisibility(View.VISIBLE);
                            }
                            else if(UserArray.size()==4){
                                nickname4.setText(Objects.requireNonNull(document.get("nickname")).toString());
                                diaryId4=document.getId();
                                saveArray.add(diaryId4);
                                nickname4.setVisibility(View.VISIBLE);
                                readBtn4.setVisibility(View.VISIBLE);
                                divider4.setVisibility(View.VISIBLE);
                            }
                            else if(UserArray.size()==5){
                                nickname5.setText(Objects.requireNonNull(document.get("nickname")).toString());
                                diaryId5=document.getId();
                                saveArray.add(diaryId5);
                                nickname5.setVisibility(View.VISIBLE);
                                readBtn5.setVisibility(View.VISIBLE);
                                divider5.setVisibility(View.VISIBLE);
                                break;
                            }
                        }
                    }
                    Map<String, Object> data = new HashMap<>();
                    data.put("same_emo_diary", saveArray);

                    db.collection("diary").document(docid)
                            .set(data, SetOptions.merge());
                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            });

            //일기 버튼
            readBtn1.setOnClickListener(view -> {
                // 일기 아이디값 넘기기
                Intent intent2 = new Intent(getApplicationContext(),ShowDiary.class);
                intent2.putExtra("docid",diaryId1);
                intent2.putExtra("actionbar",false);
                startActivity(intent2);
            });
            readBtn2.setOnClickListener(view -> {
                Intent intent3 = new Intent(getApplicationContext(),ShowDiary.class);
                intent3.putExtra("docid",diaryId2);
                intent3.putExtra("actionbar",false);
                startActivity(intent3);
            });
            readBtn3.setOnClickListener(view -> {
                Intent intent4 = new Intent(getApplicationContext(),ShowDiary.class);
                intent4.putExtra("docid",diaryId3);
                intent4.putExtra("actionbar",false);
                startActivity(intent4);
            });
            readBtn4.setOnClickListener(view -> {
                Intent intent5 = new Intent(getApplicationContext(),ShowDiary.class);
                intent5.putExtra("docid",diaryId4);
                intent5.putExtra("actionbar",false);
                startActivity(intent5);
            });
            readBtn5.setOnClickListener(view -> {
                Intent intent6 = new Intent(getApplicationContext(),ShowDiary.class);
                intent6.putExtra("docid",diaryId5);
                intent6.putExtra("actionbar",false);
                startActivity(intent6);
            });

            homeBtn.setOnClickListener(view -> {
                Intent intent7 = new Intent(getApplicationContext(),MyRoom.class);
                intent7.putExtra("email",((Info)this.getApplication()).getId());
                startActivity(intent7);
            });
        }
    }

    private void UpdateView(){
        if (splitStr != null) {
            for(int i = 1; i<splitStr.length+1; i++) {
                if (i == 1) {
                    diaryId1 = splitStr[0];
                    DocumentReference docRef2 = db.collection("diary").document(diaryId1);
                    docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                Log.d("TAG", "UpdateView success" + document.getData());
                                if (document.exists()) {
                                    nickname = document.get("nickname").toString();
                                    nickname1.setText(nickname);
                                    nickname1.setVisibility(View.VISIBLE);
                                    readBtn1.setVisibility(View.VISIBLE);
                                    divider1.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("TAG", "get failed with ", e);
                            Toast.makeText(getApplicationContext(), "최신 일기 불러오기 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (i == 2) {
                    diaryId2 = splitStr[1];
                    DocumentReference docRef2 = db.collection("diary").document(diaryId2);
                    docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    nickname = document.get("nickname").toString();
                                    nickname2.setText(nickname);
                                    nickname2.setVisibility(View.VISIBLE);
                                    readBtn2.setVisibility(View.VISIBLE);
                                    divider2.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "최신 일기 불러오기 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (i == 3) {
                    diaryId3 = splitStr[2];
                    DocumentReference docRef2 = db.collection("diary").document(diaryId3);
                    docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    nickname = document.get("nickname").toString();
                                    nickname3.setText(nickname);
                                    nickname3.setVisibility(View.VISIBLE);
                                    readBtn3.setVisibility(View.VISIBLE);
                                    divider3.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "최신 일기 불러오기 실패", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else if (i == 4) {
                    diaryId4 = splitStr[3];
                    DocumentReference docRef2 = db.collection("diary").document(diaryId4);
                    docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    nickname = document.get("nickname").toString();
                                    nickname4.setText(nickname);
                                    nickname4.setVisibility(View.VISIBLE);
                                    readBtn4.setVisibility(View.VISIBLE);
                                    divider4.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "최신 일기 불러오기 실패", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else if (i == 5) {
                    diaryId5 = splitStr[4];
                    DocumentReference docRef2 = db.collection("diary").document(diaryId5);
                    docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    nickname = document.get("nickname").toString();
                                    nickname5.setText(nickname);
                                    nickname5.setVisibility(View.VISIBLE);
                                    readBtn5.setVisibility(View.VISIBLE);
                                    divider5.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "최신 일기 불러오기 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                }
            }

            //일기 버튼
            readBtn1.setOnClickListener(view -> {
                // 일기 아이디값 넘기기
                Intent intent2 = new Intent(getApplicationContext(),ShowDiary.class);
                intent2.putExtra("docid",diaryId1);
                intent2.putExtra("actionbar",false);
                startActivity(intent2);
            });
            readBtn2.setOnClickListener(view -> {
                Intent intent3 = new Intent(getApplicationContext(),ShowDiary.class);
                intent3.putExtra("docid",diaryId2);
                intent3.putExtra("actionbar",false);
                startActivity(intent3);
            });
            readBtn3.setOnClickListener(view -> {
                Intent intent4 = new Intent(getApplicationContext(),ShowDiary.class);
                intent4.putExtra("docid",diaryId3);
                intent4.putExtra("actionbar",false);
                startActivity(intent4);
            });
            readBtn4.setOnClickListener(view -> {
                Intent intent5 = new Intent(getApplicationContext(),ShowDiary.class);
                intent5.putExtra("docid",diaryId4);
                intent5.putExtra("actionbar",false);
                startActivity(intent5);
            });
            readBtn5.setOnClickListener(view -> {
                Intent intent6 = new Intent(getApplicationContext(),ShowDiary.class);
                intent6.putExtra("docid",diaryId5);
                intent6.putExtra("actionbar",false);
                startActivity(intent6);
            });
        }
        else
            Toast.makeText(getApplicationContext(),"array가 null",Toast.LENGTH_SHORT).show();
    }
}