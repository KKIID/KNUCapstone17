package kr.ac.bist.iot_noti;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
/**
 * Created by Bist on 2017-04-02.
 */

public class IntroActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //타이틀바, 상태바 모두 없애기
        setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);

        setContentView(R.layout.activity_intro);

        //액션바 감추기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // 2초 후 액티비티 제거
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(IntroActivity.this,MainActivity.class);
                startActivity(intent);

                finish();
            }
        },2000);

    }

}
