package com.damon.ecommerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.damon.ecommerce.Chat.Comments;
import com.damon.ecommerce.Chat.VerVendedor;
import com.damon.ecommerce.Chat.ImageViewerActivity;

import com.damon.ecommerce.Model.Products;

import com.damon.ecommerce.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

//    private FloatingActionButton addToCartBtn ;
    private ImageView productImage;
   // private ElegantNumberButton numberButton;
    private LinearLayout preguntastextview;
    private TextView productPrice ,productDescription,productName,relacionados,texto_pregunta;
    private String productID ="",state = "Normal";
    private String sellerID ="";
    private String uid="";
    private int contador1 =0;
    private String postid;
    private TextView estado,cantidad,reportar_publicacion;

    String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private Dialog dialog;
    private ProgressBar progressBar;
    private ChipNavigationBar chipNavigationBar;


    private RecyclerView recycler_relacionado;
    private LinearLayoutManager layoutManager;

     private DatabaseReference orderRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);


        progressBar = findViewById(R.id.proges_dialog);
        dialog = new Dialog(this);

       final Typeface font  = Typeface.createFromAsset(getAssets(),"fonts/font.otf");
        productID = getIntent().getStringExtra("pid");

        sellerID = getIntent().getStringExtra("uid");
//        uid= getIntent().getStringExtra("key");
        postid = getIntent().getStringExtra("postid");


        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(auth);


//        addToCartBtn = findViewById(R.id.add_product_to_cart);

//        numberButton = findViewById(R.id.number_btn);
        productImage = findViewById(R.id.product_image_details);
        productName = findViewById(R.id.product_name_details);
        productDescription = findViewById(R.id.product_description_details);
        productPrice = findViewById(R.id.product_price_details);
        preguntastextview = findViewById(R.id.preguntas);
        estado = findViewById(R.id.estado);
        cantidad = findViewById(R.id.cantidad);
        reportar_publicacion = findViewById(R.id.reportar_publicacion);
        relacionados = findViewById(R.id.relacionados);
        texto_pregunta =findViewById(R.id.texto_pregunta);



        productName.setTypeface(font);
        productDescription.setTypeface(font);
        texto_pregunta.setTypeface(font);
        estado.setTypeface(font);
        cantidad.setTypeface(font);
        relacionados.setTypeface(font);

        chipNavigationBar = findViewById(R.id.contactarse);

        preguntastextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              Intent preguntas = new Intent(ProductDetailsActivity.this, Preguntas.class);
//              preguntas.putExtra("pid",productID);
//              startActivity(preguntas);
                Intent intent = new Intent(ProductDetailsActivity.this, Comments.class);
                intent.putExtra("postid", productID);
                intent.putExtra("publisherid", sellerID);
                intent.putExtra("id",postid);
                startActivity(intent);
            }
        });

        getProductDetails(  productID);//aqui se recuerpa de id de producto y se crea un metood


