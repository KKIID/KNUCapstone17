package kr.ac.bist.iot_noti.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.iid.FirebaseInstanceId;

import kr.ac.bist.iot_noti.R;
import kr.ac.bist.iot_noti.messaging.ConnManager;

/**
 * Created by Bist on 2017-04-02.
 */

public class IntroActivity extends AppCompatActivity{
    ConnManager conn = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);
        setContentView(R.layout.activity_intro);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(IntroActivity.this,MainActivity.class);
                String token = FirebaseInstanceId.getInstance().getToken();
                conn = new ConnManager();
                String[] string = {"name", Build.ID, "key", token};
                conn.execute("POST",ConnManager.main_url+ConnManager.user_url,ConnManager.makeParams(string));
                startActivity(intent);
                finish();
            }
        },2000);
    }
}
