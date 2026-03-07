package com.kamukariyer.app;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MyApplication extends Application {

    public static final String CHANNEL_ID = "kamu_ilanlari_channel";
    public static final String CHANNEL_NAME = "Kamu İlanları";
    public static final String CHANNEL_DESC = "Yeni kamu personel alım ilanları";

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Bildirim kanalı oluştur (Android 8.0+ için zorunlu)
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESC);
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
