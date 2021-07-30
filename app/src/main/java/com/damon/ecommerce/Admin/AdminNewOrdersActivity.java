package com.damon.ecommerce.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.ecommerce.Buyers.CartActivity;
import com.damon.ecommerce.Model.AdminOrders;
import com.damon.ecommerce.Prevalent.Prevalent;
import com.damon.ecommerce.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AdminNewOrdersActivity extends AppCompatActivity {

private RecyclerView orderList;
private DatabaseReference orderRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);

        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        orderList = findViewById(R.id.orders_cart_list);
        orderList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders>options =
                new  FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(orderRef,AdminOrders.class)
                .build();

        FirebaseRecyclerAdapter<AdminOrders,AdminOrdersViewHolder>adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, final int position, @NonNull final AdminOrders model) {

                        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        if (!model.getUid().equals(user)) {

                            holder.userName.setText("Nombre: " + model.getName());
                            holder.userPhoneNumber.setText("Telefono: " + model.getPhone());
                            holder.userTotalPrice.setText("Precio Total:  $" + model.getTotalAmount());
                            holder.userDateTime.setText("Orden fecha de Compra: " + model.getDate() + " " + model.getTime());
                            holder.userShippingAddress.setText("Compra Direccion: " + model.getAddress() + " ," + model.getCity());
                            holder.userShippingEmail.setText("Correo Electronico: " + "\n" + model.getEmail());
                            holder.userSate.setText("Estado de producto: " + model.getState());
                        }else {
                            Toast.makeText(AdminNewOrdersActivity.this, "No tienes ventas aun..", Toast.LENGTH_SHORT).show();
                            holder.cartOrdenes.setVisibility(View.GONE);

                        }


                        holder.showOrdersBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
                               if (!model.getUid().equals(user)) {
                                   String  uID = getRef(position).getKey();
                                   Intent intent = new Intent(AdminNewOrdersActivity.this, AdminUserProductsActivity.class);
                                   intent.putExtra("uid", uID);
                                   startActivity(intent);
                               }else {
                                   Toast.makeText(AdminNewOrdersActivity.this, "No tienes aun ventas...", Toast.LENGTH_SHORT).show();
                               }
                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                 CharSequence options []= new CharSequence[]{
                                         "Si",
                                         "No"
                                 };
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                                builder.setTitle("Presiona SI aprobar o NO para eliminar ?");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        if (i == 0){

                                            String  uID = getRef(position).getKey();
                                            CheckOrdenSate(uID);
                                            Toast.makeText(AdminNewOrdersActivity.this, "Aprobaste ponte en contacto con el comprador", Toast.LENGTH_SHORT).show();

                                        }else if (i ==1){
                                            String  uID = getRef(position).getKey();
                                            RemoveOrder(uID);
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });



                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,parent,false);
                        return  new    AdminOrdersViewHolder(view);
                    }
                };

        orderList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder {
//creamos una clase para iniclizar la inicializar el recybler adapter de orderslayout
        public TextView userName,userPhoneNumber,userTotalPrice,userDateTime,userShippingAddress,userShippingEmail,userSate;
        public Button showOrdersBtn;
        public CardView  cartOrdenes;
        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.order_user_name);
            userPhoneNumber = itemView.findViewById(R.id.order_phone_numer);
            userTotalPrice = itemView.findViewById(R.id.order_total_price);
            userDateTime = itemView.findViewById(R.id.order_date_time);
            userShippingAddress = itemView.findViewById(R.id.order_address_city);
            showOrdersBtn = itemView.findViewById(R.id.show_all_product_btn);
            userShippingEmail = itemView.findViewById(R.id.order_email);
            userSate = itemView.findViewById(R.id.order_state);
            cartOrdenes = itemView.findViewById(R.id.cart_ordenes);


        }
    }

    private void RemoveOrder(String uID) {
       orderRef.child(uID).removeValue();
    }

    private  void CheckOrdenSate(final String uID){

        DatabaseReference ref =FirebaseDatabase.getInstance().getReference().child("Orders").child(uID);

        HashMap<String,Object> userMap = new HashMap<>();
        userMap.put("state","Vendido");
       // ref.child(Prevalent.currentonlineUser.getPhone()).updateChildren(userMap);//esto es para acutlizar en la bvase de datos
        orderRef.child(uID).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               if (task.isSuccessful()){
                   Toast.makeText(AdminNewOrdersActivity.this, "Se aprovo con exito", Toast.LENGTH_SHORT).show();
               }
            }
        });

       }

}
