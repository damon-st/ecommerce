package com.damon.ecommerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.damon.ecommerce.Prevalent.Prevalent;
import com.damon.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrdenActivity extends AppCompatActivity {

    private EditText nameEditText , phoneEditText , cityEdtitText, addressEditText,emailEditText;
    private Button confirmOrderBtn;
    private String totalAmount ="";

    String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String  currentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_orden);

        totalAmount = getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "Total Preio = $"+ totalAmount, Toast.LENGTH_SHORT).show();

        confirmOrderBtn = findViewById(R.id.shippment_confirmar_btn);
        nameEditText = findViewById(R.id.shippment_name);
        phoneEditText = findViewById(R.id.shippment_phone_number);
        cityEdtitText = findViewById(R.id.shippment_city);
        addressEditText =  findViewById(R.id.shippment_address);
        emailEditText = findViewById(R.id.shippment_email);


        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Check();
            }
        });


    }

    private void Check() {
        if (TextUtils.isEmpty(nameEditText.getText().toString())){
            Toast.makeText(this, "Porfavor Escribe Tu Nombre", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(phoneEditText.getText().toString())){
            Toast.makeText(this, "Porfavor Escribe tu Numero Celular", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(cityEdtitText.getText().toString())){
            Toast.makeText(this, "Porfavor Escribe tu Cuidad", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(emailEditText.getText().toString())) {
            Toast.makeText(this, "Porfavor Escribe Tu Correo Electronico", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(this, "Porfavor Ecribe tu Direccion", Toast.LENGTH_SHORT).show();
        }else {
            ConfirmOrder();
        }
    }

    private void ConfirmOrder() {

        final String saveCurrentDate, saveCurrrentTime;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference ordersRef = FirebaseDatabase
                .getInstance().getReference()
                .child("Orders")
                .child(auth);

        HashMap<String ,Object> ordersMaP = new HashMap<>();
        ordersMaP.put("totalAmount",totalAmount);
        ordersMaP.put("name",nameEditText.getText().toString());
        ordersMaP.put("phone",phoneEditText.getText().toString());
        ordersMaP.put("date",saveCurrentDate);
        ordersMaP.put("time",saveCurrrentTime);
        ordersMaP.put("address",addressEditText.getText().toString());
        ordersMaP.put("city",cityEdtitText.getText().toString());
        ordersMaP.put("state","not shipped");
        ordersMaP.put("email",emailEditText.getText().toString());
        ordersMaP.put("uid",auth);

        ordersRef.updateChildren(ordersMaP).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference()
                            .child("Cart List")
                            .child("Users View")
                            .child(auth)
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                   if (task.isSuccessful()){
                                       Toast.makeText(ConfirmFinalOrdenActivity.this, "Tu orden final se completo correctamente", Toast.LENGTH_SHORT).show();
                                       Intent intent = new Intent(ConfirmFinalOrdenActivity.this, HomeActivity.class);
                                       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                       startActivity(intent);
                                       finish();
                                   }
                                }
                            });
                }
            }
        });



    }

}
