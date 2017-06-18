package kr.ac.knu.bist.wheather_parse.CustomNotification;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;

import kr.ac.knu.bist.wheather_parse.Activity.MainActivity;
import kr.ac.knu.bist.wheather_parse.R;

/**
 * Created by Bist on 2017-06-18.
 */

public class CustomNotification {
    private NotificationCompat.Builder mBuilder;
    private RemoteViews remoteview;
    private NotificationManager notiManager;

    private ProgressBar pbar_outsideAir, pbar_currentTemp, pbar_currentHum;
    private TextView text_outsideAir, text_currentTemp, text_currentHum;
    private TextView noti_text;
    private Context mContext;

    public CustomNotification(Context context) {
        mContext = context;
    }


    public void showCustomNotification(String s, int temp, int aircon, int hum, int weather_icon) {
        mBuilder = createNotification(s,weather_icon);
        remoteview = new RemoteViews(mContext.getPackageName(), R.layout.custom_notification);
        remoteview.setTextViewText(R.id.noti_text,s);
        remoteview.setProgressBar(R.id.pbar_currentHum,100,hum,false);
        remoteview.setProgressBar(R.id.pbar_currentTemp,50,temp,false);
        remoteview.setProgressBar(R.id.pbar_outsideAir,100,aircon,false);
        remoteview.setImageViewResource(R.id.noti_img,weather_icon);
        mBuilder.setContent(remoteview);
        mBuilder.setContentIntent(createPendingIntent());
        notiManager = (NotificationManager) (mContext.getSystemService(Context.NOTIFICATION_SERVICE));
        notiManager.notify(1,mBuilder.build());
    }

    public PendingIntent createPendingIntent() {
        Intent rIntent = new Intent(mContext, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(rIntent);


        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    public NotificationCompat.Builder createNotification(String s, int icon_id) {

        Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(),icon_id);
        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(mContext)
                .setLargeIcon(icon)
                .setSmallIcon(icon_id)
                .setOngoing(true)
                .setContentTitle("Title")
                .setContentText("text")
                .setVisibility(Notification.VISIBILITY_SECRET)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        return builder;
    }

}
