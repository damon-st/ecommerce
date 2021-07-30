package com.damon.ecommerce.FragmentAdministrador;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.ecommerce.Admin.AdminHomeActivity;
import com.damon.ecommerce.Admin.VerReportesPublicaciones;
import com.damon.ecommerce.Model.Reportar;
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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportesUsuarios extends Fragment {

    DatabaseReference unverifiedProductsRef;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public ReportesUsuarios() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.reportes_usuarios, container, false);


        unverifiedProductsRef = FirebaseDatabase.getInstance().getReference().child("Reportar");

        recyclerView = view.findViewById(R.id.reporte_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        new HiloReporteAdmin().start();

    }

    class HiloReporteAdmin extends Thread{
        @Override
        public void run() {
            super.run();
            ReportesUsuarios.super.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FirebaseRecyclerOptions<Reportar> options= new
                            FirebaseRecyclerOptions.Builder<Reportar>()
                            .setQuery(unverifiedProductsRef.orderByChild("reporte").equalTo("true"),Reportar.class)
                            .build();

                    FirebaseRecyclerAdapter<Reportar, ReportesUsuarios.ReporteHolder> adapter=
                            new FirebaseRecyclerAdapter<Reportar, ReportesUsuarios.ReporteHolder>(options) {
                                @Override
                                protected void onBindViewHolder(@NonNull final ReportesUsuarios.ReporteHolder holder, final int i, @NonNull final Reportar reportar) {

                                    final String pid = reportar.getProductID();
                                    String sellerID = reportar.getSellerID();
                                    String uidReport = reportar.getUidReport();
                                    String comentario = reportar.getComentario();
                                    String reporte = reportar.getReporte();
                                    final String key = reportar.getKey();



                                    holder.comentario_quien_reporta.setText(comentario);

                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
                                    reference.child(pid).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                // Toast.makeText(VerReportesPublicaciones.this, "adadadadaddada", Toast.LENGTH_SHORT).show();
                                                String imagen = dataSnapshot.child("image").getValue().toString();
                                                String nombreProducto = dataSnapshot.child("pname").getValue().toString();
                                                String estadoProducto = dataSnapshot.child("productState").getValue().toString();
                                                String descripcionProducto = dataSnapshot.child("description").getValue().toString();

                                                Picasso.get().load(imagen).into(holder.producto_imagen, new Callback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        holder.progressProduct.setVisibility(View.GONE);
                                                    }

                                                    @Override
                                                    public void onError(Exception e) {
                                                        Picasso.get().load(R.mipmap.ic_launcher).into(holder.producto_imagen);
                                                    }
                                                });
                                                holder.nombre_producto.setText(nombreProducto);
                                                holder.texto_activo.setText(estadoProducto);
                                                holder.texto_descripcion_producto.setText(descripcionProducto);


                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    DatabaseReference userInfo = FirebaseDatabase.getInstance().getReference().child("Users");
                                    userInfo.child(sellerID).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {

                                                String imagenPerfil = dataSnapshot.child("image").getValue().toString();
                                                String nombre = dataSnapshot.child("name").getValue().toString();

                                                Picasso.get().load(imagenPerfil).placeholder(R.drawable.profile).into(holder.foto_profile_del_vendedor);
                                                holder.nombre_vendedor.setText(nombre);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    userInfo.child(uidReport).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                String imagenProfile = dataSnapshot.child("image").getValue().toString();
                                                String nombre = dataSnapshot.child("name").getValue().toString();

                                                Picasso.get().load(imagenProfile).placeholder(R.drawable.profile).into(holder.foto_quien_reporta_usuario, new Callback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        holder.progressUser.setVisibility(View.GONE);
                                                    }

                                                    @Override
                                                    public void onError(Exception e) {
                                                        Picasso.get().load(R.mipmap.ic_launcher).into(holder.foto_quien_reporta_usuario);
                                                    }
                                                });
                                                holder.nombre_quien_reporta.setText(nombre);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    holder.mas_opciones.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            PopupMenu popupMenu = new PopupMenu(getContext(),v);
                                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                                @Override
                                                public boolean onMenuItemClick(MenuItem item) {
                                                    switch (item.getItemId()){

                                                        case R.id.eliminar:
                                                            deleteThisProduct(pid,key);
                                                            return true;
                                                        case R.id.rechazado:
                                                            String estado = "Rechazado";
                                                            ChangeProductState(pid,estado);
                                                            return true;

                                                        default:
                                                            return false;
                                                    }
                                                }
                                            });
                                            popupMenu.inflate(R.menu.admin_menu_reporte);
                                            popupMenu.show();
                                        }
                                    });

                                }

                                @NonNull
                                @Override
                                public ReportesUsuarios.ReporteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reporte_admin,parent,false);
                                    ReportesUsuarios.ReporteHolder itemViewHolder = new ReportesUsuarios.ReporteHolder(view);
                                    return itemViewHolder;
                                }
                            };
                    recyclerView.setAdapter(adapter);
                    adapter.startListening();
                }
            });
        }
    }

    public class ReporteHolder extends RecyclerView.ViewHolder{
        public ImageView producto_imagen,mas_opciones;
        public TextView nombre_producto,nombre_vendedor,nombre_quien_reporta,comentario_quien_reporta,texto_activo,texto_descripcion_producto;
        public CircleImageView foto_profile_del_vendedor,foto_quien_reporta_usuario;
        public RelativeLayout relativeLayout;
        public RatingBar ratingBar;
        public ProgressBar progressProduct,progressUser;
        public ReporteHolder(@NonNull View itemView) {
            super(itemView);
            producto_imagen = itemView.findViewById(R.id.producto_imagen);
            nombre_producto = itemView.findViewById(R.id.texto_producto);
            foto_profile_del_vendedor = itemView.findViewById(R.id.profile_del_vendedor);
            nombre_vendedor = itemView.findViewById(R.id.texto_nombre);
            foto_quien_reporta_usuario = itemView.findViewById(R.id.foto_quien_reporta);
            nombre_quien_reporta = itemView.findViewById(R.id.nombre_quien_reporta);
            comentario_quien_reporta = itemView.findViewById(R.id.comentario_quien_reporta);
            texto_activo = itemView.findViewById(R.id.texto_activo);
            texto_descripcion_producto = itemView.findViewById(R.id.texto_descripcion_producto);
            relativeLayout = itemView.findViewById(R.id.relatc);
            mas_opciones = itemView.findViewById(R.id.mas_opciones);
            ratingBar = itemView.findViewById(R.id.ratingCalf);
            progressProduct = itemView.findViewById(R.id.progessProducto);
            progressUser = itemView.findViewById(R.id.progressUser);

        }
    }

    private void ChangeProductState(String productID,String aprovado) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
        reference.child(productID).child("productState")
                .setValue(aprovado)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "EL producto esta aprobado.y ahora esta disponible en la tienda ", Toast.LENGTH_SHORT).show();

                    }
                });
    }
    private void deleteThisProduct(String postid,String key) {
        //metodo para eliminar el producto
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
        reference.child(postid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(), "El producto se elimino ", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getContext(), AdminHomeActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        unverifiedProductsRef.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getContext(), "El producto y Referencia Eliminado ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
