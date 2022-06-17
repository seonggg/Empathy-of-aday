package com.project2022.emotiondiary;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class Info extends Application {
    // 사용자의 아이디, 닉네임 전역변수 선언

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = get_id(id);
    }

    //사용자 아이디 가져오기
    private String get_id(String uid){
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("user").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        id = document.get("user_email").toString();
                    }else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
        return id;
    }

    private String nick;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = get_nickname(nick);
    }

    //사용자 닉네임 가져오기
    private String get_nickname(String uid){
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("user").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        nick = document.get("user_nickname").toString();
                    }else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
        return nick;
    }

}


