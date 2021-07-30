package com.damon.ecommerce.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.damon.ecommerce.Buyers.HomeActivity;
import com.damon.ecommerce.Buyers.MainActivity;
import com.damon.ecommerce.R;

public class AdminHomeActivity extends AppCompatActivity {
     private Button logoutbtn , checkOrdersbtn,maintainProductsBtn,checkApproveProductsbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);


        logoutbtn = findViewById(R.id.admin_logout_btn);
        checkOrdersbtn = findViewById(R.id.check_orders_btn);

        maintainProductsBtn = findViewById(R.id.maintain_btn);
        checkApproveProductsbtn = findViewById(R.id.check_approve_order_btn);

        maintainProductsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, HomeActivity.class);
                intent.putExtra("Admin","Admin");
                startActivity(intent);
            }
        });

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        checkOrdersbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, VerReportesPublicaciones.class);
                startActivity(intent);
            }
        });

        checkApproveProductsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  Intent intent = new Intent(AdminHomeActivity.this,AdminCheckNewProductsActivity.class);
                  startActivity(intent);
            }
        });


    }
}
