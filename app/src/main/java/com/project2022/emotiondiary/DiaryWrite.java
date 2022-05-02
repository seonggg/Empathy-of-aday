package com.project2022.emotiondiary;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_write);

        //날짜 표시
        TextView date = (TextView) findViewById(R.id.date_display);
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault());
        String strDate = dateFormat.format(currentTime);
        date.setText((strDate));

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
        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(getApplicationContext(), "작성완료",Toast.LENGTH_SHORT);
                toast.show();
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
                        Uri uri = intent.getData();
                        imageview.setImageURI(uri);
                    }
                }
            });
}