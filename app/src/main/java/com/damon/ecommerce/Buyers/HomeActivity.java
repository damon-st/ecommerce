package com.damon.ecommerce.Buyers;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.request.RequestOptions;
import com.damon.ecommerce.Admin.SellerMaintainProductsActivity;
import com.damon.ecommerce.Chat.VerMensajesEscritos;
import com.damon.ecommerce.Chat.NotificationActivity;
import com.damon.ecommerce.Model.Products;
import com.damon.ecommerce.Model.Users;
import com.damon.ecommerce.Notification.Token;
import com.damon.ecommerce.R;
import com.damon.ecommerce.Sellers.SellerHomeActivity;
import com.damon.ecommerce.slide.SliderActivity;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.animations.DescriptionAnimation;
import com.glide.slider.library.slidertypes.BaseSliderView;
import com.glide.slider.library.slidertypes.TextSliderView;
import com.glide.slider.library.tricks.ViewPagerEx;

import android.os.Handler;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;



import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
    , BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener
{
    private DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private String type = "";
    private FirebaseAuth auth;
    private LinearLayout linear_laptos,linear_audiculares,linear_zapatos,linear_telefono,linear_todo_categorias,linear_principal;

    private  Typeface font;
    private SliderLayout mDemoSlider;
    private TextView texto_genero_main,texto_laptop,texto_audiculares,texto_zapatos,texto_aventura_main,texto_todas_categorias;

    private Dialog dialog,erroInternet;

    private Animation animation;


    ArrayList<String> listUrl;
    ArrayList<String> listName;
    ArrayList<String> listpid;
    ArrayList<String> listcategory;
    ArrayList<String> listpostid;

    RequestOptions requestOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        erroInternet = new Dialog(this);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        mDemoSlider = findViewById(R.id.slider);


        if (networkInfo != null && networkInfo.isConnected()) {

            // Si hay conexión a Internet en este momento



        dialog = new Dialog(this);


        animation = AnimationUtils.loadAnimation(this,R.anim.animacionuno);
        font = Typeface.createFromAsset(getAssets(),"fonts/font.otf");
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle!=null){
            type = getIntent().getExtras().get("Admin").toString();

        }

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        //aqui inicailamos las nombre
        linear_laptos = findViewById(R.id.linear_laptops);
        linear_audiculares = findViewById(R.id.linear_audiculares);
        linear_zapatos = findViewById(R.id.linear_zapatos);
        linear_telefono = findViewById(R.id.linear_telefono);
        linear_todo_categorias = findViewById(R.id.linear_todo_categorias);
        linear_principal = findViewById(R.id.linear_principal);

        texto_genero_main = findViewById(R.id.texto_genero_main);
        texto_laptop = findViewById(R.id.texto_laptop);
        texto_audiculares = findViewById(R.id.texto_audiculares);
        texto_zapatos = findViewById(R.id.texto_zapatos);
        texto_aventura_main = findViewById(R.id.texto_aventura_main);
        texto_todas_categorias = findViewById(R.id.texto_todas_categorias);


        linear_principal.setAnimation(animation);




       // Paper.init(this);

        //aqui asignamos el tipo de letra
        texto_genero_main.setTypeface(font);
        texto_laptop.setTypeface(font);
        texto_audiculares.setTypeface(font);
        texto_zapatos.setTypeface(font);
        texto_aventura_main.setTypeface(font);
        texto_todas_categorias.setTypeface(font);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!type.equals("Admin")){
//                    Intent intent = new Intent(HomeActivity.this, CartActivity.class);
//                    startActivity(intent);
//                }
//
//            }
//        });}
        //instanciamos con el visisor del xml
        //creamos un array para guardadr todos los datos encontrados

             listUrl = new ArrayList<>();
             listName = new ArrayList<>();
             listpid = new ArrayList<>();
             listcategory = new ArrayList<>();
             listpostid = new ArrayList<>();
             requestOptions = new RequestOptions();
             requestOptions.centerCrop();




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.nav_app_bar_open_drawer_description, R.string.nav_app_bar_navigate_up_description);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        final TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
        final CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);
        final ProgressBar progressBar = headerView.findViewById(R.id.progress_user);

      if (!type.equals("Admin")){

         new Hilo2().start();




          auth = FirebaseAuth.getInstance();
          String mAuth = auth.getCurrentUser().getUid();
         // userNameTextView.setText(Prevalent.currentonlineUser.getName());
//          Picasso.get().load(Prevalent.currentonlineUser.getImage()).placeholder(R.drawable.profile).into(profileImageView);

          DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Users");
          updateToken(FirebaseInstanceId.getInstance().getToken());
          productRef.child(mAuth).addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                  if (dataSnapshot.exists()){

                      Users  users = dataSnapshot.getValue(Users.class);


                      userNameTextView.setText(users.getName());
                      Picasso.get().load(users.getImage()).placeholder(R.drawable.profile).into(profileImageView, new Callback() {
                          @Override
                          public void onSuccess() {
                              progressBar.setVisibility(View.GONE);
                          }

                          @Override
                          public void onError(Exception e) {
                            Picasso.get().load(R.mipmap.ic_launcher).into(profileImageView);
                          }
                      });


                  }
              }

              @Override
              public void onCancelled(DatabaseError databaseError) {

              }
          });





          linear_laptos.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent intent1 = new Intent(HomeActivity.this,CategoriaActivity.class);
                  intent1.putExtra("category","Laptops");
                  startActivity(intent1);

              }
          });
          linear_audiculares.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent intent1 = new Intent(HomeActivity.this,CategoriaActivity.class);
                  intent1.putExtra("category","HeadPhones HandFree");
                  startActivity(intent1);
              }
          });
          linear_zapatos.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent intent1 = new Intent(HomeActivity.this,CategoriaActivity.class);
                  intent1.putExtra("category","Shoes");
                  startActivity(intent1);
              }
          });
          linear_telefono.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent intent1 = new Intent(HomeActivity.this,CategoriaActivity.class);
                  intent1.putExtra("category","Mobile Phones");
                  startActivity(intent1);
              }
          });

          linear_todo_categorias.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  vertodascategorias();
              }
          });




      }

