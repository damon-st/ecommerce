package com.damon.ecommerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import com.damon.ecommerce.R;
import com.damon.ecommerce.Sellers.SellerHomeActivity;
import com.damon.ecommerce.Sellers.SellerRegistrationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
//clase encaragada de crear usuario


    private Button CreateAccountButton;
    private EditText InputName,InputNumber,InputPasswor,InputEmail,InputAddress;

    private ProgressDialog loaginBar;
    public String  link = "https://firebasestorage.googleapis.com/v0/b/ecommerce-fea29.appspot.com/o/Profile%20pictures%2Fprofile.png?alt=media&token=2cce4a2c-0f73-4684-8672-ccff91b08c31";


    private FirebaseAuth mAuth;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dialog = new Dialog(this);

        CreateAccountButton = findViewById(R.id.register_btn);
        InputName = findViewById(R.id.register_user_name_input);
        InputNumber = findViewById(R.id.register_phone_number_input);
        InputPasswor = findViewById(R.id.register_phone_password_input);
        InputEmail = findViewById(R.id.register_email_input);
        loaginBar = new ProgressDialog(this);
        InputAddress = findViewById(R.id.register_address_input);

        mAuth = FirebaseAuth.getInstance();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();



        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (networkInfo != null && networkInfo.isConnected()) {
                    CreateAccount();
                }else {
                    Toast.makeText(RegisterActivity.this,"NO TIENES CONEXCION A INTERNET INTENTA MAS TARDE",Toast.LENGTH_LONG).show();
                }
            }
        });




    }

    private void CreateAccount() {
////       Este es otro metodo de registro
//        String name = InputName.getText().toString();
//        String phone = InputNumber.getText().toString();
//        String password = InputPasswor.getText().toString();
//
//        if (TextUtils.isEmpty(name)){
//            Toast.makeText(this, "Ingresa Tu nombre", Toast.LENGTH_SHORT).show();
//        }else if (TextUtils.isEmpty(phone)){
//            Toast.makeText(this, "Ingresa Tu numero Celular", Toast.LENGTH_SHORT).show();
//        } else if (TextUtils.isEmpty(password)) {
//            Toast.makeText(this, "Ingresa Tu contraseña", Toast.LENGTH_SHORT).show();
//        }
//        else {
//
//            loaginBar.setTitle("Crear Cuenta");
//            loaginBar.setMessage("Porfavor Espera,revisando datos");
//            loaginBar.setCanceledOnTouchOutside(false);
//            loaginBar.show();
//
//            ValidatephtoneNumber(name,phone,password);
//
//
//        }


        {

             final String name = InputName.getText().toString();
             final String phone = InputNumber.getText().toString();
             final String email = InputEmail.getText().toString().trim();
            final String password = InputPasswor.getText().toString();
            final  String address   = InputAddress.getText().toString();
            //final String address = addressInput.getText().toString();

            if (!name.equals("")&&!phone.equals("")&&!email.equals("")&&!password.equals("")&&!address.equals("")){

                loaginBar.setTitle("Creando tu Cuenta");
                loaginBar.setMessage("Porfavor Espera,revisando datos");
                loaginBar.setCanceledOnTouchOutside(false);
                loaginBar.show();

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            final DatabaseReference rootRef ;
                            rootRef = FirebaseDatabase.getInstance().getReference();
                            String  sid = mAuth.getCurrentUser().getUid();

                            HashMap<String,Object> userdataMap = new HashMap<>();
                                    userdataMap.put("phone",phone);
                                    userdataMap.put("name",name);
                                    userdataMap.put("email",email);
                                    userdataMap.put("image",link);
                                    userdataMap.put("address",address);
                                    userdataMap.put("uid",sid);
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();
                            String currentUserId = mAuth.getCurrentUser().getUid();
                            rootRef.child("Users").child(currentUserId).updateChildren(userdataMap);

                            rootRef.child("Users").child(currentUserId).child("device_token")
                                    .setValue(deviceToken);

                            SendToLoginActivity();
                            Toast.makeText(RegisterActivity.this, "Cuenta Creada Coreectamente", Toast.LENGTH_SHORT).show();
                            loaginBar.dismiss();

                        }else {
                            String messenge = task.getException().toString();

                            System.out.println(messenge);

                            if(messenge.equals("com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.")){
                                //Toast.makeText(RegisterActivity.this, "Error: El Correo ya esta en uso intenta con otro correo", Toast.LENGTH_LONG).show();
                                MostrarDialogo("El correo ingresado ya existe por favor intenta con otro correo");
                                loaginBar.dismiss();//para que se pueda mostrar
                            }else if (messenge.equals("com.google.firebase.FirebaseNetworkException: A network error (such as timeout, interrupted connection or unreachable host) has occurred.")){
                               // Toast.makeText(RegisterActivity.this, "Error Al conectar con el servidor comprueba tu conexciona Internet", Toast.LENGTH_LONG).show();
                                MostrarDialogo("Error Al conectar con el servidor comprueba tu conexciona Internet");
                                loaginBar.dismiss();//para que se pueda mostrar
                            }else if (messenge.equals("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted.")){
//                                Toast.makeText(RegisterActivity.this, "Error:"+messenge, Toast.LENGTH_LONG).show();
                                MostrarDialogo("Por Favor Ingresa Un correo Electronico Valido");
                                loaginBar.dismiss();//para que se pueda mostrar
                            }else if (messenge.equals("com.google.firebase.auth.FirebaseAuthWeakPasswordException: The given password is invalid. [ Password should be at least 6 characters ]")){
                                MostrarDialogo("La contraseña deve tener minimo 6 caracteres escribe una contraseña segura para ti ");
                                loaginBar.dismiss();//para que se pueda mostrar
                            }else {
                                MostrarDialogo(messenge);
                                loaginBar.dismiss();//para que se pueda mostrar
                            }
                        }
                    }
                });

            }else {
                Toast.makeText(this, "Por favor Debes llenar todos los Datos", Toast.LENGTH_SHORT).show();
            }

        }





    }

    private void SendToLoginActivity(){
        //metodo spara ir a crear ala activity login en caso de que si tenga una cuenta
        startActivity(new Intent(RegisterActivity.this,HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }

    private void MostrarDialogo(String  message){
        dialog.setContentView(R.layout.dialogoalerta);
        TextView titulo = dialog.findViewById(R.id.texto_dialogo_uno);
        TextView mensaje = dialog.findViewById(R.id.texto_dialogo_dos);
        Button boton = dialog.findViewById(R.id.boton_mensaje_dialogo);
        ImageView imageClose = dialog.findViewById(R.id.boton_serrar_dialogo);

        titulo.setText("Error");
        mensaje.setText(message);
        boton.setText("Cerrar");
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    //este es otro metodo para crear la cuenta

//    private void ValidatephtoneNumber(final String name, final String phone, final String password) {
//        //aqui crearemos la cuenta
//        final DatabaseReference RootRef ;
//        RootRef = FirebaseDatabase.getInstance().getReference();
//
//        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                if (!dataSnapshot.child("Users").child(phone).exists()){
//
//                    HashMap<String,Object> userdataMap = new HashMap<>();
//                    userdataMap.put("phone",phone);
//                    userdataMap.put("password",password);
//                    userdataMap.put("name",name);
//
//                    RootRef.child("Users").child(phone).updateChildren(userdataMap)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()){
//                                        Toast.makeText(RegisterActivity.this, "Creado Correctamente", Toast.LENGTH_SHORT).show();
//                                        loaginBar.dismiss();
//
//                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                                        startActivity(intent);
//
//                                    }else {
//                                        loaginBar.dismiss();
//                                        Toast.makeText(RegisterActivity.this, "Error Intenta Denuevo", Toast.LENGTH_SHORT).show();
//
//                                    }
//                                }
//                            });
//
//                }else {
//                    //aqui creamos al condicion que si existe ya ese numero celualr
//                    Toast.makeText(RegisterActivity.this, "Este"+phone+"ya existe", Toast.LENGTH_SHORT).show();
//                    loaginBar.dismiss();
//                    Toast.makeText(RegisterActivity.this, "Utiliza otro Numero", Toast.LENGTH_SHORT).show();
//
//                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
//                    startActivity(intent);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//    }
}
