package com.damon.ecommerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.damon.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    EditText send_email;
    Button btn_reset;

    FirebaseAuth firebaseAuth;

    private Dialog dialog;
    private Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        dialog = new Dialog(this);

        font = Typeface.createFromAsset(getAssets(),"fonts/font.otf");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reseto de Contraseña");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        send_email = findViewById(R.id.send_email);
        btn_reset = findViewById(R.id.btn_reset);

        firebaseAuth = FirebaseAuth.getInstance();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = send_email.getText().toString();

                if (email.equals("")){
                    Toast.makeText(ResetPassword.this, "Escribe tu correo", Toast.LENGTH_SHORT).show();
                }else {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                               //Toast.makeText(ResetPassword.this, "Porfavor Revisa tu correo se a enviado un link de Recuperacion tiempo de espera de llegada hasta 5 minutos", Toast.LENGTH_LONG).show();
//                                MostrarDialogo("Se a enviado a tu correo electronico un Link para restablecer tu contraseña por favor espera la llegada del correo nota: " +
//                                        "Si no te llega ningun correo por favor Vuelve a Reintentar");
//                                startActivity(new Intent(ResetPassword.this, LoginActivity.class));
//                                finish();
                                MostrarDialogAnimation("Se a enviado un link a su correo Electronico tiempo de Demora de llegada 5 minutos",true);
                            }else {
                                String  error = task.getException().getMessage();
                                if (error.equals("There is no user record corresponding to this identifier. The user may have been deleted.")){
                                  //  MostrarDialogo("El correo Ingresado No existe en la base de Datos lo cual no es valido para el envio de recuperar contraseña");
                                    MostrarDialogAnimation("El correo Ingresado No existe en la base de Datos lo cual no es valido para el envio de recuperar contraseña",false);
                                }else if (error.equals("The email address is badly formatted.")){
                                  //  MostrarDialogo(error);
                                    MostrarDialogAnimation("Por Favor Ingresa Un correo Electronico Valido ejm: ejemplo@gmail.com",false);
                                }else {
                                    MostrarDialogAnimation(error,false);
                                }
                                System.out.println(error);
                            }
                        }
                    });
                }
            }
        });
    }

    public void  MostrarDialogo(String message){
        dialog.setContentView(R.layout.dialogoalerta);
        TextView titulo = dialog.findViewById(R.id.texto_dialogo_uno);
        TextView mensaje = dialog.findViewById(R.id.texto_dialogo_dos);
        ImageView imageClose = dialog.findViewById(R.id.boton_serrar_dialogo);
        Button btnCLose = dialog.findViewById(R.id.boton_mensaje_dialogo);

        btnCLose.setText("Cerrar");
        titulo.setText("Error");
        mensaje.setText(message);
        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnCLose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void MostrarDialogAnimation(String  messsage, final boolean balor){
        dialog.setContentView(R.layout.dialogo_animation);
        TextView textView = dialog.findViewById(R.id.texto_dialogo);
        textView.setTypeface(font);
        textView.setText(messsage);
        LottieAnimationView btn_close = dialog.findViewById(R.id.boton_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (balor){
                    startActivity(new Intent(ResetPassword.this,LoginActivity.class));
                    finish();
                }else {
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