//        recyclerView = findViewById(R.id.recycle_menu);
//        recyclerView.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);

        } else {
            // No hay conexión a Internet en este momento
          //  Toast.makeText(this,"NO AY CONEXCION A INTERNET PORFAVOR REVISA TU CONEXCION",Toast.LENGTH_LONG).show();
            erroInternet.setContentView(R.layout.dialog_internet);
            TextView message =erroInternet.findViewById(R.id.texto_dialogo_internet);
            Button button = erroInternet.findViewById(R.id.btn_resets);
            LottieAnimationView btn_close = erroInternet.findViewById(R.id.btn_close);


            btn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    erroInternet.dismiss();
                }
            });


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        erroInternet.dismiss();
                        HomeActivity.super.recreate();

                }
            });



            erroInternet.show();
        }

    }

    private void vertodascategorias() {
        dialog.setContentView(R.layout.todaslascateforiasitem);
        TextView camisetas,texto_camisetas_deportivas,
                texto_vestido_mujer,texto_sueteres,texto_lentes,texto_sombreros,
                texto_carteras_modernas,texto_relojes,texto_juegos,texto_todas;

        LinearLayout linear_camisetas,linear_camisas_deportivas,linear_vesito_mujer,linear_sueteres,linear_lentes,
                linear_sombreros,linear_carteras,linear_relojes,linear_juegos;

        linear_camisetas = dialog.findViewById(R.id.linear_camisetas);
        linear_camisas_deportivas = dialog.findViewById(R.id.linear_camisas_deportivas);
        linear_vesito_mujer = dialog.findViewById(R.id.linear_vesito_mujer);
        linear_sueteres = dialog.findViewById(R.id.linear_sueteres);
        linear_lentes  = dialog.findViewById(R.id.linear_lentes);
        linear_sombreros = dialog.findViewById(R.id.linear_sombreros);
        linear_carteras = dialog.findViewById(R.id.linear_carteras);
        linear_relojes = dialog.findViewById(R.id.linear_relojes);
        linear_juegos = dialog.findViewById(R.id.linear_juegos);

        linear_camisetas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String  categoria ="tShirts";
                iraCategorias(categoria);
            }
        });
        linear_camisas_deportivas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String categoria = "SportsTShirts";
              iraCategorias(categoria);
            }
        });
        linear_vesito_mujer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoria = "Female Dresses";
                iraCategorias(categoria);
            }
        });
        linear_sueteres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoria="sweathers";
                iraCategorias(categoria);
            }
        });
        linear_lentes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoria="Glasses";
                iraCategorias(categoria);
            }
        });
        linear_sombreros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoria="Hats Caps";
                iraCategorias(categoria);
            }
        });
        linear_carteras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String  categoria ="Wallets Bags Purses";
                iraCategorias(categoria);
            }
        });
        linear_relojes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoria = "Watches";
                iraCategorias(categoria);
            }
        });
        linear_juegos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoria = "Juegos";
                iraCategorias(categoria);
            }
        });


        camisetas = dialog.findViewById(R.id.texto_camisetas);
        texto_camisetas_deportivas = dialog.findViewById(R.id.texto_camisetas_deportivas);
        texto_vestido_mujer = dialog.findViewById(R.id.texto_vestido_mujer);
        texto_sueteres = dialog.findViewById(R.id.texto_sueteres);
        texto_lentes = dialog.findViewById(R.id.texto_lentes);
        texto_sombreros = dialog.findViewById(R.id.texto_sombreros);
        texto_carteras_modernas = dialog.findViewById(R.id.texto_carteras_modernas);
        texto_relojes = dialog.findViewById(R.id.texto_relojes);
        texto_juegos  =dialog.findViewById(R.id.texto_juegos);
        texto_todas = dialog.findViewById(R.id.texto_todas);

        texto_todas.setTypeface(font);
        camisetas.setTypeface(font);
        texto_camisetas_deportivas.setTypeface(font);
        texto_vestido_mujer.setTypeface(font);
        texto_sueteres.setTypeface(font);
        texto_lentes.setTypeface(font);
        texto_sombreros.setTypeface(font);
        texto_carteras_modernas.setTypeface(font);
        texto_relojes.setTypeface(font);
        texto_juegos.setTypeface(font);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//recuperamos la vista asignamso un bacjgfroiu
        dialog.show();//mostramos la ventana al precionar el boton
    }

    private void iraCategorias(String categoria) {
        Intent intent = new Intent(HomeActivity.this,CategoriaActivity.class);
        intent.putExtra("category",categoria);
        startActivity(intent);
    }

    private void updateToken(String  token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);

        reference.child(auth.getUid()).setValue(token1);
    }
