package com.project2022.emotiondiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    Button email;
    TextView signUp;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // FirebaseAuth 객체의 공유 인스턴스를 가져옴
        mAuth = FirebaseAuth.getInstance();

        onStart();

        email = findViewById(R.id.email_login_btn);
        email.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this,EmailLogin.class);
            startActivity(intent);
        });

        signUp = findViewById(R.id.signup_btn);
        signUp.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this,SignUp.class);
            startActivity(intent);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // 활동을 초기화할 때 사용자가 현재 로그인되어 있는지 확인
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(LoginActivity.this,MyRoom.class);
            startActivity(intent);
        }
    }
}