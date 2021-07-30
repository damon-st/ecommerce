package com.damon.ecommerce.Chat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ecommerce.Buyers.MainActivity;
import com.damon.ecommerce.Model.Comment;
import com.damon.ecommerce.Model.Respuestas;
import com.damon.ecommerce.Model.Users;
import com.damon.ecommerce.Notification.APIService;
import com.damon.ecommerce.Notification.Client;
import com.damon.ecommerce.Notification.Data;
import com.damon.ecommerce.Notification.MyResponse;
import com.damon.ecommerce.Notification.Sender;
import com.damon.ecommerce.Notification.Token;
import com.damon.ecommerce.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Comment> mComment;
    private String postid;

    private FirebaseUser firebaseUser;
    boolean notify = false;
    APIService apiService;

    public CommentAdapter(Context context, List<Comment> comments, String postid){
        mContext = context;
        mComment = comments;
        this.postid = postid;
    }

    @NonNull
    @Override
    public CommentAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
        return new CommentAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentAdapter.ImageViewHolder holder, final int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        final Comment comment = mComment.get(position);

        holder.comment.setText(comment.getComment());


        if (comment.getRespuesta()!=null){
            holder.respuesta_vendedor.setVisibility(View.VISIBLE);
            holder.respuesta_vendedor.setText(": "+comment.getRespuesta());
            holder.imagen_respuesta.setVisibility(View.VISIBLE);
            holder.nombre_respuesta.setVisibility(View.VISIBLE);
        }else {
            holder.respuesta_vendedor.setVisibility(View.GONE);
            holder.imagen_respuesta.setVisibility(View.GONE);
            holder.nombre_respuesta.setVisibility(View.GONE);
        }
        getUserInfo(holder.image_profile, holder.username, comment.getPublisher(),comment.getSellerid(),holder.imagen_respuesta,holder.nombre_respuesta,holder.progressBar);
//        holder.username.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(mContext, MainActivity.class);
//                intent.putExtra("publisherid", comment.getPublisher());
//                mContext.startActivity(intent);
//            }
//        });
//
//        holder.image_profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(mContext, MainActivity.class);
//                intent.putExtra("publisherid", comment.getPublisher());
//                mContext.startActivity(intent);
//            }
//        });

        if (comment.getSellerid().equals(firebaseUser.getUid())){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.dialogo_vendedor_respuesta);
                    TextView textoDialogo = dialog.findViewById(R.id.texto_eliminar_dialogo);
                    Button eliminar = dialog.findViewById(R.id.boton_eliminar_dialogo);
                    Button responder = dialog.findViewById(R.id.boton_responder_dialogo);
                    ImageView cerrar = dialog.findViewById(R.id.cerrar_dialogo);

                    Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),"fonts/font.otf");
                    textoDialogo.setTypeface(typeface);
                    eliminar.setTypeface(typeface);
                    responder.setTypeface(typeface);

                    eliminar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase.getInstance().getReference("Comments")
                                    .child(postid).child(comment.getCommentid())
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(mContext, "Eliminado!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            dialog.dismiss();
                        }
                    });
                    responder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Responder(comment.getCommentid(),comment.getPostid(),holder,comment.getPublisher());
                            dialog.dismiss();
                        }
                    });

                    cerrar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
//                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
//                    alertDialog.setTitle("Quieres Responder esta  Pregunta? o Eliminarla");
//                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Eliminar",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                }
//                            });
//                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Responder",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//
////                                    Intent intent = new Intent(mContext,VerReportesPublicaciones.class);
////                                    intent.putExtra("postid",comment.getPostid());
////                                    intent.putExtra("publisher",comment.getPublisher());
////                                    intent.putExtra("sellerid",comment.getSellerid());
////                                    intent.putExtra("commentid",comment.getCommentid());
////                                    mContext.startActivity(intent);
//
//
//                                }
//                            });
//                    alertDialog.show();
                }
            });
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (comment.getPublisher().equals(firebaseUser.getUid())) {

                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Quieres Eliminar tu Pregunta?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Si",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseDatabase.getInstance().getReference("Comments")
                                            .child(postid).child(comment.getCommentid())
                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(mContext, "Eliminado!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                return true;
            }
        });

    }

    private void Responder(final String commentCommentid, final String postid, final ImageViewHolder holder, final String sellerid) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.contestar_comentario);
        final EditText texto = dialog.findViewById(R.id.add_comment);
        TextView enviar = dialog.findViewById(R.id.post);
        final CircleImageView imagen = dialog.findViewById(R.id.image_profile);
        DatabaseReference users = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(firebaseUser.getUid());
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String  image = dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(image).into(imagen);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(texto.getText().toString())){
                    Toast.makeText(mContext, "Escribe tu Respuesta", Toast.LENGTH_SHORT).show();
                }else {
                    notify=true;
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("respuesta", texto.getText().toString());
                    hashMap.put("uidVendedor",firebaseUser.getUid());
                    reference.child(commentCommentid).updateChildren(hashMap);
                    addNotification(sellerid,texto,postid);
                    dialog.dismiss();
                }
            }
        });



        dialog.show();
    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }
    private void addNotification(final String sellerid, final EditText texto, String  postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(sellerid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("sellerid",firebaseUser.getUid());
        hashMap.put("text", "respondio tu Pregunta: "+texto.getText().toString());
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);
        hashMap.put("id",postid);

        reference.push().setValue(hashMap);
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Contacts contacts = dataSnapshot.getValue(Contacts.class);
                if (notify) {
                    sendNotifiaction(sellerid, contacts.getName(), texto.getText().toString());
                }
                notify = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile;
        public TextView username, comment,respuesta_vendedor,nombre_respuesta;
        public CircleImageView imagen_respuesta;
        public ProgressBar progressBar;

        public ImageViewHolder(View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
            respuesta_vendedor =itemView.findViewById(R.id.respuesta_vendedor);
            imagen_respuesta = itemView.findViewById(R.id.imagen_respuesta);
            nombre_respuesta = itemView.findViewById(R.id.nombre_respuesta);
            progressBar = itemView.findViewById(R.id.progres_user_comment);
        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String publisher, String publisherid, final CircleImageView imagen_respuesta, final TextView nombreRespuesta, final ProgressBar progressBar){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(publisher);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);

                Picasso.get().load(user.getImage()).resize(40,40).into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(R.mipmap.ic_launcher).into(imageView);
                    }
                });
                username.setText(user.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(publisherid);

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);

                Picasso.get().load(user.getImage()).resize(20,20).into(imagen_respuesta);
                nombreRespuesta.setText(user.getName());
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
                            Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, username + ": " + message, "Respondio Tu Pregunta",
                                    receiver);

                            Sender sender = new Sender(data, token.getToken());

                            apiService.sendNotification(sender)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.code() == 200) {
                                                if (response.body().success != 1) {
                                                    Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show();
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