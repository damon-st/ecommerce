package com.damon.ecommerce.Chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> usermessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private FirebaseStorage storage ;
    private Context context;
    private String uri;

    public MessageAdapter(List<Messages>usermessagesList){
        this.usermessagesList = usermessagesList;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView senderMessageText , reciverMessageText;
        public CircleImageView reciverProfileImage;
        public ImageView messageSenderPicture,messageReceiverPicture;


        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText = itemView.findViewById(R.id.sender_message_text);
            reciverMessageText = itemView.findViewById(R.id.reciver_message_text);
            reciverProfileImage = itemView.findViewById(R.id.message_profile_image);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_imageView);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_imageView);



        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_messages_layout,viewGroup,false);

        mAuth = FirebaseAuth.getInstance();


        return new MessageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int i) {

        String messageSenderID = mAuth.getCurrentUser().getUid();
        Messages messages = usermessagesList.get(i);

        String fromUserID = messages.getFrom();
        String formMessageType = messages.getType();

        storage = FirebaseStorage.getInstance();
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

        Typeface font = Typeface.createFromAsset(holder.itemView.getContext().getAssets(),"fonts/ArimaKoshi-Medium.otf");


        holder.reciverMessageText.setTypeface(font);
        holder.senderMessageText.setTypeface(font);

        holder.reciverMessageText.setVisibility(View.GONE);//estas son para mostrar o no texto
        holder.reciverProfileImage.setVisibility(View.GONE);
        holder.senderMessageText.setVisibility(View.GONE);

        holder.messageSenderPicture.setVisibility(View.GONE);//aqui serra las imagenes
        holder.messageReceiverPicture.setVisibility(View.GONE);

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
        else if (formMessageType.equals("image")){
            //esto es para las imagenes
            if (fromUserID.equals(messageSenderID)){

                holder.messageSenderPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(holder.messageSenderPicture);
                //Picasso.get().load(messages.getMessage()).resize(1000, 1000).centerInside().into(holder.messageSenderPicture);

            }else {
                holder.reciverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(holder.messageReceiverPicture);


                //  Picasso.get().load(messages.getMessage()).resize(150, 150).centerInside().into(holder.messageSenderPicture);
            }
        }else if (formMessageType.equals("pdf")||formMessageType.equals("docx")){
            if (fromUserID.equals(messageSenderID)){
                //esto es para archivos y lo descargara el que envia
                holder.messageSenderPicture.setVisibility(View.VISIBLE);
                //  holder.messageSenderPicture.setBackgroundResource(R.drawable.file);
                //estas linea es para que descargue desde la base de datos la imagen del icono de archivos
                Picasso.get()
                        .load("https://firebasestorage.googleapis.com/v0/b/ecommerce-fea29.appspot.com/o/file.png?alt=media&token=5757cdcb-414a-42a2-86ba-a7d00eab6811")
                        .into(holder.messageSenderPicture);


            }else {
                //y esto es apra quien recive
                holder.reciverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);
//                holder.messageReceiverPicture.setBackgroundResource(R.drawable.file);

                //estas linea es para que descargue desde la base de datos la imagen del icono de archivos
                Picasso.get()
                        .load("https://firebasestorage.googleapis.com/v0/b/ecommerce-fea29.appspot.com/o/file.png?alt=media&token=5757cdcb-414a-42a2-86ba-a7d00eab6811")
                        .into(holder.messageReceiverPicture);

            }
        }
        //desde aqui espesamso aser para eleimizar mensajes
        if (fromUserID.equals(messageSenderID)){

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (usermessagesList.get(i).getType().equals("pdf")||usermessagesList.get(i).getType().equals("docx")){

                        CharSequence options[] = new CharSequence[]{
                                "Eliminar para mi",
                                "Descargar Documento",
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
                                    Intent intent = new Intent(holder.itemView.getContext(), VerMensajesEscritos.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }else  if (position==1){

                                    //i es la posicion que esta alado del holder en onBindViewHolder
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(usermessagesList.get(i).getMessage()));
                                    holder.itemView.getContext().startActivity(intent);


                                }else if (position==3){
                                    DeleteMessageForEveryone(i,holder);
                                    Intent intent = new Intent(holder.itemView.getContext(), VerMensajesEscritos.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }

                            }
                        });
                        builder.show();

                    } else   if (usermessagesList.get(i).getType().equals("text")){
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

                                    Intent intent = new Intent(holder.itemView.getContext(), VerMensajesEscritos.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }else if (position==2){
                                    DeleteMessageForEveryone(i,holder);

                                    Intent intent = new Intent(holder.itemView.getContext(), VerMensajesEscritos.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }

                            }
                        });
                        builder.show();

                    } else if (usermessagesList.get(i).getType().equals("image")){

                        CharSequence options[] = new CharSequence[]{
                                "Eliminar para mi",
                                "Cancel",
                                "Eliminar Mensaje para todos"
                        };
                        final AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Eliminar Mensaje");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {

                                if (position ==0){
                                    DeleteSentMessages(i,holder);

                                    Intent intent = new Intent(holder.itemView.getContext(), VerMensajesEscritos.class);
                                    holder.itemView.getContext().startActivity(intent);


                                }else if (position==2){
                                    DeleteMessageForEveryoneWithImage(i,holder,usermessagesList.get(i).getMessage());
                                    Intent intent = new Intent(holder.itemView.getContext(), VerMensajesEscritos.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }

                            }
                        });
                        builder.show();

                    }
                    return false;
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (usermessagesList.get(i).getType().equals("image")){

                        Intent intent = new Intent(holder.itemView.getContext(),ImageViewerActivity.class);
                        intent.putExtra("url",usermessagesList.get(i).getMessage());
                        holder.itemView.getContext().startActivity(intent);

                    }
                }
            });
        }else {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (usermessagesList.get(i).getType().equals("pdf")||usermessagesList.get(i).getType().equals("docx")){

                        CharSequence options[] = new CharSequence[]{
                                "Eliminar para mi",
                                "Descargar Documento",
                                "Cancel",
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Eliminar Mensaje");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {

                                if (position ==0){
                                    Intent intent = new Intent(holder.itemView.getContext(), VerMensajesEscritos.class);
                                    holder.itemView.getContext().startActivity(intent);
                                    DeleteReceiveMessages(i,holder);
                                }else if (position==1){
                                    //i es la posicion que esta alado del holder en onBindViewHolder
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(usermessagesList.get(i).getMessage()));
                                    holder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();

                    } else   if (usermessagesList.get(i).getType().equals("text")){
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
                                    Intent intent = new Intent(holder.itemView.getContext(), VerMensajesEscritos.class);
                                    holder.itemView.getContext().startActivity(intent);
                                    DeleteReceiveMessages(i,holder);
                                }
                            }
                        });
                        builder.show();

                    } else if (usermessagesList.get(i).getType().equals("image")){

                        CharSequence options[] = new CharSequence[]{
                                "Eliminar para mi",

                                "Cancel"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Eliminar Mensaje");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {

                                if (position ==0){
                                    DeleteReceiveMessages(i,holder);

                                    Intent intent = new Intent(holder.itemView.getContext(), VerMensajesEscritos.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }

                            }
                        });
                        builder.show();

                    }
                    return false;
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (usermessagesList.get(i).getType().equals("image")){
                        Intent intent = new Intent(holder.itemView.getContext(),ImageViewerActivity.class);
                        intent.putExtra("url",usermessagesList.get(i).getMessage());
                        holder.itemView.getContext().startActivity(intent);
                    }

                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return usermessagesList.size();
    }

    private void DeleteSentMessages(final int position ,final MessageViewHolder holder){
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

    private void DeleteReceiveMessages(final int position ,final MessageViewHolder holder){
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
    private void DeleteMessageForEveryone(final int position ,final MessageViewHolder holder){
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

    private void DeleteMessageForEveryoneWithImage(final int position , final MessageViewHolder holder, final String image){
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
                                deleteImage(image,holder);
                            }
                        }
                    });


                }else {
                    Toast.makeText(holder.itemView.getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteImage(String image, final MessageViewHolder holder){
        final StorageReference reference = storage.getReferenceFromUrl(image);
        reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(holder.itemView.getContext(), "Exito al Eliminar", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(holder.itemView.getContext(), "Error al Eliminar", Toast.LENGTH_SHORT).show();
                    }
            }
        });
    }

}
