package com.project2022.emotiondiary;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class EmailLogin extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button login_btn;
    EditText email_edit;
    EditText pw_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);

        // FirebaseAuth 객체의 공유 인스턴스를 가져옴
        mAuth = FirebaseAuth.getInstance();

        login_btn = findViewById(R.id.login_btn);
        email_edit = findViewById(R.id.email_editText);
        pw_edit = findViewById(R.id.pw_editText);

        login_btn.setOnClickListener(view -> signIn(email_edit.getText().toString(),pw_edit.getText().toString()));
    }

    // 로그인
    private void signIn(String email, String password) {
        if (!email_edit.getText().toString().equals("") && !pw_edit.getText().toString().equals("")) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(EmailLogin.this, "로그인되었습니다", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "signInWithEmail:success");
                            Intent intent = new Intent(EmailLogin.this,MyRoom.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(EmailLogin.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}