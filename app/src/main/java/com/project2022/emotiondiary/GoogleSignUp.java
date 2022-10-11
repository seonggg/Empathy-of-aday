package com.project2022.emotiondiary;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class GoogleSignUp extends AppCompatActivity {

    EditText email_edit;
    EditText nick_edit;
    Button nick_check;
    Button ok_btn;
    private FirebaseAuth mAuth;

    int n_check;
    String n_store = null;
    private InputMethodManager imm;
    FirebaseFirestore db= FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_up);

        email_edit = findViewById(R.id.email_editText);
        nick_edit = findViewById(R.id.nickname_editText);
        nick_check = findViewById(R.id.nickname_check_btn);
        ok_btn = findViewById(R.id.signup_ok_btn);

        mAuth = FirebaseAuth.getInstance();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        email_edit.setText(mAuth.getCurrentUser().getEmail());

        nick_check.setOnClickListener(new View.OnClickListener() {
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
                            if(nick_edit.getText().toString().equals(document.get("user_nickname"))){
                                n_check = 1;
                            }
                        }
                        if(n_check==1)
                            Toast.makeText(GoogleSignUp.this,"이미 존재하는 닉네임입니다", Toast.LENGTH_SHORT).show();
                        else{
                            if(nick_edit.getText().toString().equals(""))
                                Toast.makeText(GoogleSignUp.this,"닉네임을 입력하세요", Toast.LENGTH_SHORT).show();
                            else{
                                Toast.makeText(GoogleSignUp.this,"사용 가능한 닉네임입니다", Toast.LENGTH_SHORT).show();
                                n_store = nick_edit.getText().toString();
                            }
                        }
                    }
                });
            }
        });

        ok_btn.setOnClickListener(view -> {
            // 공백이 없는 경우
            if (!nick_edit.getText().toString().equals("")) {
                if(!n_store.equals(nick_edit.getText().toString()) || n_store ==null){
                    Toast.makeText(GoogleSignUp.this, "닉네임 중복 확인을 해주세요", Toast.LENGTH_SHORT).show();
                }
                else
                    writeNewUser(email_edit.getText().toString(),n_store);
            }
            // 공백이 하나라도 있는 경우
            else {
                Toast.makeText(GoogleSignUp.this, "닉네임은 필수입력사항입니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void writeNewUser(String email, String nickname){
        Map<String, Object> user = new HashMap<>();
        user.put("user_email", email);
        user.put("user_nickname", nickname);
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("user").document(uid)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(GoogleSignUp.this, "계정 생성 성공", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(GoogleSignUp.this,MyRoom.class);
                        startActivity(intent);
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