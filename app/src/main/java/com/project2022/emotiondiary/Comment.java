package com.project2022.emotiondiary;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Comment extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    Adapter adapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText commentTxt;
    Button commentBtn;
    String myNickname;
    public String docid;

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
        Objects.requireNonNull(actionBar).setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목을 없애줍니다.
        actionBar.setDisplayHomeAsUpEnabled(true); //뒤로 가기 버튼

        Intent intent = getIntent();
        docid = intent.getStringExtra("docid");
        Log.d("TAG", "docid: "+docid);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new Adapter();

        // 댓글 표시
        db.collection("comment").whereEqualTo("diary_id",docid).orderBy("c_time").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot document : task.getResult()){
                    Log.d(TAG, document.getId() + " => " + document.getData());

                    String nickname, time, content;

                    nickname = Objects.requireNonNull(document.get("c_nickname")).toString();
                    time = Objects.requireNonNull(document.get("c_time")).toString();
                    content = Objects.requireNonNull(document.get("c_content")).toString();

                    // 삭제 버튼 보이게 할지 여부
                    del_vision = nickname.equals(myNickname);

                    adapter.setArrayData(new CommentData(nickname, time, content, del_vision, document.getId(),docid));
                }
                recyclerView.setAdapter(adapter);
            }
            else
                Log.d(TAG, "Error getting documents: ", task.getException());
        });

        // 댓글 입력
        commentTxt = findViewById(R.id.comment_txt);
        commentBtn = findViewById(R.id.comment_btn);

        commentBtn.setOnClickListener(view -> {

            long datetime = System.currentTimeMillis();
            Timestamp timestamp = new Timestamp(datetime);

            String time, content;
            time = timestamp.toString();
            content = commentTxt.getText().toString();

            Map<String, Object> commentMap = new HashMap<>();
            commentMap.put("c_nickname", myNickname);
            commentMap.put("c_time",time);
            commentMap.put("c_content",content);
            commentMap.put("diary_id",docid);

            db.collection("comment").add(commentMap).addOnSuccessListener(documentReference -> {
                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                commentTxt.setText(null);
                del_vision = true;
                adapter.setArrayData(new CommentData(myNickname, time, content, del_vision, documentReference.getId(),docid));
                recyclerView.setAdapter(adapter);
                adapter.notifyItemInserted(adapter.getItemCount() - 1);
            }).addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));

        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}