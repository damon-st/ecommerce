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

import com.damon.ecommerce.Buyers.MainActivity;
import com.damon.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.HashMap;

public class SellerRegistrationActivity extends AppCompatActivity {

    private Button sellerLoginBegin;

    private EditText nameInput ,phoneInput,emailInput,passwordInput,addressInput;
    private Button registerButton;

    private FirebaseAuth mAuth;

    private ProgressDialog loaginBar;
    public String  link = "https://firebasestorage.googleapis.com/v0/b/ecommerce-fea29.appspot.com/o/Profile%20pictures%2Fprofile.png?alt=media&token=2cce4a2c-0f73-4684-8672-ccff91b08c31";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_registration);

        mAuth = FirebaseAuth.getInstance();



        sellerLoginBegin = findViewById(R.id.seller_already_have_account_btn);
        nameInput = findViewById(R.id.seller_name);
        phoneInput = findViewById(R.id.seller_phone);
        emailInput = findViewById(R.id.seller_email);
        passwordInput = findViewById(R.id.seller_password);
        addressInput = findViewById(R.id.seller_address);
        registerButton = findViewById(R.id.seller_register_btn);
        loaginBar = new ProgressDialog(this);


        sellerLoginBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerRegistrationActivity.this,SellerLoginActivity.class);
                startActivity(intent);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerSeller();
            }
        });


    }

    private void registerSeller() {

        final String name = nameInput.getText().toString();
        final String phone = phoneInput.getText().toString();
        final String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        final String address = addressInput.getText().toString();

        if (!name.equals("")&&!phone.equals("")&&!email.equals("")&&!password.equals("")&&!address.equals("")){

            loaginBar.setTitle("Creando Vendedor  Cuenta");
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
                        RequestCreator nt = Picasso.get().load(R.drawable.profile);
                        HashMap<String,Object> sellerMap = new HashMap<>();
                        sellerMap.put("uid",sid);//este es el ai del vendedor
                        sellerMap.put("phone",phone);
                        sellerMap.put("email",email);
                        sellerMap.put("address",address);
                        sellerMap.put("image",link);
                        sellerMap.put("name",name);

                        rootRef.child("Sellers").child(sid).updateChildren(sellerMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        loaginBar.dismiss();
                                        Toast.makeText(SellerRegistrationActivity.this, "Tu registro Fue correctamente", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SellerRegistrationActivity.this, SellerHomeActivity.class);
                                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                    }
                }
            });

        }else {
            Toast.makeText(this, "Por favor Debes llenar todos los Datos", Toast.LENGTH_SHORT).show();
        }

    }

}
