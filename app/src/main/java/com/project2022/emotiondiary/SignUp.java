package com.project2022.emotiondiary;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button signup_ok_btn;
    EditText email_edit;
    EditText pw_edit;
    EditText nickname_edit;
    EditText birthdate_edit;
    //String email;
    //String emailValidation;
    Calendar c;
    int mYear;
    int mMonth;
    int mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email_edit = findViewById(R.id.email_editText);
        //email = email_edit.getText().toString();
        //emailValidation = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"; // 이메일 정규식
        pw_edit = findViewById(R.id.pw_editText);
        nickname_edit = findViewById(R.id.nickname_editText);
        birthdate_edit = findViewById(R.id.birthdate_editText);

        // FirebaseAuth 객체의 공유 인스턴스를 가져옴
        mAuth = FirebaseAuth.getInstance();

        signup_ok_btn = findViewById(R.id.signup_ok_btn);
        signup_ok_btn.setOnClickListener(view -> {
            if (!email_edit.getText().toString().equals("") && !pw_edit.getText().toString().equals("")
            && !nickname_edit.getText().toString().equals("")) {
                // 이메일과 비밀번호가 공백이 아닌 경우
                createAccount(email_edit.getText().toString(), pw_edit.getText().toString()
                        , nickname_edit.getText().toString());
            }
            else {
                // 이메일 혹은 비밀번호가 공백인 경우
                Toast.makeText(SignUp.this, "이메일, 비밀번호, 닉네임은 필수입력사항입니다", Toast.LENGTH_LONG).show();
            }
        });

        // 생일 선택
        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar
                ,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                birthdate_edit.setText(year+"." + (month+1) + "." + dayOfMonth);
            }
        }, mYear, mMonth, mDay);

        datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        birthdate_edit.setInputType(0);
        birthdate_edit.setOnClickListener(view -> {
            datePickerDialog.show();
        });
    }

    //계정 생성
    private void createAccount(String email, String password, String nickname){
        //신규 사용자의 이메일 주소와 비밀번호를 createUserWithEmailAndPassword에 전달하여 신규 계정을 생성
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseAuth.getInstance().signOut(); // 자동 로그인 방지
                        writeNewUser(email,nickname);
                        Toast.makeText(SignUp.this, "계정 생성 성공", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUp.this,EmailLogin.class);
                        startActivity(intent);
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignUp.this, "계정 생성 실패", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void writeNewUser(String email, String nickname){
        // 이메일, 닉네임 저장
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> user = new HashMap<>();
        user.put("user_email", email);
        user.put("user_nickname", nickname);
        user.put("user_birthdate", Arrays.asList(mYear,mMonth,mDay));

        db.collection("user").document(email)
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