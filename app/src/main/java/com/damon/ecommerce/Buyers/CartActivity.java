package com.damon.ecommerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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

import com.damon.ecommerce.Model.Cart;
import com.damon.ecommerce.Prevalent.Prevalent;
import com.damon.ecommerce.R;
import com.damon.ecommerce.ViewHolder.CartViewHolder;
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

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView ;
    private  RecyclerView.LayoutManager layoutManager;
    private Button Nextproccesbttn;
    private TextView txtTotalAmout,txtMsg1;

    private int  overTotalPrice = 0;

    String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //estas cuatro lineas de codigo son apra crear el recyclervire
        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Nextproccesbttn = findViewById(R.id.next_process_btn);
        txtTotalAmout = findViewById(R.id.total_price);
        txtMsg1 = findViewById(R.id.msg1);


        Nextproccesbttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (overTotalPrice!= 0) {
                    txtTotalAmout.setText("Total Precio = $" + String.valueOf(overTotalPrice));

                    Intent intent = new Intent(CartActivity.this, ConfirmFinalOrdenActivity.class);
                    intent.putExtra("Total Price", String.valueOf(overTotalPrice));
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(CartActivity.this, "No tienes Compras en tu carrito..", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckOrdenSate();
     //primero se crea un data referencia
        //segundfo creamos un Firebaserecycleroptions con el parametor de la clase Cart que es donde esta nos datos nombre
        //la primera llave es user vire la segunda el telefono y la tercera el producto
        //en firebase recicler adaparter se para la clase cart y la calse carvireholder que es donde esta inicializada la parte grafica
        final DatabaseReference carlisRef  = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart>options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(carlisRef.child("Users View")
                .child(auth).child("Products"),Cart.class).build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {

                        holder.txtProductQuantity.setText("Cantidad= "+model.getQuantity());
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductPrice.setText("Precio= "+model.getPrice()+"$");
                        //holder.descripcion.setText("Descripcion de producto "+"\n"+model.getDiscount());
                        holder.descripcion.setText("Descripcion Producto: "+"\n"+model.getDescription());

                        int oneTypeProductTPrice  = ((Integer.valueOf(model.getPrice()))* Integer.valueOf(model.getQuantity()));//para calcular precio individual
                        overTotalPrice = overTotalPrice + oneTypeProductTPrice;//aqui sera todos lods valorejuntos






                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]{
                                        "Editar",
                                        "Eliminar Producto"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                builder.setTitle("Carro Opciones");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        if (i ==0){
                                            Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                            intent.putExtra("pid",model.getPid());//para que sea especifico cdon el producto
                                            startActivity(intent);
                                        }
                                        if (i==1){
                                            carlisRef.child("Users View")
                                                    .child(auth)
                                                    .child("Products")
                                                    .child(model.getPid())
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                           if (task.isSuccessful()){
                                                               Toast.makeText(CartActivity.this, "Eliminado con existo", Toast.LENGTH_SHORT).show();

                                                               Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                                                               startActivity(intent);
                                                           }
                                                        }
                                                    });
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });


                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        //aqui es donde se ase para poder visualizar se pasa el layiyt creado
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private  void CheckOrdenSate(){
        DatabaseReference orderRef;
        String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(auth);

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String shoppingState = dataSnapshot.child("state").getValue().toString();
                    String  userName = dataSnapshot.child("name").getValue().toString();

                    if (shoppingState.equals("shipped")){

                        txtTotalAmout.setText("Sr."+userName+"\n tu compra esta correctamente");
                        recyclerView.setVisibility(View.GONE);

                        txtMsg1.setVisibility(View.VISIBLE);
                        txtMsg1.setText("Felicidades, tu compra a sido comprada correctamente , Pronto resiviras la verificacion a tu correo electronico");
                        Nextproccesbttn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this, "Tu puedes ver mas productos, una ves recivida tu orden  ", Toast.LENGTH_SHORT).show();


                    }else if (shoppingState.equals("not shipped")){

                        txtTotalAmout.setText("Estado de Compra = No comprado en proceso");
                        recyclerView.setVisibility(View.GONE);

                        txtMsg1.setVisibility(View.VISIBLE);
                        Nextproccesbttn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this, "Tu puedes ver mas productos, una ves recivida tu orden ", Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
