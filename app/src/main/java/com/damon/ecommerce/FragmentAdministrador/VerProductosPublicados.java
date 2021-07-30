package com.damon.ecommerce.FragmentAdministrador;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.damon.ecommerce.Model.Products;
import com.damon.ecommerce.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class VerProductosPublicados extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Typeface typeface;
    private DatabaseReference reference;

    public VerProductosPublicados() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_ver_productos_publicados, container, false);

        recyclerView = view.findViewById(R.id.recycler_productos);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        typeface = Typeface.createFromAsset(getActivity().getAssets(),"fonts/font.otf");

        reference = FirebaseDatabase.getInstance().getReference().child("Products");

        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Products> options = new
                FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(reference,Products.class)
                .build();

        FirebaseRecyclerAdapter<Products,ProductsHolder>adapter = new
                FirebaseRecyclerAdapter<Products, ProductsHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ProductsHolder holder, int i, @NonNull Products products) {
                        String stado = products.getProductState();
                        if (stado.equals("Aprovado")){
                            Picasso.get().load(products.getImage()).into(holder.imagen_producto, new Callback() {
                                @Override
                                public void onSuccess() {
                                    holder.progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(R.drawable.ic_errores).into(holder.imagen_producto);
                                }
                            });
                            holder.nombre_juego.setTypeface(typeface);
                            holder.nombre_juego.setText(products.getPname());
                        }else {
                            holder.relativeLayout.removeAllViews();
                        }
                    }

                    @NonNull
                    @Override
                    public ProductsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categoria,parent,false);
                        ProductsHolder productsHolder = new ProductsHolder(view);
                        return productsHolder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    class ProductsHolder extends RecyclerView.ViewHolder{

        public RelativeLayout relativeLayout;
        public ImageView imagen_producto;
        public TextView nombre_juego;
        public ProgressBar progressBar;
        public ProductsHolder(View viewHolder){
            super(viewHolder);

            relativeLayout = viewHolder.findViewById(R.id.relatiyve);
            imagen_producto = viewHolder.findViewById(R.id.imagen_categori);
            nombre_juego = viewHolder.findViewById(R.id.nombre_juego);
            progressBar = viewHolder.findViewById(R.id.proges_dialog);

        }
    }
}
