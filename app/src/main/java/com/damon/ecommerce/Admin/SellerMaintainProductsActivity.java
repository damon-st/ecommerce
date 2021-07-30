package com.damon.ecommerce.Admin;

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
import android.widget.TextView;
import android.widget.Toast;

import com.damon.ecommerce.Chat.ImageViewerActivity;
import com.damon.ecommerce.R;
import com.damon.ecommerce.Sellers.SellerHomeActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import id.zelory.compressor.Compressor;

public class SellerMaintainProductsActivity extends AppCompatActivity {

    private Button applyChangesBtn, deleteBtn;
    private EditText name,price,description,product_disponible;
    private ImageView imagen;
    private String productID = "";
    private TextView cambiarImagenProducto;

    private DatabaseReference productsRef;
    private Uri uri;
    String checker ="";
    private ProgressDialog loaginBar;
    private Bitmap bitmap;
    private StorageReference ProductImagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_maitain_products);

        productID = getIntent().getStringExtra("pid");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);
        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");

        applyChangesBtn = findViewById(R.id.aplly_changes_btn);
        name = findViewById(R.id.product_name_maintain);
        price = findViewById(R.id.product_Price_maintain);
        description = findViewById(R.id.product_Description_maitain);
        imagen = findViewById(R.id.produc_image_maintain);
        deleteBtn = findViewById(R.id.delete_product_btn);
        product_disponible = findViewById(R.id.product_disponible);
        cambiarImagenProducto = findViewById(R.id.cambiarImagenProducto);
        loaginBar = new ProgressDialog(this);




        displaySepecificProductInfo();


        applyChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.equals("clicket")){
                    guardarSoloImagen();
                }else {
                    applyChanges();
                }
            }
        });

        deleteBtn.setVisibility(View.GONE);
//        deleteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deleteThisProduct();
//            }
//        });

        cambiarImagenProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "clicket";
                CropImage.activity(uri)
                        .start(SellerMaintainProductsActivity.this);
            }
        });




    }

    private void guardarSoloImagen() {
        loaginBar.setTitle("Cambiando Foto");
        loaginBar.setMessage("Porfavor Espera,Cambiando foto...");
        loaginBar.setCanceledOnTouchOutside(false);
        loaginBar.show();
        if (uri != null) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");//mes año
            String saveCurrentDate = currentDate.format(calendar.getTime());

            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");//hora segundos
            String saveCurrentTime = currentTime.format(calendar.getTime());

            String productRandomKey = saveCurrentDate.replace('.', ':').replace(',', ' ') + saveCurrentTime.replace('.', ':').replace(',', ' ');

            final StorageReference filePath = ProductImagesRef.child(uri.getLastPathSegment() + productRandomKey + ".jpg");
            File tumb_filePath = new File(uri.getPath());
            try {
                bitmap = new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(70)
                        .compressToBitmap(tumb_filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
            final byte[] thumb_byte = byteArrayOutputStream.toByteArray();
            final UploadTask uploadTask = filePath.putBytes(thumb_byte);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String myUrl = downloadUri.toString();

                        HashMap<String, Object> objectHashMap = new HashMap<>();
                        objectHashMap.put("image", myUrl);
                        objectHashMap.put("productState", "Pendiente");

                        productsRef.updateChildren(objectHashMap);
                        loaginBar.dismiss();
                        startActivity(new Intent(SellerMaintainProductsActivity.this, SellerHomeActivity.class));
                        Toast.makeText(SellerMaintainProductsActivity.this, "Foto Actualizado", Toast.LENGTH_SHORT).show();
                        finish();


                    } else {
                        loaginBar.dismiss();
                        Toast.makeText(SellerMaintainProductsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void deleteThisProduct() {
     //metodo para eliminar el producto
        productsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(SellerMaintainProductsActivity.this, "El producto se elimino ", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(SellerMaintainProductsActivity.this, SellerHomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void applyChanges() {

        String Pname = name.getText().toString();
        String Pdescription = description.getText().toString();
        String Pprice = price.getText().toString();
        String Pdisponible = product_disponible.getText().toString();

        if (Pname.equals("")){
            Toast.makeText(this, "Escribe el nombre del Producto ", Toast.LENGTH_SHORT).show();
        }else if (Pdescription.equals("")){
            Toast.makeText(this, "Escribe la descripcion del Producto ", Toast.LENGTH_SHORT).show();
        }else if (Pprice.equals("")){
            Toast.makeText(this, "Escribe el precio del Producto ", Toast.LENGTH_SHORT).show();
        }else  if (TextUtils.isEmpty(Pdisponible)){
            Toast.makeText(this, "Escribe La cantidad disponible del producto", Toast.LENGTH_SHORT).show();
        }else {

            HashMap<String ,Object> productMap  = new HashMap<>();
            productMap.put("pid",productID);
            productMap.put("description",Pdescription);
            productMap.put("price",Pprice);
            productMap.put("pname",Pname);
            productMap.put("cantidad",Pdisponible);
            productMap.put("productState","Pendiente");

            productsRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(SellerMaintainProductsActivity.this, "Se actualizo Corrrectamente", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(SellerMaintainProductsActivity.this, SellerHomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }

    private void displaySepecificProductInfo() {

        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    String pName = dataSnapshot.child("pname").getValue().toString();
                    String pPrice = dataSnapshot.child("price").getValue().toString();
                    String pDescription = dataSnapshot.child("description").getValue().toString();
                    final String pImage = dataSnapshot.child("image").getValue().toString();
                    String cantidad = dataSnapshot.child("cantidad").getValue().toString();


                    name.setText(pName);
                    price.setText(pPrice);
                    description.setText(pDescription);
                    product_disponible.setText(cantidad);
                    Picasso.get().load(pImage).into(imagen);

                    imagen.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(SellerMaintainProductsActivity.this, ImageViewerActivity.class);
                            intent.putExtra("url",pImage);
                            startActivity(intent);
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode ==RESULT_OK &&data!=null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            uri = result.getUri();
            imagen.setImageURI(uri);

        }else {
            Toast.makeText(this, "Error, Intenta Nuevamente", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SellerMaintainProductsActivity.this,SellerHomeActivity.class));
            finish();
        }
    }
}
