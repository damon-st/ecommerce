package com.damon.ecommerce.Notification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import com.damon.ecommerce.Chat.ChatActivity;
import com.damon.ecommerce.Chat.NotificationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String sented = remoteMessage.getData().get("sented");
        String user = remoteMessage.getData().get("user");

        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        String currentUser = preferences.getString("currentuser", "none");

        // String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        try {
            if (firebaseUser != null  && sented.equals(firebaseUser.getUid())){
                if (!currentUser.equals(user)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        sendOreoNotification(remoteMessage);
                    } else {
                        sendNotification(remoteMessage);
                    }
                }
            }
        }catch (Exception e){
            System.out.println("ERROR--"+e);
        }

    }

    private void sendOreoNotification(RemoteMessage remoteMessage){

        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

//        SharedPreferences preferences = getSharedPreferences("comentarios", Context.MODE_PRIVATE);
//        String pref = preferences.getString("coment","s");

        if (title.equals("Un nuevo Comentario")) {

            Intent intent = new Intent(this, NotificationActivity.class);
            redirigirActivityNormal(remoteMessage,user,icon,title,body,intent);

        } else if (title.equals("Estado Producto")){

            Intent intent = new Intent(this, NotificationActivity.class);
            redirigirActivityNormal(remoteMessage,user,icon,title,body,intent);

        }else if (title.equals("Respondio Tu Pregunta")){
            Intent intent = new Intent(this, NotificationActivity.class);
            redirigirActivityNormal(remoteMessage,user,icon,title,body,intent);
        }else{
            Intent intent = new Intent(this, ChatActivity.class);
            redirigirActivityNormal(remoteMessage,user,icon,title,body,intent);
        }

    }




    private void sendNotification(RemoteMessage remoteMessage) {

        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

//        SharedPreferences preferences = getSharedPreferences("comentarios", Context.MODE_PRIVATE);
//        String pref = preferences.getString("coment","s");

        if (title.equals("Un nuevo Comentario")) {
            Intent intent = new Intent(this, NotificationActivity.class);
            redirigirActivity(remoteMessage,user,icon,title,body,intent);
        } else if (title.equals("Estado Producto")){
            Intent intent = new Intent(this, NotificationActivity.class);
            redirigirActivity(remoteMessage,user,icon,title,body,intent);
        }else if (title.equals("Respondio Tu Pregunta")){
            Intent intent = new Intent(this, NotificationActivity.class);
            redirigirActivity(remoteMessage,user,icon,title,body,intent);
        }else{
            Intent intent = new Intent(this, ChatActivity.class);
            redirigirActivity(remoteMessage,user,icon,title,body,intent);
        }
    }

    private void redirigirActivity(RemoteMessage remoteMessage, String user, String icon, String title, String body, Intent intent) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
//        Intent intent = new Intent(this, NotificationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userid", user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        OreoNotification oreoNotification = new OreoNotification(this);
        try {

//            Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent,
//                    defaultSound, icon);
//
//            int i = 0;
//            if (j > 0) {
//                i = j;
//            }
//
//            oreoNotification.getManager().notify(i, builder.build());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void redirigirActivityNormal(RemoteMessage remoteMessage, String user, String icon, String title, String body, Intent intent) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
//        Intent intent = new Intent(this, NotificationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userid", user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification oreoNotification = new OreoNotification(this);
        Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent,
                defaultSound, icon);

        int i = 0;
        if (j > 0) {
            i = j;
        }

        oreoNotification.getManager().notify(i, builder.build());
    }




}
