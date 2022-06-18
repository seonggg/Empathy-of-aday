package com.project2022.emotiondiary;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Adapter extends RecyclerView.Adapter<ViewHolder>{

    public ArrayList<CommentData> arrayList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> idList = new ArrayList<>();

    public Adapter() {
        this.arrayList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_list, parent, false);

        return new ViewHolder(context, view);
    }


    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String nickname = arrayList.get(position).nickname;
        String time = arrayList.get(position).time;
        String content = arrayList.get(position).content;
        AtomicBoolean del_vision = new AtomicBoolean(arrayList.get(position).del_vision.get());
        String commentId = arrayList.get(position).commentId;
        String diaryId = arrayList.get(position).diaryId;
        String type = arrayList.get(position).type;
        String uid = arrayList.get(position).uid;

        holder.nicknameView.setText(nickname);
        holder.timeView.setText(time);
        holder.contentView.setText(content);
        if (del_vision.get()) {
            holder.deleteView.setVisibility(View.VISIBLE);
        } else {
            holder.deleteView.setVisibility(View.GONE);
        }

        // 타입이 'r'(대댓글)일 때
        if(type.equals("r")){
            // 앞에 여백 주기
            holder.frontView.setVisibility(View.VISIBLE);
            // 답글 버튼 없애기
            holder.recommentView.setVisibility(View.GONE);
        }

        // 댓글 삭제 시 답글까지 모두 삭제
        // 댓글 삭제
        holder.deleteView.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("댓글 삭제");
            builder.setMessage("댓글을 삭제 하시겠습니까?");
            builder.setPositiveButton("예", (dialogInterface, i) -> {
                Log.d(TAG, "commentId: " + commentId + "/ diaryId: " + diaryId);

                // 답글이면 해당 답글만 삭제
                if(type.equals("r")){
                    // db에서 삭제
                    db.collection("comment").document(commentId).delete()
                            .addOnCompleteListener(task -> Log.d(TAG, "DocumentSnapshot successfully deleted!"));

                    // ArrayList에서 해당 데이터를 삭제
                    arrayList.remove(position);

                    // 어댑터에서 RecyclerView에 반영
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                }
                // 댓글이면 포함된 답글까지 모두 삭제
                else{
                    // db에서 uid가 같은 문서들의 id를 배열에 저장
                    db.collection("comment").whereEqualTo("uid",uid).get()
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    for (QueryDocumentSnapshot document : task.getResult()){
                                        idList.add(document.getId());
                                    }
                                    Log.d(TAG, "삭제할 댓글 id: " + idList);
                                }
                                else
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                            });

                    // idList에 저장된 id값에 해당하는 문서들만 db에서 삭제
                    for(int j = 0; j < idList.size(); j++){
                        System.out.println(idList.get(j));
                        String id = idList.get(j);
                        db.collection("comment").document(id).delete()
                                .addOnCompleteListener(task -> Log.d(TAG, "DocumentSnapshot successfully deleted!"));

                        // ArrayList에서 해당 데이터를 모두 삭제
                        arrayList.removeIf(k -> k.uid.equals(uid));

                        // 어댑터에서 RecyclerView에 반영
                        notifyDataSetChanged();
                    }


                }
            });
            builder.setNegativeButton("아니오", null);
            builder.create().show();
        });

        // 답글 버튼 클릭 시
        holder.recommentView.setOnClickListener(view1 -> {
            ((Comment)Comment.context).type = "r";
            ((Comment)Comment.context).uid = time;

            //키보드 보이게
            InputMethodManager imm = (InputMethodManager) view1.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);

            // 닉네임 + '에게 답글' 자동 입력
            ((Comment) Comment.context).commentTxt.setText(nickname + "님에게 답글: ");
            // 커서를 맨끝으로 이동
            ((Comment) Comment.context).commentTxt.setSelection(((Comment) Comment.context).commentTxt.length());
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setArrayData(CommentData strData) {
        arrayList.add(strData);
    }
}
