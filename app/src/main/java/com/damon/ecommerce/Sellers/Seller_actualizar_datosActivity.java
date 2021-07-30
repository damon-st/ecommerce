package com.damon.ecommerce.Sellers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.ecommerce.Buyers.HomeActivity;
import com.damon.ecommerce.Buyers.SettinsActivity;
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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Seller_actualizar_datosActivity extends AppCompatActivity {

    private StorageReference storageProlifePictureRef;
    private CircleImageView prolifeImageView;
    private EditText fullNameEditText , userPhoneEditText,addressEditText;
    private TextView prolifeChangeTextBtn , closeTextBtn, saveTextButton,fullEamil;
    private DatabaseReference Sellerreference;


    private Uri imageUri;
    private String myUrl ="";
    private String checker="";
    private StorageTask uploadTask;
    private Button securityQuestionBtn;
    public String  link = "https://firebasestorage.googleapis.com/v0/b/ecommerce-fea29.appspot.com/o/Profile%20pictures%2Fprofile.png?alt=media&token=2cce4a2c-0f73-4684-8672-ccff91b08c31";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_actualizar_datos);

        storageProlifePictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        Sellerreference = FirebaseDatabase.getInstance().getReference().child("Sellers");

        fullNameEditText = findViewById(R.id.seller_actualizar_nombre);
        userPhoneEditText = findViewById(R.id.seller_actualizar_numero_celular);
        addressEditText = findViewById(R.id.seller_actualizar_direccion);
        fullEamil = findViewById(R.id.seller_actualizar_correo);
        securityQuestionBtn = findViewById(R.id.seller_actualizar_boton);
        prolifeImageView = findViewById(R.id.seller_actualizar_foto);

        userInfoDisplay(prolifeImageView,fullNameEditText,userPhoneEditText,addressEditText,fullEamil);


        prolifeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checker = "clicked";
                //Comience a recortar la actividad para la imagen adquirida previamente guardada en el dispositivo
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(Seller_actualizar_datosActivity.this);
            }
        });

        securityQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.equals("clicked")){
                    UserInfoSaved();
                }else {
                    updateOnlyUserInfo();
                }
            }
        });




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
            startActivity(new Intent(Seller_actualizar_datosActivity.this,SellerHomeActivity.class));
            finish();
        }
    }



    private void userInfoDisplay(final CircleImageView prolifeImageView, final EditText fullNameEditText, final EditText userPhoneEditText, final EditText addressEditText,final TextView fullEamil) {
        String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Sellers").child(auth);

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //primero verimificacmos si existe
                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("image").exists()){
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();
                        String email = dataSnapshot.child("email").getValue().toString();

                        Picasso.get().load(image).placeholder(R.drawable.profile).into(prolifeImageView);
                        fullNameEditText.setText(name);
                        userPhoneEditText.setText(phone);
                        addressEditText.setText(address);
                        fullEamil.setText(email);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateOnlyUserInfo() {


        DatabaseReference ref =FirebaseDatabase.getInstance().getReference().child("Sellers");
        String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();

        HashMap<String,Object> userMap = new HashMap<>();
        userMap.put("name",fullNameEditText.getText().toString());
        userMap.put("address",addressEditText.getText().toString());
        userMap.put("phone",userPhoneEditText.getText().toString());
        ref.child(auth).updateChildren(userMap);//esto es para acutlizar en la bvase de datos



        startActivity(new Intent( Seller_actualizar_datosActivity.this, SellerHomeActivity.class));
        Toast.makeText(Seller_actualizar_datosActivity.this, "Perfil Actualizado", Toast.LENGTH_SHORT).show();
        finish();

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

            String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final  StorageReference  fileRef= storageProlifePictureRef
                    //.child(Sellerreference.orderByChild("sid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())+".jpg");
            .child(auth+".jpg");
            uploadTask = fileRef.putFile(imageUri);

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

                        DatabaseReference ref =FirebaseDatabase.getInstance().getReference().child("Sellers");
                        String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        HashMap<String,Object> userMap = new HashMap<>();
                        userMap.put("name",fullNameEditText.getText().toString());
                        userMap.put("address",addressEditText.getText().toString());
                        userMap.put("phone",userPhoneEditText.getText().toString());
                        userMap.put("image",myUrl);
                        ref.child(auth).updateChildren(userMap);//esto es para acutlizar en la bvase de datos

                        progressDialog.dismiss();
                        startActivity(new Intent( Seller_actualizar_datosActivity.this,SellerHomeActivity.class));
                        Toast.makeText(Seller_actualizar_datosActivity.this, "Perfil Actualizado", Toast.LENGTH_SHORT).show();
                        finish();

                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(Seller_actualizar_datosActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else {
            Toast.makeText(this, "imagen no esta seleccionada", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
