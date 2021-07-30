package com.damon.ecommerce.Buyers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.damon.ecommerce.Chat.ImageViewerActivity;
import com.damon.ecommerce.Model.Users;
import com.damon.ecommerce.Prevalent.Prevalent;
import com.damon.ecommerce.R;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettinsActivity extends AppCompatActivity {

    private CircleImageView prolifeImageView;
    private EditText fullNameEditText , userPhoneEditText,addressEditText;
    private TextView prolifeChangeTextBtn , closeTextBtn, saveTextButton,settings_correo;

    private Uri imageUri;
    private String myUrl ="";
    private StorageReference storageProlifePictureRef;
    private String checker="";
    private StorageTask uploadTask;
    private Button securityQuestionBtn;
    private Bitmap bitmap;
    String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settins);

        storageProlifePictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");


        prolifeImageView = findViewById(R.id.settings_prolife_image);
        fullNameEditText = findViewById(R.id.settings_full_name);
        userPhoneEditText = findViewById(R.id.settings_phone_numer);
        addressEditText = findViewById(R.id.settings_address);
        prolifeChangeTextBtn = findViewById(R.id.profile_image_change_btn);
        closeTextBtn = findViewById(R.id.close_settings);
        saveTextButton= findViewById(R.id.update_settings);
        securityQuestionBtn = findViewById(R.id.security_questions_btn);
        settings_correo = findViewById(R.id.settings_correo);

        userInfoDisplay(prolifeImageView,fullNameEditText,userPhoneEditText,addressEditText,settings_correo);

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.equals("clicked")){
                    UserInfoSaved();
                }else {
                    updateOnlyUserInfo();
                }
            }
        });

        prolifeChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checker = "clicked";
              //Comience a recortar la actividad para la imagen adquirida previamente guardada en el dispositivo
                CropImage.activity(imageUri)
//                        .setAspectRatio(1,1)
                        .start(SettinsActivity.this);



            }
        });
        securityQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SettinsActivity.this, ResestPasswordActivity.class);
                intent.putExtra("check","settings");
                startActivity(intent);

            }
        });
    }

    private void updateOnlyUserInfo() {

        String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference().child("Users").child(auth);

        HashMap<String,Object> userMap = new HashMap<>();
        userMap.put("name",fullNameEditText.getText().toString().toLowerCase());
        userMap.put("address",addressEditText.getText().toString());
        userMap.put("phoneOrder",userPhoneEditText.getText().toString());
        userMap.put("phone",userPhoneEditText.getText().toString());
        ref.updateChildren(userMap);//esto es para acutlizar en la bvase de datos



        startActivity(new Intent( SettinsActivity.this, HomeActivity.class));
        Toast.makeText(SettinsActivity.this, "Perfil Actualizado", Toast.LENGTH_SHORT).show();
        finish();



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode ==RESULT_OK&&data!=null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();//aqui recueramos
            prolifeImageView.setImageURI(imageUri);//aqui asignamos

        }else {
            Toast.makeText(this, "Error, Intenta Nuevamente", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettinsActivity.this,SettinsActivity.class));
            finish();
        }
    }

    private void UserInfoSaved() {
        //metodopara guardar o sdatos
        if (TextUtils.isEmpty(fullNameEditText.getText().toString())){
            Toast.makeText(this, "Nombre es obligatrio", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(this, "Direccion es obligatorio", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(userPhoneEditText.getText().toString())){
            Toast.makeText(this, "Telefono es obligatorio", Toast.LENGTH_SHORT).show();
        }else if (checker.equals("clicked")){
            uploadImage();
        }
    }

    private void uploadImage() {
        //subir iamgen e acutalizar
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Actualizando perfil");
        progressDialog.setMessage("Espera porfavor,actualizando");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if (imageUri != null){
            final  StorageReference  fileRef= storageProlifePictureRef
                    .child(auth+".jpg");

            File tumb_filePath = new File(imageUri.getPath());
            try {
                bitmap = new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(70)
                        .compressToBitmap(tumb_filePath);
            }catch (IOException e){
                e.printStackTrace();
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,70,byteArrayOutputStream);
            final  byte[] thumb_byte =byteArrayOutputStream.toByteArray();

            uploadTask = fileRef.putBytes(thumb_byte);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){

                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                       // DatabaseReference ref =FirebaseDatabase.getInstance().getReference().child("Users");
                        String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                        HashMap<String,Object> userMap = new HashMap<>();
                        userMap.put("name",fullNameEditText.getText().toString().toLowerCase());
                        userMap.put("address",addressEditText.getText().toString());
                        userMap.put("phone",userPhoneEditText.getText().toString());
                        userMap.put("image",myUrl);
                       // ref.child(Prevalent.currentonlineUser.getPhone()).updateChildren(userMap);//esto es para acutlizar en la bvase de datos
                        ref.child(auth).updateChildren(userMap);

                        progressDialog.dismiss();
                        startActivity(new Intent( SettinsActivity.this,HomeActivity.class));
                        Toast.makeText(SettinsActivity.this, "Perfil Actualizado", Toast.LENGTH_SHORT).show();
                        finish();

                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(SettinsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else {
            Toast.makeText(this, "imagen no esta seleccionada", Toast.LENGTH_SHORT).show();
        }
    }

    private void userInfoDisplay(final CircleImageView prolifeImageView, final EditText fullNameEditText, final EditText userPhoneEditText, final EditText addressEditText, final TextView settings_correo) {

       // DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentonlineUser.getPhone());
        final String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        UsersRef.child(auth).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //primero verimificacmos si existe
                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("image").exists()){
                        final String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();
                        String email = dataSnapshot.child("email").getValue().toString();

                        Picasso.get().load(image).placeholder(R.drawable.profile).into(prolifeImageView);
                        fullNameEditText.setText(name);
                        userPhoneEditText.setText(phone);
                        addressEditText.setText(address);
                        settings_correo.setText(email);

                        prolifeImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(SettinsActivity.this, ImageViewerActivity.class);
                                intent.putExtra("url",image);
                                startActivity(intent);
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
}
