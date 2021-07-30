package com.damon.ecommerce.Admin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.damon.ecommerce.FragmentAdministrador.ReportesUsuarios;
import com.damon.ecommerce.FragmentAdministrador.TodosLosUsuarios;
import com.damon.ecommerce.FragmentAdministrador.VerProductosPublicados;
import com.damon.ecommerce.R;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;


public class VerReportesPublicaciones extends AppCompatActivity {
//    DatabaseReference unverifiedProductsRef;
//
//    private RecyclerView recyclerView;
//    private RecyclerView.LayoutManager layoutManager;
    private ChipNavigationBar menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_reportes_publicaciones);

//        unverifiedProductsRef =FirebaseDatabase.getInstance().getReference().child("Reportar");
//
//        recyclerView = findViewById(R.id.reporte_recycler);
//        recyclerView.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);

        menu = findViewById(R.id.nav_administrador);

        menu.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                if (i ==R.id.home){
                    Fragment fragment;
                    fragment = new ReportesUsuarios();
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.addToBackStack(null);
                    transaction.replace(R.id.reporte_fragement,fragment);
                    transaction.commit();
                }else if (i==R.id.discover){
                    Fragment fragment;
                    fragment = new VerProductosPublicados();
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.addToBackStack(null);
                    transaction.replace(R.id.reporte_fragement,fragment);
                    transaction.commit();
                }else if (i==R.id.account){
                    Fragment fragment;
                    fragment = new TodosLosUsuarios();
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.reporte_fragement,fragment);
                    transaction.commit();
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
//        FirebaseRecyclerOptions<Products>options
//                = new FirebaseRecyclerOptions.Builder<Products>()
//                .setQuery(unverifiedProductsRef.orderByChild("reporte").equalTo(true),Products.class)
//                .build();
//
//        FirebaseRecyclerAdapter<Products, ProductViewHolder>adapter
//                = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {
//
//                holder.txtprdouctName.setText(model.getPname());
//                holder.txtProductDescription.setText(model.getDescription());
//                holder.txtProductPrice.setText("Precio = " + model.getPrice() + "$");
//                Picasso.get().load(model.getImage()).into(holder.imageView);
//
//
//            }
//
//            @NonNull
//            @Override
//            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
//                ProductViewHolder holder = new ProductViewHolder(view);
//                return holder;
//            }
//        };
//        recyclerView.setAdapter(adapter);
//        adapter.startListening();



