package com.project2022.emotiondiary;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DiaryWrite extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    ImageView imageview;
    Button completeBtn;
    ImageButton weatherB;
    EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_write);

        editText = (EditText)findViewById(R.id.editText);

        //날짜 표시
        TextView date = (TextView) findViewById(R.id.date_display);
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault());
        String strDate = dateFormat.format(currentTime);
        date.setText((strDate));

        //날씨 다이얼로그
        weatherB = (ImageButton)findViewById(R.id.weather_button);
        weatherB.setOnClickListener(this::OnClickHandler);

        //이미지 업로드
        imageview = findViewById(R.id.photoView);

        imageview.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            launcher.launch(intent);
        });

        //작성완료 버튼 이벤트
        completeBtn = (Button)findViewById(R.id.ok_btn);
        completeBtn.setOnClickListener(view -> {
            //예외 처리
            if(editText.getText().toString().length() == 0){
                Toast toast = Toast.makeText(getApplicationContext(),"내용을 입력하세요",Toast.LENGTH_SHORT);
                toast.show();
            }
            else{
                // 감정 분석 화면으로 전환
                Intent intent = new Intent(getApplicationContext(), BeadsMaking.class);
                startActivity(intent);
            }
        });
    }

    //이미지 받아오기
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    if (result.getResultCode() == RESULT_OK)
                    {
                        Log.e(TAG, "result : " + result);
                        Intent intent = result.getData();
                        Log.e(TAG, "intent : " + intent);
                        assert intent != null;
                        Uri uri = intent.getData();
                        imageview.setImageURI(uri);
                    }
                }
            });

    //날씨 선택 다이얼로그 이벤트
    public void OnClickHandler(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("날씨 선택");

        builder.setItems(R.array.LAN, (dialog, pos) -> {
            String[] items = getResources().getStringArray(R.array.LAN);
            if(items[pos].equals("맑음"))
                weatherB.setImageResource(R.drawable.sunny);
            if(items[pos].equals("흐림"))
                weatherB.setImageResource(R.drawable.cloudy);
            if(items[pos].equals("비"))
                weatherB.setImageResource(R.drawable.rainy);
            if(items[pos].equals("눈"))
                weatherB.setImageResource(R.drawable.snowy);
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}