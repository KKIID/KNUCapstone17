package kr.ac.knu.bist.wheather_parse;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import kr.ac.knu.bist.wheather_parse.Permission.PermissionRequester;

public class MainActivity extends AppCompatActivity {

    private weatherParse myWeatherPerse = null;
    private ArrayList<String> weatherState, airCondition, sunSetRise;
    private Button btn;
    private String longitude,latitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences appPreferences = getSharedPreferences("LOCATION",0);
        longitude = appPreferences.getString("LONGITUDE","");
        latitude = appPreferences.getString("LATITUDE","");
        new TedPermission(this).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

            }
        }).setRationaleMessage("지도를 이용하기 위해서 권한이 필요합니다.").setPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION).
                check();
        btn = (Button) findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });




        myWeatherPerse = new weatherParse(latitude, longitude);
        /*weatherState index 설명*/
        /*0:풍향(degree) 1:풍속(m/s) 2:강수형태코드 3:1시간 누적 강수량(or적설량) 4:하늘상태코드명 5:1시간현재기온 6:오늘의 최고기온 7:오늘의 최저기온
        * 8:상대습도 9:낙뢰유무 10:발표시간
        * 자세한 것은 weatherParse 클래스 주석 참고*/

        /*sunSetRise index 설명*/
        /*0:sunRise Time 1:sunSet Time*/

        /*airCondition index 설명*/
        /*농도(㎍/㎥) - 0~30: 좋음, 31~80: 보통, 81~120: 약간나쁨, 121~200: 나쁨, 201~300: 매우나쁨
        /*등급 - 좋음, 보통, 약간나쁨, 나쁨, 매우나쁨*/
        Thread t = new Thread(new Runnable() {/*Thread 빠지면 안됨.*/
            @Override
            public void run() {
                try {
                    airCondition = myWeatherPerse.getAirCondition();
                    weatherState = myWeatherPerse.getWeather();
                    sunSetRise = myWeatherPerse.getSunsetrise();
                    int size = weatherState.size();
                    int size2 = airCondition.size();
                    int size3 = sunSetRise.size();
                    for (int i=0;i<size3;i++)
                        Log.d("TAG", sunSetRise.get(i).toString());
                    for (int i=0;i<size2;i++)
                        Log.d("TAG", airCondition.get(i).toString());
                    for (int i = 0; i < size; i++)
                        Log.d("TAG", weatherState.get(i).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();


    }
    public int permissionRequester(String permission){
        int i= new PermissionRequester.Builder(MainActivity.this)
                .setTitle("권한 요청")
                .setMessage("권한을 요청합니다.")
                .setPositiveButtonName("네")
                .setNegativeButtonName("아니요.")
                .create()
                .request(permission, 1000, new PermissionRequester.OnClickDenyButtonListener() {
                    @Override
                    public void onClick(Activity activity) {
                    }
                });
        return i;
    }
}
