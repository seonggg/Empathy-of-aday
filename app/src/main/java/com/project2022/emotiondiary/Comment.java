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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class Comment extends AppCompatActivity {

    public static Object context;
    Toolbar toolbar;
    RecyclerView recyclerView;
    Adapter adapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText commentTxt;
    Button commentBtn;
    String myNickname;
    public String docid;
    public String type;

    AtomicBoolean del_vision;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        type = "c";

        context = this;

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
        db.collection("comment").whereEqualTo("diary_id",docid).orderBy("c_time")
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot document : task.getResult()){
                    Log.d(TAG, document.getId() + " => " + document.getData());

                    String nickname, time, content;

                    nickname = Objects.requireNonNull(document.get("c_nickname")).toString();
                    time = Objects.requireNonNull(document.get("c_time")).toString();
                    content = Objects.requireNonNull(document.get("c_content")).toString();

                    // 삭제 버튼 보이게 할지 여부
                    del_vision = new AtomicBoolean(false);
                    if(nickname.equals(myNickname)) {
                        // 로그인된 닉네임과 db에 저장된 닉네임이 같으면 true
                        del_vision = new AtomicBoolean(true);
                    }
                    adapter.setArrayData(new CommentData(nickname, time, content, del_vision, document.getId(),docid));
                }
                recyclerView.setAdapter(adapter);
            }
            else
                Log.d(TAG, "Error getting documents: ", task.getException());
        });

        // db에 댓글 저장 및 뷰 업데이트
        commentTxt = findViewById(R.id.comment_txt);
        commentBtn = findViewById(R.id.comment_btn);

        commentBtn.setOnClickListener(view -> {
            // 공백 체크
            if(commentTxt.getText().toString().replace(" ", "").equals("")){
                Toast.makeText(getApplicationContext(),"내용을 입력해주세요", Toast.LENGTH_SHORT).show();
            }
            else{
                //키보드 내리기
                InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                long datetime = System.currentTimeMillis();
                Timestamp timestamp = new Timestamp(datetime);

                String time, content;
                time = timestamp.toString();
                content = commentTxt.getText().toString();

                Map<String, Object> commentMap = new HashMap<>();
                commentMap.put("c_nickname", myNickname); // 닉네임
                commentMap.put("c_time",time); // 날짜 및 시간
                commentMap.put("c_content",content); // 내용
                commentMap.put("diary_id",docid); // 일기 id

                db.collection("comment").add(commentMap).addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    commentTxt.setText(null); // 에디트텍스트 내용 지우기
                    del_vision = new AtomicBoolean(true); // 본인이 작성한 댓글이므로 true로 변경
                    adapter.setArrayData(new CommentData(myNickname, time, content,
                            del_vision, documentReference.getId(),docid)); // 어댑터로 값 넘기기
                    recyclerView.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결
                    adapter.notifyItemInserted(adapter.getItemCount() - 1); // 어댑터에게 삽입된 내용이 있음을 알림
                    recyclerView.scrollToPosition(adapter.getItemCount()-1); // 스크롤을 맨 아래로 내림
                }).addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
            }
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