//    @Override
//    protected void onStart()
//    {
//        super.onStart();
////esa liena de codigo tenemso que ver si se pone en el motor de vusqueda en la acitvbity search
//        FirebaseRecyclerOptions<Products> options =
//                new FirebaseRecyclerOptions.Builder<Products>()
//                        .setQuery(ProductsRef.orderByChild("productState").equalTo("Aprovado"), Products.class)
//                        .build();
//
//
//        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
//                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
//                    @Override
//                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model)
//                    {
//                        holder.txtprdouctName.setText(model.getPname());
//                        holder.txtProductDescription.setText(model.getDescription());
//                        holder.txtProductPrice.setText("Precio = " + model.getPrice() + "$");
//                        Picasso.get().load(model.getImage()).into(holder.imageView);
//                        final String usersID = getRef(position).getKey();
//
//                        holder.itemView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                if (type.equals("Admin")){
//                                    Intent intent = new Intent(HomeActivity.this, SellerMaintainProductsActivity.class);
//                                    intent.putExtra("pid",model.getPid());
//                                    startActivity(intent);
//                                }else{
//                                    Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
//                                    intent.putExtra("pid",model.getPid());
//                                    intent.putExtra("uid",model.getUid());
//                                    intent.putExtra("key",usersID);
//                                    intent.putExtra("postid",model.getPostid());
//                                    startActivity(intent);
//                                }
//
//                            }
//                        });
//
//                    }
//
//                    @NonNull
//                    @Override
//                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
//                    {
//                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
//                        ProductViewHolder holder = new ProductViewHolder(view);
//                        return holder;
//                    }
//                };
//        recyclerView.setAdapter(adapter);
//        adapter.startListening();
//    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

