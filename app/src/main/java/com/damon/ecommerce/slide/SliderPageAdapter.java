package com.damon.ecommerce.slide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.airbnb.lottie.LottieAnimationView;
import com.damon.ecommerce.R;

import java.util.List;

public class SliderPageAdapter extends PagerAdapter {

    private Context mContext;
    private List<SlideModels> mList;

    public SliderPageAdapter(Context mContext, List<SlideModels> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View slideLayout = inflater.inflate(R.layout.slide_item,null);

        LottieAnimationView slideAniamtion = slideLayout.findViewById(R.id.slide_img);
        TextView  textoSLide = slideLayout.findViewById(R.id.slide_title);
        slideAniamtion.setAnimation(mList.get(position).getImage());
        textoSLide.setText(mList.get(position).getTitle());

        container.addView(slideLayout);
        return  slideLayout;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
