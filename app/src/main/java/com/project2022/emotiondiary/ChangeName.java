package com.project2022.emotiondiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ChangeName extends AppCompatActivity {

    Toolbar toolbar;
    EditText nicknameEdit;
    Button checkBtn;
    Button changeBtn;

    int n_check;
    String n_store = "null";
    String myNick;
    private InputMethodManager imm;
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);

        //액션바 커스텀
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목을 없애줍니다.
        actionBar.setDisplayHomeAsUpEnabled(true); //뒤로 가기 버튼

        // 이전 액티비티 종료
        SettingActivity settingActivity = (SettingActivity)SettingActivity.settingActivity;
        settingActivity.finish();

        nicknameEdit = findViewById(R.id.nickname_editText);

        mAuth = FirebaseAuth.getInstance();

        // 에디트텍스트에 본인 닉네임 표시
        DocumentReference docRef = db.collection("user").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    nicknameEdit.setText(document.get("user_nickname").toString());
                    myNick = document.get("user_nickname").toString();
                }
                else{
                    nicknameEdit.setText(document.get("닉네임 오류").toString());
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

        // 중복확인
        checkBtn = findViewById(R.id.check_btn);
        checkBtn.setOnClickListener(view -> {
            n_check = 0;
            if(imm != null){
                imm.hideSoftInputFromWindow(view.getWindowToken(),0);
            }
            CollectionReference colRef = db.collection("user");
            colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for(QueryDocumentSnapshot document : task.getResult()){
                        if(nicknameEdit.getText().toString().equals(document.get("user_nickname"))){
                            // 변경할 닉네임이 현재 닉네임과 동일할 때
                            if(nicknameEdit.getText().toString().equals(myNick))
                                n_check = 1;
                            // 변경할 닉네임이 현재 닉네임과 동일하진 않으나 이미 존재하는 닉네임일 때
                            else
                                n_check = 2;
                        }
                    }
                    if(n_check==1)
                        Toast.makeText(ChangeName.this,"현재 닉네임입니다", Toast.LENGTH_SHORT).show();
                    else if(n_check==2)
                        Toast.makeText(ChangeName.this,"이미 존재하는 닉네임입니다", Toast.LENGTH_SHORT).show();
                    else{
                        if(nicknameEdit.getText().toString().trim().equals(""))
                            Toast.makeText(ChangeName.this,"닉네임을 입력하세요", Toast.LENGTH_SHORT).show();
                        else{
                            Toast.makeText(ChangeName.this,"사용 가능한 닉네임입니다", Toast.LENGTH_SHORT).show();
                            n_store = nicknameEdit.getText().toString();
                        }
                    }
                }
            });
        });

        // 변경버튼
        changeBtn = findViewById(R.id.change_btn);
        changeBtn.setOnClickListener(view -> {
            // 에디트 텍스트가 공백이 아닌 경우
            if (!nicknameEdit.getText().toString().equals("")) {
                if(!n_store.equals(nicknameEdit.getText().toString())){
                    Toast.makeText(ChangeName.this, "이메일 혹은 닉네임 중복 확인을 해주세요", Toast.LENGTH_SHORT).show();
                }
                else{
                    //닉네임 업데이트(user 문서 업데이트)
                    //DocumentReference docRef = db.collection("user").document(mAuth.getCurrentUser().getUid());
                    docRef.update("user_nickname",nicknameEdit.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("UpdateSuccess", "DocumentSnapshot successfully updated!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("UpdateFailure", "Error updating document", e);
                        }
                    });
                    Toast.makeText(ChangeName.this, "닉네임이 변경되었습니다",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ChangeName.this,SettingActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            // 에디트 텍스트가 공백인 경우
            else {
                Toast.makeText(ChangeName.this, "변경하실 닉네임을 입력하세요", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                //select back button
                Intent intent = new Intent(ChangeName.this,SettingActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}