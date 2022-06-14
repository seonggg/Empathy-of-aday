package com.project2022.emotiondiary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<ViewHolder>{

    public ArrayList<CommentData> arrayList;

    public Adapter() {
        arrayList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_list, parent, false);

        ViewHolder viewholder = new ViewHolder(context, view);

        return viewholder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String nickname = arrayList.get(position).nickname;
        String time = arrayList.get(position).time;
        String content = arrayList.get(position).content;
        boolean del_vision = arrayList.get(position).del_vision;

        holder.nicknameView.setText(nickname);
        holder.timeView.setText(time);
        holder.contentView.setText(content);
        if(del_vision){
            holder.deleteView.setVisibility(View.VISIBLE);
        }
        else{
            holder.deleteView.setVisibility(View.GONE);
        }
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
