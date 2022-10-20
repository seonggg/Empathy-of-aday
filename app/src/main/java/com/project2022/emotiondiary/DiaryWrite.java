package com.project2022.emotiondiary;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DiaryWrite extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseAuth mAuth;

    private final String TAG = this.getClass().getSimpleName();

    Toolbar toolbar;

    ImageView imageview;
    ImageButton weatherB;
    EditText editText;

    LinearLayout recyclerCase;
    private ViewPager2 sliderViewPager;

    String weather="맑음";
    String docid;
    String nick, id;

    String strDate;

    ArrayList<Uri> uriList= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_write);

        mAuth=FirebaseAuth.getInstance();
        String user_uid = mAuth.getCurrentUser().getUid();
        //Toast.makeText(getApplicationContext(), user_uid, Toast.LENGTH_LONG).show();
        id = ((Info)this.getApplication()).getId();
        nick = ((Info)this.getApplication()).getNick();

        //액션바 커스텀
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목을 없애줍니다.
        actionBar.setDisplayHomeAsUpEnabled(true); //뒤로 가기 버튼

        editText = findViewById(R.id.editText);

        //날짜 표시
        TextView date = findViewById(R.id.date_display);
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault());
        strDate = dateFormat.format(currentTime);
        date.setText((strDate));

        //날씨 다이얼로그
        weatherB = findViewById(R.id.weather_button);
        weatherB.setOnClickListener(this::OnClickHandler);

        //이미지 처리
        imageview = findViewById(R.id.photoView);
        recyclerCase = findViewById(R.id.recyclerCase);
        sliderViewPager=findViewById(R.id.sliderViewPager);

        //이미지 업로드 클릭시
        recyclerCase.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);


            launcher.launch(intent);

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
                        Intent intent = result.getData();

                        if(intent == null){   // 어떤 이미지도 선택하지 않은 경우
                            Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
                        }
                        else{   // 이미지를 하나라도 선택한 경우
                            if(intent.getClipData() == null){     // 이미지를 하나만 선택한 경우
                                Log.e("single choice: ", String.valueOf(intent.getData()));
                                Uri imageUri = intent.getData();
                                uriList.add(imageUri);
                            }
                            else{      // 이미지를 여러장 선택한 경우
                                ClipData clipData = intent.getClipData();
                                Log.e("clipData", String.valueOf(clipData.getItemCount()));

                                if(clipData.getItemCount() > 5){   // 선택한 이미지가 5장 이상인 경우
                                    Toast.makeText(getApplicationContext(), "사진은 5장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                                }
                                else{   // 선택한 이미지가 5장 이하인 경우
                                    Log.e(TAG, "multiple choice");

                                    for (int i = 0; i < clipData.getItemCount(); i++){
                                        Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                                        try {
                                            uriList.add(imageUri);  //uri를 list에 담는다.

                                        } catch (Exception e) {
                                            Log.e(TAG, "File select error", e);
                                        }
                                    }
                                }
                            }

                            //선택한 이미지 viewpager2에 출력
                            imageview.setVisibility(View.GONE);
                            sliderViewPager.setVisibility(View.VISIBLE);

                            sliderViewPager.setOffscreenPageLimit(1);
                            sliderViewPager.setAdapter(new ImageSliderAdapter(getApplicationContext(), uriList));

                        }
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
            if(items[pos].equals("맑음")) {
                weatherB.setImageResource(R.drawable.sunny);
                weather="맑음";
            }
            if(items[pos].equals("흐림")) {
                weatherB.setImageResource(R.drawable.cloudy);
                weather="흐림";
            }
            if(items[pos].equals("비")) {
                weatherB.setImageResource(R.drawable.rainy);
                weather="비";
            }
            if(items[pos].equals("눈")) {
                weatherB.setImageResource(R.drawable.snowy);
                weather="눈";
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //storage에 사진 저장
    private void uploadImg(Uri file, int num){
        StorageReference storageRef = storage.getReference();
        StorageReference riversRef = storageRef.child("diary/"+docid+"_"+num+".jpg");
        UploadTask uploadTask = riversRef.putFile(file);

        try{
            InputStream in = getContentResolver().openInputStream(file);
            Bitmap img = BitmapFactory.decodeStream(in);
            in.close();
            imageview.setImageBitmap(img);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DiaryWrite.this, "사진 업로드 실패",Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(DiaryWrite.this, "사진 업로드 성공",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_write, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //작성 완료 버튼 클릭
            case R.id.add_btn:
                //예외 처리
                if (editText.getText().toString().length() == 0) {
                    Toast toast = Toast.makeText(getApplicationContext(), "내용을 입력하세요", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    //firebase에 저장
                    Long datetime = System.currentTimeMillis();
                    Timestamp timestamp = new Timestamp(datetime);
                    Integer pictures = uriList.size();
                    Diary data = new Diary(mAuth.getCurrentUser().getUid(), id, nick, editText.getText().toString(), timestamp, strDate, weather, pictures);
                    data.toMap();
                    Log.i("firebase_diary", data.toString());

                    db.collection("diary")
                            .add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());

                                    //파이어베이스 storage에 이미지 저장
                                    docid = documentReference.getId();
                                    for (int i = 0; i <= uriList.size() - 1; i++) {
                                        uploadImg(uriList.get(i), i);
                                    }

                                    // 감정 분석 화면으로 전환
                                    Intent intent = new Intent(getApplicationContext(), BeadsMaking.class);
                                    intent.putExtra("docid", docid);
                                    intent.putExtra("content",editText.getText().toString());
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });
                }
                return true;
            case android.R.id.home:
                //select back button
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
