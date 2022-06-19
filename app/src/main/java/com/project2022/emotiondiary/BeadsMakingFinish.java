package com.project2022.emotiondiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firestore.v1.WriteResult;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BeadsMakingFinish extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<Integer> emoArray=new ArrayList<>();
    ArrayList<String> topArray=new ArrayList<>();
    ImageView bead1;
    ImageView bead2;
    ImageView bead3;
    Button select;
    Button mix;

    String docid, emotion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beads_making_finish);

        Intent intent = getIntent();
        docid = intent.getStringExtra("docid");
        emotion = intent.getStringExtra("emotion");
        Log.d("감정분석", "emotion 넘겨받은 값:"+emotion);

        bead1 = (ImageView)findViewById(R.id.beadsImage1);
        bead2 = (ImageView)findViewById(R.id.beadsImage2);
        bead3 = (ImageView)findViewById(R.id.beadsImage3);

        EmoExtraction(emotion);
        
        //추출된 감정 정보 파이어베이스 문서에 저장
        Map<String, Object> data = new HashMap<>();
        data.put("emotion", topArray);

        db.collection("diary").document(docid)
                .set(data, SetOptions.merge());

        //구슬 선택
        select = (Button)findViewById(R.id.button);
        select.setOnClickListener(view -> SelectButton());

        // 구슬 혼합
        mix = (Button)findViewById(R.id.button2);
        mix.setOnClickListener(view -> MixButton());
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
        intent.putExtra("docid",docid);
        startActivity(intent);
    }

    // 구슬 혼합 버튼 이벤트
    public void MixButton(){
        Intent intent = new Intent(getApplicationContext(), MixResult.class);
        intent.putExtra("크기",topArray.size());
        for(int i=1; i<topArray.size()+1;i++){
            intent.putExtra("감정"+i,topArray.get(i-1));
        }
        intent.putExtra("docid",docid);
        startActivity(intent);
    }

    // 상위 감정 3개 추출
    public void EmoExtraction(String emotion){

//        emoArray = new ArrayList<>(); // 감정 6개 리스트
//        emoArray.add(new Emo("angry", 8)); // 빨강
//        emoArray.add(new Emo("sad", 56)); // 하늘
//        emoArray.add(new Emo("anxiety", 1)); // 남색
//        emoArray.add(new Emo("hurt", 40)); // 보라
//        emoArray.add(new Emo("emb", 60)); // 연두
//        emoArray.add(new Emo("happy", 20)); // 노랑
//
//        topArray = new ArrayList<>(); // 상위 감정 리스트
//
//        // 비율이 가장 높은 감정 3개 찾기
//        for(int i=0; i<3; i++){
//            int maxIndex = 0;
//            for(int j = 1; j<emoArray.size(); j++){
//                if(emoArray.get(j).ratio>emoArray.get(maxIndex).ratio){
//                    maxIndex = j;
//                }
//            }
//            topArray.add(emoArray.get(maxIndex).emo_name); // topArray에 추가
//            emoArray.remove(maxIndex); // emoArray에서 삭제
//        }

//        emoArray.add(new Emo("angry", 8)); // 빨강
//        emoArray.add(new Emo("sad", 56)); // 하늘
//        emoArray.add(new Emo("anxiety", 1)); // 남색
//        emoArray.add(new Emo("hurt", 40)); // 보라
//        emoArray.add(new Emo("emb", 60)); // 연두
//        emoArray.add(new Emo("happy", 20)); // 노랑

        //서버에서 받은 문자열로 감정 가져오기
        if (emotion.indexOf("happy")>=0){
            emoArray.add(emotion.indexOf("happy"));
        }
        if (emotion.indexOf("angry")>=0){
            emoArray.add(emotion.indexOf("angry"));
        }
        if (emotion.indexOf("emb")>=0){
            emoArray.add(emotion.indexOf("emb"));
        }
        if (emotion.indexOf("sad")>=0){
            emoArray.add(emotion.indexOf("sad"));
        }
        if (emotion.indexOf("anxiety")>=0){
            emoArray.add(emotion.indexOf("anxiety"));
        }
        if (emotion.indexOf("hurt")>=0){
            emoArray.add(emotion.indexOf("hurt"));
        }

        //Collections.sort(emoArray, Collections.reverseOrder());

        for (int i=0; i<=emoArray.size()-1; i++){
            if (emotion.indexOf("happy") == emoArray.get(i)){
                topArray.add("happy");
                continue;
            } else if (emotion.indexOf("angry") == emoArray.get(i)){
                topArray.add("angry");
                continue;
            } else if (emotion.indexOf("sad") == emoArray.get(i)){
                topArray.add("sad");
                continue;
            } else if (emotion.indexOf("anxiety") == emoArray.get(i)){
                topArray.add("anxiety");
                continue;
            } else if (emotion.indexOf("hurt") == emoArray.get(i)){
                topArray.add("hurt");
                continue;
            } else if (emotion.indexOf("emb") == emoArray.get(i)){
                topArray.add("emb");
                continue;
            }
        }
        Log.d("감정분석", emoArray.toString());
        Log.d("감정분석", topArray.toString());

        Toast toast = Toast.makeText(getApplicationContext(), "추출된 감정:"+topArray.get(0)
                +","+topArray.get(1)+","+topArray.get(2),Toast.LENGTH_SHORT);
        toast.show();

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