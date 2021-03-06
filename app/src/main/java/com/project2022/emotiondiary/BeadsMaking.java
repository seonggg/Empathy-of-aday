package com.project2022.emotiondiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BeadsMaking extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // server의 url(매번 변경해야 함)
    private final String BASE_URL = "https://a258-222-101-88-23.jp.ngrok.io";
    private EmotionAPI emotionAPI;

    String content, result, emotion;
    String docid;

    ImageView img;

    Integer post, get;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beads_making);

        Intent get_intent = getIntent();
        docid = get_intent.getStringExtra("docid");
        content = get_intent.getStringExtra("content");

        img=findViewById(R.id.imageView);

        post=0;
        get=0;

        //서버 연결
        initAPI(BASE_URL);

        //서버를 통해 감정분석 진행
        Log.d("감정분석","POST");
        TextItem item = new TextItem();
        item.setText(content);
        Log.d("감정분석",item.getText());

        retrofit2.Call<TextItem> postCall = emotionAPI.text_text(item);

        //일기 내용 서버로 보내기
        postCall = emotionAPI.text_text(item);
        postCall.enqueue(new Callback<TextItem>() {
            @Override
            public void onResponse(retrofit2.Call<TextItem> call, Response<TextItem> response) {
                if(response.isSuccessful()){
                    Log.d("감정분석","등록 완료");
                    post+=1;
                }else {
                    Log.d("감정분석","Status Code : " + response.code());
                    Log.d("감정분석",response.errorBody().toString());
                    Log.d("감정분석",call.request().body().toString());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<TextItem> call, Throwable t) {
                Log.d("감정분석","Fail msg : " + t.getMessage());
            }
        });

        //분석된 감정 값 가져오기
        new Handler().postDelayed(() -> {
            Log.d("감정분석","GET");
            retrofit2.Call<List<TextItem>> getCall = emotionAPI.get_text();
            getCall.enqueue(new Callback<List<TextItem>>() {
                @Override
                public void onResponse(retrofit2.Call<List<TextItem>> call, Response<List<TextItem>> response) {
                    if (response.isSuccessful()) {
                        List<TextItem> mList = response.body();
                        for (TextItem item : mList) {
                            result = item.getText();
                        }
                    } else {
                        Log.d("감정분석", "Status Code : " + response.code());
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<List<TextItem>> call, Throwable t) {
                    Log.d("감정분석", "Fail msg : " + t.getMessage());
                }
            });

            new Handler().postDelayed(() -> {
                //서버 연결이 안되면 임의의 값 집어넣기
                if (result==null){
                    Log.d("감정분석", "결과값 비었음");
                    result="['emb', 'sad', 'hurt']";
                }

                Log.d("감정분석",result);
                Intent intent = new Intent(BeadsMaking.this,BeadsMakingFinish.class);
                intent.putExtra("docid",docid);
                intent.putExtra("emotion",result);
                startActivity(intent);
            },5000);
        },100000);

    }

    //뒤로가기 막기
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    //서버 통신 api 초기화
    private void initAPI(String baseUrl){

        Log.d("감정분석","initAPI : " + baseUrl);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        emotionAPI = retrofit.create(EmotionAPI.class);
    }

}