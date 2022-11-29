package com.project2022.emotiondiary;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button signup_ok_btn;
    Button email_check;
    Button nickname_check;
    EditText email_edit;
    EditText pw_edit;
    EditText nickname_edit;
    //String email;
    //String emailValidation;
    FirebaseFirestore db= FirebaseFirestore.getInstance();

    Toolbar toolbar;

    int e_check;
    int n_check;
    String e_store = null;
    String n_store = null;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //액션바 커스텀
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목을 없애줍니다.

        email_edit = findViewById(R.id.email_editText);
        //email = email_edit.getText().toString();
        //emailValidation = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"; // 이메일 정규식
        pw_edit = findViewById(R.id.pw_editText);
        nickname_edit = findViewById(R.id.nickname_editText);

        // FirebaseAuth 객체의 공유 인스턴스를 가져옴
        mAuth = FirebaseAuth.getInstance();

        signup_ok_btn = findViewById(R.id.signup_ok_btn);
        signup_ok_btn.setOnClickListener(view -> {
            // 공백이 없는 경우
            if (!email_edit.getText().toString().equals("") && !pw_edit.getText().toString().equals("")
            && !nickname_edit.getText().toString().equals("")) {
                if(!e_store.equals(email_edit.getText().toString()) || !n_store.equals(nickname_edit.getText().toString()) || e_store==null || n_store ==null){
                    Toast.makeText(SignUp.this, "이메일 혹은 닉네임 중복 확인을 해주세요", Toast.LENGTH_SHORT).show();
                }
                else
                    createAccount(email_edit.getText().toString(), pw_edit.getText().toString()
                            , nickname_edit.getText().toString());
            }
            // 공백이 하나라도 있는 경우
            else {
                Toast.makeText(SignUp.this, "이메일,비밀번호,닉네임은 필수입력사항입니다", Toast.LENGTH_SHORT).show();
            }
        });

        // 중복확인
        email_check = findViewById(R.id.email_check_btn);
        nickname_check = findViewById(R.id.nickname_check_btn);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        email_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                e_check=0;
                if(imm != null){
                    imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                }
                CollectionReference colRef = db.collection("user");
                colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot document : task.getResult()){
                            if(email_edit.getText().toString().equals(document.get("user_email"))){
                                e_check=1;
                            }
                        }
                        if(e_check==1)
                            Toast.makeText(SignUp.this,"이미 존재하는 이메일입니다", Toast.LENGTH_SHORT).show();
                        else{
                            if(email_edit.getText().toString().equals(""))
                                Toast.makeText(SignUp.this,"이메일을 입력하세요", Toast.LENGTH_SHORT).show();
                            else{
                                Toast.makeText(SignUp.this,"사용 가능한 이메일입니다", Toast.LENGTH_SHORT).show();
                                e_store = email_edit.getText().toString();
                            }
                        }
                    }
                });
            }
        });

        nickname_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                n_check = 0;
                if(imm != null){
                    imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                }
                CollectionReference colRef2 = db.collection("user");
                colRef2.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot document : task.getResult()){
                            if(nickname_edit.getText().toString().equals(document.get("user_nickname"))){
                                n_check = 1;
                            }
                        }
                        if(n_check==1)
                            Toast.makeText(SignUp.this,"이미 존재하는 닉네임입니다", Toast.LENGTH_SHORT).show();
                        else{
                            if(nickname_edit.getText().toString().equals(""))
                                Toast.makeText(SignUp.this,"닉네임을 입력하세요", Toast.LENGTH_SHORT).show();
                            else{
                                Toast.makeText(SignUp.this,"사용 가능한 닉네임입니다", Toast.LENGTH_SHORT).show();
                                n_store = nickname_edit.getText().toString();
                            }
                        }
                    }
                });
            }
        });
    }

    //계정 생성
    private void createAccount(String email, String password, String nickname){
        //신규 사용자의 이메일 주소와 비밀번호를 createUserWithEmailAndPassword에 전달하여 신규 계정을 생성
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseAuth.getInstance().signOut(); // 자동 로그인 방지
                        String uid=task.getResult().getUser().getUid();
                        writeNewUser(email,nickname,uid);
                        Toast.makeText(SignUp.this, "계정 생성 성공", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUp.this,EmailLogin.class);
                        startActivity(intent);
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignUp.this, "계정 생성 실패", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void writeNewUser(String email, String nickname,String uid){
        // 이메일, 닉네임 저장
        Map<String, Object> user = new HashMap<>();
        user.put("user_email", email);
        user.put("user_nickname", nickname);

        db.collection("user").document(uid)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }
}