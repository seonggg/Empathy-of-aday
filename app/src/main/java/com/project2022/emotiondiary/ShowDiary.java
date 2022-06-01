package com.project2022.emotiondiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ShowDiary extends AppCompatActivity {

    Toolbar toolbar;

    ImageView weather_img, bead;
    TextView date_display, content;

    private ViewPager2 sliderViewPager;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    ArrayList<Uri> imgArray = new ArrayList<>();

    String docid="XeUE4Tlbv7pNOfIaJdkk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_diary);

        //액션바 커스텀
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목을 없애줍니다.
        actionBar.setDisplayHomeAsUpEnabled(true); //뒤로 가기 버튼

        weather_img=findViewById(R.id.weather_img);
        bead=findViewById(R.id.beads_img);

        content=findViewById(R.id.content);
        date_display=findViewById(R.id.date_display);

        sliderViewPager=findViewById(R.id.sliderViewPager);

        DocumentReference docRef = db.collection("diary").document(docid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());

                        //Diary diary = document.toObject(Diary.class);

                        //구슬 불러오기
                        printBead(document.get("beads").toString().split(",").length, document.get("beads").toString());

                        //날짜 불러오기
                        date_display.setText(document.get("date").toString());

                        //일기 내용 불러오기
                        content.setText(document.get("content").toString());

                        //날씨 불러오기
                        switch (document.get("weather").toString()){
                            case "맑음":
                                weather_img.setImageResource(R.drawable.sunny);
                                break;
                            case "흐림":
                                weather_img.setImageResource(R.drawable.cloudy);
                                break;
                            case "비":
                                weather_img.setImageResource(R.drawable.rainy);
                                break;
                            case "눈":
                                weather_img.setImageResource(R.drawable.snowy);
                                break;
                        }

                        //이미지 불러오기
                        Integer pictures = Integer.valueOf(document.get("pictures").toString());

                        if (pictures !=0) {
                            ArrayList<Uri> uriArray = new ArrayList<>();
                            StorageReference storageRef = storage.getReference();
                            StorageReference pathRef = storageRef.child("diary");
                            if (pathRef == null) {
                                Toast.makeText(getApplicationContext(), "저장소에 사진이 없습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                for (int i = 0; i < pictures; i++) {
                                    storageRef.child("diary/" + docid + "_" + i + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            // Got the download URL for 'users/me/profile.png'
                                            uriArray.add(uri);
                                            //불러온 이미지 viewpager2에 출력
                                            sliderViewPager.setOffscreenPageLimit(1);
                                            sliderViewPager.setAdapter(new ImageSliderAdapter(getApplicationContext(), uriArray));
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Handle any errors
                                        }
                                    });
                                }
                            }
                        }else {
                            sliderViewPager.setVisibility(View.GONE);
                        }

                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.edit_btn:
                Toast.makeText(getApplicationContext(),"수정하기",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), EditDiary.class);
                intent.putExtra("docid", docid);
                startActivity(intent);

                return true;
            case R.id.delete_btn:
                Toast.makeText(getApplicationContext(),"삭제하기",Toast.LENGTH_SHORT).show();

                return true;
            case android.R.id.home:
                //select back button
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //구슬 이미지 불러오기
    void printBead(Integer beads_count, String topArray){
        // 감정 1개만 추출됐을 때
        if(beads_count==1){
            if (topArray.contains("angry")) {
                bead.setImageResource(R.drawable.angry);
            }
            else if (topArray.contains("anxiety")) {
                bead.setImageResource(R.drawable.anxiety);
            }
            else if (topArray.contains("emb")) {
                bead.setImageResource(R.drawable.emb);
            }
            else if (topArray.contains("happy")) {
                bead.setImageResource(R.drawable.happy);
            }
            else if (topArray.contains("hurt")) {
                bead.setImageResource(R.drawable.hurt);
            }
            else if (topArray.contains("sad")) {
                bead.setImageResource(R.drawable.sad);
            }
        }
        // 감정 2개 추출됐을 때
        else if(beads_count==2){
            if (topArray.contains("angry")) {
                if(topArray.contains("anxiety"))
                    bead.setImageResource(R.drawable.angry_anxiety);
                else if(topArray.contains("emb"))
                    bead.setImageResource(R.drawable.angry_emb);
                else if(topArray.contains("happy"))
                    bead.setImageResource(R.drawable.angry_happy);
                else if(topArray.contains("hurt"))
                    bead.setImageResource(R.drawable.angry_hurt);
                else if(topArray.contains("sad"))
                    bead.setImageResource(R.drawable.angry_sad);
            }
            else if (topArray.contains("anxiety")) {
                if(topArray.contains("emb"))
                    bead.setImageResource(R.drawable.anxiety_emb);
                else if(topArray.contains("happy"))
                    bead.setImageResource(R.drawable.anxiety_happy);
                else if(topArray.contains("hurt"))
                    bead.setImageResource(R.drawable.anxiety_hurt);
                else if(topArray.contains("sad"))
                    bead.setImageResource(R.drawable.sad_anxiety);
            }
            else if (topArray.contains("emb")) {
                if(topArray.contains("happy"))
                    bead.setImageResource(R.drawable.emb_happy);
                else if(topArray.contains("hurt"))
                    bead.setImageResource(R.drawable.hurt_emb);
                else if(topArray.contains("sad"))
                    bead.setImageResource(R.drawable.sad_emb);
            }
            else if (topArray.contains("happy")) {
                if(topArray.contains("hurt"))
                    bead.setImageResource(R.drawable.hurt_happy);
                else if(topArray.contains("sad"))
                    bead.setImageResource(R.drawable.sad_happy);
            }
            else if (topArray.contains("hurt") && topArray.contains("sad")) {
                bead.setImageResource(R.drawable.sad_hurt);
            }
        }
        // 감정 3개 추출됐을 때
        else if(beads_count==3){
            if (topArray.contains("angry")) {
                if(topArray.contains("anxiety")){
                    if(topArray.contains("emb"))
                        bead.setImageResource(R.drawable.emb_angry_anxiety); //분노+불안+당황
                    else if(topArray.contains("happy"))
                        bead.setImageResource(R.drawable.happy_angry_anxiety); //분노+불안+기쁨
                    else if(topArray.contains("hurt"))
                        bead.setImageResource(R.drawable.angry_anxiety_hurt); //분노+불안+상처
                    else if(topArray.contains("sad"))
                        bead.setImageResource(R.drawable.angery_sad_anxiety); //분노+불안+슬픔
                }
                else if(topArray.contains("emb")){
                    if(topArray.contains("happy"))
                        bead.setImageResource(R.drawable.happy_emb_anxiety); //분노+당황+기쁨
                    else if(topArray.contains("hurt"))
                        bead.setImageResource(R.drawable.emb_angry_hurt); //분노+당황+상처
                    else if(topArray.contains("sad"))
                        bead.setImageResource(R.drawable.angry_sad_emb); //분노+당황+슬픔
                }
                else if(topArray.contains("happy")) {
                    if(topArray.contains("hurt"))
                        bead.setImageResource(R.drawable.happy_angry_hurt); //분노+기쁨+상처
                    else if(topArray.contains("sad"))
                        bead.setImageResource(R.drawable.angry_sad_happy); //분노+기쁨+슬픔
                }
                else if(topArray.contains("hurt"))
                    if(topArray.contains("sad"))
                        bead.setImageResource(R.drawable.angry_hurt_sad); //분노+상처+슬픔
            }
            else if (topArray.contains("anxiety")) {
                if(topArray.contains("emb")){
                    if(topArray.contains("happy"))
                        bead.setImageResource(R.drawable.happy_emb_anxiety); //불안+당황+기쁨
                    else if(topArray.contains("hurt"))
                        bead.setImageResource(R.drawable.emb_anxiety_hurt); // 불안+당황+상처
                    else if(topArray.contains("sad"))
                        bead.setImageResource(R.drawable.emb_anxiety_sad); //불안+당황+슬픔
                }
                else if(topArray.contains("happy")){
                    if(topArray.contains("hurt"))
                        bead.setImageResource(R.drawable.happy_anxiety_hurt); // 불안+기쁨+상처
                    else if(topArray.contains("sad"))
                        bead.setImageResource(R.drawable.happy_anxiety_sad); // 불안+기쁨+슬픔
                }
                else if(topArray.contains("hurt") && topArray.contains("sad"))
                    bead.setImageResource(R.drawable.anxiety_hurt_sad); //불안+상처+슬픔
            }
            else if (topArray.contains("emb")) {
                if(topArray.contains("happy")){
                    if(topArray.contains("hurt"))
                        bead.setImageResource(R.drawable.happy_emb_hurt); //당황+기쁨+상처
                    else if(topArray.contains("sad"))
                        bead.setImageResource(R.drawable.happy_emb_sad); //당황+기쁨+슬픔
                }
                else if(topArray.contains("hurt") && topArray.contains("sad"))
                    bead.setImageResource(R.drawable.emb_hurt_sad); //당황+상처+슬픔
            }
            else if (topArray.contains("happy")) {
                if(topArray.contains("hurt") && topArray.contains("sad"))
                    bead.setImageResource(R.drawable.happy_hurt_sad); //기쁨+상처+슬픔
            }
        }
    }

    //이미지 파이어베이스 스토리지에서 가져오기
    private ArrayList<Uri> getFireBaseImage(String docid, Integer pictures){
        //https://art-coding3.tistory.com/38
        ArrayList<Uri> uriArray = new ArrayList<>();
        StorageReference storageRef = storage.getReference();
        StorageReference pathRef = storageRef.child("diary");
        if (pathRef == null){
            Toast.makeText(getApplicationContext(), "저장소에 사진이 없습니다.", Toast.LENGTH_SHORT).show();
        }
        else {
            for (int i=0;i<pictures;i++){
                storageRef.child("diary/"+docid+"_"+i+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'users/me/profile.png'
                        uriArray.add(uri);
                        Toast.makeText(getApplicationContext(), imgArray.toString(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }
        }
        return uriArray;
    }
}
