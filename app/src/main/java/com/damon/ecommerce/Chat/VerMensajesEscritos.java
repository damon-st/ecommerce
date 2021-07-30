package com.damon.ecommerce.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.ecommerce.Adapters.TabsAccessorAdapter;
import com.damon.ecommerce.Buyers.HomeActivity;
import com.damon.ecommerce.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class VerMensajesEscritos extends AppCompatActivity {
    private StorageReference storageProlifePictureRef;
    private CircleImageView prolifeImageView;
    private TextView fullNameEditText , userPhoneEditText,addressEditText;
    private TextView prolifeChangeTextBtn , closeTextBtn, saveTextButton,fullEamil;
    private DatabaseReference Sellerreference;


    //la tabla es donde ira ubicado los nombres de los contenidos que al pulsar nos enviara al fragment
    private TabLayout myTabLayout;
    //cremaos un objeto de la clase tabsaccesadapter que tiene los items y title de los fragments
    private TabsAccessorAdapter myTabsAccessorAdapter;

    private ViewPager myViewPager;


    private Uri imageUri;
    private String key ="";
    private String checker="";
    private StorageTask uploadTask;
    private Button securityQuestionBtn;
    private String currentUserID;
    private FirebaseAuth mAuth;

    public String  link = "https://firebasestorage.googleapis.com/v0/b/ecommerce-fea29.appspot.com/o/Profile%20pictures%2Fprofile.png?alt=media&token=2cce4a2c-0f73-4684-8672-ccff91b08c31";


    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedProductsRef;
    private DatabaseReference chatsRef,UsersRef,ChatRequestsRef,usersRef,contactsRef,Contactsref;

    private DatabaseReference RootRef,RootReff;
    String mauth = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private ChipNavigationBar chipNavigationBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_datos);
        //key = getIntent().getStringExtra("sid");
        // checker = getIntent().getStringExtra("key");
        RootRef = FirebaseDatabase.getInstance().getReference().child("Chat");
        RootReff = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        chatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestsRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        Contactsref = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);

        chipNavigationBar = findViewById(R.id.miscontactos);

