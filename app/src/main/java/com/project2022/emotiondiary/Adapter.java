package com.project2022.emotiondiary;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<ViewHolder>{

    public ArrayList<CommentData> arrayList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Adapter() {
        arrayList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_list, parent, false);

        return new ViewHolder(context, view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String nickname = arrayList.get(position).nickname;
        String time = arrayList.get(position).time;
        String content = arrayList.get(position).content;
        boolean del_vision = arrayList.get(position).del_vision;
        String commentId = arrayList.get(position).commentId;
        String diaryId = arrayList.get(position).diaryId;

        holder.nicknameView.setText(nickname);
        holder.timeView.setText(time);
        holder.contentView.setText(content);
        if(del_vision){
            holder.deleteView.setVisibility(View.VISIBLE);
        }
        else{
            holder.deleteView.setVisibility(View.GONE);
        }

        // 댓글 삭제
        holder.deleteView.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("댓글 삭제");
            builder.setMessage("댓글을 삭제 하시겠습니까?");
            builder.setPositiveButton("예", (dialogInterface, i) -> {
                Log.d(TAG, "commentId: " + commentId + "/ diaryId: " + diaryId);

                // 댓글 데이터 삭제
                db.collection("comment").document(commentId).delete()
                        .addOnCompleteListener(task -> Log.d(TAG, "DocumentSnapshot successfully deleted!"));

                // ArrayList에서 해당 데이터를 삭제
                arrayList.remove(position);

                // 어댑터에서 RecyclerView에 반영
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, arrayList.size());
            });
            builder.setNegativeButton("아니오", null);
            builder.create().show();
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    // 데이터를 입력
    public void setArrayData(CommentData strData) {
        arrayList.add(strData);
    }
}
