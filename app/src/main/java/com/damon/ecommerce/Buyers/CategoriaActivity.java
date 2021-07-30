package com.damon.ecommerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.ecommerce.Model.Products;
import com.damon.ecommerce.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class CategoriaActivity extends AppCompatActivity {
    private String categoria;
    private ImageButton atras;
    private TextView titulo;
    private RecyclerView recyclerView;
    private DatabaseReference unverifiedProductsRef;
    private String  categoiras;
    private ImageView imagenNoProductos;
    private TextView  textoNoProductos;
    Typeface font;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);



        categoria = getIntent().getStringExtra("category");
        atras = findViewById(R.id.categoria_atras);
        titulo = findViewById(R.id.titulo_categoria);
        categoiras = categoria.toLowerCase();

        recyclerView = findViewById(R.id.recycler_categoria);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(CategoriaActivity.this,R.anim.layout_slide_rigth);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.scheduleLayoutAnimation();
        unverifiedProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        imagenNoProductos = findViewById(R.id.signo_nohay_productos);
        textoNoProductos = findViewById(R.id.texto_nohay_productos);


        titulo.setText("Categoria: "+categoria);

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        font = Typeface.createFromAsset(getAssets(),"fonts/font.otf");
        titulo.setTypeface(font);
        textoNoProductos.setTypeface(font);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(unverifiedProductsRef.orderByChild("category").equalTo(categoria),Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products,CategoriViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, CategoriViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final CategoriViewHolder holder, final int i, @NonNull final Products juegos) {



                                    try {
                                        String  product = juegos.getProductState();
                                        if (product.equals("Aprovado")) {

                                            imagenNoProductos.setVisibility(View.GONE);
                                            textoNoProductos.setVisibility(View.GONE);


                                            final String llave = getRef(i).getKey();

                                            Picasso.get().load(juegos.getImage())
                                                    .resize(200,150)
                                                    .centerCrop()
                                                    .into(holder.imagen, new Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    holder.progressBar.setVisibility(View.GONE);
                                                }

                                                @Override
                                                public void onError(Exception e) {
                                                    Picasso.get().load(R.mipmap.ic_launcher).into(holder.imagen);
                                                }
                                            });
                                            holder.nombre.setTypeface(font);
                                            holder.nombre.setText(juegos.getPname());
                                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(CategoriaActivity.this, ProductDetailsActivity.class);
                                                    intent.putExtra("pid", juegos.getPid().toString());
                                                    intent.putExtra("uid", juegos.getUid());
                                                    //     intent.putExtra("key", juegos.getUid());
                                                    intent.putExtra("postid", juegos.getPostid());
                                                    startActivity(intent);


                                                }
                                            });

                                        }else {

                                            holder.relatiyve.removeAllViews();
//                                            holder.imagen.setVisibility(View.GONE);
//                                            holder.nombre.setVisibility(View.GONE);

                                        }

//                            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                                @Override
//                                public boolean onLongClick(View v) {
//                                    AlertDialog.Builder builder = new AlertDialog.Builder(CategoriaActivity.this,R.style.AlertDialog);
//                                    builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            delete(juegos.getPid());
//                                        }
//                                    });
//                                    builder.show();
//                                    return false;
//                                }
//                            });


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                    }

                    @NonNull
                    @Override
                    public CategoriViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categoria,parent,false);
                        CategoriViewHolder categoriViewHolder = new CategoriViewHolder(view);
                        return categoriViewHolder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void delete(String llave) {
        Toast.makeText(this, ""+llave, Toast.LENGTH_SHORT).show();
        unverifiedProductsRef.child(llave).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(CategoriaActivity.this, "Eliminado Correcto", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }


    public class CategoriViewHolder extends RecyclerView.ViewHolder{

        public ImageView imagen;
        public TextView nombre;
        public RelativeLayout relatiyve;
        public ProgressBar progressBar;

        public CategoriViewHolder(@NonNull View itemView) {
            super(itemView);

            imagen = itemView.findViewById(R.id.imagen_categori);
            nombre = itemView.findViewById(R.id.nombre_juego);
            relatiyve =itemView.findViewById(R.id.relatiyve);
            progressBar = itemView.findViewById(R.id.proges_dialog);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
