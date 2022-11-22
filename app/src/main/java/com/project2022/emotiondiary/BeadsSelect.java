package com.project2022.emotiondiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.util.ArrayList;

public class BeadsSelect extends AppCompatActivity {

    ArrayList<String> topArray;
    ArrayList<String> checkArray;
    int size;
    ImageView bead1;
    ImageView bead2;
    ImageView bead3;
    CheckBox cb1;
    CheckBox cb2;
    CheckBox cb3;
    Button btn;

    String docid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beads_select);

        checkArray = new ArrayList<>();

        bead1 = (ImageView) findViewById(R.id.bead_select1);
        bead2 = (ImageView) findViewById(R.id.bead_select2);
        bead3 = (ImageView) findViewById(R.id.bead_select3);

        cb1 = (CheckBox) findViewById(R.id.checkbox1);
        cb2 = (CheckBox) findViewById(R.id.checkbox2);
        cb3 = (CheckBox) findViewById(R.id.checkbox3);

        btn = (Button)findViewById(R.id.btn);
        btn.setOnClickListener(view -> IntentPut());

        Intent intent = getIntent();
        topArray = new ArrayList<>();
        size = intent.getIntExtra("크기",0);

        for(int i=1;i<size+1;i++){
            topArray.add(intent.getStringExtra("감정"+i));
        }

        docid=intent.getStringExtra("docid");

        BeadsSetting();
    }

    //다음 엑티비티로 값 넘기기
    public void IntentPut(){
        Intent put_intent = new Intent(getApplicationContext(), MixResult.class);
        if(cb1.isChecked())
            checkArray.add(topArray.get(0));
        if(cb2.isChecked())
            checkArray.add(topArray.get(1));
        if(cb3.isChecked())
            checkArray.add(topArray.get(2));
        for(int i=1; i<checkArray.size()+1;i++){
            put_intent.putExtra("감정"+i,checkArray.get(i-1));
        }
        put_intent.putExtra("크기",checkArray.size());
        put_intent.putExtra("docid",docid);
        startActivity(put_intent);
    }

    //구슬 이미지
    public void BeadsSetting(){

        bead1.setVisibility(View.GONE);
        bead2.setVisibility(View.GONE);
        bead3.setVisibility(View.GONE);
        cb1.setVisibility(View.GONE);
        cb2.setVisibility(View.GONE);
        cb3.setVisibility(View.GONE);

        for(int i = 0; i<topArray.size(); i++) {
            switch (topArray.get(i)) {
                case "angry":
                    if (i == 0) {
                        bead1.setImageResource(R.drawable.angry);
                        bead1.setVisibility(View.VISIBLE); // 추출된 감정은 이미지뷰 보이도록 변경
                        cb1.setVisibility(View.VISIBLE);
                    } else if (i == 1) {
                        bead2.setImageResource(R.drawable.angry);
                        bead2.setVisibility(View.VISIBLE);
                        cb2.setVisibility(View.VISIBLE);
                    } else {
                        bead3.setImageResource(R.drawable.angry);
                        bead3.setVisibility(View.VISIBLE);
                        cb3.setVisibility(View.VISIBLE);
                    }
                    break;
                case "sad":
                    if (i == 0) {
                        bead1.setImageResource(R.drawable.sad);
                        bead1.setVisibility(View.VISIBLE);
                        cb1.setVisibility(View.VISIBLE);
                    } else if (i == 1) {
                        bead2.setImageResource(R.drawable.sad);
                        bead2.setVisibility(View.VISIBLE);
                        cb2.setVisibility(View.VISIBLE);
                    } else {
                        bead3.setImageResource(R.drawable.sad);
                        bead3.setVisibility(View.VISIBLE);
                        cb3.setVisibility(View.VISIBLE);
                    }
                    break;
                case "anxiety":
                    if (i == 0) {
                        bead1.setImageResource(R.drawable.anxiety);
                        bead1.setVisibility(View.VISIBLE);
                        cb1.setVisibility(View.VISIBLE);
                    } else if (i == 1) {
                        bead2.setImageResource(R.drawable.anxiety);
                        bead2.setVisibility(View.VISIBLE);
                        cb2.setVisibility(View.VISIBLE);
                    } else {
                        bead3.setImageResource(R.drawable.anxiety);
                        bead3.setVisibility(View.VISIBLE);
                        cb3.setVisibility(View.VISIBLE);
                    }
                    break;
                default:
                    if (i == 0) {
                        bead1.setImageResource(R.drawable.happy);
                        bead1.setVisibility(View.VISIBLE);
                        cb1.setVisibility(View.VISIBLE);
                    } else if (i == 1) {
                        bead2.setImageResource(R.drawable.happy);
                        bead2.setVisibility(View.VISIBLE);
                        cb2.setVisibility(View.VISIBLE);
                    } else {
                        bead3.setImageResource(R.drawable.happy);
                        bead3.setVisibility(View.VISIBLE);
                        cb3.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    }
}