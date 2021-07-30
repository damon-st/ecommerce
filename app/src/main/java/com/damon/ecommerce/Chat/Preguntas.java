package com.damon.ecommerce.Chat;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.damon.ecommerce.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class Preguntas extends AppCompatActivity {

    private  String pid ;
    private DatabaseReference preguntasRef,UserRef;
    private FirebaseAuth mAuth;
    private String currentGroupName,currentUserID,currentUserName,currentTime,currentData;
    private PreguntasAdapter preguntasAdapter;
    private ImageButton SendMessageButton;
    private EditText enviarMensaje;
    private RecyclerView recyclerView;

    private Toolbar mToolbar;
    private EditText userMessageInput;
    private ScrollView mScrollView;
    private TextView displayTextMessages;


    private DatabaseReference GropuNameRef,GroupMessageKeyRef;





    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_blank);

        pid = getIntent().getStringExtra("pid");
        GropuNameRef = FirebaseDatabase.getInstance().getReference().child("Products").child(pid);
        mAuth = FirebaseAuth.getInstance();
        currentUserID  = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");


        InitializeFields();

        GetUserInfo();

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SaveMessageInfoToDataBase();

                userMessageInput.setText("");

                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }

    private void InitializeFields() {

        mToolbar = findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);//aqui le pasamos el parametro

        SendMessageButton = findViewById(R.id.send_message_button);
        userMessageInput = findViewById(R.id.input_group_message);
        displayTextMessages =findViewById(R.id.group_chat_text_display);
        mScrollView = findViewById(R.id.my_scroll_view);

    }

    private void DisplayMessages(DataSnapshot dataSnapshot) {

        //AQui sera el metodo apra mostrar los mensajes
        Iterator iterator = dataSnapshot.getChildren().iterator();
        //este es un bucle que recorera linea por linea viendo si ay mensajes
        //y si los ay los dibujara utilizando el textView que llamamos displaytextmessages
        while (iterator.hasNext()){

            String chatDate =(String)  ((DataSnapshot)iterator.next()).getValue();
            String chatMessage =(String)  ((DataSnapshot)iterator.next()).getValue();
            String chatName =(String)  ((DataSnapshot)iterator.next()).getValue();
            String chatTime =(String)  ((DataSnapshot)iterator.next()).getValue();

            displayTextMessages.append(chatName+" :\n"+chatMessage+"\n"+chatTime+"     "+chatDate+"\n\n\n");

            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);



        }
    }

    private void SaveMessageInfoToDataBase() {
        //aqui es para poner la ora al moeemnto de enviar un mensaje
        String  message = userMessageInput.getText().toString();
        String messageKey = GropuNameRef.push().getKey();

        if (TextUtils.isEmpty(message)){
            Toast.makeText(this, "porfavor escribe el mensaje", Toast.LENGTH_SHORT).show();
        }else {

            //este es el calendario que utiliza firebase
            // creamos su instanci y lo transformamos
            Calendar ccalForData = Calendar.getInstance();
            SimpleDateFormat currentDataFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentData  = currentDataFormat.format(ccalForData.getTime());


            Calendar ccalForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime   = currentTimeFormat.format(ccalForTime.getTime());

            //este arreglo es para crear un chat dentro del grupo utilizando sus keys
            HashMap<String, Object> groupMessageKey = new HashMap<>();
            GropuNameRef.updateChildren(groupMessageKey);

            GroupMessageKeyRef = GropuNameRef.child(messageKey);//aqui actualizamos lo que recuperamos arriba

            //aqui enviamos los datos al servidor
            HashMap<String , Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name",currentUserName);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",currentData);
            messageInfoMap.put("time",currentTime);
            //aqui se envia las llaves de refrerencia de los mensajes con todos esos datos
            GroupMessageKeyRef.updateChildren(messageInfoMap);


        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        GropuNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void GetUserInfo() {

        //aqui recuperamos los datos

        UserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    currentUserName = dataSnapshot.child("name").getValue().toString();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
