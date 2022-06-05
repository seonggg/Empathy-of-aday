package com.project2022.emotiondiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SettingActivity extends AppCompatActivity {

    TextView email;
    TextView logout;
    String emailText;

    //*******************************아직 미완성***************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        email = findViewById(R.id.email_editText);
        logout = findViewById(R.id.logout_textView);
        Intent intent = getIntent();
        emailText = intent.getStringExtra("email");
        email.setText(emailText);

        /*
        DocumentReference docRef = db.collection("user").document(emailText);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });*/

        //로그아웃
        logout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(SettingActivity.this,"로그아웃되었습니다",Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(SettingActivity.this,LoginActivity.class);
            startActivity(intent1);
        });
    }
}