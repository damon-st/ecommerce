package com.damon.ecommerce.slide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.damon.ecommerce.Buyers.MainActivity;
import com.damon.ecommerce.Buyers.ProductDetailsActivity;
import com.damon.ecommerce.Model.Products;
import com.damon.ecommerce.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.tabs.TabLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;



public class SliderActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private Handler sliderHandler  = new Handler();
    private int position;
    private TabLayout indicator;//este sirvio para aser las bolitas de cuanto este la imgen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        viewPager2 = findViewById(R.id.viewPagerImagesSlider);

        //here im preparing list of inmage form drwable
        //you can get it from API as well


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");

        FirebaseRecyclerOptions<Products> options = new
                FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(reference,Products.class)
                .build();

        final FirebaseRecyclerAdapter<Products,SliderViewHolder> adapter = new FirebaseRecyclerAdapter<Products, SliderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final SliderViewHolder holder, int i, @NonNull final Products products) {
                            Picasso.get().load(products.getImage()).into(holder.imageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    holder.progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(R.mipmap.ic_launcher).into(holder.imageView, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Log.e("ERROR","ERROR");
                                        }
                                    });
                                }
                            });
                            position=i;
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(SliderActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("pid",products.getPid());
                                    intent.putExtra("uid",products.getUid());
                                    intent.putExtra("postid",products.getPostid());
                                    startActivity(intent);
                                }
                            });

            }

            @NonNull
            @Override
            public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_item_count,parent,false);
                return new SliderViewHolder(view);
            }
        };
        viewPager2.setAdapter(adapter);
        adapter.startListening();



        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1-Math.abs(position);
                page.setScaleY(0.85f+r*0.15f);

            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);

        //metood para que el slider se mueva solo
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunable);
                sliderHandler.postDelayed(sliderRunable,3000);//slide duration 3 seconds
            }
        });



    }

    //funcion para que se mueva soilo
    private Runnable sliderRunable = new Runnable() {
        @Override
        public void run() {
            //viewPager2.setCurrentItem(viewPager2.getCurrentItem()+1);
            if (viewPager2.getCurrentItem()<=position){
                viewPager2.setCurrentItem(viewPager2.getCurrentItem()+1);
            }else {
                viewPager2.setCurrentItem(0);
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunable,3000);
    }




    class  SliderViewHolder extends RecyclerView.ViewHolder{

        public RoundedImageView imageView;
        public ProgressBar progressBar;
        public RelativeLayout relativeLayout;
        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
            progressBar = itemView.findViewById(R.id.proges_dialog);
            relativeLayout = itemView.findViewById(R.id.relatyve_Slider);
        }
    }

}
