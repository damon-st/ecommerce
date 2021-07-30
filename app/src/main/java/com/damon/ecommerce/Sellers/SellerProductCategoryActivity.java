package com.damon.ecommerce.Sellers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.damon.ecommerce.Buyers.MainActivity;
import com.damon.ecommerce.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellerProductCategoryActivity extends AppCompatActivity {


    private ImageView tshirts,sportShirts,femaleDress,sweathers;
    private ImageView glasses, hatsCaps, walletsBagasPurses,shoes;
    private ImageView headPhonesHandFree,laptops,watches,mobilesPhones,juegos;


     CircleImageView image;

     private DatabaseReference reference;

     String uid;

     ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_product_category);

        try {
            uid = getIntent().getExtras().get("uid").toString();
        }catch (Exception e){
            e.printStackTrace();
        }

        progressBar = findViewById(R.id.progress);


        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        tshirts = findViewById(R.id.t_shirts);
        sportShirts = findViewById(R.id.sports_t_shirts);
        femaleDress = findViewById(R.id.female_dress);
        sweathers = findViewById(R.id.sweather);
        glasses = findViewById(R.id.glasses);
        hatsCaps = findViewById(R.id.hats_caps);
        walletsBagasPurses = findViewById(R.id.purses_bags_walles);
        shoes = findViewById(R.id.shoes);
        headPhonesHandFree = findViewById(R.id.headphonees_handfree);
        laptops = findViewById(R.id.laptops_pc);
        watches = findViewById(R.id.watches);
        mobilesPhones= findViewById(R.id.mobilephones);
        juegos = findViewById(R.id.juegos);


        image = findViewById(R.id.perfil_vendedor);




        tshirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class);
                intent.putExtra("Category","tShirts");
                startActivity(intent);
                finish();
            }
        });
        sportShirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class);
                intent.putExtra("Category","SportsTShirts");
                startActivity(intent);
                finish();
            }
        });
        femaleDress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class);
                intent.putExtra("Category","Female Dresses");
                startActivity(intent);
                finish();
            }
        });
        sweathers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class);
                intent.putExtra("Category","sweathers");
                startActivity(intent);
                finish();
            }
        });
        glasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class);
                intent.putExtra("Category","Glasses");
                startActivity(intent);
                finish();
            }
        });
        hatsCaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class);
                intent.putExtra("Category","Hats Caps");
                startActivity(intent);
                finish();
            }
        });
        walletsBagasPurses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class);
                intent.putExtra("Category","Wallets Bags Purses");
                startActivity(intent);
                finish();
            }
        });
        shoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class);
                intent.putExtra("Category","Shoes");
                startActivity(intent);
                finish();
            }
        });
        headPhonesHandFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class);
                intent.putExtra("Category","HeadPhones HandFree");
                startActivity(intent);
                finish();
            }
        });
        laptops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class);
                intent.putExtra("Category","Laptops");
                startActivity(intent);
                finish();
            }
        });
        watches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class);
                intent.putExtra("Category","Watches");
                startActivity(intent);
                finish();
            }
        });
        mobilesPhones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class);
                intent.putExtra("Category","Mobile Phones");
                startActivity(intent);
                finish();
            }
        });
        juegos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class);
                intent.putExtra("Category","Juegos");
                startActivity(intent);
                finish();
            }
        });

        new UsuarioImage().start();

    }

    class UsuarioImage extends Thread{
        @Override
        public void run() {
            super.run();
            SellerProductCategoryActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("image").exists()){
                                String imageUrl = dataSnapshot.child("image").getValue().toString();
                                Picasso.get().load(imageUrl).into(image, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(R.drawable.ic_errores).into(image);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
