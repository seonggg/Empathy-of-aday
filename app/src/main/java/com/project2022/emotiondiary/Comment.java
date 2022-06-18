package com.project2022.emotiondiary;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
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
    public String type = "c";
    public String uid;

    AtomicBoolean del_vision;
    long backKeyPressingTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

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
        // 1. 일기 아이디가 같은 댓글들 가져오기
        // 2. uid 기준 오름차순 정렬
        // 3. c_time 기준 오름차순 정렬
        db.collection("comment").whereEqualTo("diary_id",docid).orderBy("uid").orderBy("c_time")
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot document : task.getResult()){
                    Log.d(TAG, document.getId() + " => " + document.getData());

                    String nickname, time, content;

                    nickname = Objects.requireNonNull(document.get("c_nickname")).toString();
                    time = Objects.requireNonNull(document.get("c_time")).toString();
                    content = Objects.requireNonNull(document.get("c_content")).toString();
                    type = Objects.requireNonNull(document.get("type")).toString();
                    uid = Objects.requireNonNull(document.get("uid")).toString();

                    // 삭제 버튼 보이게 할지 여부
                    del_vision = new AtomicBoolean(false);
                    if(nickname.equals(myNickname)){
                        // 로그인된 닉네임과 db에 저장된 닉네임이 같으면 true
                        del_vision = new AtomicBoolean(true);
                    }
                    adapter.setArrayData(new CommentData(nickname, time, content, del_vision, document.getId(),docid,type,uid));
                }
                recyclerView.setAdapter(adapter);
                type = "c";
            }
            else
                Log.d(TAG, "Error getting documents: ", task.getException());
        });

        // db에 댓글 저장 및 뷰 업데이트
        commentTxt = findViewById(R.id.comment_txt);
        commentBtn = findViewById(R.id.comment_btn);

        commentBtn.setOnClickListener(view -> {
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
            commentMap.put("type",type); // 댓글인지 답글인지 구분

            // 답글이면 어댑터에서 받은 uid값 저장 (uid - 같은 그룹의 댓글과 답글을 구별할 수 있는 식별자)
            if(type.equals("r")){
                commentMap.put("uid",uid);
            }
            // 일반 댓글이면 timestamp값 저장
            else{
                commentMap.put("uid",time);
            }

            db.collection("comment").add(commentMap).addOnSuccessListener(documentReference -> {
                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                commentTxt.setText(null); // 에디트텍스트 내용 지우기
                del_vision = new AtomicBoolean(true); // 본인이 작성한 댓글이므로 true로 변경
                adapter.setArrayData(new CommentData(myNickname, time, content,
                        del_vision, documentReference.getId(),docid,type,uid)); // 어댑터로 값 넘기기
                recyclerView.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결
                adapter.notifyItemInserted(adapter.getItemCount() - 1); // 어댑터에게 삽입된 내용이 있음을 알림
                if(type.equals("c"))
                    recyclerView.scrollToPosition(adapter.getItemCount()-1); // 댓글인 경우 스크롤을 맨 아래로 내림
                else
                    type = "c"; // 답글이었을 경우 type을 c로 변경
            }).addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));

        });
    }

    @Override
    public void onBackPressed() {
        // type이 "r"일 때
        if(type.equals("r")){
            // 한 번 누르면 답글 취소
            if(System.currentTimeMillis()>backKeyPressingTime + 2500){
                backKeyPressingTime = System.currentTimeMillis();
                AlertDialog.Builder dlg = new AlertDialog.Builder(Comment.this);
                dlg.setTitle("답글 취소");
                dlg.setMessage("답글 입력을 취소하시겠습니까?");
                dlg.setPositiveButton("예", (dialogInterface, i) -> {
                    type = "r";
                    commentTxt.setText("");
                });
                dlg.setNegativeButton("아니오", (dialogInterface, i) -> {
                });
                dlg.show();
                return;
            }
            // 두 번 눌러야 이전 액티비티로 돌아감
            if(System.currentTimeMillis()<=backKeyPressingTime + 2500){
                super.onBackPressed();
            }
        }
        // type이 "c"일 때
        else
            super.onBackPressed(); // 바로 이전 액티비티로 돌아감
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