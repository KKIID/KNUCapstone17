package kr.ac.bist.iot_noti.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.ExecutionException;

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
                Intent intent = new Intent(IntroActivity.this,ScrollingActivity.class);
                String token = FirebaseInstanceId.getInstance().getToken();
                conn = new ConnManager();
                SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                conn.setMain_url(appPreferences.getString("key_serverIP",""));

                String[] string = {"name", Build.ID, "key", token};
                try {
                    if(conn.execute("POST", ConnManager.main_url + ConnManager.user_url, ConnManager.makeParams(string)).get().equals("IOException")||
                            (conn.execute("POST", ConnManager.main_url + ConnManager.user_url, ConnManager.makeParams(string)).get().equals("ProtocolException"))){
                        //인터넷 연결 문제 발생
                        Toast.makeText(getApplicationContext(),"서버와 연결되지 않습니다. 인터넷 연결 상태를 확인해주세요.",Toast.LENGTH_SHORT).show();

                     }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                startActivity(intent);
                finish();
            }
        },2000);
    }
}
