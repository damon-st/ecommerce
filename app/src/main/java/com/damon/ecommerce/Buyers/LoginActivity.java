package com.damon.ecommerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.damon.ecommerce.Admin.AdminHomeActivity;
import com.damon.ecommerce.Sellers.SellerHomeActivity;
import com.damon.ecommerce.Sellers.SellerLoginActivity;
import com.damon.ecommerce.Sellers.SellerProductCategoryActivity;
import com.damon.ecommerce.Model.Users;
import com.damon.ecommerce.Prevalent.Prevalent;
import com.damon.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText InputNumber,InputPassword;
    private Button LoginButton;

    private ProgressDialog loaginBar;

    private CheckBox cnkBoxRemeberme;
    private boolean seleccion = false;

    private String parentDbName = "Users";
    private TextView AdminLink, NotAdminLink, ForgetPassword;
    private FirebaseAuth mAuth;
    private String i = "a";
    private Dialog dialog;
    Typeface font;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dialog = new Dialog(this);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
         font = Typeface.createFromAsset(getAssets(),"fonts/ArimaKoshi-Medium.otf");
        AdminLink = findViewById(R.id.admin_panel_link);
        NotAdminLink = findViewById(R.id.not_admin_panel_link);

       ForgetPassword = findViewById(R.id.forgert_password_link);
        InputNumber = findViewById(R.id.login_phone_number_input);
        InputPassword = findViewById(R.id.login_phone_password_input);
        LoginButton = findViewById(R.id.login_btn);
        //estas dos lineas son de el check bos aque inicializar el paperl con el contexto
        cnkBoxRemeberme = findViewById(R.id.remember_me_chkb);
        Paper.init(this);

        mAuth = FirebaseAuth.getInstance();

        loaginBar = new ProgressDialog(this);


        InputNumber.setTypeface(font);
        InputPassword.setTypeface(font);
        AdminLink.setTypeface(font);
        NotAdminLink.setTypeface(font);
        ForgetPassword.setTypeface(font);
        LoginButton.setTypeface(font);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // LoginUsers();

                if (networkInfo!=null&&networkInfo.isConnected()){

                    if (parentDbName.equals("Users")){
                        loginSeller();
                    }else {
                        admin();
                    }

                }else{
                    Toast.makeText(LoginActivity.this, "NO DISPONES DE CONEXCION A INTERNET PORFAVOR INTENTA MAS TARDE", Toast.LENGTH_SHORT).show();
                }


            }
        });
       //esto ara inicio con la base de datos que creamos llamada Admins en Firebase Manualmente
        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginButton.setText("Inicio Administrador");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });
        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Inicio Seccion");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";

            }
        });
        ForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPassword.class);
//                intent.putExtra("check","login");
                startActivity(intent);

            }
        });


    }

//    private void LoginUsers() {
//
//        String phone = InputNumber.getText().toString();
//        String password = InputPassword.getText().toString();
//
//        if (TextUtils.isEmpty(phone)){
//            Toast.makeText(this, "Ingresa Tu numero Celular", Toast.LENGTH_SHORT).show();
//        } else if (TextUtils.isEmpty(password)) {
//            Toast.makeText(this, "Ingresa Tu contraseña", Toast.LENGTH_SHORT).show();
//        }
//        else {
//
//            loaginBar.setTitle("Iniciando Cuenta");
//            loaginBar.setMessage("Porfavor Espera,revisando datos");
//            loaginBar.setCanceledOnTouchOutside(false);
//            loaginBar.show();
//
//            AllowAccesToAccoun(phone,password);
//
//
//
//        }
//    }

