package com.damon.ecommerce.calificaciones;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.damon.ecommerce.Chat.ImageViewerActivity;
import com.damon.ecommerce.Model.Reportar;
import com.damon.ecommerce.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class CalificacionesNegativas extends Fragment {


    private RecyclerView recyclerView;
    String pid;

    DatabaseReference reference,productRef ;
    FirebaseDatabase database;

    FirebaseRecyclerOptions<Reportar> options;
    FirebaseRecyclerAdapter<Reportar,CalfViewHolder> adapter;


    public CalificacionesNegativas() {
        // Required empty public constructor
    }

    public CalificacionesNegativas(String pid){
        this.pid = pid;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_calificaciones_negativas, container, false);

        recyclerView = view.findViewById(R.id.recycler_negative);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),LinearLayoutManager.VERTICAL,false));

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Reportar");
        productRef = FirebaseDatabase.getInstance().getReference("Products");

        options = new FirebaseRecyclerOptions.Builder<Reportar>()
                .setQuery(reference.orderByChild("sellerID").equalTo(pid),Reportar.class)
                .build();


        adapter = new FirebaseRecyclerAdapter<Reportar, CalfViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CalfViewHolder holder, final int i, @NonNull Reportar reportar) {
                String comentario = reportar.getComentario();
                String  uidUser = reportar.getUidReport();
                String pid = reportar.getProductID();
                try {

                    holder.texto_reporte.setText("Usuario quien califico al Vendedor");
                    holder.texto_titulo.setText("Puntuacion Negativa");
                    holder.mas_opciones.setVisibility(View.GONE);
                    holder.foto_profile_del_vendedor.setVisibility(View.GONE);
                    holder.texto_precio.setVisibility(View.GONE);
                    holder.nombre_vendedor.setVisibility(View.GONE);
                    holder.texto_activo.setVisibility(View.GONE);
                    holder.texto_descripcion_producto.setVisibility(View.GONE);
                    holder.linea.setVisibility(View.GONE);

                    holder.comentario_quien_reporta.setText(comentario);
                    holder.colorEstado.setImageResource(R.drawable.color_denegado);
                    DatabaseReference reference =  FirebaseDatabase.getInstance().getReference("Users");

                    reference.child(uidUser).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                String nameUser = dataSnapshot.child("name").getValue().toString();
                                String imagUrl = dataSnapshot.child("image").getValue().toString();

                                holder.nombre_quien_reporta.setText(nameUser);

                                Picasso.get().load(imagUrl)
                                        .resize(50,50)
                                        .into(holder.foto_quien_reporta_usuario,
                                        new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                holder.progressUser.setVisibility(View.GONE);
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                Picasso.get().load(R.mipmap.ic_launcher).into(holder.foto_quien_reporta_usuario);
                                            }
                                        });


                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    productRef.child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                final String imageUrl = dataSnapshot.child("image").getValue().toString();
                                String productName = dataSnapshot.child("pname").getValue().toString();
                                holder.nombre_producto.setText(productName);
                                Picasso.get()
                                        .load(imageUrl)
                                        .resize(50,50)
                                        .into(holder.producto_imagen, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.progressProducto.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(R.mipmap.ic_launcher).into(holder.producto_imagen);
                                    }
                                });

                                holder.producto_imagen.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(view.getContext(), ImageViewerActivity.class);
                                        intent.putExtra("url",imageUrl);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @NonNull
            @Override
            public CalfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View vistaV = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reporte_admin,parent,false);
                return new CalfViewHolder(vistaV);
            }
        };


        recyclerView.setAdapter(adapter);
        adapter.startListening();


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter !=null){
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        if (adapter !=null){
            adapter.stopListening();
        }
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter !=null){
            adapter.startListening();
        }
    }
}