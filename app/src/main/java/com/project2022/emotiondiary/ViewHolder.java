package com.project2022.emotiondiary;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {

    public TextView nicknameView;
    public TextView timeView;
    public TextView contentView;
    public TextView deleteView;

    public ViewHolder(Context context, @NonNull View itemView) {
        super(itemView);

        nicknameView = itemView.findViewById(R.id.nickname_view);
        timeView = itemView.findViewById(R.id.time_view);
        contentView = itemView.findViewById(R.id.content_view);
        deleteView = itemView.findViewById(R.id.cDelete_btn);
    }
}