//        FirebaseRecyclerOptions<Reportar>options= new
//                FirebaseRecyclerOptions.Builder<Reportar>()
//                .setQuery(unverifiedProductsRef.orderByChild("reporte").equalTo("true"),Reportar.class)
//                .build();
//
//        FirebaseRecyclerAdapter<Reportar, ReporteHolder>adapter=
//                new FirebaseRecyclerAdapter<Reportar, ReporteHolder>(options) {
//                    @Override
//                    protected void onBindViewHolder(@NonNull final ReporteHolder holder, final int i, @NonNull final Reportar reportar) {
//
//                        final String pid = reportar.getProductID();
//                        String sellerID = reportar.getSellerID();
//                        String uidReport = reportar.getUidReport();
//                        String comentario = reportar.getComentario();
//                        String reporte = reportar.getReporte();
//                        final String key = reportar.getKey();
//
//
//
//                            holder.comentario_quien_reporta.setText(comentario);
//
//                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
//                            reference.child(pid).addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    if (dataSnapshot.exists()) {
//                                        // Toast.makeText(VerReportesPublicaciones.this, "adadadadaddada", Toast.LENGTH_SHORT).show();
//                                        String imagen = dataSnapshot.child("image").getValue().toString();
//                                        String nombreProducto = dataSnapshot.child("pname").getValue().toString();
//                                        String estadoProducto = dataSnapshot.child("productState").getValue().toString();
//                                        String descripcionProducto = dataSnapshot.child("description").getValue().toString();
//
//                                        Picasso.get().load(imagen).into(holder.producto_imagen);
//                                        holder.nombre_producto.setText(nombreProducto);
//                                        holder.texto_activo.setText(estadoProducto);
//                                        holder.texto_descripcion_producto.setText(descripcionProducto);
//
//
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//
//                            DatabaseReference userInfo = FirebaseDatabase.getInstance().getReference().child("Users");
//                            userInfo.child(sellerID).addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    if (dataSnapshot.exists()) {
//
//                                        String imagenPerfil = dataSnapshot.child("image").getValue().toString();
//                                        String nombre = dataSnapshot.child("name").getValue().toString();
//
//                                        Picasso.get().load(imagenPerfil).placeholder(R.drawable.profile).into(holder.foto_profile_del_vendedor);
//                                        holder.nombre_vendedor.setText(nombre);
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//
//                            userInfo.child(uidReport).addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    if (dataSnapshot.exists()) {
//                                        String imagenProfile = dataSnapshot.child("image").getValue().toString();
//                                        String nombre = dataSnapshot.child("name").getValue().toString();
//
//                                        Picasso.get().load(imagenProfile).placeholder(R.drawable.profile).into(holder.foto_quien_reporta_usuario);
//                                        holder.nombre_quien_reporta.setText(nombre);
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//
//                            holder.mas_opciones.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    PopupMenu popupMenu = new PopupMenu(getApplicationContext(),v);
//                                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                                        @Override
//                                        public boolean onMenuItemClick(MenuItem item) {
//                                            switch (item.getItemId()){
//
//                                                case R.id.eliminar:
//                                                    deleteThisProduct(pid,key);
//                                                    return true;
//                                                case R.id.rechazado:
//                                                    String estado = "Rechazado";
//                                                    ChangeProductState(pid,estado);
//                                                    return true;
//
//                                                default:
//                                                    return false;
//                                            }
//                                        }
//                                    });
//                                    popupMenu.inflate(R.menu.admin_menu_reporte);
//                                    popupMenu.show();
//                                }
//                            });
//
//                    }
//
//                    @NonNull
//                    @Override
//                    public ReporteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reporte_admin,parent,false);
//                        ReporteHolder itemViewHolder = new ReporteHolder(view);
//                        return itemViewHolder;
//                    }
//                };
//        recyclerView.setAdapter(adapter);
//        adapter.startListening();
    }

//    public static class ReporteHolder extends RecyclerView.ViewHolder{
//        public ImageView producto_imagen,mas_opciones;
//        public TextView nombre_producto,nombre_vendedor,nombre_quien_reporta,comentario_quien_reporta,texto_activo,texto_descripcion_producto;
//        public CircleImageView foto_profile_del_vendedor,foto_quien_reporta_usuario;
//        public RelativeLayout relativeLayout;
//        public ReporteHolder(@NonNull View itemView) {
//            super(itemView);
//            producto_imagen = itemView.findViewById(R.id.producto_imagen);
//            nombre_producto = itemView.findViewById(R.id.texto_producto);
//            foto_profile_del_vendedor = itemView.findViewById(R.id.profile_del_vendedor);
//            nombre_vendedor = itemView.findViewById(R.id.texto_nombre);
//            foto_quien_reporta_usuario = itemView.findViewById(R.id.foto_quien_reporta);
//            nombre_quien_reporta = itemView.findViewById(R.id.nombre_quien_reporta);
//            comentario_quien_reporta = itemView.findViewById(R.id.comentario_quien_reporta);
//            texto_activo = itemView.findViewById(R.id.texto_activo);
//            texto_descripcion_producto = itemView.findViewById(R.id.texto_descripcion_producto);
//            relativeLayout = itemView.findViewById(R.id.relatc);
//            mas_opciones = itemView.findViewById(R.id.mas_opciones);
//
//        }
//    }

//    private void ChangeProductState(String productID,String aprovado) {
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
//        reference.child(productID).child("productState")
//                .setValue(aprovado)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Toast.makeText(VerReportesPublicaciones.this, "EL producto esta aprobado.y ahora esta disponible en la tienda ", Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//    }
//    private void deleteThisProduct(String postid,String key) {
//        //metodo para eliminar el producto
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
//        reference.child(postid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                Toast.makeText(VerReportesPublicaciones.this, "El producto se elimino ", Toast.LENGTH_SHORT).show();
//
//                Intent intent = new Intent(VerReportesPublicaciones.this, AdminHomeActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//        unverifiedProductsRef.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()){
//                    Toast.makeText(VerReportesPublicaciones.this, "El producto y Referencia Eliminado ", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
