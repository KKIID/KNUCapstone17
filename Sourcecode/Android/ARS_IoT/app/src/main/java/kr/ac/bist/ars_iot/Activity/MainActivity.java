package kr.ac.bist.ars_iot.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import kr.ac.bist.ars_iot.Connection.ConnManager;
import kr.ac.bist.ars_iot.Engine.SpeakerEngine;
import kr.ac.bist.ars_iot.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    TextView dial_text;
    Button dial_1, dial_2, dial_3, dial_4, dial_5, dial_6, dial_7, dial_8, dial_9, dial_0, dial_s, dial_n;
    ImageButton dial_call, dial_del;
    int dial_length=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        preSetting();

    }
    @Override
    public void onClick(View v) {
        SpeakerEngine speakerEngine = new SpeakerEngine();
        switch(v.getId()) {
            case R.id.dial_1:
                speakerEngine.execute("1");
                dial_text.append("1");
                dial_length++;
                break;
            case R.id.dial_2:
                speakerEngine.execute("2");
                dial_text.append("2");
                dial_length++;
                break;
            case R.id.dial_3:
                speakerEngine.execute("3");
                dial_text.append("3");
                dial_length++;
                break;
            case R.id.dial_4:
                speakerEngine.execute("4");
                dial_text.append("4");
                dial_length++;
                break;
            case R.id.dial_5:
                speakerEngine.execute("5");
                dial_text.append("5");
                dial_length++;
                break;
            case R.id.dial_6:
                speakerEngine.execute("6");
                dial_text.append("6");
                dial_length++;
                break;
            case R.id.dial_7:
                speakerEngine.execute("7");
                dial_text.append("7");
                dial_length++;
                break;
            case R.id.dial_8:
                speakerEngine.execute("8");
                dial_text.append("8");
                dial_length++;
                break;
            case R.id.dial_9:
                speakerEngine.execute("9");
                dial_text.append("9");
                dial_length++;
                break;
            case R.id.dial_0:
                speakerEngine.execute("0");
                dial_text.append("0");
                dial_length++;
                break;
            case R.id.dial_s:
                speakerEngine.execute("별");
                dial_text.append("*");
                dial_length++;
                break;
            case R.id.dial_n:
                speakerEngine.execute("샵");
                dial_text.append("#");
                dial_length++;
                break;
            case R.id.dial_del:
                if (dial_length==0)
                    break;
                dial_length--;
                if (dial_length==4 || dial_length==9)
                    dial_length--;
                dial_text.setText(dial_text.getText().toString().substring(0,dial_length));
                break;
            case R.id.dial_call:
                if(dial_text.getText().toString().equals("012-3456-789")) {
                    ConnManager manager = new ConnManager();
                    Intent int_call = new Intent(getApplicationContext(),CallActivity.class);
                    String s = null;
                    try {
                        s = manager.execute("GET",ConnManager.main_url).get();
                    } catch (Exception e) {

                    }
                    if(s != null) {
                        int_call.putExtra("phone", dial_text.getText().toString());
                        startActivity(int_call);
                    } else {
                        SpeakerEngine engine = new SpeakerEngine();
                        engine.execute("스마트홈 서버가 꺼져있습니다. 다시 확인후 전화해 주세요.");
                    }
                } else {
                    new TedPermission(MainActivity.this)
                            .setPermissionListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted() {
                                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + dial_text.getText().toString()));
                                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                        return;
                                    }
                                    startActivity(intent);
                                }

                                @Override
                                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                }
                            })
                            .setDeniedMessage("전화를 걸기 위해서는 권한이 필요합니다\n\n[설정] > [권한]에서 전화권한을 켜주세요")
                            .setPermissions(Manifest.permission.CALL_PHONE)
                            .check();
                    break;
                }
        }
        if (dial_length==4 || dial_length==9) {
            String temp = dial_text.getText().toString().substring(dial_length-1,dial_length);
            dial_text.setText(dial_text.getText().toString().substring(0,dial_length-1)+"-"+temp);
            dial_length++;
        }
    }
    public void preSetting() {
        dial_text = (TextView) findViewById(R.id.dial_text);
        dial_1 = (Button)findViewById(R.id.dial_1);
        dial_2 = (Button)findViewById(R.id.dial_2);
        dial_3 = (Button)findViewById(R.id.dial_3);
        dial_4 = (Button)findViewById(R.id.dial_4);
        dial_5 = (Button)findViewById(R.id.dial_5);
        dial_6 = (Button)findViewById(R.id.dial_6);
        dial_7 = (Button)findViewById(R.id.dial_7);
        dial_8 = (Button)findViewById(R.id.dial_8);
        dial_9 = (Button)findViewById(R.id.dial_9);
        dial_0 = (Button)findViewById(R.id.dial_0);
        dial_s = (Button)findViewById(R.id.dial_s);
        dial_n = (Button)findViewById(R.id.dial_n);
        dial_call = (ImageButton) findViewById(R.id.dial_call);
        dial_del = (ImageButton)findViewById(R.id.dial_del);

        dial_1.setOnClickListener(this);
        dial_2.setOnClickListener(this);
        dial_3.setOnClickListener(this);
        dial_4.setOnClickListener(this);
        dial_5.setOnClickListener(this);
        dial_6.setOnClickListener(this);
        dial_7.setOnClickListener(this);
        dial_8.setOnClickListener(this);
        dial_9.setOnClickListener(this);
        dial_0.setOnClickListener(this);
        dial_s.setOnClickListener(this);
        dial_n.setOnClickListener(this);
        dial_call.setOnClickListener(this);
        dial_del.setOnClickListener(this);
        dial_del.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dial_text.setText("");
                dial_length = 0;
                return false;
            }
        });
    }
}
