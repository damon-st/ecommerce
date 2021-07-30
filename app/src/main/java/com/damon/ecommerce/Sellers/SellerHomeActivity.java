package com.damon.ecommerce.Sellers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.ecommerce.Admin.SellerMaintainProductsActivity;
import com.damon.ecommerce.Buyers.HomeActivity;
import com.damon.ecommerce.Buyers.ProductDetailsActivity;
import com.damon.ecommerce.Buyers.SearchProductsActivity;
import com.damon.ecommerce.Chat.ChatActivity;
import com.damon.ecommerce.Chat.ImageViewerActivity;
import com.damon.ecommerce.Model.Products;
import com.damon.ecommerce.Model.Saves;
import com.damon.ecommerce.Model.Users;
import com.damon.ecommerce.R;
import com.damon.ecommerce.ViewHolder.ItemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.internal.http2.Header;

public class SellerHomeActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedProductsRef,Sellerreference,users;


    private StorageTask uploadTask;
    private Uri imageUri;
    private String myUrl ="";
    private String checker="";
    private SellersViewHolder sellersViewHolder;
    private StorageReference storageProlifePictureRef;
    private FirebaseAuth auth;
    private  String hola;
    private View view;
    public CircleImageView imagen;
    private FirebaseAuth mAuth;
    public String  link = "https://firebasestorage.googleapis.com/v0/b/ecommerce-fea29.appspot.com/o/Profile%20pictures%2Fprofile.png?alt=media&token=2cce4a2c-0f73-4684-8672-ccff91b08c31";
    private ChipNavigationBar chipNavigationBar;
    private RecyclerView favoriteRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseStorage storage;

    private String uidUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);

        unverifiedProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        mAuth = FirebaseAuth.getInstance();//instanciamos la clase de inico

        storageProlifePictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");
        hola = auth.getInstance().getCurrentUser().getUid();
        Sellerreference = FirebaseDatabase.getInstance().getReference().child("Users");
        users = FirebaseDatabase.getInstance().getReference().child("Users");


        storage = FirebaseStorage.getInstance();

        recyclerView = findViewById(R.id.seller_home_recyclerview);
        favoriteRecyclerView = findViewById(R.id.seller_favorite_home);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        favoriteRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        favoriteRecyclerView.setLayoutManager(linearLayoutManager);





        uidUser = mAuth.getCurrentUser().getUid();



        chipNavigationBar = findViewById(R.id.nav_seller_home);
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch (i){
                    case R.id.navigation_home:
                        principal();
                        break;
                    case  R.id.navigation_add:
                        Intent añadir = new Intent(getApplicationContext(), SellerProductCategoryActivity.class);
                        añadir.putExtra("uid",uidUser);
                        startActivity(añadir);
                        break;
                    case  R.id.misfavoritos:
                        mostrarFavoritos();
                        break;
                }
            }
        });

      //  toolbar.setTitle("Shop");
    }

    private void principal() {
        favoriteRecyclerView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void mostrarFavoritos() {
        recyclerView.setVisibility(View.GONE);
        favoriteRecyclerView.setVisibility(View.VISIBLE);
        favoritos();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new HiloSellerHome().start();
    }

    class HiloSellerHome extends Thread{
        @Override
        public void run() {
            super.run();
            SellerHomeActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FirebaseRecyclerOptions<Products> options
                            = new FirebaseRecyclerOptions.Builder<Products>()
                            .setQuery(unverifiedProductsRef.orderByChild("uid")
                                    .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()),Products.class)
                            .build();

                    FirebaseRecyclerAdapter<Products, ItemViewHolder> adapter
                            = new FirebaseRecyclerAdapter<Products, ItemViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull final ItemViewHolder holder, int position, @NonNull final Products model) {

                            holder.txtprdouctName.setText(model.getPname());
                            holder.txtProductDescription.setText(model.getDescription());
                            holder.txtProductPrice.setText("Precio = " + model.getPrice() + "$");
                            seenNumber(holder,model.getPid());
                            final String color = model.getProductState();
                            if (color.equals("Aprovado")){
                                holder.colorEstado.setImageResource(R.drawable.nuevocolor);
                                holder.txtProductState.setBackgroundResource(R.drawable.nuevocolor);
                            }else if (color.equals("Rechazado")){
                                holder.colorEstado.setImageResource(R.drawable.color_denegado);
                                holder.txtProductState.setTextColor(Color.parseColor("#ffffff"));
                                holder.txtProductState.setBackgroundResource(R.drawable.color_denegado);
                            }else if (color.equals("Pendiente")){
                                holder.colorEstado.setImageResource(R.drawable.color_estandar);
                                holder.txtProductState.setTextColor(Color.parseColor("#ffffff"));
                                holder.txtProductState.setBackgroundResource(R.drawable.color_estandar);
                            }else {
                                holder.colorEstado.setImageResource(R.drawable.color_verde);
                                holder.txtProductState.setTextColor(Color.parseColor("#ffffff"));
                                holder.txtProductState.setBackgroundResource(R.drawable.color_verde);
                            }
                            holder.txtProductState.setText(model.getProductState());
                            holder.txtfecha.setText("Publicado desde: "+model.getDate());

                            Picasso.get().load(model.getImage()).into(holder.imageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    holder.progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(R.mipmap.ic_launcher).into(holder.imageView);
                                }
                            });

                            holder.imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent imagenGrande = new Intent(SellerHomeActivity.this, ImageViewerActivity.class);
                                    imagenGrande.putExtra("url",model.getImage());
                                    startActivity(imagenGrande);
                                }
                            });

                            holder.mas_opciones.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                        final  String productID =model.getPid();
