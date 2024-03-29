package com.project2022.emotiondiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import java.util.ArrayList;

public class ShowDiary extends AppCompatActivity {

    Toolbar toolbar;

    ImageView weather_img, bead;
    TextView date_display, content;

    private ViewPager2 sliderViewPager;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    ArrayList<Uri> imgArray = new ArrayList<>();

    String docid, id;
    Boolean actionBarView;

    Integer pictures;
    Button commentBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_diary);

        id = ((Info)this.getApplication()).getId();

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

        Intent intent = getIntent();
        docid = intent.getStringExtra("docid");

        actionBarView = intent.getBooleanExtra("actionbar",true);

        //docid에 해당하는 일기 불러오기
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
                        pictures = Integer.valueOf(document.get("pictures").toString());

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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "최신 일기 불러오기 실패", Toast.LENGTH_SHORT).show();
            }
        });

        commentBtn = findViewById(R.id.comment_btn);

        commentBtn.setOnClickListener(view->{
            Intent comIntent = new Intent(getApplicationContext(),Comment.class);
            comIntent.putExtra("docid",docid);
            startActivity(comIntent);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(actionBarView){
            getMenuInflater().inflate(R.menu.menu_show, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.edit_btn:
                //일기 수정
                Intent intent = new Intent(getApplicationContext(), EditDiary.class);
                intent.putExtra("docid", docid);
                startActivity(intent);

                return true;
            case R.id.delete_btn:
                //일기 삭제
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowDiary.this);
                builder.setTitle("일기 삭제하기");
                builder.setMessage("일기를 삭제하면 복구할 수 없습니다.\n삭제하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.collection("diary").document(docid).delete();
                        delete_img(docid, pictures);
                        Intent intent = new Intent(getApplicationContext(), MyRoom.class);
                        intent.putExtra("email",id);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("아니오", null);
                builder.setNeutralButton("취소", null);
                builder.create().show();
                return true;
            case R.id.show_btn:
                //같은 감정 일기 보여주기
                Intent intent2 = new Intent(getApplicationContext(),ShareList.class);
                intent2.putExtra("show",true);
                intent2.putExtra("docid",docid);
                startActivity(intent2);
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
            else if (topArray.contains("happy")) {
                bead.setImageResource(R.drawable.happy);
            }
            else if (topArray.contains("anxiety")) {
                bead.setImageResource(R.drawable.anxiety);
            }
            else if (topArray.contains("sad")) {
                bead.setImageResource(R.drawable.sad);
            }
        }
        // 감정 2개 추출됐을 때
        else if(beads_count==2){
            if (topArray.contains("angry")) {
                if(topArray.contains("happy"))
                    bead.setImageResource(R.drawable.angry_happy);
                else if(topArray.contains("anxiety"))
                    bead.setImageResource(R.drawable.angry_anxiety);
                else if(topArray.contains("sad"))
                    bead.setImageResource(R.drawable.angry_sad);
            }
            else if (topArray.contains("happy")) {
                if(topArray.contains("anxiety"))
                    bead.setImageResource(R.drawable.anxiety_happy);
                else if(topArray.contains("sad"))
                    bead.setImageResource(R.drawable.sad_happy);
            }
            else if (topArray.contains("anxiety") && topArray.contains("sad")) {
                bead.setImageResource(R.drawable.sad_anxiety);
            }
        }
        // 감정 3개 추출됐을 때
        else if(beads_count==3){
            if (topArray.contains("angry")) {
                if(topArray.contains("happy")) {
                    if(topArray.contains("anxiety"))
                        bead.setImageResource(R.drawable.happy_angry_anxiety); //분노+기쁨+상처
                    else if(topArray.contains("sad"))
                        bead.setImageResource(R.drawable.angry_sad_happy); //분노+기쁨+슬픔
                }
                else if(topArray.contains("anxiety"))
                    if(topArray.contains("sad"))
                        bead.setImageResource(R.drawable.angry_anxiety_sad); //분노+상처+슬픔
            }
            else if (topArray.contains("happy")) {
                if(topArray.contains("anxiety") && topArray.contains("sad"))
                    bead.setImageResource(R.drawable.happy_anxiety_sad); //기쁨+상처+슬픔
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
                        //Toast.makeText(getApplicationContext(), imgArray.toString(), Toast.LENGTH_SHORT).show();
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

    //storage에 이미지 제거
    private void delete_img(String docid, Integer size) {
        for (int i=0;i<size;i++){
            storage.getReference().child("diary/" + docid + "_" + i + ".jpg").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    //Toast.makeText(getApplicationContext(), "삭제완료", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "삭제 실패", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
