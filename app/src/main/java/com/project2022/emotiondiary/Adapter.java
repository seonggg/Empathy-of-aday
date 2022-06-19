package com.project2022.emotiondiary;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Adapter extends RecyclerView.Adapter<ViewHolder>{

    public ArrayList<CommentData> arrayList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        holder.nicknameView.setText(nickname);
        holder.timeView.setText(time);
        holder.contentView.setText(content);
        if (del_vision.get()) {
            holder.deleteView.setVisibility(View.VISIBLE);
        } else {
            holder.deleteView.setVisibility(View.GONE);
        }

        // 댓글 삭제
        holder.deleteView.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("댓글 삭제");
            builder.setMessage("댓글을 삭제 하시겠습니까?");
            builder.setPositiveButton("예", (dialogInterface, i) -> {
                Log.d(TAG, "commentId: " + commentId + " / diaryId: " + diaryId);
                // db에서 삭제
                db.collection("comment").document(commentId).delete()
                        .addOnCompleteListener(task -> Log.d(TAG, "DocumentSnapshot successfully deleted!"));

                arrayList.remove(position); // ArrayList에서 해당 데이터를 삭제

                notifyItemRemoved(position); // 아이템이 삭제됨을 알림
                notifyItemRangeChanged(position, getItemCount());
            });
            builder.setNegativeButton("아니오", null);
            builder.create().show();
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
