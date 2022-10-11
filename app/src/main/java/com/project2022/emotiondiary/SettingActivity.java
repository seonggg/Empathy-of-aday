package com.project2022.emotiondiary;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project2022.emotiondiary.applock.HomePage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView nickname;
    String email;
    TextView logoutBtn;
    TextView beadsCount;
    TextView changeNameBtn;
    TextView noticeBtn;
    TextView lockBtn;
    CircleImageView profileImg;

    FirebaseAuth mAuth;
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public static Activity settingActivity;
    public static Context context_main;
    public static String path;
    String id;

    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        settingActivity = SettingActivity.this;

        //액션바 커스텀
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목을 없애줍니다.
        actionBar.setDisplayHomeAsUpEnabled(true); //뒤로 가기 버튼

        nickname = findViewById(R.id.show_nickname);
        logoutBtn = findViewById(R.id.logout_btn);
        beadsCount = findViewById(R.id.show_beadscount);
        changeNameBtn = findViewById(R.id.change_nick_btn);
        noticeBtn = findViewById(R.id.notice_btn);
        lockBtn = findViewById(R.id.lock_btn);
        profileImg = findViewById(R.id.profileImage);

        mAuth = FirebaseAuth.getInstance();
        id = mAuth.getCurrentUser().getUid();

        context_main = this;
        path = "start";

        // 프로필 사진 변경
        profileImg.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityResult.launch(intent);
        });

        // 프로필 사진 불러오기
        StorageReference storageRef = storage.getReference();
        StorageReference pathRef = storageRef.child("user");
        if (pathRef == null) {
            Toast.makeText(getApplicationContext(), "저장소에 사진이 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            storageRef.child("user/" + id + "_profileImg.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'
                    Glide.with(getApplicationContext())
                            .load(uri)
                            .into(profileImg);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.d("profile_fail","프로필 사진 불러오기 실패");
                }
            });
        }

        // 닉네임 표시
        DocumentReference docRef = db.collection("user").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    nickname.setText(document.get("user_nickname").toString() + " 님");
                }
                else{
                    nickname.setText(document.get("닉네임 오류").toString());
                }
            }
            else{
                Log.d("NoDoc", "No such document");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "최신 일기 불러오기 실패", Toast.LENGTH_SHORT).show();
                Log.w("DocError", "get failed with ", e);
            }
        });

        // 구슬 개수 표시
        email = ((Info)this.getApplication()).getId();
        CollectionReference colRef = db.collection("diary");
        colRef.whereEqualTo("writer_id",email).get().addOnCompleteListener(task -> {
           if(task.isSuccessful()){
               int count = 0;
               for (QueryDocumentSnapshot document : task.getResult()) {
                   count++;
               }
               beadsCount.setText("구슬 "+ count + "개 " + "보유중");
           }
           else{
               Log.d("Error", "Error getting documents: ", task.getException());
           }
        });

        //닉네임 변경
        changeNameBtn.setOnClickListener(view -> {
            Intent intent1 = new Intent(SettingActivity.this,ChangeName.class);
            startActivity(intent1);
        });

        //알림 온오프
        noticeBtn.setOnClickListener(view -> {
            Intent intent2 = new Intent(SettingActivity.this, PushNotification.class);
            startActivity(intent2);
        });

        //비밀번호로 잠그기
        lockBtn.setOnClickListener(view -> {
            path = "setting";
            Intent intent3 = new Intent(SettingActivity.this, HomePage.class);
            startActivity(intent3);
        });


        //테마 바꾸기

        //로그아웃
        logoutBtn.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(SettingActivity.this,"로그아웃되었습니다",Toast.LENGTH_SHORT).show();
            Intent intent5 = new Intent(SettingActivity.this,LoginActivity.class);
            startActivity(intent5);
        });

        //의견 보내기

        //FAQ

        //버전 정보

        //이용약관
    }

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK && result.getData() != null){
                        uri = result.getData().getData();

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                            profileImg.setImageBitmap(bitmap);
                            uploadImg(uri);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            });

    // firebase storage에 프로필 사진 저장
    private void uploadImg(Uri file){
        StorageReference storageRef = storage.getReference();
        StorageReference riversRef = storageRef.child("user/"+id+"_profileImg.jpg");
        UploadTask uploadTask = riversRef.putFile(file);

        try{
            InputStream in = getContentResolver().openInputStream(file);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SettingActivity.this, "사진 업로드 실패",Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SettingActivity.this, "사진 업로드 성공",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                //select back button
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}