package com.damon.ecommerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.ecommerce.Prevalent.Prevalent;
import com.damon.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResestPasswordActivity extends AppCompatActivity {

    private String check = "";
    private TextView pageTitle,titleQuestion;
    private EditText phoneNumber,question1,question2;
    private Button verifyButton;
    private String auth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resest_password);


        check = getIntent().getStringExtra("check");
        pageTitle = findViewById(R.id.page_tittle);
        titleQuestion = findViewById(R.id.title_question);
        phoneNumber = findViewById(R.id.find_phone_number);
        question1 = findViewById(R.id.question_1);
        question2 = findViewById(R.id.question_2);
        verifyButton = findViewById(R.id.verify_btn);





    }

    @Override
    protected void onStart() {
        super.onStart();
        phoneNumber.setVisibility(View.GONE);


        if (check.equals("settings")){
            displayPreviousAnsers();
            pageTitle.setText("Preguntas para tu seguridad");

            titleQuestion.setText("Por favor, Responde las siguientes Pregustas de Seguridad?");
            verifyButton.setText("Actualizar");
            verifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setAnswers();

                }
            });

        }else if (check.equals("login")){
            phoneNumber.setVisibility(View.VISIBLE);

            verifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    verifyUser();
                }
            });
        }
    }

    private void setAnswers(){
        //metodo para asiganar preguntas de seguridad

        String quiestionuno ,questiondos;
        quiestionuno = question1.getText().toString().toLowerCase();
        questiondos = question2.getText().toString().toLowerCase();
        if (questiondos.equals("")&&quiestionuno.equals("")) {
            Toast.makeText(ResestPasswordActivity.this, "Por favor escribe tu respuesta", Toast.LENGTH_SHORT).show();
        }else {
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference().child("Users")
                    .child("email");

            HashMap<String,Object> userdataMap = new HashMap<>();
            userdataMap.put("answer1",quiestionuno);
            userdataMap.put("answer2",questiondos);

            ref.child("Security Questions").updateChildren(userdataMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ResestPasswordActivity.this, "Tus respuestas de seguridad fueron guardadas correctamente", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent( ResestPasswordActivity.this, HomeActivity.class );
                                startActivity(intent);
                                finish();
                            }
                        }
                    });

        }
    }
    private void displayPreviousAnsers(){

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference().child("Users")
                .child("email");

        ref.child("Security Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              if (dataSnapshot.exists()){
                  String ans2 = dataSnapshot.child("answer2").getValue().toString();
                  String ans1 = dataSnapshot.child("answer1").getValue().toString();

                  question1.setText(ans1);
                  question2.setText(ans2);
              }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void verifyUser() {

        final String phone = phoneNumber.getText().toString();
        final String answer1 = question1.getText().toString().toLowerCase();
        final String answer2 = question2.getText().toString().toLowerCase();

        if (!phone.equals("")&&!answer1.equals("")&&!answer2.equals("")) {

            final DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference().child("Users").child("email");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("Users").hasChild("email")) {
                        String mPhone = dataSnapshot.child("phone").getValue().toString();
                        if (dataSnapshot.hasChild("Security Questions")) {

                            String ans2 = dataSnapshot.child("Security Questions").child("answer2").getValue().toString();
                            String ans1 = dataSnapshot.child("Security Questions").child("answer1").getValue().toString();

                            if (!ans1.equals(answer1)) {
                                Toast.makeText(ResestPasswordActivity.this, "Primera respuesta Incorrecta", Toast.LENGTH_SHORT).show();
                            } else if (!ans2.equals(answer2)) {
                                Toast.makeText(ResestPasswordActivity.this, "Segunda respuesta Incorrecta", Toast.LENGTH_SHORT).show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ResestPasswordActivity.this);
                                builder.setTitle("Nueva Contraseña");

                                final EditText newPassword = new EditText(ResestPasswordActivity.this);
                                newPassword.setHint("Escribe tu contraseña aqui");
                                builder.setView(newPassword);

                                builder.setPositiveButton("Cambiar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        if (!newPassword.getText().toString().equals("")) {
                                            ref.child("password")
                                                    .setValue(newPassword.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(ResestPasswordActivity.this, "Contraseña Cambiada correctamente", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(ResestPasswordActivity.this, MainActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                builder.show();
                            }
                        } else {
                            Toast.makeText(ResestPasswordActivity.this, "Tu no tienes preguntas de seguridad", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(ResestPasswordActivity.this, "El numero no existe", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else {
            Toast.makeText(this, "Porfavor completa las preguntas", Toast.LENGTH_SHORT).show();
        }
    }
}
