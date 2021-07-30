package com.damon.ecommerce.Sellers;

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
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.damon.ecommerce.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import id.zelory.compressor.Compressor;

public class SellerAddNewProductActivity extends AppCompatActivity {

    private String CategoryName, Description,Price,Pname,saveCurrentDate,saveCurrentTime;
    private Button AddNewProductButton;
    private ImageView InputProductImage;
    private EditText InputProductName,InputProductDescripcion,InputProductPrice;
    private static final  int GalleryPick=1;
    private Uri ImageUrl;

    private String productRandomKey, downloadImageUrl;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef,sellerRef;

    private ProgressDialog loaginBar;

    private String sName ,sAddress,sPhone,sEmail,sID;

    private Uri imageUri;
    private String myUrl ="";
    private String checker="";
    private Bitmap bitmap;
    private RadioGroup grupo_estado;
    private EditText cantidad_disponible;
    private String cantidad;
    private String estadoProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_add_new_product);



        CategoryName = getIntent().getExtras().get("Category").toString();
        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        sellerRef = FirebaseDatabase.getInstance().getReference().child("Users");


        Toast.makeText(this, CategoryName, Toast.LENGTH_SHORT).show();

        AddNewProductButton = findViewById(R.id.add_new_product);
        InputProductImage = findViewById(R.id.select_product_image);
        InputProductName = findViewById(R.id.product_name);
        InputProductDescripcion = findViewById(R.id.product_description);
        InputProductPrice = findViewById(R.id.product_price);
        loaginBar = new ProgressDialog(this);
        grupo_estado = findViewById(R.id.grupo_estado);
        cantidad_disponible = findViewById(R.id.cantidad_disponible);

        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });


        sellerRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){
                             sName = dataSnapshot.child("name").getValue().toString();
                             sAddress = dataSnapshot.child("address").getValue().toString();
                             sPhone = dataSnapshot.child("phone").getValue().toString();
                             sEmail = dataSnapshot.child("email").getValue().toString();
                             sID = dataSnapshot.child("uid").getValue().toString();//esto es el ID

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        grupo_estado.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.estado_nuevo){
                    estadoProducto = "Nuevo";
                }else if (checkedId ==R.id.estado_usado){
                    estadoProducto ="Usado";
                }
            }
        });

    }


    private void OpenGallery() {
         //metodo praa entrar a ala galeria
//        Intent galleryIntent = new Intent();
//        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//        galleryIntent.setType("image/*");
//        startActivityForResult(galleryIntent,GalleryPick);


        checker = "clicked";
        //Comience a recortar la actividad para la imagen adquirida previamente guardada en el dispositivo
        CropImage.activity(imageUri)
               // .setAspectRatio(1,1)
                .start(SellerAddNewProductActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode ==RESULT_OK&&data!=null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();//aqui recueramos
            InputProductImage.setImageURI(imageUri);//aqui asignamos

        }else {
            Toast.makeText(this, "Error, Intenta Nuevamente", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SellerAddNewProductActivity.this,SellerHomeActivity.class));
            finish();
        }

    }




    private void ValidateProductData() {

         Description = InputProductDescripcion.getText().toString();
         Price = InputProductPrice.getText().toString();
         Pname = InputProductName.getText().toString().toLowerCase();
         cantidad = cantidad_disponible.getText().toString();
         if (imageUri == null){
             Toast.makeText(this, "Producto imagen es obligatorio", Toast.LENGTH_SHORT).show();
         }else if (TextUtils.isEmpty(Description)){
             Toast.makeText(this, "Porfavor Escribe la Descripcion", Toast.LENGTH_SHORT).show();
         }else if (TextUtils.isEmpty(Price)){
             Toast.makeText(this, "Porfavor Escribe el Precio", Toast.LENGTH_SHORT).show();
         }else if (TextUtils.isEmpty(Pname)){
             Toast.makeText(this, "Porvafor escribe el nombre del producto", Toast.LENGTH_SHORT).show();
         }else if (estadoProducto == null){
             Toast.makeText(this, "Porfavor selecciona el Estado del Producto", Toast.LENGTH_SHORT).show();
         } else if (TextUtils.isEmpty(cantidad)){
             Toast.makeText(this, "Porfavor escribe la cantidad disponible del producto", Toast.LENGTH_SHORT).show();
         }else {
             StoreProductInformation();
         }
    }

    private void StoreProductInformation() {

        loaginBar.setTitle("Añadiendo Producto");
        loaginBar.setMessage("Porfavor Espera,añadiendo producto...");
        loaginBar.setCanceledOnTouchOutside(false);
        loaginBar.show();

        Calendar calendar =  Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");//mes año
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");//hora segundos
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate.replace('.', ':').replace(',', ' ') + saveCurrentTime.replace('.', ':').replace(',', ' ');

        //combinamos con la hoora
        final StorageReference filePath = ProductImagesRef.child(imageUri.getLastPathSegment()+productRandomKey+".jpg");


        File tumb_filePath = new File(imageUri.getPath());
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


        final UploadTask uploadTask = filePath.putBytes(thumb_byte);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String message = e.toString();
                Toast.makeText(SellerAddNewProductActivity.this, "Error:"+message, Toast.LENGTH_SHORT).show();
                loaginBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SellerAddNewProductActivity.this, "Imagen Guardada con Existo", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if (!task.isSuccessful()){
                            throw task.getException();

                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return  filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                          if (task.isSuccessful()){
                              downloadImageUrl = task.getResult().toString();

                              Toast.makeText(SellerAddNewProductActivity.this, "Producto imagen guardado ", Toast.LENGTH_SHORT).show();

                              SaveProductInfoDatabase();

                          }
                    }
                });
            }
        });

    }

    private void SaveProductInfoDatabase() {
        String postid = ProductsRef.push().getKey();

        HashMap<String ,Object> productMap  = new HashMap<>();
        productMap.put("pid",productRandomKey);
        productMap.put("date",saveCurrentDate);
        productMap.put("time",saveCurrentTime);
        productMap.put("description",Description);
        productMap.put("image",downloadImageUrl);
        productMap.put("category",CategoryName);
        productMap.put("price",Price);
        productMap.put("pname",Pname.toLowerCase());

       //nuevo creado para los vendedores
        productMap.put("name",sName);
        productMap.put("address",sAddress);
        productMap.put("phone",sPhone);
        productMap.put("email",sEmail);
        productMap.put("uid",sID);
        productMap.put("postid",postid);
      //  productMap.put("productState","No Aprovado"); como nose como aser el buscador cuando esta ese estado lo apruebo todo
        productMap.put("productState","Pendiente");
        productMap.put("estado",estadoProducto);
        productMap.put("cantidad",cantidad);


        String clearCaracter = productRandomKey.replace('.', ':').replace(',', ' ');


        ProductsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful()){

                           Intent intent = new Intent(SellerAddNewProductActivity.this, SellerHomeActivity.class);
                           startActivity(intent);

                           loaginBar.dismiss();
                           Toast.makeText(SellerAddNewProductActivity.this, "Producto esta añadido Correctamente Esperando Aprovacion", Toast.LENGTH_SHORT).show();
                           finish();
                       }
                       else {
                           loaginBar.dismiss();
                           String message =task.getException().toString();
                           Toast.makeText(SellerAddNewProductActivity.this, "Error"+message, Toast.LENGTH_SHORT).show();
                       }
                    }
                });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
