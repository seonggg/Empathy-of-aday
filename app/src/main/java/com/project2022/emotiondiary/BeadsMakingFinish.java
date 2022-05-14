package com.project2022.emotiondiary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class BeadsMakingFinish extends AppCompatActivity {

    ArrayList<Emo> emoArray;
    ArrayList<String> topArray;
    ImageView bead1;
    ImageView bead2;
    ImageView bead3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beads_making_finish);

        bead1 = (ImageView)findViewById(R.id.beadsImage1);
        bead2 = (ImageView)findViewById(R.id.beadsImage2);
        bead3 = (ImageView)findViewById(R.id.beadsImage3);

        EmoExtraction();
    }

    // 상위 감정 3개 추출
    public void EmoExtraction(){

        emoArray = new ArrayList<Emo>(); // 감정 6개 리스트
        emoArray.add(new Emo("anger",10)); // 빨강
        emoArray.add(new Emo("sad",20)); // 파랑
        emoArray.add(new Emo("anxiety",30)); // 주황
        emoArray.add(new Emo("hurt",20)); // 보라
        emoArray.add(new Emo("emb",10)); // 초록
        emoArray.add(new Emo("happy",10)); // 노랑

        topArray = new ArrayList<String>(); // 상위 감정 리스트

        for(int i=0; i<3; i++){
            int maxIndex = 0;
            int a = i+1;
            for(int j = 1; j<emoArray.size(); j++){
                if(emoArray.get(j).ratio>emoArray.get(maxIndex).ratio){
                    maxIndex = j;
                }
            }
            topArray.add(emoArray.remove(maxIndex).emo_name); // 찾은 최대값은 emoArray에서 삭제하고 topArray에 삽입
        }

        Toast toast = Toast.makeText(getApplicationContext(), "상위 감정 3개:"+topArray.get(0)+","
                +topArray.get(1)+","+topArray.get(2),Toast.LENGTH_LONG);
        toast.show();

        BeadsMaking();
    }

    public void BeadsMaking(){
        for(int i = 0; i<3; i++){
            if(topArray.get(i).equals("anger")){
                if (i==0)
                    bead1.setImageResource(R.drawable.circle_red);
                else if(i==1)
                    bead2.setImageResource(R.drawable.circle_red);
                else
                    bead3.setImageResource(R.drawable.circle_red);
            }
            else if(topArray.get(i).equals("sad")){
                if (i==0)
                    bead1.setImageResource(R.drawable.circle_blue);
                else if(i==1)
                    bead2.setImageResource(R.drawable.circle_blue);
                else
                    bead3.setImageResource(R.drawable.circle_blue);
            }
            else if(topArray.get(i).equals("anxiety")){
                if (i==0)
                    bead1.setImageResource(R.drawable.circle_orange);
                else if(i==1)
                    bead2.setImageResource(R.drawable.circle_orange);
                else
                    bead3.setImageResource(R.drawable.circle_orange);
            }
            else if(topArray.get(i).equals("hurt")){
                if (i==0)
                    bead1.setImageResource(R.drawable.circle_purple);
                else if(i==1)
                    bead2.setImageResource(R.drawable.circle_purple);
                else
                    bead3.setImageResource(R.drawable.circle_purple);
            }
            else if(topArray.get(i).equals("emb")){
                if (i==0)
                    bead1.setImageResource(R.drawable.circle_green);
                else if(i==1)
                    bead2.setImageResource(R.drawable.circle_green);
                else
                    bead3.setImageResource(R.drawable.circle_green);
            }
            else{
                if (i==0)
                    bead1.setImageResource(R.drawable.circle_yellow);
                else if(i==1)
                    bead2.setImageResource(R.drawable.circle_yellow);
                else
                    bead3.setImageResource(R.drawable.circle_yellow);
            }
        }
    }

    class Emo{
        String emo_name;
        int ratio;

        Emo(String emo_name, int ratio){
            this.emo_name = emo_name;
            this.ratio = ratio;
        }
    }
}