//    private void AllowAccesToAccoun(final String phone, final String password) {
//
//        if (cnkBoxRemeberme.isChecked()){
//            Paper.book().write(Prevalent.UserPhonKey,phone);
//            Paper.book().write(Prevalent.UserPasswordKey,password);
//        }
//
//        //metodo para inciar sesion
//        //aqui utilizamos la key que sera el phone
//        final DatabaseReference RootRef ;
//        RootRef = FirebaseDatabase.getInstance().getReference();
//       //primero creamos un evento
//        //y con datasnapshot vuscamos la llame Users y phone
//        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.child(parentDbName).child(phone).exists()){
//                    //Utilizamos la clase creada de Users
//                    Users userData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);
//                    if(userData.getPhone().equals(phone)){
//                        if(userData.getPassword().equals(password)){
//
//                            if (parentDbName.equals("Admins")){
//
//                                Toast.makeText(LoginActivity.this, "Bienvenido Administrador", Toast.LENGTH_SHORT).show();
//                                loaginBar.dismiss();
//
//                                Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
//                                startActivity(intent);
//                            }
//                            else if (parentDbName.equals("Users")){
//
//
//
//                                Toast.makeText(LoginActivity.this, "Inicio Correcto", Toast.LENGTH_SHORT).show();
//                                loaginBar.dismiss();
//
//                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                                Prevalent.currentonlineUser = userData;
//                                startActivity(intent);
//
//                            }
//                        } else {
//                            Toast.makeText(LoginActivity.this, "Contraseña Incorrecta", Toast.LENGTH_SHORT).show();
//                            loaginBar.dismiss();
//                        }
//                    }
//
//                }else {
//                    Toast.makeText(LoginActivity.this, "Cuenta con"+phone+"no existe", Toast.LENGTH_SHORT).show();
//                    loaginBar.dismiss();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }




    private void updateUserStates(String state){
        //aqui sera para poner al ora aslo ususario su iltimas conexcion
        String saveCutrrentTime ,saveCurrentData;
        DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();//creanos la conexion con la base de datos
        String currentUserID;
        Calendar calendar = Calendar.getInstance();
        //aqui estamos creando faroma del calendario dia mes año
        SimpleDateFormat currentData  = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentData = currentData.format(calendar.getTime());
        //aqui estamos creando forma hora minuto y segundo
        SimpleDateFormat currentTime  = new SimpleDateFormat("hh:mm a");
        saveCutrrentTime = currentTime.format(calendar.getTime());

        //aqui creamos una lista con la llave y objeto para mandar ala base de datos Firebase
        HashMap<String , Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time",saveCutrrentTime);
        onlineStateMap.put("date",saveCurrentData);
        onlineStateMap.put("state",state);

        currentUserID = mAuth.getCurrentUser().getUid();
        //aqui creamos la referencia con la vase de datos y nombre dela nueva tabla o dato para labase de datos
        RootRef.child("Users").child(currentUserID).child("userSate")
                .updateChildren(onlineStateMap);



    }


    private void loginSeller() {

        final String email = InputNumber.getText().toString();
        final String password = InputPassword.getText().toString();

        if (!email.equals("")&&!password.equals("")) {

            loaginBar.setTitle("Iniciando Sesion");
            loaginBar.setMessage("Porfavor Espera,revisando datos....");
            loaginBar.setCanceledOnTouchOutside(false);
            loaginBar.show();

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){

                                loaginBar.dismiss();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            }else {
                                String  message = task.getException().getMessage().toString();
                                System.out.println(message);
                                if(message.equals("The password is invalid or the user does not have a password.")){
//                                    Toast.makeText(LoginActivity.this, "Contraseña Incorrecta Intenta Nueva Mente", Toast.LENGTH_SHORT).show();
                                 //   mostrarDialogo("La Contraseña Ingresada es Incorrecta Intenta Nueva Mente");
                                    MostrarDialogoAnimacion("La Contraseña Ingresada es Incorrecta Intenta Nueva Mente");
                                    loaginBar.dismiss();
                                }else if (message.equals("There is no user record corresponding to this identifier. The user may have been deleted.")){
//                                    Toast.makeText(LoginActivity.this, "El Correo Ingresado No existe en Nuestra base de datos", Toast.LENGTH_SHORT).show();
                                   // mostrarDialogo("El correo Ingresado No existe en Nuestra Base de Datos Intenta Nueva Mente");
                                    MostrarDialogoAnimacion("El correo Ingresado No existe en Nuestra Base de Datos Intenta Nueva Mente");
                                    loaginBar.dismiss();
                                }else if (message.equals("We have blocked all requests from this device due to unusual activity. Try again later. [ Too many unsuccessful login attempts. Please try again later. ]")){
//                                    Toast.makeText(LoginActivity.this, "Contraseña Incorrecta o Correo"+message, Toast.LENGTH_SHORT).show();
                                 //   mostrarDialogo("Demasiados intentos de inicio de sesión fallidos. Por favor, inténtelo de nuevo más tarde.");
                                    MostrarDialogoAnimacion("Demasiados intentos de inicio de sesión fallidos. Por favor, inténtelo de nuevo más tarde.");
                                    loaginBar.dismiss();
                                }else if (message.equals("The email address is badly formatted.")){
                                 //   mostrarDialogo("Error:"+message);
                                    MostrarDialogoAnimacion("Por favor intruce un correo Electronico valido ");
                                    loaginBar.dismiss();
                                }else {
                                    MostrarDialogoAnimacion("Error: " +message);
                                    loaginBar.dismiss();
                                }

                                System.out.println(message);

                            }
                        }
                    });

        }else {
            Toast.makeText(this, "Porfavor Completa los Datos", Toast.LENGTH_SHORT).show();
        }
    }

    private void admin(){

        final String phone = InputNumber.getText().toString();
        final String contraseña = InputPassword.getText().toString();

        if (!TextUtils.isEmpty(phone)&&!TextUtils.isEmpty(contraseña)) {
            //metodo para inciar sesion
//        //aqui utilizamos la key que sera el phone
            final DatabaseReference RootRef;
            RootRef = FirebaseDatabase.getInstance().getReference();
            //primero creamos un evento
            //y con datasnapshot vuscamos la llame Users y phone
            RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(parentDbName).child(phone).exists()) {
                        //Utilizamos la clase creada de Users
                        Users userData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);
                        if (userData.getPhone().equals(phone)) {
                            if (userData.getPassword().equals(contraseña)) {

                                if (parentDbName.equals("Admins")) {

                                    Toast.makeText(LoginActivity.this, "Bienvenido Administrador", Toast.LENGTH_SHORT).show();
                                    loaginBar.dismiss();

                                    Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                                    startActivity(intent);
                                }
//                            else if (parentDbName.equals("Users")){
//
//
//
//                                Toast.makeText(LoginActivity.this, "Inicio Correcto", Toast.LENGTH_SHORT).show();
//                                loaginBar.dismiss();
//
//                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                                Prevalent.currentonlineUser = userData;
//                                startActivity(intent);
//
//                            }
                            } else {
                                Toast.makeText(LoginActivity.this, "Contraseña Incorrecta", Toast.LENGTH_SHORT).show();
                                loaginBar.dismiss();
                            }
                        }

                    } else {
                        Toast.makeText(LoginActivity.this, "Cuenta con" + phone + "no existe", Toast.LENGTH_SHORT).show();
                        loaginBar.dismiss();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else {
            Toast.makeText(this, "Escribe el usuario administrador", Toast.LENGTH_SHORT).show();
        }

    }


