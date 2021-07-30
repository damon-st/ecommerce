package com.damon.ecommerce.slide;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.damon.ecommerce.Model.Products;
import com.damon.ecommerce.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder>   {

    List<String> slideItemList;
    ViewPager2 viewPager2;
    private Products products;
    private Activity context;

    public SliderAdapter(List<String> slideItemList, ViewPager2 viewPager2,Products products,Activity context) {
        this.slideItemList = slideItemList;
        this.viewPager2 = viewPager2;
        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_item_count,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final SliderViewHolder holder, final int position) {
        holder.setImage(slideItemList.get(position));

        if (position==slideItemList.size()-2){
            viewPager2.post(runnable);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, slideItemList.get(position), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return slideItemList.size();
    }

    class  SliderViewHolder extends RecyclerView.ViewHolder{

        private RoundedImageView imageView;
        private ProgressBar progressBar;
        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
            progressBar = itemView.findViewById(R.id.proges_dialog);
        }

        void setImage(String sliderItem){
            //if you want to dispay image from the internet,
            //you can put code here. usind glide or picasso

//            Transformation transformation = new RoundedTransformationBuilder()
//                    .borderColor(Color.BLACK)
//                    .borderWidthDp(3)
//                    .cornerRadiusDp(12)
//                    .oval(false)
//                    .build();

         //   Picasso.get().load(sliderItem).fit().transform(transformation).into(imageView);
            Picasso.get().load(sliderItem).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(R.mipmap.ic_launcher).into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("Error","Error");
                        }
                    });
                }
            });
//            imageView.setImageResource(sliderItem.getImage());
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            slideItemList.addAll(slideItemList);
            notifyDataSetChanged();
        }
    };
}
