package com.project2022.emotiondiary;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolderBead extends RecyclerView.ViewHolder {

    ImageView grid_img;
    TextView grid_id;

    public ViewHolderBead(@NonNull View itemView) {
        super(itemView);

        grid_img = itemView.findViewById(R.id.grid_img);
        grid_id = itemView.findViewById(R.id.grid_id);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),ShowDiary.class);
                intent.putExtra("docid",grid_id.getText());
                view.getContext().startActivity(intent);
            }
        });

    }

    public void onBind(DataBead data){
        grid_img.setImageResource(data.getImage());
        grid_id.setText(data.getDocid());
    }
}
