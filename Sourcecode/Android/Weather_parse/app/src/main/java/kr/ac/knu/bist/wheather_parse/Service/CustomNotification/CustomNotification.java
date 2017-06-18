package kr.ac.knu.bist.wheather_parse.Service.CustomNotification;

        import android.app.Notification;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.content.Context;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.support.v4.app.TaskStackBuilder;
        import android.support.v7.app.NotificationCompat;
        import android.widget.RemoteViews;

        import kr.ac.knu.bist.wheather_parse.Activity.MainActivity;
        import kr.ac.knu.bist.wheather_parse.R;

/**
 * Created by Bist on 2017-06-18.
 */

public class CustomNotification {
    public static void showNotification(Context mContext, String text, double temperature, double aircondition, double humidity, int weather_icon,String airString/*대기질*/) {
        NotificationCompat.Builder builder = createNoti(mContext, weather_icon);
        RemoteViews remoteview = createRemoteView(mContext, text, temperature, aircondition, humidity, weather_icon,airString);
        builder.setContent(remoteview);
        builder.setContentIntent(createPIntent(mContext));
        Notification noti = builder.build();
        noti.bigContentView = remoteview;
        NotificationManager notificationManager = (NotificationManager) (mContext.getSystemService(Context.NOTIFICATION_SERVICE));
        notificationManager.notify(1,noti);
    }
    private static PendingIntent createPIntent(Context mContext) {
        Intent rIntent = new Intent(mContext, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(rIntent);
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    private static RemoteViews createRemoteView(Context mContext, String text, double temperature, double aircondition, double humidity, int weather_icon,String airString/*대기질*/) {
        RemoteViews remoteView = new RemoteViews(mContext.getPackageName(), R.layout.custom_notification);
        remoteView.setTextViewText(R.id.noti_text, text);
        remoteView.setProgressBar(R.id.pbar_currentHum,100,(int)humidity,false);
        remoteView.setProgressBar(R.id.pbar_currentTemp,50,(int)temperature,false);
        remoteView.setProgressBar(R.id.pbar_outsideAir,100,(int)aircondition,false);
        remoteView.setTextViewText(R.id.text_currentTemp,temperature+"");
        remoteView.setTextViewText(R.id.text_currentHum,humidity+"");
        remoteView.setTextViewText(R.id.text_outsideAir,airString);
        remoteView.setImageViewResource(R.id.noti_img,weather_icon);
        return remoteView;
    }
    private static NotificationCompat.Builder createNoti(Context mContext, int icon_id) {
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