//        recyclerView = findViewById(R.id.vendedor_datos_recyclerview);
//
//        recyclerView.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);



        myViewPager = findViewById(R.id.main_tabs_pager);//inicializamos el viewpager
        myTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);//aqui pasamos el parametro de la calse adapter


        myTabLayout = findViewById(R.id.main_tabs);//esta tabla crea los 3 bloques contacto chats etc
        myTabLayout.setupWithViewPager(myViewPager);//aqui pasamos la vista que tendra cada tabla

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                if (i==R.id.nav_mensajes_contactos){

                }else if (i==R.id.navigation_logout_contactos){
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

//    @Override
//    public void onStart() {
//        super.onStart();

//        FirebaseRecyclerOptions<Contacts> options =
//                new FirebaseRecyclerOptions.Builder<Contacts>()
//                        .setQuery(RootRef.child(currentUserID),Contacts.class)
//                        .build();
//
//        FirebaseRecyclerAdapter<Contacts,ChatsViewHolder> adapter =
//                new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
//                    @Override
//                    protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Contacts model) {
//
//                        final String usersID = getRef(position).getKey();
//
//                        final String[] retImage = {"default_image"};
//
//                        UsersRef.child(usersID).addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                                if (dataSnapshot.exists()){
//                                    if (dataSnapshot.hasChild("image")){
//
//                                        retImage[0] =dataSnapshot.child("image").getValue().toString();
//                                        Picasso.get().load(retImage[0]).placeholder(R.drawable.profile).into(holder.profileimage);
//
//                                    }
//
//                                    final String retName = dataSnapshot.child("name").getValue().toString();
//                                    final String retStatus = dataSnapshot.child("phone").getValue().toString();
//
//                                    holder.userName.setText(retName);
//                                    holder.userStatus.setText("Ultima vez conectado"+"\n"+"Date"+"Time");
//
//
//                                    //condicion para crear la notificacionj
//                                    if(dataSnapshot.child("userSate").hasChild("state")){
//
//                                        String state = dataSnapshot.child("userSate").child("state").getValue().toString();
//                                        String date = dataSnapshot.child("userSate").child("date").getValue().toString();
//                                        String time = dataSnapshot.child("userSate").child("time").getValue().toString();
//                                        if (state.equals("online")){
//
//                                            holder.userStatus.setText("conectado");
//                                        }
//                                        else if (state.equals("offline")){
//
//                                            holder.userStatus.setText("Ultima vez conectado"+ date +" "+ time);
//                                        }
//                                    }else {
//                                        holder.userStatus.setText("Desconectado");
//                                    }
//
//                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                            Intent chatintent = new Intent(VerMensajesEscritos.this,ChatActivity.class);
//                                            chatintent.putExtra("userid",usersID);
//                                            chatintent.putExtra("name",retName);
//                                            chatintent.putExtra("image", retImage[0]);
//
//                                            startActivity(chatintent);
//                                        }
//                                    });
//                                    //metodo para eliminar el contacdto
//                                    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                                        @Override
//                                        public boolean onLongClick(View v) {
//                                            AlertDialog alertDialog = new AlertDialog.Builder(VerMensajesEscritos.this).create();
//                                            alertDialog.setTitle("Quieres Eliminar este Contacto?");
//                                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No",
//                                                    new DialogInterface.OnClickListener() {
//                                                        public void onClick(DialogInterface dialog, int which) {
//                                                            dialog.dismiss();
//                                                        }
//                                                    });
//                                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Si",
//                                                    new DialogInterface.OnClickListener() {
//                                                        public void onClick(DialogInterface dialog, int which) {
//                                                            RootRef.child(currentUserID).child(usersID).removeValue()
//                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                        @Override
//                                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                                            if (task.isSuccessful()){
//                                                                                Toast.makeText(VerMensajesEscritos.this, "Eliminado Correcto", Toast.LENGTH_SHORT).show();
//                                                                            }
//                                                                        }
//                                                                    });
//                                                            dialog.dismiss();
//                                                        }
//                                                    });
//                                            alertDialog.show();
//                                            return false;
//                                        }
//                                    });
//                                }
//
//
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//                    }
//
//                    @NonNull
//                    @Override
//                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//
//                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
//                        return new ChatsViewHolder(view);
//
//                    }
//                };
//        recyclerView.setAdapter(adapter);
//        adapter.startListening();
//    }
//    public static class  ChatsViewHolder extends RecyclerView.ViewHolder{
//        CircleImageView profileimage ;
//        TextView userStatus ,userName;
//        ImageView imagenProducto;
//
//
//        public ChatsViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            userName = itemView.findViewById(R.id.user_profile_name);
//            userStatus = itemView.findViewById(R.id.user_status);
//            profileimage =itemView.findViewById(R.id.users_profile_image);
//            imagenProducto = itemView.findViewById(R.id.imagenProducto);
//
//
//
//        }
//    }



    private void vermiscontactos() {

        FirebaseRecyclerOptions options = new
                FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(Contactsref,Contacts.class)//currentuserid.child(currentUserID)
                .build();

        FirebaseRecyclerAdapter<Contacts, ContactsFragment.ContactsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts, ContactsFragment.ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsFragment.ContactsViewHolder holder, final int position, @NonNull Contacts model) {

                final String usersId = getRef(position).getKey();



//                DatabaseReference getTypeRef = getRef(position).child("request_type");//

                UsersRef.child(usersId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            //condicion para crear la notificacionj
                            if(dataSnapshot.child("userSate").hasChild("state")){

                                String state = dataSnapshot.child("userSate").child("state").getValue().toString();
                                String date = dataSnapshot.child("userSate").child("date").getValue().toString();
                                String time = dataSnapshot.child("userSate").child("time").getValue().toString();
                                if (state.equals("online")){

                                    holder.onlineIcon.setVisibility(View.VISIBLE);

                                }
                                else if (state.equals("offline")){

                                    holder.onlineIcon.setVisibility(View.INVISIBLE);
                                }
                            }else {
                                holder.onlineIcon.setVisibility(View.INVISIBLE);
                            }

                            if (dataSnapshot.hasChild("image")){
                                String usersImage =dataSnapshot.child("image").getValue().toString();
                                String profilename = dataSnapshot.child("name").getValue().toString();
                                String profilestatus = dataSnapshot.child("phone").getValue().toString();

                                holder.userName.setText(profilename);
                                holder.userStatus.setText(profilestatus);

                                //primero se manda lo que recuperamos con el datasnapshot
                                //segundo es el nombre de al variable que esta en el circlview
                                //el placeholder es obcional para poner una foto por defecto encaso que no aya tenido foto el usuario
                                Picasso.get().load(usersImage).placeholder(R.drawable.profile).into(holder.prolifeImage);

                            }else {
                                String profilename = dataSnapshot.child("name").getValue().toString();
                                String profilestatus = dataSnapshot.child("status").getValue().toString();

                                holder.userName.setText(profilename);
                                holder.userStatus.setText(profilestatus);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        CharSequence options[] = new CharSequence[]{
                                "Se eliminaran Todos Tus Contactos"
                        };
                        AlertDialog.Builder builder =new  AlertDialog.Builder(VerMensajesEscritos.this);
                        builder.setTitle("Seguro quieres eliminarlo?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, final int which) {

                                if (which ==0){
                                    Contactsref.child(currentUserID).child(usersId)
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Contactsref.child(usersId).child(currentUserID)
                                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                Toast.makeText(VerMensajesEscritos.this, "Eliminado", Toast.LENGTH_SHORT).show();
                                                                DeleteSentMessages(position,holder);
                                                            }
                                                        });
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
            public ContactsFragment.ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
                ContactsFragment.ContactsViewHolder viewHolder = new ContactsFragment.ContactsViewHolder(view);
                return viewHolder;

            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    //esta calse es para poder utilizar el recyvler view para poder ver contactos es necesarias
    public static class  ContactsViewHolder extends RecyclerView.ViewHolder{

        TextView userName , userStatus;
        CircleImageView prolifeImage;
        ImageView onlineIcon ;
        public ContactsViewHolder(@NonNull final View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            prolifeImage = itemView.findViewById(R.id.users_profile_image);
            onlineIcon = itemView.findViewById(R.id.user_online_status);

        }
    }



    private void versolicitudes() {

        FirebaseRecyclerOptions<Contacts> options  =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(ChatRequestsRef.child(currentUserID),Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, RequestsFragment.RequestViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, RequestsFragment.RequestViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestsFragment.RequestViewHolder holder, int position, @NonNull Contacts model) {
                        holder.itemView.findViewById(R.id.request_accept_button).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.request_cancel_button).setVisibility(View.VISIBLE);

                        final String list_user_id = getRef(position).getKey();

                        DatabaseReference getTypeRef = getRef(position).child("request_type");

                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    String type = dataSnapshot.getValue().toString();
                                    if (type.equals("received")){

                                        usersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.hasChild("image")){

                                                    final  String requestProlifeImage = dataSnapshot.child("image").getValue().toString();
                                                    Picasso.get().load(requestProlifeImage).placeholder(R.drawable.profile).into(holder.prolifeImage);

                                                }

                                                final  String requestUsername = dataSnapshot.child("name").getValue().toString();
                                                final  String requestUserstatus = dataSnapshot.child("phone").getValue().toString();
                                                holder.userName.setText(requestUsername);
                                                holder.userStatus.setText("Quiere ser tu amigo");

                                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        CharSequence options[] = new CharSequence[]{
                                                                "Aceptar",
                                                                "cancelar"
                                                        };
                                                        AlertDialog.Builder builder =new  AlertDialog.Builder(VerMensajesEscritos.this);
                                                        builder.setTitle(requestUsername+" Te envio una Solicitud");

                                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int i) {
                                                                if (i ==0){

                                                                    contactsRef.child(currentUserID).child(list_user_id).child("Contact").setValue("Saved")
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()){
                                                                                        contactsRef.child(list_user_id).child(currentUserID).child("Contact").setValue("Saved")
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        if (task.isSuccessful()){

                                                                                                            ChatRequestsRef.child(currentUserID).child(list_user_id)
                                                                                                                    .removeValue()
                                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                        @Override
                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                            if (task.isSuccessful()){

                                                                                                                                ChatRequestsRef.child(list_user_id).child(currentUserID)
                                                                                                                                        .removeValue()
                                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                            @Override
                                                                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                                                                Toast.makeText(VerMensajesEscritos.this, "Nuevo Contacto Guardado", Toast.LENGTH_SHORT).show();


                                                                                                                                            }
                                                                                                                                        });
                                                                                                                            }


                                                                                                                        }
                                                                                                                    });

                                                                                                        }

                                                                                                    }
                                                                                                });

                                                                                    }

                                                                                }
                                                                            });


                                                                }
                                                                if (i ==1){
                                                                    ChatRequestsRef.child(currentUserID).child(list_user_id)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()){

                                                                                        ChatRequestsRef.child(list_user_id).child(currentUserID)
                                                                                                .removeValue()
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                                        Toast.makeText(VerMensajesEscritos.this, "Contacto Eliminado", Toast.LENGTH_SHORT).show();


                                                                                                    }
                                                                                                });
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

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    }else if (type.equals("sent")){

                                        Button request_sent_button = holder.itemView.findViewById(R.id.request_accept_button);
                                        request_sent_button.setText("Solicitud Enviada");
                                        holder.itemView.findViewById(R.id.request_cancel_button).setVisibility(View.INVISIBLE);
                                        usersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.hasChild("image")){

                                                    final  String requestProlifeImage = dataSnapshot.child("image").getValue().toString();
                                                    Picasso.get().load(requestProlifeImage).placeholder(R.drawable.profile).into(holder.prolifeImage);

                                                }

                                                final  String requestUsername = dataSnapshot.child("name").getValue().toString();
                                                final  String requestUserstatus = dataSnapshot.child("phone").getValue().toString();

                                                holder.userName.setText(requestUsername);
                                                holder.userStatus.setText("Tu has enviado una solicitud a "+requestUsername);

                                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        CharSequence options[] = new CharSequence[]{
                                                                "Cancelar Solicitud"
                                                        };
                                                        AlertDialog.Builder builder =new  AlertDialog.Builder(VerMensajesEscritos.this);
                                                        builder.setTitle("Seguro quieres cancelar?");

                                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int i) {

                                                                if (i ==0){
                                                                    ChatRequestsRef.child(currentUserID).child(list_user_id)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()){

                                                                                        ChatRequestsRef.child(list_user_id).child(currentUserID)
                                                                                                .removeValue()
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                                        Toast.makeText(VerMensajesEscritos.this, "Tu as cancelado la solicitud", Toast.LENGTH_SHORT).show();


                                                                                                    }
                                                                                                });
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

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public RequestsFragment.RequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {

                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
                        RequestsFragment.RequestViewHolder holder = new RequestsFragment.RequestViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }



    private void updateUserStates(String state) {
        //aqui sera para poner al ora aslo ususario su iltimas conexcion
        String saveCutrrentTime, saveCurrentData;

        Calendar calendar = Calendar.getInstance();
        //aqui estamos creando faroma del calendario dia mes año
        SimpleDateFormat currentData = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentData = currentData.format(calendar.getTime());
        //aqui estamos creando forma hora minuto y segundo
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCutrrentTime = currentTime.format(calendar.getTime());

        //aqui creamos una lista con la llave y objeto para mandar ala base de datos Firebase
        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCutrrentTime);
        onlineStateMap.put("date", saveCurrentData);
        onlineStateMap.put("state", state);

        currentUserID = mAuth.getCurrentUser().getUid();
        //aqui creamos la referencia con la vase de datos y nombre dela nueva tabla o dato para labase de datos
        RootReff.child("Users").child(currentUserID).child("userSate")
                .updateChildren(onlineStateMap);



    }





    private void DeleteSentMessages(final int position ,final ContactsFragment.ContactsViewHolder holder){
        //metodo para eliminar los mensajes
        DatabaseReference rootRef= FirebaseDatabase.getInstance().getReference();
        rootRef.child("Contacts").child(currentUserID)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    Toast.makeText(holder.itemView.getContext(), "Eliminado Correcto", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(holder.itemView.getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public int getItemCount() {
        return recyclerView.getItemDecorationCount();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder{
        TextView userName , userStatus;
        CircleImageView prolifeImage;
        Button AccepButton , CancelButton;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            prolifeImage = itemView.findViewById(R.id.users_profile_image);
            AccepButton = itemView.findViewById(R.id.request_accept_button);
            CancelButton = itemView.findViewById(R.id.request_cancel_button);

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();//linea que funciona como conector si ubo inico de sesion
        //esta condicion es para evaluar si ay alguna persona iniciada cecion en el telefono
        //sino esta iniciado ninguna secion nos enviara al login para ingresar
        if (currentUser != null) {
            updateUserStates("online");
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser currentUser = mAuth.getCurrentUser();//linea que funciona como conector si ubo inico de sesion
        if (currentUser != null) {
            updateUserStates("offline");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser currentUser = mAuth.getCurrentUser();//linea que funciona como conector si ubo inico de sesion
        if (currentUser != null) {
            updateUserStates("offline");

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();//linea que funciona como conector si ubo inico de sesion
        if (currentUser != null) {
            updateUserStates("online");

        }
    }
}


