package com.damon.ecommerce.Chat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import com.damon.ecommerce.R;


import com.damon.ecommerce.ViewHolder.ItemViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;


import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {
//en esta clase sera la encaragada de buscar amigos

    private Toolbar mToolbar;
    private RecyclerView FindFriendsrecyclerList;

    private DatabaseReference usersRef;

    private String uid,pid;
    public String  link = "https://firebasestorage.googleapis.com/v0/b/ecommerce-fea29.appspot.com/o/Profile%20pictures%2Fprofile.png?alt=media&token=2cce4a2c-0f73-4684-8672-ccff91b08c31";
    private CircleImageView perfilFoto;
    private TextView nombre;
    private Button chatearConVendedor,boton_mensaje_dialogo;
    private DatabaseReference reference,ProductRef;
    private FirebaseAuth mauth;
    private String currentUserID;
    private boolean comprubea = false;
    private Dialog  dialog;
    private TextView primerTextoDialgo,texto_dialogo_dos,direccion_vendedor;
    private Typeface font ;
    private ImageView boton_serrar_dialogo;
    private String imagen,nombres,direccion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);


       FindFriendsrecyclerList = findViewById(R.id.find_friends_recybleList);//lo inicializamos
       FindFriendsrecyclerList.setLayoutManager(new LinearLayoutManager(this));//aqui creamos un nuevo lista


        dialog = new Dialog(this);

        SharedPreferences preferences = getSharedPreferences("pid", Context.MODE_PRIVATE);
       final String comprobar = preferences.getString("pid","s");

        font = Typeface.createFromAsset(getAssets(),"fonts/font.otf");
        final ArrayList<String> listpostid = new ArrayList<>();

        try {
            uid = getIntent().getStringExtra("uid");
        }catch (Exception e){
            e.printStackTrace();
        }

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        pid = getIntent().getStringExtra("pid");
        //Toast.makeText(this, ""+uid, Toast.LENGTH_SHORT).show();
        perfilFoto = findViewById(R.id.imagen_perfil);
        nombre = findViewById(R.id.nombreUsuario);
        chatearConVendedor = findViewById(R.id.boton_chatear_vendedor);
        direccion_vendedor = findViewById(R.id.direccion_vendedor);

        reference = FirebaseDatabase.getInstance().getReference().child("Chat");
        ProductRef = FirebaseDatabase.getInstance().getReference().child("Products");





//        mToolbar = findViewById(R.id.find_frinds_toolbar);//inicializamos el toobar que es la barrita
//        setSupportActionBar(mToolbar);//aque lo asigaamos
//        getSupportActionBar().setDisplayShowHomeEnabled(true);//aqui recuperamos para que se muestre
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("Ver Vendedor");//aqui le asignamso el titulo que tendra

        nombre.setTypeface(font);
        chatearConVendedor.setTypeface(font);
        direccion_vendedor.setTypeface(font);

//        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });


        try {
            seenNumber(pid);
        }catch (Exception e){
            e.printStackTrace();
        }



        usersRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    imagen= dataSnapshot.child("image").getValue().toString();
                    nombres = dataSnapshot.child("name").getValue().toString();
                    final  String id = dataSnapshot.child("uid").getValue().toString();
                    direccion = dataSnapshot.child("address").getValue().toString();

                    try {

                        Picasso.get().load(imagen).into(perfilFoto);
                        nombre.setText(nombres);
                        direccion_vendedor.setText(direccion);

                        perfilFoto.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(FindFriendsActivity.this,ImageViewerActivity.class);
                                intent.putExtra("url",imagen);
                                startActivity(intent);
                            }
                        });

                            chatearConVendedor.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                                       // if (!pid.equals(comprobar)) {
                                            crear(uid);
