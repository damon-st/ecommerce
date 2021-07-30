package com.damon.ecommerce.FragmentsChat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.ecommerce.Chat.Chat;
import com.damon.ecommerce.Chat.ChatActivity;
import com.damon.ecommerce.Chat.Contacts;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class MensajesVentasCompras extends Fragment {


    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference RootRef, ProductRef,UserRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    public MensajesVentasCompras() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista =  inflater.inflate(R.layout.fragment_mensajes_ventas_compras, container, false);

        recyclerView = vista.findViewById(R.id.recycler_view_ventas);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        RootRef = FirebaseDatabase.getInstance().getReference().child("Chat");
        UserRef  =FirebaseDatabase.getInstance().getReference().child("Users");
        ProductRef = FirebaseDatabase.getInstance().getReference().child("Products");

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        return  vista;
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Chat> options =
                new FirebaseRecyclerOptions.Builder<Chat>()
                        .setQuery(RootRef.child(currentUserID),Chat.class)
                        .build();

        FirebaseRecyclerAdapter<Chat, MensajesVentasCompras.ChatsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Chat, MensajesVentasCompras.ChatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final MensajesVentasCompras.ChatsViewHolder holder, int position, @NonNull Chat model) {

                        final String usersID = getRef(position).getKey();
                        final String pid = model.getPid();
                        final String sender = model.getSender();
                        final String[] retName = new String[1];
                        final String[] retImage = {"default_image"};

                        ProductRef.child(pid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()){
                                    if (dataSnapshot.hasChild("image")){
                                        String  uid = dataSnapshot.child("uid").getValue().toString();
                                        if (uid.equals(currentUserID)) {
                                            retImage[0] = dataSnapshot.child("image").getValue().toString();
                                            Picasso.get().load(retImage[0])
                                                    .resize(90,90)
                                                    .placeholder(R.drawable.profile)
                                                    .into(holder.imagenProducto);
                                            String pname = dataSnapshot.child("pname").getValue().toString();
                                            holder.userName.setText("Producto: "+pname);

                                            UserRef.child(sender).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.child("image").exists()){
                                                        retName[0] = dataSnapshot.child("name").getValue().toString();
                                                        holder.userStatus.setText("Comprador: "+retName[0]);
                                                        retImage[0] = dataSnapshot.child("image").getValue().toString();

                                                        Picasso.get().load(retImage[0])
                                                                .resize(38,38)
                                                                .placeholder(R.drawable.profile)
                                                                .into(holder.profileimage, new Callback() {
                                                                    @Override
                                                                    public void onSuccess() {
                                                                        holder.progressBar.setVisibility(View.GONE);
                                                                    }

                                                                    @Override
                                                                    public void onError(Exception e) {
                                                                        Picasso.get().load(R.mipmap.ic_launcher).into(holder.profileimage);
                                                                    }
                                                                });
                                                    }else {
                                                        holder.relativeLayout.setVisibility(View.GONE);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    Intent chatintent = new Intent(getContext(), ChatActivity.class);
                                                    chatintent.putExtra("userid",sender);
                                                    chatintent.putExtra("name", retName[0]);
                                                    chatintent.putExtra("image", retImage[0]);

                                                    startActivity(chatintent);
                                                }
                                            });
                                            //metodo para eliminar el contacdto
                                            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                                                @Override
                                                public boolean onLongClick(View v) {
                                                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                                                    alertDialog.setTitle("Quieres Eliminar este Contacto?");
                                                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.dismiss();
                                                                }
                                                            });
                                                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Si",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    RootRef.child(currentUserID).child(usersID).removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()){
                                                                                        Toast.makeText(getContext(), "Eliminado Correcto", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            });
                                                                    dialog.dismiss();
                                                                }
                                                            });
                                                    alertDialog.show();
                                                    return false;
                                                }
                                            });
                                        }else {
                                            holder.relativeLayout.removeAllViews();
                                        }


                                    }
                                }else {
                                    holder.relativeLayout.removeAllViews();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }

                    @NonNull
                    @Override
                    public MensajesVentasCompras.ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.displayuno,viewGroup,false);
                        return new MensajesVentasCompras.ChatsViewHolder(view);

                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class  ChatsViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profileimage ;
        TextView userStatus ,userName;
        ImageView imagenProducto;
        RelativeLayout relativeLayout;
        ProgressBar progressBar;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileimage =itemView.findViewById(R.id.users_profile_image);
            imagenProducto = itemView.findViewById(R.id.productimage);
            relativeLayout = itemView.findViewById(R.id.principal);
            progressBar = itemView.findViewById(R.id.progesimages);


        }
    }
}