//        addToCartBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //la condicion creada es para que cree un timpo de respuesta de aver precionado el boton
//                if (contador1 == 0) {
//                    Snackbar.make(v, "Hola...Precio de nuevo para añadir a tu carrito", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                    contador1++;
//
//                }else {
//
//                    if (state.equals("Order Placed")|| state.equals("Order Shipped")){
//                        Toast.makeText(ProductDetailsActivity.this, "Puedes agregar mas productos,una vez que se haya enviado tu pedido o confirmado tu compra", Toast.LENGTH_LONG).show();
//                    }else {
//                        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                            addingToCartList();//este es el metodo para añadir ala lista carrtio
//                            finish();
//                    }
//                }
//                new CountDownTimer(3000,1000){
//
//                    @Override
//                    public void onTick(long millisUntilFinished) {
//
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        contador1=0;
//                    }
//                }.start();
//            }
//
//        });


        //esta es la varrita de abajo para ver como le acemos


        reportar_publicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sellerID.equals(FirebaseAuth.getInstance().getUid())){
                    Toast.makeText(ProductDetailsActivity.this, "No puedes Reportar Tu propia Publicacion", Toast.LENGTH_SHORT).show();
                }else {
                    dialog.setContentView(R.layout.contestar_comentario);
                    TextView texos = dialog.findViewById(R.id.texos);
                    TextView enviar = dialog.findViewById(R.id.post);
                    final EditText escribir = dialog.findViewById(R.id.add_comment);

                    texos.setVisibility(View.VISIBLE);
                    texos.setTypeface(font);

                    enviar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String  comentario  =escribir.getText().toString();
                            if (TextUtils.isEmpty(comentario)){
                                Toast.makeText(ProductDetailsActivity.this, "Escribe tu respuesta Porfavor", Toast.LENGTH_SHORT).show();
                            }else {
                                ReportarPublicacion(productID,sellerID,postid,comentario);
                                dialog.dismiss();
                            }
                        }
                    });

                    dialog.show();



                }
            }
        });

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {

                if (i == R.id.navigation_mensajeria) {
                    Intent a = new Intent(ProductDetailsActivity.this, VerVendedor.class);
                    a.putExtra("uid", sellerID);
                    a.putExtra("pid", productID);
                    startActivity(a);

                }
            }
        });
        recycler_relacionado = findViewById(R.id.recycler_relacionado);
        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recycler_relacionado.setLayoutManager(layoutManager);
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(ProductDetailsActivity.this,R.anim.layout_slide_rigth);
        recycler_relacionado.setLayoutAnimation(controller);
        recycler_relacionado.scheduleLayoutAnimation();

    }


    //este es el metodo para el reporte de publicacion
    private void ReportarPublicacion(String productID, String sellerID, String postid,String comentario) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Reportar");
        String key  = productRef.push().getKey();
        HashMap<String ,Object> hashMap = new HashMap<>();
        hashMap.put("reporte","true");
        hashMap.put("uidReport",FirebaseAuth.getInstance().getUid());
        hashMap.put("productID",productID);
        hashMap.put("sellerID",sellerID);
        hashMap.put("id",postid);
        hashMap.put("comentario",comentario);
        hashMap.put("key",key);

        productRef.child(key).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ProductDetailsActivity.this, "Gracias Por Reportar el mal Uso de la App", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckOrdenSate();
    }

    private void addingToCartList() {
        //este es para añadir ala tienda
        String saveCurrrentTime , saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrrentTime = currentTime.format(calForDate.getTime());

        final   DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String ,Object> cartMap  = new HashMap<>();
        cartMap.put("pid",productID);
        cartMap.put("pname",productName.getText().toString());
        cartMap.put("price",productPrice.getText().toString());
        cartMap.put("date",saveCurrentDate);
        cartMap.put("time",saveCurrrentTime);
        //cartMap.put("quantity",numberButton.getNumber());
        cartMap.put("description",productDescription.getText().toString());
        cartMap.put("discount","");

        cartListRef.child("Users View").child(auth)
                .child("Products").child(productID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            cartListRef.child("Admin View").child(auth)
                                    .child("Products").child(productID)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(ProductDetailsActivity.this, "Añadino al Carrito", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent( ProductDetailsActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                        }
                    }
                });




    }

    private void getProductDetails(final String productID) {
//aqui recueperamos toda la informacion de los productos
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        productRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    final Products products = dataSnapshot.getValue(Products.class);

                    productName.setText(products.getPname());
                    productPrice.setText("Precio= " + products.getPrice() + " $");
                    productDescription.setText("DESCRIPCION DEL PRODUCTO:" + "\n" + products.getDescription());
                    String  categoria = products.getCategory();
                    Picasso.get().load(products.getImage()).into(productImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(R.mipmap.ic_launcher).into(productImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Log.e("Error","Error");
                                }
                            });
                        }
                    });
                    estado.setText("" + products.getEstado());
                    cantidad.setText("Cantidad: " + products.getCantidad());

                    productImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ProductDetailsActivity.this, ImageViewerActivity.class);
                            intent.putExtra("url", products.getImage());
                            startActivity(intent);
                        }
                    });

                    RelacionadosProductos(categoria);


                    try {
                        addView(products.getPid());
                    }catch (Exception e) {
                      e.printStackTrace();
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void addView(String storyid){
        //aqui creamos el metodo para que se creee cuando lo aya visto
        FirebaseDatabase.getInstance().getReference().child("Story").child(sellerID)
                .child(storyid).child("views").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
    }

    private  void CheckOrdenSate(){

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String shoppingState = dataSnapshot.child("state").getValue().toString();
                    String uid = dataSnapshot.child("uid").getValue().toString();
                    String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();


                    if (shoppingState.equals("shipped")){
                        state = "Order Shipped";

                    }else if (shoppingState.equals("not shipped")){

                        state = "Order Placed";
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    void RelacionadosProductos(String category){
        relacionados.setText("PRODUCTOS RELACIONADOS CON "+category);
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        FirebaseRecyclerOptions<Products> options = new
                FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(productRef.orderByChild("category").equalTo(category),Products.class)
                .build();

        FirebaseRecyclerAdapter<Products,RelacionadoProducto>adapter=new
                FirebaseRecyclerAdapter<Products, RelacionadoProducto>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RelacionadoProducto holder, int i, @NonNull final Products products) {
                                String estado = products.getProductState();
                                String pid = products.getPid();
                                if (estado.equals("Aprovado")&&!pid.equals(productID)){
                                    Picasso.get()
                                            .load(products.getImage())
                                            .resize(200,200)
                                            .into(holder.imagen_relacionado, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            holder.progressBar.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Picasso.get().load(R.mipmap.ic_launcher).into(holder.imagen_relacionado);
                                        }
                                    });

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(ProductDetailsActivity.this, ProductDetailsActivity.class);
                                            intent.putExtra("pid",products.getPid());
                                            intent.putExtra("uid",products.getUid());
                                            intent.putExtra("postid",products.getPostid());
                                            startActivity(intent);
                                        }
                                    });
                                }else {
                                    holder.relativeLayout.removeAllViewsInLayout();
                                }
                    }

                    @NonNull
                    @Override
                    public RelacionadoProducto onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_relacionado_producto,parent,false);
                       return  new RelacionadoProducto(view);
                    }
                };
        recycler_relacionado.setAdapter(adapter);
        adapter.startListening();
    }

    class RelacionadoProducto  extends RecyclerView.ViewHolder{

        public ImageView imagen_relacionado;
        public RelativeLayout relativeLayout;
        public ProgressBar progressBar;


        public RelacionadoProducto(@NonNull View itemView) {
            super(itemView);

            imagen_relacionado = itemView.findViewById(R.id.imagen_relacionado);
            relativeLayout = itemView.findViewById(R.id.relatyve_relacionado);
            progressBar = itemView.findViewById(R.id.proges_dialog);
        }
    }




}
