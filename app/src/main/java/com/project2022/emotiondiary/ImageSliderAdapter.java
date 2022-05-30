package com.project2022.emotiondiary;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Uri> sliderImage;

    public ImageSliderAdapter(Context context, ArrayList<Uri> sliderImage) {
        this.context = context;
        this.sliderImage = sliderImage;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;    // context에서 LayoutInflater 객체를 얻는다.
        View view = inflater.inflate(R.layout.item_slider, parent, false) ;	// 리사이클러뷰에 들어갈 아이템뷰의 레이아웃을 inflate.
        ImageSliderAdapter.MyViewHolder vh = new ImageSliderAdapter.MyViewHolder(view) ;

        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindSliderImage(sliderImage.get(position));
    }

    @Override
    public int getItemCount() {
        return sliderImage.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageSlider);
        }

        public void bindSliderImage(Uri imageURL) {
            Glide.with(context)
                    .load(imageURL)
                    .into(mImageView);
        }
    }
}
