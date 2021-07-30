package com.damon.ecommerce.Chat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.damon.ecommerce.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import dmax.dialog.SpotsDialog;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageViewerActivity extends AppCompatActivity {
    private ImageView imageView;
    private String imageUrl;
    private PhotoViewAttacher photoViewAttacher;//esta libreria hace todo el trabajo del zoom
    private ImageView dowloadImage;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        progressBar = findViewById(R.id.progesImageViewer);

        dowloadImage = findViewById(R.id.btn_dowload);
        imageView = findViewById(R.id.image_viewer);
        imageUrl = getIntent().getStringExtra("url");
        photoViewAttacher = new PhotoViewAttacher(imageView); //creamos una instancia y le pasamos el imageView



        Picasso.get().load(imageUrl).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(R.mipmap.ic_launcher).into(imageView);
            }
        });


        dowloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(ImageViewerActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
                    }
                }else {
                    AlertDialog dialog = new SpotsDialog(ImageViewerActivity.this);
                    dialog.show();
                    dialog.setMessage("Por Favor Espere...");

                    String filename = UUID.randomUUID().toString() + ".png";
                    Picasso.get()
                            .load(imageUrl)
                            .into(new SaveImageHelper(getBaseContext(),
                                    dialog,
                                    getApplicationContext().getContentResolver(),
                                    filename,
                                    "Descripcion"));
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 100:
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    AlertDialog dialog = new SpotsDialog(ImageViewerActivity.this);
                    dialog.show();
                    dialog.setMessage("Por Favor Espere...");

                    String filename = UUID.randomUUID().toString() + ".png";
                    Picasso.get().load(imageUrl)
                            .into(new SaveImageHelper(
                                    getBaseContext(),
                                    dialog,
                                    getApplicationContext().getContentResolver(),
                                    filename,
                                    "Descripcion"));

                    //primer paremetro el contexto
                    //segundo parametro el dialogo
                    //tercero el resolver utiliando picaso
                    //nombre y descipcion
                }else {
                    Toast.makeText(this, "Permiso Necesario Para Descargar la Imagen", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
}
