package com.damon.ecommerce.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.ecommerce.Buyers.SettinsActivity;
import com.damon.ecommerce.Notification.APIService;
import com.damon.ecommerce.Notification.Client;
import com.damon.ecommerce.Notification.Data;
import com.damon.ecommerce.Notification.MyResponse;
import com.damon.ecommerce.Notification.Sender;
import com.damon.ecommerce.Notification.Token;
import com.damon.ecommerce.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {


    private String messageReciverID ,messageReciverName, messageReciverImage,messagemSenderID;
    private TextView userName , userLassSenn;
    private CircleImageView userImage;

    private Toolbar ChatToolbar;

    private ImageButton sendMessageButton,sendeFilesButton;
    private EditText messageInputText;

    private DatabaseReference RootRef;

    private FirebaseAuth mAuth;
    APIService apiService;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView usersMessagesList;

    private String  saveCutrrentTime ,saveCurrentData;
    private String checker="",myUrl="";
    private StorageTask uploadTask;//encaraga de enviar archivos base de datos FIREBASE
    private Uri fileUri;

    private ProgressDialog loadingBar;
    private StorageReference storageReference;

    boolean notify = false;
    private Bitmap bitmap;
    private Typeface font;

    private SwipeRefreshLayout mRefreshLayout;
    public static final  int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;
    private int itemPost =0;
    private  String mLastKey = "";
    private String mPrevKey= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_activity);

        mRefreshLayout = findViewById(R.id.swiperefresh);

        font = Typeface.createFromAsset(getAssets(),"fonts/ArimaKoshi-Medium.otf");
        RootRef = FirebaseDatabase.getInstance().getReference();
        messageReciverID = getIntent().getExtras().get("userid").toString();
//        messageReciverName =getIntent().getExtras().get("name").toString();
//        messageReciverImage = getIntent().getExtras().get("image").toString();
        if (notify == true) {

            messageReciverName = getIntent().getExtras().get("name").toString();
            messageReciverImage = getIntent().getExtras().get("image").toString();
        } else {
            RootRef.child("Users").child(messageReciverID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        messageReciverName = dataSnapshot.child("name").getValue().toString();
                        messageReciverImage = dataSnapshot.child("image").getValue().toString();

                        userName.setTypeface(font);

                        userName.setText(messageReciverName);
                        Picasso.get().load(messageReciverImage).placeholder(R.drawable.profile).into(userImage);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        mAuth =FirebaseAuth.getInstance();
        messagemSenderID =mAuth.getCurrentUser().getUid();

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        InitializeControls();

        userName.setText(messageReciverName);
        Picasso.get().load(messageReciverImage).placeholder(R.drawable.profile).into(userImage);

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this,ImageViewerActivity.class);
                intent.putExtra("url",messageReciverImage);
                startActivity(intent);
            }
        });

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                SendMessage();
            }
        });

        DisplayLastSeen();

        sendeFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menuImage:
                                checker ="image";

                                CropImage.activity(fileUri).start(ChatActivity.this);
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.menu_files);
                popupMenu.show();
            }
        });

