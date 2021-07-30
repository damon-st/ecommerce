package com.damon.ecommerce.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.damon.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SellerLoginActivity extends AppCompatActivity {

    private Button loginSellerBegin;

    private EditText  emailInput,passwordInput;
    private ProgressDialog loaginBar;

    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Sellers");
        passwordInput = findViewById(R.id.seller_password_login);
        emailInput = findViewById(R.id.seller_email_login);

        loginSellerBegin = findViewById(R.id.seller_login_btn);
        loaginBar = new ProgressDialog(this);

        loginSellerBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginSeller();
            }
        });

    }

    private void loginSeller() {

        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();

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

                               Intent intent = new Intent(SellerLoginActivity.this, SellerHomeActivity.class);
                               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                               startActivity(intent);
                               finish();
                           }
                        }
                    });

        }else {
            Toast.makeText(this, "Porfavor Completa los Datos", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( loaginBar!=null && loaginBar.isShowing() ){
            loaginBar.cancel();
        }
    }
}
