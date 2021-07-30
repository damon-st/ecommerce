package com.damon.ecommerce.Chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ecommerce.Buyers.ProductDetailsActivity;
import com.damon.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PreguntasAdapter extends RecyclerView.Adapter<PreguntasAdapter.PreguntasViewHolder> {

    private List<Messages> usermessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private Context context;
    private String uri;

    public PreguntasAdapter(List<Messages>usermessagesList){
        this.usermessagesList = usermessagesList;
    }

    public class PreguntasViewHolder extends RecyclerView.ViewHolder{

        public TextView senderMessageText , reciverMessageText;
        public CircleImageView reciverProfileImage;
        public ImageView messageSenderPicture,messageReceiverPicture;


        public PreguntasViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText = itemView.findViewById(R.id.enviador_mensaje);
            reciverMessageText = itemView.findViewById(R.id.receptor_mensaje);
            reciverProfileImage = itemView.findViewById(R.id.pregunta_imagen);

        }
    }

    @NonNull
    @Override
    public PreguntasAdapter.PreguntasViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_message_preguntas,viewGroup,false);

        mAuth = FirebaseAuth.getInstance();


        return new PreguntasAdapter.PreguntasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PreguntasViewHolder holder, final int i) {

        String messageSenderID = mAuth.getCurrentUser().getUid();
        Messages messages = usermessagesList.get(i);

        String fromUserID = messages.getFrom();
        String formMessageType = messages.getType();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("image")){
                    String reciverImage = dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(reciverImage).placeholder(R.drawable.profile).into(holder.reciverProfileImage);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        holder.reciverMessageText.setVisibility(View.GONE);//estas son para mostrar o no texto
        holder.reciverProfileImage.setVisibility(View.GONE);
        holder.senderMessageText.setVisibility(View.GONE);


        if (formMessageType.equals("text")){

            if (fromUserID.equals(messageSenderID)){

                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                //   holder.senderMessageText.setTextColor(Color.BLACK);//OPCIONAL
                holder.senderMessageText.setText(messages.getMessage()+"\n \n"+messages.getTime()+" - "+messages.getDate());


            }else {
                holder.reciverMessageText.setVisibility(View.VISIBLE);
                holder.reciverProfileImage.setVisibility(View.VISIBLE);
                //holder.reciverMessageText.setVisibility(View.VISIBLE);

                holder.reciverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
                //   holder.reciverMessageText.setTextColor(Color.BLACK);//OPCIONAL
                holder.reciverMessageText.setText(messages.getMessage()+"\n \n"+messages.getTime()+" - "+messages.getDate());

            }

        }
        if (usermessagesList.get(i).getType().equals("text")){
                        //para eliminar mensajes
                        CharSequence options[] = new CharSequence[]{
                                "Eliminar para mi",
                                "Cancel",
                                "Eliminar Mensaje para todos"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Eliminar Mensaje");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {

                                if (position ==0){
                                    DeleteSentMessages(i,holder);

                                    Intent intent = new Intent(holder.itemView.getContext(), ProductDetailsActivity.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }else if (position==2){
                                    DeleteMessageForEveryone(i,holder);

                                    Intent intent = new Intent(holder.itemView.getContext(),ProductDetailsActivity.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }

                            }
                        });
                        builder.show();
                    }if (usermessagesList.get(i).getType().equals("text")){
                        //para eliminar mensajes
                        CharSequence options[] = new CharSequence[]{
                                "Eliminar para mi",
                                "Cancel",
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Eliminar Mensaje");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {

                                if (position ==0){
                                    Intent intent = new Intent(holder.itemView.getContext(),ProductDetailsActivity.class);
                                    holder.itemView.getContext().startActivity(intent);
                                    DeleteReceiveMessages(i,holder);
                                }
                            }
                        });
                        builder.show();

                    }

                }


    @Override
    public int getItemCount() {
        return usermessagesList.size();
    }
    private void DeleteSentMessages(final int position ,final PreguntasViewHolder holder){
        //metodo para eliminar los mensajes
        DatabaseReference rootRef= FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(usermessagesList.get(position).getFrom())
                .child(usermessagesList.get(position).getTo())
                .child(usermessagesList.get(position).getMessageID())
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

    private void DeleteReceiveMessages(final int position ,final PreguntasViewHolder holder){
        //metodo para yo  eliminar los mensajes para
        DatabaseReference rootRef= FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(usermessagesList.get(position).getTo())
                .child(usermessagesList.get(position).getFrom())
                .child(usermessagesList.get(position).getMessageID())
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
    private void DeleteMessageForEveryone(final int position ,final PreguntasViewHolder holder){
        //metodo para eliminar los mensajes para todos
        final DatabaseReference rootRef= FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(usermessagesList.get(position).getTo())
                .child(usermessagesList.get(position).getFrom())
                .child(usermessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    rootRef.child("Messages")
                            .child(usermessagesList.get(position).getFrom())
                            .child(usermessagesList.get(position).getTo())
                            .child(usermessagesList.get(position).getMessageID())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(holder.itemView.getContext(), "Eliminado Correcto", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }else {
                    Toast.makeText(holder.itemView.getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


