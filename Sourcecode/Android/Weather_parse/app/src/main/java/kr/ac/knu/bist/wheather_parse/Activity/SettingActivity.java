package kr.ac.knu.bist.wheather_parse.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import kr.ac.knu.bist.wheather_parse.R;

public class SettingActivity extends PreferenceActivity {
    static String old_ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings);
        SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        old_ip = appPreferences.getString("key_serverIP","");

        appPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if(key.equals("key_serverIP")) {
                    Toast.makeText(SettingActivity.this,"변경되었습니다.",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }
}
