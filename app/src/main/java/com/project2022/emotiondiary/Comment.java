package com.project2022.emotiondiary;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.Any;
import com.project2022.emotiondiary.databinding.ActivityMainBinding;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Comment extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    Adapter adapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String docid;

    EditText commentTxt;
    Button commentBtn;
    String myNickname;

    TextView deleteBtn;
    TextView recomment;
    boolean del_vision = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        myNickname = ((Info)this.getApplication()).getNick();

        //액션바 커스텀
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목을 없애줍니다.
        actionBar.setDisplayHomeAsUpEnabled(true); //뒤로 가기 버튼

        Intent intent = getIntent();
        docid = intent.getStringExtra("docid");
        Log.d("TAG", "docid: "+docid);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new Adapter();

        CollectionReference colRef = db.collection("diary").document(docid).collection("comment");
        colRef.orderBy("c_time")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());

                        String nickname, time, content;

                        nickname = document.get("c_nickname").toString();
                        time = document.get("c_time").toString();
                        content = document.get("c_content").toString();

                        // 삭제 버튼 보이게 할지 여부
                        if(nickname.equals(myNickname)){
                            del_vision = true;
                        }
                        else{
                            del_vision = false;
                        }

                        adapter.setArrayData(new CommentData(nickname, time, content, del_vision));
                    }
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        // 댓글 입력
        commentTxt = findViewById(R.id.comment_txt);
        commentBtn = findViewById(R.id.comment_btn);

        commentBtn.setOnClickListener(view -> {

            Long datetime = System.currentTimeMillis();
            Timestamp timestamp = new Timestamp(datetime);

            String time, content;
            time = timestamp.toString();
            content = commentTxt.getText().toString();

            Map<String, Object> commentMap = new HashMap<>();
            commentMap.put("c_nickname", myNickname);
            commentMap.put("c_time",time);
            commentMap.put("c_content",content);

            colRef.add(commentMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    commentTxt.setText(null);
                    del_vision = true;
                    adapter.setArrayData(new CommentData(myNickname, time, content, del_vision));
                    recyclerView.setAdapter(adapter);
                    adapter.notifyItemInserted(adapter.getItemCount() - 1);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error adding document", e);
                }
            });

        });

        // 댓글 삭제
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}