package com.project2022.emotiondiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

public class MixResult extends AppCompatActivity {

    ArrayList<String> topArray;
    ImageView bead;
    int size;
    Button room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mix_result);

        bead = (ImageView) findViewById(R.id.bead_img);

        Intent intent = getIntent();
        topArray = new ArrayList<>();
        size = intent.getIntExtra("크기",0);

        for(int i=1;i<size+1;i++){
            topArray.add(intent.getStringExtra("감정"+i));
        }

        BeadsMix();

        //방으로 돌아가기 버튼
        room = (Button) findViewById(R.id.room_btn);
        room.setOnClickListener(view -> {
            Intent intent1 = new Intent(getApplicationContext(),MyRoom.class);
            startActivity(intent1);
        });
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