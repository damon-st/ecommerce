package com.damon.ecommerce.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.damon.ecommerce.Chat.Comments;
import com.damon.ecommerce.Chat.Contacts;
import com.damon.ecommerce.Interface.ItemClickListener;
import com.damon.ecommerce.Model.Products;
import com.damon.ecommerce.Notification.APIService;
import com.damon.ecommerce.Notification.Client;
import com.damon.ecommerce.Notification.Data;
import com.damon.ecommerce.Notification.MyResponse;
import com.damon.ecommerce.Notification.Sender;
import com.damon.ecommerce.Notification.Token;
import com.damon.ecommerce.R;
import com.damon.ecommerce.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCheckNewProductsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedProductsRef;
    FirebaseUser firebaseUser;
    boolean notify = false;
    APIService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_check_new_products);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        unverifiedProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        recyclerView = findViewById(R.id.admin_products_cherklist);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products>options
                = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(unverifiedProductsRef.orderByChild("productState").equalTo("Pendiente"),Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder>adapter
                = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {

                holder.txtprdouctName.setText(model.getPname());
                holder.txtProductDescription.setText(model.getDescription());
                holder.txtProductPrice.setText("Precio = " + model.getPrice() + "$");
                Picasso.get().load(model.getImage()).into(holder.imageView);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final  String productID =model.getPid();
                        final String uid = model.getUid();
                        final String postid =model.getPid();
                        final String idnose = model.getPostid();


                        CharSequence options[]= new CharSequence[]{
                                "Aprovar el Producto",
                                "No"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminCheckNewProductsActivity.this);
                        builder.setTitle("Tu quieres aprovar este Producto.Estas seguro?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                if (i ==0){
                                    notify=true;
                                    String aprovar ="Aprovado";
                                    ChangeProductState(productID,aprovar);
                                    addNotification(uid,postid,idnose,aprovar);
                                }else if (i ==1){
                                    notify=true;
                                    String aprovar ="Rechazado";
                                    addNotification(uid,postid,idnose,aprovar);
                                    ChangeProductState(productID,aprovar);
                                }
                            }
                        });
                        builder.show();
                    }
                });

            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    private void ChangeProductState(String productID,String aprovado) {

        unverifiedProductsRef.child(productID).child("productState")
                .setValue(aprovado)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(AdminCheckNewProductsActivity.this, "EL producto esta aprobado.y ahora esta disponible en la tienda ", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void addNotification(final String  uid, String postid, String idnose, final String aprovar){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(uid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", uid);
        hashMap.put("sellerid",uid);
        hashMap.put("text", "producto: "+aprovar);
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);
        hashMap.put("id",idnose);

        reference.push().setValue(hashMap);

        reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Contacts contacts = dataSnapshot.getValue(Contacts.class);
                if (notify) {
                    sendNotifiaction(uid, contacts.getName(), aprovar);
                }
                notify = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendNotifiaction(final String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Token token = snapshot.getValue(Token.class);
                            Data data = new Data(receiver, R.mipmap.ic_launcher, username + ": " + message, "Estado Producto",
                                    receiver);

                            Sender sender = new Sender(data, token.getToken());

                            apiService.sendNotification(sender)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.code() == 200) {
                                                if (response.body().success != 1) {
                                                    Toast.makeText(AdminCheckNewProductsActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {

                                        }
                                    });
                        }
                    } catch (Exception e) {
                        System.out.println("Error" + e);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