//        sendeFilesButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //codigo para crear las optiones apra enviar archivos
//                CharSequence options[] = new CharSequence[]{
//                        "Imagenes"
////                        "PDF Archivos",
////                        "Ms Word Archivos"
//                };
//                //creamos la parte visual
//                AlertDialog.Builder builder = new  AlertDialog.Builder(ChatActivity.this);
//                builder.setTitle("Seleccion el Archivo");
//
//                builder.setItems(options, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int i) {
//                        if (i == 0){
//                            //esta sera para imgenes
//                            checker ="image";
////                            Intent intent = new Intent();
////                            intent.setAction(Intent.ACTION_GET_CONTENT);
////                            intent.setType("image/*");
////                            startActivityForResult(intent.createChooser(intent,"Select Image"),438);
//
//                            CropImage.activity(fileUri)
////                        .setAspectRatio(1,1)
//                                    .start(ChatActivity.this);
//                        }
////                        if (i == 1){
////                            //esta sera para archivos pdf
////                            checker = "pdf";
////
////                            Intent pdf = new Intent();
////                            pdf.setAction(Intent.ACTION_GET_CONTENT);
////                            pdf.setType("application/pdf");
////                            startActivityForResult(pdf.createChooser(pdf,"Select PDF File"),438);
////
////                        }
////                        if (i == 2){
////                            //esta sera para archivos Word
////                            checker = "docx";
////                            Intent word = new Intent();
////                            word.setAction(Intent.ACTION_GET_CONTENT);
////                            word.setType("application/msword");
////                            startActivityForResult(word.createChooser(word,"Select WORD File"),438);
////
////                        }
//                    }
//                });
//                builder.show();
//
//            }
//        });


        lerrMensajes();


        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
                itemPost =0;
                loadMoreMessages();
            }
        });

    }

    private void loadMoreMessages() {
        DatabaseReference messageRef = RootRef.child("Messages").child(messagemSenderID).child(messageReciverID);

        Query query = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                String messageKey = dataSnapshot.getKey();

                if (!mPrevKey.equals(messageKey)){
                    messagesList.add(itemPost++,messages);
                }else {
                    mPrevKey = mLastKey;
                }

                if(itemPost ==1){

                    mLastKey = messageKey;
                }


                messageAdapter.notifyDataSetChanged();

               // usersMessagesList.smoothScrollToPosition(usersMessagesList.getAdapter().getItemCount());

                mRefreshLayout.setRefreshing(false);

                linearLayoutManager.scrollToPositionWithOffset(10,0);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void InitializeControls() {

        loadingBar = new ProgressDialog(this);
        ChatToolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(ChatToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater= (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbarView = layoutInflater.inflate(R.layout.custom_chat_bar,null);
        actionBar.setCustomView(actionbarView);

        ChatToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




        userImage = findViewById(R.id.custom_profile_IMAGE);
        userName = findViewById(R.id.custom_profile_name);
        userLassSenn = findViewById(R.id.custom_user_last_seen);

        sendMessageButton = findViewById(R.id.enviar_message_button);
        messageInputText = findViewById(R.id.input_message);
        sendeFilesButton =findViewById(R.id.enviar_files_button);

        messageInputText.setTypeface(font);



        messageAdapter = new MessageAdapter(messagesList);
        usersMessagesList = findViewById(R.id.private_messages_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        usersMessagesList.setLayoutManager(linearLayoutManager);
        usersMessagesList.setAdapter(messageAdapter);



        Calendar calendar = Calendar.getInstance();
        //aqui estamos creando faroma del calendario dia mes año
        SimpleDateFormat currentData  = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentData = currentData.format(calendar.getTime());
        //aqui estamos creando forma hora minuto y segundo
        SimpleDateFormat currentTime  = new SimpleDateFormat("hh:mm a");
        saveCutrrentTime = currentTime.format(calendar.getTime());


    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //sera encargada de cargar las imgenes para enviar
        if (requestCode ==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode ==RESULT_OK&&data!=null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            loadingBar.setTitle("Enviando Archivo");//aqui saldra el dialog bar que es un visor de tiempo para que vea el usuario
            loadingBar.setMessage("Por favor espera, enviando Archivo...");// ya que mostrara el progreso
            loadingBar.setCanceledOnTouchOutside(false);// aqui no permitira al usuario tocar la pantalla asta que aya terminado
            loadingBar.show();//aqui sera para que se pueda mostrar el show permite eso

            fileUri = result.getUri();

            if (!checker.equals("image")){
                //aqui es para archivos
                storageReference = FirebaseStorage.getInstance().getReference().child("Document Files");
                final String  messageSenderRef = "Messages/"+messagemSenderID+"/"+messageReciverID;
                final String  messageReciverRef = "Messages/"+messageReciverID+"/"+messagemSenderID;

                DatabaseReference userMessagerKeyRef= RootRef.child("Messages")
                        .child(messagemSenderID).child(messageReciverID).push();

                final   String messagePushID = userMessagerKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushID+"."+checker);

                filePath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()){


                            Map messageTextBody =new HashMap();
                            messageTextBody.put("message",task.getResult().getDownloadUrl().toString());
                            messageTextBody.put("name",fileUri.getLastPathSegment());
                            messageTextBody.put("type",checker);
                            messageTextBody.put("from",messagemSenderID);
                            messageTextBody.put("to",messageReciverID);
                            messageTextBody.put("messageID",messagePushID);
                            messageTextBody.put("time",saveCutrrentTime);
                            messageTextBody.put("date",saveCurrentData);


                            Map messageBodyDetails = new HashMap();
                            messageBodyDetails.put(messageSenderRef+"/" +messagePushID,messageTextBody);
                            messageBodyDetails.put(messageReciverRef+"/" +messagePushID,messageTextBody);

                            RootRef.updateChildren(messageBodyDetails);
                            loadingBar.dismiss();

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingBar.dismiss();
                        Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double p = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        loadingBar.setMessage((int)+p+"% Subiendo..");
                    }
                });

            }else if (checker.equals("image")){

                notify = true;//esto es para que si envia sea verdadero para que suene la notificacion
                storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");
                final String  messageSenderRef = "Messages/"+messagemSenderID+"/"+messageReciverID;
                final String  messageReciverRef = "Messages/"+messageReciverID+"/"+messagemSenderID;

                DatabaseReference userMessagerKeyRef= RootRef.child("Messages")
                        .child(messagemSenderID).child(messageReciverID).push();

                final   String messagePushID = userMessagerKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushID+".jpg");

                File tumb_filePath = new File(fileUri.getPath());
                try {
                    bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(100)
                            .compressToBitmap(tumb_filePath);
                }catch (IOException e){
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,70,byteArrayOutputStream);
                final  byte[] thumb_byte =byteArrayOutputStream.toByteArray();


                uploadTask = filePath.putBytes(thumb_byte);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {

                        if (!task.isSuccessful()){
                            throw  task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri> (){
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            try {

                                Uri dowloadUrl=task.getResult();
                                myUrl= dowloadUrl.toString();

                                Map messageTextBody =new HashMap();
                                messageTextBody.put("message",myUrl);
                                messageTextBody.put("name",fileUri.getLastPathSegment());
                                messageTextBody.put("type",checker);
                                messageTextBody.put("from",messagemSenderID);
                                messageTextBody.put("to",messageReciverID);
                                messageTextBody.put("messageID",messagePushID);
                                messageTextBody.put("time",saveCutrrentTime);
                                messageTextBody.put("date",saveCurrentData);


                                Map messageBodyDetails = new HashMap();
                                messageBodyDetails.put(messageSenderRef+"/" +messagePushID,messageTextBody);
                                messageBodyDetails.put(messageReciverRef+"/" +messagePushID,messageTextBody);

                                RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()){

                                            Toast.makeText(ChatActivity.this, "Mensaje Enviado", Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }else {

                                            Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }
                                        messageInputText.setText("");
                                    }
                                });
                            }catch (Exception e){
                                Toast.makeText(ChatActivity.this, ""+e, Toast.LENGTH_LONG).show();
                                System.out.println("MENSAJE "+e);
                            }
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                            final String msg = "Te a enviado una imagen";

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid());
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Contacts contacts = dataSnapshot.getValue(Contacts.class);
                                    if (notify) {
                                        sendNotifiaction(messageReciverID, contacts.getName(), msg);
                                    }
                                    notify = false;
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }
                    }
                });

            }else {

                Toast.makeText(this, "No Seleccionado.Error..", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }

        }
    }

    private void DisplayLastSeen(){
        //metodo para la ultima coneccion para que sea en el chat
        RootRef.child("Users").child(messageReciverID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //condicion para crear la notificacionj
                        if(dataSnapshot.child("userSate").hasChild("state")){

                            userLassSenn.setTypeface(font);

                            String state = dataSnapshot.child("userSate").child("state").getValue().toString();
                            String date = dataSnapshot.child("userSate").child("date").getValue().toString();
                            String time = dataSnapshot.child("userSate").child("time").getValue().toString();
                            if (state.equals("online")){

                                userLassSenn.setText("conectado");
                            }
                            else if (state.equals("offline")){

                                userLassSenn.setText("Ultima vez conectado"+ date +"\n"+ time);
                            }
                        }else {
                            userLassSenn.setText("Desconectado");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//
//    }

    public void lerrMensajes(){

        DatabaseReference messageRef = RootRef.child("Messages").child(messagemSenderID).child(messageReciverID);

        Query query = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);


        query.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Messages messages = dataSnapshot.getValue(Messages.class);

                        itemPost++;

                        if(itemPost ==1){
                            String messageKey = dataSnapshot.getKey();

                            mLastKey = messageKey;
                            mPrevKey = messageKey;
                        }

                        messagesList.add(messages);
                        messageAdapter.notifyDataSetChanged();

                        usersMessagesList.smoothScrollToPosition(usersMessagesList.getAdapter().getItemCount());

                        mRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void SendMessage(){
        String messageText= messageInputText.getText().toString();
        if (TextUtils.isEmpty(messageText)){
            Toast.makeText(this, "Debes escribir el mensaje", Toast.LENGTH_SHORT).show();
        }else {

            String  messageSenderRef = "Messages/"+messagemSenderID+"/"+messageReciverID;
            String  messageReciverRef = "Messages/"+messageReciverID+"/"+messagemSenderID;

            DatabaseReference userMessagerKeyRef= RootRef.child("Messages")
                    .child(messagemSenderID).child(messageReciverID).push();

            String messagePushID = userMessagerKeyRef.getKey();

            Map messageTextBody =new HashMap();
            messageTextBody.put("message",messageText);
            messageTextBody.put("type","text");
            messageTextBody.put("from",messagemSenderID);
            messageTextBody.put("to",messageReciverID);
            messageTextBody.put("messageID",messagePushID);
            messageTextBody.put("time",saveCutrrentTime);
            messageTextBody.put("date",saveCurrentData);


            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef+"/" +messagePushID,messageTextBody);
            messageBodyDetails.put(messageReciverRef+"/" +messagePushID,messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ChatActivity.this, "Mensaje Enviado", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    messageInputText.setText("");
                }
            });

            final String msg = messageText;

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


            reference = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Contacts contacts = dataSnapshot.getValue(Contacts.class);
                    if (notify) {
                        sendNotifiaction(messageReciverID, contacts.getName(), msg);
                    }
                    notify = false;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    private void sendNotifiaction(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {


                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Token token = snapshot.getValue(Token.class);
                            Data data = new Data(mAuth.getUid(), R.mipmap.ic_launcher, username + ": " + message, "Nuevo Mensaje",
                                    messageReciverID);

                            Sender sender = new Sender(data, token.getToken());

                            apiService.sendNotification(sender)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.code() == 200) {
                                                if (response.body().success != 1) {
                                                    Toast.makeText(ChatActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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

        messagemSenderID = mAuth.getCurrentUser().getUid();
        DatabaseReference RootReff  = FirebaseDatabase.getInstance().getReference();
        //aqui creamos la referencia con la vase de datos y nombre dela nueva tabla o dato para labase de datos
        RootReff.child("Users").child(messagemSenderID).child("userSate")
                .updateChildren(onlineStateMap);



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
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser currentUser = mAuth.getCurrentUser();//linea que funciona como conector si ubo inico de sesion
        if (currentUser != null) {
            updateUserStates("offline");

        }
    }


}
