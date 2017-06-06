package kr.ac.bist.iot_noti.Service;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import kr.ac.bist.iot_noti.Activity.ScrollingActivity;
import kr.ac.bist.iot_noti.R;

/**
 * Created by BIST120 on 2017-05-07.
 */


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private Boolean setVibrate;
    private Vibrator vibrator;
    private AudioManager audioManager;
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    private AlertDialog.Builder builder;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                try {
                    builder.show();
                }catch (Exception e){
                    e.printStackTrace();
                }
                ScrollingActivity.mAdapter.notifyDataSetChanged();

            }
        }
    };

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]
        SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        setVibrate = appPreferences.getBoolean("key_vibrationSet", true);
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wakeLock.acquire(3000);

        switch (audioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
            case AudioManager.RINGER_MODE_VIBRATE:
                vibrator.vibrate(new long[]{200, 100, 200, 100, 200, 100}, -1);
                break;
            case AudioManager.RINGER_MODE_SILENT:
            if (setVibrate) {
                vibrator.vibrate(new long[]{200, 100, 200, 100, 200, 100}, -1);
            }
            break;
        }
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, setVibrate + "//");
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d("TAG", "Send Notification 실행 중 message");

        sendNotification(remoteMessage);
        Log.d("TAG", "Send Notification 실행 중 message");

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }
        try {
            builder = new AlertDialog.Builder(ScrollingActivity.mContext);
            builder.setTitle(remoteMessage.getData().get("title"))
                    .setMessage(remoteMessage.getData().get("body"))
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            vibrator.cancel();
                            dialogInterface.dismiss();
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getData() != null) {

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent("kr.ac.bist.iot_noti.fcmNotification");
                    sendBroadcast(intent);
                    handler.sendEmptyMessage(0);
                }
            });
            thread.run();

        }
    }

    // Also if you intend on generating your own notifications as a result of a received FCM
    // message, here is where that should be initiated. See sendNotification method below.


    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }


    private void sendNotification(RemoteMessage remoteMessage) {             //메세지를 Notification으로 띄워주는 부분.
        Log.d("TAG", "Send Notification 실행 중 message");


        Intent intent = new Intent(this, ScrollingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("body"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{200, 100, 200, 100, 200, 100})
                .setLights(000000000025, 500, 2000);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }
}