//                        if (!color.equals("Rechazado")) {
//                            CharSequence options[] = new CharSequence[]{
//                                    "Eliminar Producto",
//                                    "Marcar Como Vendido",
//                                    "Cambiar Datos del Producto"
//                            };
//                            AlertDialog.Builder builder = new AlertDialog.Builder(SellerHomeActivity.this);
//                            builder.setTitle("Escoje una de las Opciones?");
//                            builder.setItems(options, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int i) {
//                                    if (i == 0) {
//                                        DeleteProduct(productID);
//                                    } else if (i == 1) {
//                                        MarcarVendido(productID);
//                                    } else if (i == 2) {
//                                        Intent intent = new Intent(SellerHomeActivity.this, SellerMaintainProductsActivity.class);
//                                        intent.putExtra("pid", model.getPid());
//                                        startActivity(intent);
//                                    }
//                                }
//                            });
//                            builder.show();
//                        }else {
//                            Toast.makeText(SellerHomeActivity.this, "Producto Rechazado No cumple Con Nuestras Politicas ", Toast.LENGTH_SHORT).show();
//
                                    if (color.equals("Rechazado")||color.equals("Vendido")) {
                                        final String productID = model.getPid();
                                        final String image = model.getImage();
                                        PopupMenu popupMenu = new PopupMenu(getApplicationContext(),v);
                                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                            @Override
                                            public boolean onMenuItemClick(MenuItem item) {
                                                switch (item.getItemId()){
                                                    case R.id.eliminar_producto_rechazado:
                                                        DeleteProduct(productID,image);
                                                        return true;
                                                    default:
                                                        return false;
                                                }
                                            }
                                        });
                                        popupMenu.inflate(R.menu.menu_rechazado);
                                        popupMenu.show();
                                    }else if (color.equals("Pendiente")){
                                        final String productID = model.getPid();
                                        final String image = model.getImage();
                                        PopupMenu popupMenu  = new PopupMenu(getApplicationContext(),v);
                                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                            @Override
                                            public boolean onMenuItemClick(MenuItem item) {
                                                switch (item.getItemId()){

                                                    case R.id.edit_product:
                                                        Intent intent = new Intent(SellerHomeActivity.this, SellerMaintainProductsActivity.class);
                                                        intent.putExtra("pid", model.getPid());
                                                        startActivity(intent);
                                                        return true;

                                                    case R.id.eliminar_product:
                                                        DeleteProduct(productID,image);
                                                        return true;

                                                    default:
                                                        return false;
                                                }

                                            }
                                        });
                                        popupMenu.inflate(R.menu.menu_pendiente);
                                        popupMenu.show();
                                    }else {
                                        final String productID = model.getPid();
                                        final String image = model.getImage();
                                        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                            @Override
                                            public boolean onMenuItemClick(MenuItem menuItem) {
                                                switch (menuItem.getItemId()) {
                                                    case R.id.editar_producto:
                                                        Intent intent = new Intent(SellerHomeActivity.this, SellerMaintainProductsActivity.class);
                                                        intent.putExtra("pid", model.getPid());
                                                        startActivity(intent);
                                                        return true;
                                                    case R.id.eliminar:
                                                        DeleteProduct(productID,image);
                                                        return true;
                                                    case R.id.vendido:
                                                        MarcarVendido(productID);
                                                        return true;
                                                    default:
                                                        return false;
                                                }
                                            }
                                        });
                                        popupMenu.inflate(R.menu.mas_opciones);
                                        popupMenu.show();
                                    }
                                }
                            });

                        }

                        @NonNull
                        @Override
                        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nuevodisenoventas, parent, false);
                            ItemViewHolder holder = new ItemViewHolder(view);
                            return holder;
                        }
                    };
                    recyclerView.setAdapter(adapter);
                    adapter.startListening();
                }
            });
        }
    }

    private void seenNumber(final ItemViewHolder h, String storyid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(mAuth.getUid()).child(storyid).child("views");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                h.txtVistas.setText("Vistas: "+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void MarcarVendido(String productID) {
        HashMap<String ,Object> hashMap = new HashMap<>();
        hashMap.put("productState","Vendido");

        unverifiedProductsRef.child(productID).updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(SellerHomeActivity.this, "Vendido!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(SellerHomeActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void DeleteProduct(String productID, String  image) {
    //metodo para eliminar el prodecto
        final StorageReference reference = storage.getReferenceFromUrl(image);
        unverifiedProductsRef.child(productID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(SellerHomeActivity.this, "EL producto se Elimino Correctamente", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(SellerHomeActivity.this, "Error al Eliminar", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    }
                });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode ==RESULT_OK&&data!=null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();//aqui recueramos
            sellersViewHolder.perfil.setImageURI(imageUri);//aqui asignamos

        }else {
            Toast.makeText(this, "Error, Intenta Nuevamente", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SellerHomeActivity.this,SellerHomeActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser currentUser = mAuth.getCurrentUser();//linea que funciona como conector si ubo inico de sesion

    }

   ArrayList<String> saves;

    private void favoritos(){
        String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        saves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves");
        leerFavoritos();


    }

    private void leerFavoritos() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves");
        String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseRecyclerOptions<Saves> options =
                new FirebaseRecyclerOptions.Builder<Saves>()
                .setQuery(reference.child(auth),Saves.class)
                .build();

        FirebaseRecyclerAdapter<Saves,ProductosViewHolder> adapter =
                new FirebaseRecyclerAdapter<Saves, ProductosViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ProductosViewHolder holder, int position, @NonNull final Saves model) {

                        final String usersID = getRef(position).getKey();
                        final String pid = model.getPid();
                        final String[] retName = new String[1];
                        final String[] retImage = {"default_image"};

                        unverifiedProductsRef.child(pid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    if (dataSnapshot.hasChild("image")){
                                        String image = dataSnapshot.child("image").getValue().toString();
                                        String pname = dataSnapshot.child("pname").getValue().toString();
                                        String precio = dataSnapshot.child("price").getValue().toString();
                                        final String postid = dataSnapshot.child("postid").getValue().toString();


                                        Picasso.get().load(image)
                                                .resize(200,200)
                                                .centerCrop()
                                                .into(holder.imagne_producto, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                holder.progressBar.setVisibility(View.GONE);
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                Picasso.get().load(R.mipmap.ic_launcher).into(holder.imagne_producto);
                                            }
                                        });
                                        holder.tituloProducto.setText(pname);
                                        holder.precioProducto.setText("Precio: " + precio + "$");

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

                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(SellerHomeActivity.this, ProductDetailsActivity.class);
                                                intent.putExtra("pid",model.getPid());
                                                intent.putExtra("uid",model.getUid());
                                                intent.putExtra("postid",postid);
                                                startActivity(intent);
                                            }
                                        });


                                    }
                                }else {
                                    holder.relativeLayout.removeAllViews();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }

                    @NonNull
                    @Override
                    public ProductosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nuevo_card_products_search,parent,false);
                        return new ProductosViewHolder(view);
                    }
                };
        favoriteRecyclerView.setAdapter(adapter);
        adapter.startListening();

    }


    public static class SellersViewHolder extends RecyclerView.ViewHolder{

        public TextView name,address,phone,email;
        public CircleImageView perfil;
        public Button actualizar;
        public TextView perfilActualizar;

        public SellersViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.seller_perfil_nombre);
            address = itemView.findViewById(R.id.seller_perfil_direccion);
            phone  = itemView.findViewById(R.id.seller_perfil_numero_celular);
            email= itemView.findViewById(R.id.seller_perfil_correo);
            actualizar = itemView.findViewById(R.id.seller_perfil_boton);
            perfil = itemView.findViewById(R.id.seller_perfil_foto);

        }
    }

    private void users(){
        final String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseRecyclerOptions<Users> options
                = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(users,Users.class)
                .build();
        FirebaseRecyclerAdapter<Users,SellersViewHolder> adapter
                = new FirebaseRecyclerAdapter<Users, SellersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final SellersViewHolder holder, final int position, @NonNull final Users model) {
                String  mauth =  FirebaseAuth.getInstance().getCurrentUser().getUid();
                final DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mauth);
                final String[] retImage = {"default_image"};
                holder.name.setText(model.getName());
                holder.phone.setText(model.getPhone());
                holder.email.setText(model.getEmail());
                holder.address.setText(model.getAddress());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.profile).into(holder.perfil);
                final String usersID = getRef(position).getKey();
                final String s  = model.getName();
                retImage[0] = String.valueOf(Picasso.get().load(model.getImage()));
                UsersRef.child(mauth).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                          if (dataSnapshot.exists()){
                              if (dataSnapshot.child("image").exists()){
                                  String image = dataSnapshot.child("image").getValue().toString();
                                  Picasso.get().load(image).placeholder(R.drawable.profile).into(holder.perfil);
                                  final String retName = dataSnapshot.child("name").getValue().toString();



                                  holder.itemView.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {

                                          Intent chatintent = new Intent(SellerHomeActivity.this, ChatActivity.class);
                                          chatintent.putExtra("visit_user_id",usersID);
                                          chatintent.putExtra("visit_user_name",retName);
                                          chatintent.putExtra("visit_image", retImage[0]);

                                          startActivity(chatintent);
                                      }
                                  });

                              }else {
                                  Picasso.get().load(link).into(holder.perfil);
                                  final String retName = dataSnapshot.child("name").getValue().toString();
                              }
                          }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent chatintent = new Intent(SellerHomeActivity.this, ChatActivity.class);
                        chatintent.putExtra("visit_user_id",usersID);
                        chatintent.putExtra("visit_user_name",s);
                        chatintent.putExtra("visit_image", retImage[0]);

                        startActivity(chatintent);
                    }
                });

            }

            @NonNull
            @Override
            public SellersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.perfil_seller_layout,parent,false);
                return new SellersViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
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

    }