//        if (id == R.id.action_settings)
//        {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_cart)
//        {
//          //basicamente si es diferente al administrador pordra usar las confituraciones apra un usuarios
//            if (!type.equals("Admin")) {
//                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
//                startActivity(intent);
//            }
//        }
       // else
            if (id == R.id.nav_orders)
        {
            if (!type.equals("Admin")) {
                //este seria para buscar products
                Intent intent = new Intent(HomeActivity.this, SearchProductsActivity.class);
                startActivity(intent);
            }
        }
        else if (id == R.id.nav_categories)
        {
            if (!type.equals("Admin")) {
                Intent intent = new Intent(HomeActivity.this, SellerHomeActivity.class);
                startActivity(intent);
            }


        }
        else if (id == R.id.nav_settings)
        {
            if (!type.equals("Admin")) {
                Intent intent = new Intent(HomeActivity.this, SettinsActivity.class);
                startActivity(intent);
            }
        }
        else if (id == R.id.nav_logout)
        {
            if (!type.equals("Admin")) {
//                Paper.book().destroy();

                final FirebaseAuth mAuth;
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();

                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

        }
        else if (id ==R.id.nav_contactos){

            if (!type.equals("Admin")){
                Intent contactos = new Intent(HomeActivity.this, VerMensajesEscritos.class);
                startActivity(contactos);
            }
        }
        else if (id==R.id.nav_misventas){
            if (!type.equals("Admin")){
                Intent notification = new Intent(HomeActivity.this, NotificationActivity.class);
                startActivity(notification);
            }
        }else if (id ==R.id.nav_options){
               if (!type.equals("Admin")){
                   Intent notification = new Intent(HomeActivity.this, OptionsActivity.class);
                   startActivity(notification);
               }
            }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStop() {
        // Para evitar una pérdida de memoria en la rotación, asegúrese de llamar a stopAutoCycle () en el control deslizante antes de destruir la actividad o el fragmento
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        //Toast.makeText(this, slider.getBundle().getString("extra") + "", Toast.LENGTH_SHORT).show();
        String pid = slider.getBundle().getString("pid");
        String category = slider.getBundle().getString("uid");
        String postid = slider.getBundle().getString("postid");
        if (type.equals("Admin")) {
            Intent intent = new Intent(HomeActivity.this, SellerMaintainProductsActivity.class);
            intent.putExtra("pid", pid);
            startActivity(intent);
        }else {
            Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
            intent.putExtra("pid", pid);
            intent.putExtra("uid", category);
//        intent.putExtra("key",category);
            intent.putExtra("postid", postid);
            startActivity(intent);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {


    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    class Hilo2 extends Thread{
        @Override
        public void run() {
            super.run();
            HomeActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ProductsRef.orderByChild("productState").equalTo("Aprovado").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                        Products juegos = snapshot.getValue(Products.class);
                                        listUrl.add(juegos.getImage());
                                        listName.add(juegos.getPname());
                                        listpid.add(juegos.getPid());
                                        listcategory.add(juegos.getUid());
                                        listpostid.add(juegos.getPostid());

                                    }

                                    for (int i = 0; i < listUrl.size(); i++) {

                                        TextSliderView sliderView = new TextSliderView(HomeActivity.this);
                                        // si desea mostrar solo la imagen / sin texto descriptivo, use DefaultSliderView en su lugar

                                        // aqui creamos numeros randon asta 7 para que oslo salgan 7 imagenes
                                        Random random = new Random();
                                        int r = random.nextInt(7);
                                        //random.nextInt(listUrl.size());

                                        if (i<r) {
                                            sliderView
                                                    .image(listUrl.get(i))
                                                    .description(listName.get(i))
                                                    .setRequestOption(requestOptions)
                                                    .setProgressBarVisible(true)
                                                    .setOnSliderClickListener(HomeActivity.this);

                                            //agrega tu información extra
                                            // estamos creando bandel para guradad datos para enviar a producdetasilactivuty
                                            sliderView.bundle(new Bundle());
                                            sliderView.getBundle().putString("extra", listName.get(i));
                                            sliderView.getBundle().putString("pid",listpid.get(i));
                                            sliderView.getBundle().putString("uid",listcategory.get(i));
                                            sliderView.getBundle().putString("postid",listpostid.get(i));
                                            mDemoSlider.addSlider(sliderView);
                                        }
                                    }


                                    // establecer animación de transición de control deslizante
                                    // mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
                                    mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);

                                    mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                                    mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                                    mDemoSlider.setDuration(4000);
                                    mDemoSlider.addOnPageChangeListener(HomeActivity.this);
                                    mDemoSlider.stopCyclingWhenTouch(false);

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
            });
        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        final FirebaseAuth mAuth;
//        mAuth = FirebaseAuth.getInstance();
//        mAuth.signOut();
//    }
}