package com.damon.ecommerce.Buyers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.ecommerce.Model.Users;
import com.damon.ecommerce.Prevalent.Prevalent;
import com.damon.ecommerce.R;
import com.damon.ecommerce.Sellers.SellerHomeActivity;
import com.damon.ecommerce.Sellers.SellerRegistrationActivity;
import com.damon.ecommerce.slide.SlideModels;
import com.damon.ecommerce.slide.SliderPageAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button joinButton,loginButton;
    private TextView sellerBegin;

    ImageView bgaap,clover;
    LinearLayout textsplash,texhome,menu;
    Animation froombuttons;
    private TextView crear_main,login_main,titlulo,subtitulo;


    private ViewPager slidePager;
    private TabLayout indicator;
    private List<SlideModels> lastSlider; //creamos una lista con los slide


    private ProgressDialog loaginBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Typeface font = Typeface.createFromAsset(getAssets(),"fonts/font.otf");

        joinButton = findViewById(R.id.main_join_now_btn);
       loginButton = findViewById(R.id.main_login_btn);
//      //  sellerBegin = findViewById(R.id.seller_begin);
//        crear_main = findViewById(R.id.crear_main);
//        login_main = findViewById(R.id.login_main);
//        titlulo = findViewById(R.id.titlulo);
//        subtitulo = findViewById(R.id.subtitulo);



//        crear_main.setTypeface(font);
//        login_main.setTypeface(font);
//        titlulo.setTypeface(font);
//        subtitulo.setTypeface(font);


        loaginBar = new ProgressDialog(this);


//        sellerBegin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, SellerRegistrationActivity.class);
//                startActivity(intent);
//            }
//        });


        froombuttons = AnimationUtils.loadAnimation(this,R.anim.frombuttons);

//        bgaap = findViewById(R.id.bgapp);
//        clover = findViewById(R.id.clover);
//        textsplash = findViewById(R.id.textsplash);
//        texhome = findViewById(R.id.texthome);
//        menu= findViewById(R.id.menus);




//        bgaap.animate().translationY(-1000).setDuration(800).setStartDelay(300);
//        clover.animate().alpha(0).setDuration(800).setStartDelay(300);
//        textsplash.animate().translationY(140).alpha(0).setDuration(800).setStartDelay(300);

//        texhome.startAnimation(froombuttons);
//        menu.startAnimation(froombuttons);


        slidePager = findViewById(R.id.slider_pager);
        indicator = findViewById(R.id.indicator);

//        lastSlider = new ArrayList<>();
//        lastSlider.add(new SlideModels(R.raw.cart_female, "Market \n Puedes comprar los productos que veas publicados"));
//        lastSlider.add(new SlideModels(R.raw.vender, "Market \n Puedes Vender los productos tuyos"));
//        lastSlider.add(new SlideModels(R.raw.usermessage,"Market \n Puede Crear Un chat con el vendedor de un Producto que te guste"));
//        lastSlider.add(new SlideModels(R.raw.proteccion,"Martet \n Estamos compremetidos con tigo y tu seguridad puedes reportar cualquier problema que tengas"));
//
//
//        SliderPageAdapter adapter = new SliderPageAdapter(this,lastSlider);
//        slidePager.setAdapter(adapter);
//
//        //asignamos tiempo
//        Timer tiempo = new Timer();
//       // tiempo.scheduleAtFixedRate(new MainActivity.SliderTimer(),2000,4000);
//
//        indicator.setupWithViewPager(slidePager,true);

        new  Hilo1().start();

        Paper.init(this);//primero la inicializamos ya que esta libreria sera quien recuerde users

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1  =  new  Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent1);
            }
        });


         String UserPhoneKey = Paper.book().read(Prevalent.UserPhonKey);
         String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);
        //aqui estamos creando la condicion para ver si esta ono guardada la selecion que iso
        // ya que comparara
         if (UserPhoneKey != ""&& UserPasswordKey != "" ){
             if (!TextUtils.isEmpty(UserPhoneKey)&&!TextUtils.isEmpty(UserPasswordKey)){

                 AllowAccess(UserPhoneKey,UserPasswordKey);
                 loaginBar.setTitle("Ya iniciado sesión");
                 loaginBar.setMessage("Porfavor Espera...");
                 loaginBar.setCanceledOnTouchOutside(false);
                 loaginBar.show();
             }
         }

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        try {
            if (firebaseUser!=null){

                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }else{
            }
        }catch (RuntimeException e){
            e.printStackTrace();
        }

    }

    class  SliderTimer extends TimerTask{

        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (slidePager.getCurrentItem()<lastSlider.size()-1){
                        slidePager.setCurrentItem(slidePager.getCurrentItem()+1);
                    }else {
                        slidePager.setCurrentItem(0);
                    }
                }
            });
        }
    }

    class Hilo1 extends Thread{
        @Override
        public void run() {
            super.run();
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        lastSlider = new ArrayList<>();
                        lastSlider.add(new SlideModels(R.raw.cart_female, "Market \n Puedes comprar los productos que veas publicados"));
                        lastSlider.add(new SlideModels(R.raw.vender, "Market \n Puedes Vender los productos tuyos"));
                        lastSlider.add(new SlideModels(R.raw.usermessage,"Market \n Puede Crear Un chat con el vendedor de un Producto que te guste"));
                        lastSlider.add(new SlideModels(R.raw.proteccion,"Martet \n Estamos compremetidos con tigo y tu seguridad puedes reportar cualquier problema que tengas"));


                        SliderPageAdapter adapter = new SliderPageAdapter(MainActivity.this,lastSlider);
                        slidePager.setAdapter(adapter);

                        //asignamos tiempo
                        Timer tiempo = new Timer();
                        // tiempo.scheduleAtFixedRate(new MainActivity.SliderTimer(),2000,4000);

                        indicator.setupWithViewPager(slidePager,true);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void AllowAccess(final String phone, final String password) {


        final DatabaseReference RootRef ;
        RootRef = FirebaseDatabase.getInstance().getReference();
        //primero creamos un evento
        //y con datasnapshot vuscamos la llame Users y phone
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users").child(phone).exists()){
                    //Utilizamos la clase creada de Users
                    Users userData = dataSnapshot.child("Users").child(phone).getValue(Users.class);
                    if(userData.getPhone().equals(phone)){
                        if(userData.getPassword().equals(password)){

                            Toast.makeText(MainActivity.this, "Iniciando...", Toast.LENGTH_SHORT).show();
                            loaginBar.dismiss();

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            Prevalent.currentonlineUser = userData;//esta es importante aqui porque alamaceda su esta conectado o no
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Contraseña Incorrecta", Toast.LENGTH_SHORT).show();
                            loaginBar.dismiss();
                        }
                    }

                }else {
                    Toast.makeText(MainActivity.this, "Cuenta con"+phone+"no existe", Toast.LENGTH_SHORT).show();
                    loaginBar.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