//    private  void  admin(){
//        final String email = InputNumber.getText().toString();
//        final String password = InputPassword.getText().toString();
//        loaginBar.setTitle("Iniciando Sesion");
//        loaginBar.setMessage("Porfavor Espera,revisando datos....");
//        loaginBar.setCanceledOnTouchOutside(false);
//        loaginBar.show();
//        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful()){
//                    loaginBar.dismiss();
//                    startActivity(new Intent(LoginActivity.this,AdminHomeActivity.class));
//                    finish();
//                }else {
//                    loaginBar.dismiss();
//                    String  error = task.getException().getMessage();
//                    System.out.println(error);
//                }
//            }
//        });
//    }


    private  void mostrarDialogo(String  message){
        dialog.setContentView(R.layout.dialogoalerta);
        TextView titulo = dialog.findViewById(R.id.texto_dialogo_uno);
        TextView messages = dialog.findViewById(R.id.texto_dialogo_dos);
        ImageView imagenCLose  = dialog.findViewById(R.id.boton_serrar_dialogo);
        ImageView imageError = dialog.findViewById(R.id.imagenerror);
        imageError.setImageResource(R.drawable.ic_report);
        Button button = dialog.findViewById(R.id.boton_mensaje_dialogo);
        button.setText("Cerrar");

        titulo.setText("Error");
        messages.setText(message);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        imagenCLose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private  void MostrarDialogoAnimacion(String  meesages){
        dialog.setContentView(R.layout.dialogo_animation);
        LottieAnimationView erroImage = dialog.findViewById(R.id.animationView);
        LottieAnimationView btn_close = dialog.findViewById(R.id.boton_close);
        TextView dialogoMessage = dialog.findViewById(R.id.texto_dialogo);

        dialogoMessage.setTypeface(font);

        erroImage.setAnimation(R.raw.emaildos);

        dialogoMessage.setText(meesages);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
