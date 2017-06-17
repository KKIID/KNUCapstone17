package kr.ac.bist.iot_noti.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import kr.ac.bist.iot_noti.R;

public class SettingsActivity extends PreferenceActivity {
    private String phoneNbr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings);
        SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        phoneNbr = appPreferences.getString("key_userNbr","");
        Log.d("TAG", phoneNbr+"");
        appPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if(key.equals("key_serverIP")) {
                    Intent i = new Intent(SettingsActivity.this, IntroActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    sendBroadcast(new Intent("kr.ac.bist.iot_noti.settingChange"));
                }
            }
        });
    }

/*    @Override
    protected void onDestroy() {
        Intent intent = new Intent(SettingsActivity.this, ScrollingActivity.class);
        startActivity(intent);
        super.onDestroy();
    }*/
}
