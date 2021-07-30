package com.damon.ecommerce.Buyers;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.damon.ecommerce.R;
import static org.apache.commons.lang3.StringUtils.capitalize;
public class OptionsActivity extends AppCompatActivity {
    private LinearLayout llprivacyPolicy,recomendar_amigos,calificar_App,reportar_errores;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        llprivacyPolicy = findViewById(R.id.llprivacypolicy);
        recomendar_amigos  = findViewById(R.id.recomendar_a);
        calificar_App = findViewById(R.id.califica);
        reportar_errores = findViewById(R.id.reportar_errores);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Opciones");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        llprivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             politicaprivadidad();
            }
        });

        recomendar_amigos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecomendarAmigos();
            }
        });

        calificar_App.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalificarApp();
            }
        });
        reportar_errores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportarErroresApp();
            }
        });
    }

    private void ReportarErroresApp() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        PackageManager manager = getApplicationContext().getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String version = info.versionName;
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.developer_email)});
        i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name) + version);
        i.putExtra(Intent.EXTRA_TEXT,
                "\n" + " Dispositivo :" + getDeviceName() +
                        "\n" + " Version Sistema:" + Build.VERSION.SDK_INT +
                        "\n" + " Largo Pantalla  :" + height + "px" +
                        "\n" + " Ancho Pantalla  :" + width + "px" +
                        "\n\n" + "Cual fue el Problema? Porfavor contactanos para ayudarte y que la app sea mejor para ti!" +
                        "\n");
        startActivity(Intent.createChooser(i, "Enviar Email"));
    }

    private void CalificarApp() {
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }


    private void RecomendarAmigos() {
        final String shareappPackageName = getPackageName();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hola te recomiendo  " + getResources().getString(R.string.app_name) + " App at: https://play.google.com/store/apps/details?id=" + shareappPackageName);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void politicaprivadidad() {
        AlertDialog dialog = new AlertDialog.Builder(OptionsActivity.this,R.style.AlertDialog)
                .setTitle(R.string.PRIVACYPOLICY)
                .setMessage(R.string.privacy_message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(R.drawable.ic_info_outline_black_24dp)
                .show();
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        textView.setScroller(new Scroller(OptionsActivity.this));
        textView.setVerticalScrollBarEnabled(true);
        textView.setMovementMethod(new ScrollingMovementMethod());
    }

    //metodo para edevolcer el discposivoto
    private String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }
}
