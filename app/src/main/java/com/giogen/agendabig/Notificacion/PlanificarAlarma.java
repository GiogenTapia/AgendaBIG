package com.giogen.agendabig.Notificacion;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.giogen.agendabig.Modelos.Agenda;
import com.giogen.agendabig.Principal;
import com.giogen.agendabig.R;


public class PlanificarAlarma extends BroadcastReceiver {
    Agenda agenda;
    String titulo="";


    @Override
    public void onReceive(Context context, Intent intent) {
        lanzarNotificacion(context);
        Bundle bundle=intent.getExtras();
        titulo=bundle.getString("titulo");
    }





    public void lanzarNotificacion(Context context){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Nottificacion";
            NotificationChannel notificationChannel = new NotificationChannel("NOTIFICACION",name,NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }



        Intent intent=new Intent(context, Principal.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,0);
        NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(context,"ficha")
                .setSmallIcon(R.drawable.nota)
                .setContentTitle("Ficha")
                .setContentText("Descripcion")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(0,mBuilder.build());
    }




    public void setMensaje(Agenda agenda) {
        this.agenda = agenda;
    }
}