//                                        }
                                        Intent intent = new Intent(FindFriendsActivity.this, ChatActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("userid", uid);
                                        intent.putExtra("name", nombres);
                                        intent.putExtra("image", imagen);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        Toast.makeText(FindFriendsActivity.this, "Es tu Publicacion", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void crear(final String uid) {

        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chat").child(uid);

        final String key = reference.push().getKey();
        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender",FirebaseAuth.getInstance().getUid());
        hashMap.put("receiver",uid);
        hashMap.put("pid",pid);





        //lo resolvimos creando usduario primero y despues el pid del producto
        reference.child(FirebaseAuth.getInstance().getUid()).child(key).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

//                    Toast.makeText(FindFriendsActivity.this, "Enviado", Toast.LENGTH_SHORT).show();

                    reference.child(uid).child(key).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
//                                Toast.makeText(FindFriendsActivity.this, "Segundo", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
       // reference.push().setValue(hashMap);

        guardar(pid);
        probar(pid);


    }
    //creamos esta clase para poder agreglar los problemas
    private void probar(String storyid){
        //aqui creamos el metodo para que se creee cuando lo aya visto para que solucionmeos que no pueda crrear mas de dos veces el chat
        FirebaseDatabase.getInstance().getReference().child("Datos").child(uid)
                .child(storyid).child("views").child(FirebaseAuth.getInstance().getUid()).child("valor").setValue(true);
    }
    private void seenNumber( String storyid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Datos")
                .child(uid).child(storyid).child("views").child(FirebaseAuth.getInstance().getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("valor").exists()){
                    String valor = dataSnapshot.child("valor").getValue().toString();
                    if (valor.equals("true")){
                        chatearConVendedor.setEnabled(false);
                        dialog.setContentView(R.layout.dialogoalerta);
                        primerTextoDialgo = dialog.findViewById(R.id.texto_dialogo_uno);
                        texto_dialogo_dos = dialog.findViewById(R.id.texto_dialogo_dos);
                        boton_mensaje_dialogo = dialog.findViewById(R.id.boton_mensaje_dialogo);
                        boton_serrar_dialogo = dialog.findViewById(R.id.boton_serrar_dialogo);

                        primerTextoDialgo.setTypeface(font);
                        texto_dialogo_dos.setTypeface(font);
                        boton_mensaje_dialogo.setTypeface(font);

                        boton_serrar_dialogo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                               dialog.dismiss();
                            }
                        });
                        boton_mensaje_dialogo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(FindFriendsActivity.this,ChatActivity.class);
                                intent.putExtra("userid",uid);
                                intent.putExtra("name",nombres);
                                intent.putExtra("image",imagen);
                                startActivity(intent);
                            }
                        });



                        dialog.show();
                    }else {
                        chatearConVendedor.setEnabled(true);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void  guardar(String  pid){
        SharedPreferences preferences = getSharedPreferences("pid", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("pid",pid);
        editor.apply();
    }



//    private void crear(final String uid){
//        final HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("sender",FirebaseAuth.getInstance().getUid());
//        hashMap.put("receiver",uid);
//        hashMap.put("pid",pid);
//
//        reference.child(pid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()){
//
////                    Toast.makeText(FindFriendsActivity.this, "Enviado", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//    }


//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        FirebaseRecyclerOptions<Contacts> options  =
//                new FirebaseRecyclerOptions.Builder<Contacts>()
//                        .setQuery(usersRef.child(uid),Contacts.class)
//                        .build();
//
//
//        FirebaseRecyclerAdapter<Contacts,FindFriendsViewHolder> adapter =
//                new FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder>(options) {
//                    @Override
//                    protected void onBindViewHolder(@NonNull final FindFriendsViewHolder holder, final int position, @NonNull final Contacts model) {
//
//                        // aqui utilizamos el inicializador de la calse findfriends
//                        //que se encarga de asiganar los nonbre ala lista de contactos
//                        holder.username.setText(model.getName());
//                        holder.userStatus.setText(model.getPhone());
//                        //metodo de la libreia picaso para ver la imgane
//                       Picasso.get().load(model.getImage()).placeholder(R.drawable.profile).into(holder.prolifeImage);
//                        //este itemView sera quien se encargue de resivir lo que aya tocado el usuario
//                        // el itemView pertenece ala clase FindFriendsView Holder
//                        holder.itemView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                //aqui recuperamos la llave del usuario y la guardamos
//                                String visit_user_id = getRef(position).getKey();
//                                Intent prolifeIntent = new Intent(FindFriendsActivity.this,ProlifeActivity.class);
//                                //aqui estamso enviando la llave
//                                prolifeIntent.putExtra("visit_user_id",visit_user_id);
//                                startActivity(prolifeIntent);
//                            }
//                        });
//
//                    }
//
//                    @NonNull
//                    @Override
//                    public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
//                        //aqui creamoas un view i lo infamos con  el laytout que creamos con todos los datos de los uruarios
//                       View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
//                       FindFriendsViewHolder viewHolder = new FindFriendsViewHolder(view);
//                       return  viewHolder;
//
//                    }
//                };
//
//        FindFriendsrecyclerList.setAdapter(adapter);
//
//        adapter.startListening();
//
//    }
////    creamos una clase aqui
////    para que se pueda mostrar en los usuarios
//    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder{
//        TextView username, userStatus;
//        CircleImageView prolifeImage;
//
//        public FindFriendsViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            username = itemView.findViewById(R.id.user_profile_name);
//            userStatus = itemView.findViewById(R.id.user_status);
//            prolifeImage = itemView.findViewById(R.id.users_profile_image);
//        }
//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

}
