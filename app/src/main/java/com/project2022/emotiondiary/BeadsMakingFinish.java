package com.project2022.emotiondiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class BeadsMakingFinish extends AppCompatActivity {

    ArrayList<Emo> emoArray;
    ArrayList<String> topArray;
    ImageView bead1;
    ImageView bead2;
    ImageView bead3;
    Button select;
    Button mix;
    Button re_analizing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beads_making_finish);

        bead1 = (ImageView)findViewById(R.id.beadsImage1);
        bead2 = (ImageView)findViewById(R.id.beadsImage2);
        bead3 = (ImageView)findViewById(R.id.beadsImage3);

        EmoExtraction();

        //구슬 선택
        select = (Button)findViewById(R.id.button);
        select.setOnClickListener(view -> SelectButton());

        // 구슬 혼합
        mix = (Button)findViewById(R.id.button2);
        mix.setOnClickListener(view -> MixButton());

        // 다시 분석
        re_analizing = (Button)findViewById(R.id.button3);
        re_analizing.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), BeadsMaking.class);
            startActivity(intent);
        });
    }

    //뒤로가기 막기
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    // 구슬 선택 버튼 이벤트
    public void SelectButton(){
        Intent intent = new Intent(getApplicationContext(), BeadsSelect.class);
        intent.putExtra("크기",topArray.size());
        for(int i=1; i<topArray.size()+1;i++){
            intent.putExtra("감정"+i,topArray.get(i-1));
        }
        startActivity(intent);
    }

    // 구슬 혼합 버튼 이벤트
    public void MixButton(){
        Intent intent = new Intent(getApplicationContext(), MixResult.class);
        intent.putExtra("크기",topArray.size());
        for(int i=1; i<topArray.size()+1;i++){
            intent.putExtra("감정"+i,topArray.get(i-1));
        }
        startActivity(intent);
    }

    // 상위 감정 3개 추출
    public void EmoExtraction(){

        emoArray = new ArrayList<>(); // 감정 6개 리스트
        emoArray.add(new Emo("angry", 8)); // 빨강
        emoArray.add(new Emo("sad", 56)); // 하늘
        emoArray.add(new Emo("anxiety", 40)); // 남색
        emoArray.add(new Emo("hurt", 80)); // 보라
        emoArray.add(new Emo("emb", 60)); // 연두
        emoArray.add(new Emo("happy", 40)); // 노랑

        topArray = new ArrayList<>(); // 상위 감정 리스트

        // 비율이 가장 높은 감정 3개 찾기
        for(int i=0; i<3; i++){
            int maxIndex = 0;
            for(int j = 1; j<emoArray.size(); j++){
                if(emoArray.get(j).ratio>emoArray.get(maxIndex).ratio){
                    maxIndex = j;
                }
            }
            if(emoArray.get(maxIndex).ratio>=50){
                topArray.add(emoArray.get(maxIndex).emo_name); // 찾은 최대값이 50이상인 경우에만 topArray에 삽입
            }
            emoArray.remove(maxIndex); // emoArray에서 삭제
        }

        // 한 개만 추출된 경우
        if(topArray.size()==1){
            Toast toast = Toast.makeText(getApplicationContext(), "추출된 감정:"+topArray.get(0),Toast.LENGTH_LONG);
            toast.show();
        }
        // 두 개 추출된 경우
        else if(topArray.size()==2){
            Toast toast = Toast.makeText(getApplicationContext(), "추출된 감정:"+topArray.get(0)
                    +","+topArray.get(1),Toast.LENGTH_LONG);
            toast.show();
        }
        // 세 개 추출된 경우
        else if(topArray.size()==3){
            Toast toast = Toast.makeText(getApplicationContext(), "추출된 감정:"+topArray.get(0)
                    +","+topArray.get(1)+","+topArray.get(2),Toast.LENGTH_LONG);
            toast.show();
        }
        // 그 외(4개 이상)
        else{
            Toast toast = Toast.makeText(getApplicationContext(), "추출된 감정: 없음",Toast.LENGTH_LONG);
            toast.show();
        }

        BeadsMaking();
    }

    // 구슬 이미지 표시
    public void BeadsMaking(){
        // 이미지뷰 공간 없애기
        bead1.setVisibility(View.GONE);
        bead2.setVisibility(View.GONE);
        bead3.setVisibility(View.GONE);
        for(int i = 0; i<topArray.size(); i++){
            switch (topArray.get(i)) {
                case "angry":
                    if (i == 0) {
                        bead1.setImageResource(R.drawable.angry);
                        bead1.setVisibility(View.VISIBLE); // 추출된 감정은 이미지뷰 보이도록 변경
                    } else if (i == 1) {
                        bead2.setImageResource(R.drawable.angry);
                        bead2.setVisibility(View.VISIBLE);
                    } else {
                        bead3.setImageResource(R.drawable.angry);
                        bead3.setVisibility(View.VISIBLE);
                    }
                    break;
                case "sad":
                    if (i == 0) {
                        bead1.setImageResource(R.drawable.sad);
                        bead1.setVisibility(View.VISIBLE);
                    } else if (i == 1) {
                        bead2.setImageResource(R.drawable.sad);
                        bead2.setVisibility(View.VISIBLE);
                    } else {
                        bead3.setImageResource(R.drawable.sad);
                        bead3.setVisibility(View.VISIBLE);
                    }
                    break;
                case "anxiety":
                    if (i == 0) {
                        bead1.setImageResource(R.drawable.anxiety);
                        bead1.setVisibility(View.VISIBLE);
                    } else if (i == 1) {
                        bead2.setImageResource(R.drawable.anxiety);
                        bead2.setVisibility(View.VISIBLE);
                    } else {
                        bead3.setImageResource(R.drawable.anxiety);
                        bead3.setVisibility(View.VISIBLE);
                    }
                    break;
                case "hurt":
                    if (i == 0) {
                        bead1.setImageResource(R.drawable.hurt);
                        bead1.setVisibility(View.VISIBLE);
                    } else if (i == 1) {
                        bead2.setImageResource(R.drawable.hurt);
                        bead2.setVisibility(View.VISIBLE);
                    } else {
                        bead3.setImageResource(R.drawable.hurt);
                        bead3.setVisibility(View.VISIBLE);
                    }
                    break;
                case "emb":
                    if (i == 0) {
                        bead1.setImageResource(R.drawable.emb);
                        bead1.setVisibility(View.VISIBLE);
                    } else if (i == 1) {
                        bead2.setImageResource(R.drawable.emb);
                        bead2.setVisibility(View.VISIBLE);
                    } else {
                        bead3.setImageResource(R.drawable.emb);
                        bead3.setVisibility(View.VISIBLE);
                    }
                    break;
                default:
                    if (i == 0) {
                        bead1.setImageResource(R.drawable.happy);
                        bead1.setVisibility(View.VISIBLE);
                    } else if (i == 1) {
                        bead2.setImageResource(R.drawable.happy);
                        bead2.setVisibility(View.VISIBLE);
                    } else {
                        bead3.setImageResource(R.drawable.happy);
                        bead3.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    }

    static class Emo{
        String emo_name;
        int ratio;

        Emo(String emo_name, int ratio){
            this.emo_name = emo_name;
            this.ratio = ratio;
        }
    }
}