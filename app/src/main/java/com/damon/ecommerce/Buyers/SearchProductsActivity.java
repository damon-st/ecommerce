package com.damon.ecommerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.damon.ecommerce.Model.Products;
import com.damon.ecommerce.R;
import com.damon.ecommerce.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SearchProductsActivity extends AppCompatActivity {

    private Button  searchButton;
    private EditText inputText;
    private RecyclerView searchList;
    private String Searchinput;
    private Typeface font;


    private SwipeRefreshLayout refreshLayout;

    FirebaseRecyclerAdapter<Products, ProductosViewHolder> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);

        refreshLayout = findViewById(R.id.swiperefresh);

        font = Typeface.createFromAsset(getAssets(),"fonts/font.otf");
        inputText = findViewById(R.id.search_product_name);
        searchButton = findViewById(R.id.boton_buscar);
        searchList = findViewById(R.id.search_list);
        searchList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(SearchProductsActivity.this,R.anim.layout_slide_rigth);
        searchList.setLayoutAnimation(controller);
        searchList.scheduleLayoutAnimation();


        searchButton.setTypeface(font);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Searchinput = inputText.getText().toString().toLowerCase();
                onStart();
            }
        });


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                onStart();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //aqui estamos creando el metodo para buscar

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");



        FirebaseRecyclerOptions<Products>options =
                new FirebaseRecyclerOptions.Builder<Products>()
              .setQuery(reference.orderByChild("pname").startAt(Searchinput).endAt(Searchinput+"\uf8ff"),Products.class)
                .build();

         adapter = new FirebaseRecyclerAdapter<Products, ProductosViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ProductosViewHolder holder, int position, @NonNull final Products model) {

                String estado = model.getProductState();

                if (estado.equals("Aprovado")){
                    holder.tituloProducto.setTypeface(font);


                    holder.tituloProducto.setText(model.getPname());

                    holder.precioProducto.setText("Precio = " + model.getPrice() + "$");
                    Picasso.get().load(model.getImage())
                            .resize(100,100)
                            .centerCrop().into(holder.imagne_producto, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(R.mipmap.ic_launcher).into(holder.imagne_producto, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Log.e("ERRROR","ERROR");
                                }
                            });
                        }
                    });

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(SearchProductsActivity.this, ProductDetailsActivity.class);
                            intent.putExtra("pid",model.getPid());
                            intent.putExtra("uid",model.getUid());
                            intent.putExtra("postid",model.getPostid());
                            startActivity(intent);
                        }
                    });
                    isSaved(model.getPid(), holder.marcarFavorito);
                    holder.marcarFavorito.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (holder.marcarFavorito.getTag().equals("save")){
                                HashMap<String,Object> hashMap = new HashMap<>();
                                hashMap.put("pid",model.getPid());
                                hashMap.put("uid",model.getUid());


                                FirebaseDatabase.getInstance().getReference().child("Saves").child(FirebaseAuth.getInstance().getUid()).child(model.getPid())
                                        .updateChildren(hashMap);
                            } else {
                                FirebaseDatabase.getInstance().getReference().child("Saves").child(FirebaseAuth.getInstance().getUid())
                                        .child(model.getPid()).removeValue();
                            }
                        }
                    });


                }else {
                   holder.relativeLayout.removeAllViews();
                }


            }

            @NonNull
            @Override
            public ProductosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nuevo_card_products_search, parent, false);
                ProductosViewHolder holder = new ProductosViewHolder(view);
                return holder;
            }
        };
        searchList.setAdapter(adapter);
        adapter.startListening();



    }

    class ProductosViewHolder extends RecyclerView.ViewHolder{

        public ImageView imagne_producto;
        public TextView  tituloProducto,precioProducto;
        public RelativeLayout relativeLayout;
        public ProgressBar progressBar;
        public ImageView marcarFavorito;

        public ProductosViewHolder(@NonNull View itemView) {
            super(itemView);

            imagne_producto = itemView.findViewById(R.id.imagne_producto);
            tituloProducto = itemView.findViewById(R.id.titulo_producto);
            precioProducto = itemView.findViewById(R.id.precio_producto);
            relativeLayout = itemView.findViewById(R.id.relative_search);
            progressBar = itemView.findViewById(R.id.proges_dialog);
            marcarFavorito  = itemView.findViewById(R.id.marcar_favorito);
        }
    }

    private void isSaved(final String postid, final ImageView imageView){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Saves").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postid).exists()){
                    imageView.setImageResource(R.drawable.ic_favorite_red);
                    imageView.setTag("saved");
                } else{
                    imageView.setImageResource(R.drawable.ic_favorite_black);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}


