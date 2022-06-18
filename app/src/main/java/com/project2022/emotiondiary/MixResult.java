package com.project2022.emotiondiary;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MixResult extends AppCompatActivity {

    FirebaseFirestore db= FirebaseFirestore.getInstance();

    ArrayList<String> topArray;
    ImageView bead;
    int size;
    Button room, share;

    String docid;
    MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mix_result);

        // 재생될 노래 선택
        int[] mp3Arr = {R.raw.sample1, R.raw.sample2, R.raw.sample3};
        Random random = new Random();
        int randNum = random.nextInt(mp3Arr.length);

        mMediaPlayer = MediaPlayer.create(this, mp3Arr[randNum]);
        mMediaPlayer.start();

        bead = findViewById(R.id.bead_img);

        Intent intent = getIntent();
        topArray = new ArrayList<>();
        size = intent.getIntExtra("크기",0);

        for(int i=1;i<size+1;i++){
            topArray.add(intent.getStringExtra("감정"+i));
        }

        docid=intent.getStringExtra("docid");

        BeadsMix();

        //선택한 구슬 색깔 정보 db 저장
        Map<String, Object> beads = new HashMap<>();
        beads.put("beads", topArray);

        db.collection("diary").document(docid)
                .set(beads, SetOptions.merge());

        //방으로 돌아가기 버튼
        room = findViewById(R.id.room_btn);
        room.setOnClickListener(view -> {
            Intent intent1 = new Intent(getApplicationContext(),MyRoom.class);
            intent1.putExtra("email",((Info)this.getApplication()).getId());
            startActivity(intent1);
            finish(); // 음악 계속 재생됨 방지
        });

        //공유하기 버튼
        share = findViewById(R.id.share_btn);
        share.setOnClickListener(view -> {
            //공유 허용 정보 db 저장
            Map<String, Object> data = new HashMap<>();
            data.put("share", true);

            db.collection("diary").document(docid)
                    .set(data, SetOptions.merge());

            AlertDialog.Builder dlg = new AlertDialog.Builder(MixResult.this);
            dlg.setMessage("공유가 완료되었습니다.\n같은 구슬을 공유한 사람들을 보시겠습니까?");
            dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(getApplicationContext(),ShareList.class);
                    for(int j = 1; j<topArray.size()+1; j++){
                        intent.putExtra("감정"+j,topArray.get(j-1));
                    }
                    intent.putExtra("size",size);
                    intent.putExtra("docid",docid);
                    startActivity(intent);
                    finish(); // 음악 계속 재생됨 방지
                }
            });
            dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            dlg.show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
    }

    //뒤로가기 막기
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    // 혼합 결과
    public void BeadsMix() {
        // 감정 1개만 추출됐을 때
        if(size==1){
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
        else if(size==2){
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
        else if(size==3){
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
}