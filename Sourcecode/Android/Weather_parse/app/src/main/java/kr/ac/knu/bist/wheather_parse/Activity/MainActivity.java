package kr.ac.knu.bist.wheather_parse.Activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import kr.ac.knu.bist.wheather_parse.CardView.CardViewAdapter;
import kr.ac.knu.bist.wheather_parse.CardView.CardViewData;
import kr.ac.knu.bist.wheather_parse.Connection.ConnManager;
import kr.ac.knu.bist.wheather_parse.DataRequest.weatherParse;
import kr.ac.knu.bist.wheather_parse.ProgressBarAnimation;
import kr.ac.knu.bist.wheather_parse.R;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Button.OnClickListener {

    private weatherParse myWeatherPerse = null;
    private ArrayList<String> weatherState, airCondition, sunSetRise;
    private String longitude, latitude;
    private CircleProgress tempProgress, humProgress,airProgress;
    private int humidity,temp, airConditionValue;
    private String airConditonString;
    private TextView weatherLocation, weatherTime,currentWeather,outsideAirCondition;
    private ImageView weatherIcon,homeIconView;
    private Button button1, button2, button3;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<CardViewData> myDataset;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private CollapsingToolbarLayout toolbarLayout;
    private DrawerLayout drawer;
    private ConnManager connManager;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xmlIdConnection();

        /*사실 뭔지 잘모르겠음 ^^ ; 카드뷰 UI 부분임*/
        InitializingUI();

        /*UI event listener 연결*/
        createListener();
        /*최초 실행 시 지도 사용 퍼미션 획득*/
        requestPermission();
        /*기존에 저장되어 있던 위치 불러옴*/
        getSavedLocation();
        /*저장된 위치를 바탕으로 날씨 정보를 UI에 깔아줌*/
        createViewContents();
        /*Test Code*/

        /*기기 목록을 가져오기 위해 필요한 부분*/
        myDataset = new ArrayList<>();
        mAdapter = new CardViewAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);

        /*기기 목록을 가져와서 add를 해주고 changenotified 해줘야함.*/
        //아래는 TEST CODE입니다.
        myDataset.add(new CardViewData("에어컨", R.drawable.airconditioner, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"에어컨 동작 중, 잠시 기다려 주세요.",Toast.LENGTH_SHORT).show();
                connManager = new ConnManager();
                String[] params  = {"id","1","status","1"};
                connManager.execute("PUT",ConnManager.main_url+ConnManager.dev_url,ConnManager.makeParams(params));
                Toast.makeText(getApplicationContext(),"에어컨 동작 완료.",Toast.LENGTH_SHORT).show();
            }
        }));
        mAdapter.notifyDataSetChanged();


    }
    public void InitializingUI(){
        setSupportActionBar(toolbar);
        toolbarLayout.setTitle("  ");
        toolbar.setTitle(" ");
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }
    public void getSavedLocation(){
        SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        longitude = appPreferences.getString("LONGITUDE", "");
        latitude = appPreferences.getString("LATITUDE", "");

        Log.d("TAG","저장되어 있던 위도 경도"+latitude+"/"+longitude);
    }

    public void requestPermission(){
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
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void createViewContents(){
        myWeatherPerse = new weatherParse(latitude, longitude);
        /*sk 웨더플래닛에 있는 날씨 정보 파싱함.*/
        weatherParseStart();
        /*파싱한 데이터를 불러옴*/
        getWeatherValues();
        /*각 UI component를 파싱된 데이터로 세팅함*/
        humProgressUI();
        tempProgressUI();
        airProgressUI();
        weatherIconUI();
        weatherLocation.setText(weatherState.get(11)+" "+weatherState.get(12)+" "+weatherState.get(13));
        weatherTime.setText(weatherState.get(10).substring(5,7)+"월 "+weatherState.get(10).substring(8,10)+"일 "+weatherState.get(10).substring(11,13)+"시 기준");
        currentWeather.setText(weatherState.get(4));
    }

    private void weatherIconUI() {
        Log.d("TAG",weatherState.get(14).toString());
        Calendar cal = Calendar.getInstance();
        int time = cal.get(Calendar.HOUR_OF_DAY);
        Boolean afterSunSet;
        /*sunSet이후를 파악하여 아이콘을 달리하기 위함.*/
        if(time>Integer.parseInt(sunSetRise.get(0).toString().substring(0,2))&&time<Integer.parseInt(sunSetRise.get(1).toString().substring(0,2))){
            afterSunSet=true;
        }else{
            afterSunSet=false;
        }
        if (weatherState.get(14).toString().equals("SKY_O00")) {/*하늘 상태 코드명, 참고 : https://developers.skplanetx.com/apidoc/kor/weather/*/
            weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.weather38));
        }
        if(weatherState.get(14).toString().equals("SKY_O01")){
            if(afterSunSet) {
                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.weather01));
            }else{

                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.weather08));
            }
        }
        if(weatherState.get(14).toString().equals("SKY_O02")){
            if(afterSunSet) {
                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.weather02));
            }else{

                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.weather09));
            }
        }

        if(weatherState.get(14).toString().equals("SKY_O03")){
            if(afterSunSet) {
                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.weather03));
            }
            else{
                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.weather10));
            }
        }
        if(weatherState.get(14).toString().equals("SKY_O04")){
            if(afterSunSet) {
                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.weather12));
            }else {
                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.weather40));
            }
        }
        if(weatherState.get(14).toString().equals("SKY_O05")){
            if(afterSunSet) {
                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.weather13));
            }else {
                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.weather41));
            }
        }
        if(weatherState.get(14).toString().equals("SKY_O06")){
            if(afterSunSet) {
                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.weather14));
            }else {
                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.weather42));
            }
        }
        if(weatherState.get(14).toString().equals("SKY_O07")){
            weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.weather18));
        }
        if(weatherState.get(14).toString().equals("SKY_O08")){
            weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.weather21));
        }
        if(weatherState.get(14).toString().equals("SKY_O09")){
            weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.weather32));
        }
        if(weatherState.get(14).toString().equals("SKY_O10")){
            weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.weather04));
        }
        if(weatherState.get(14).toString().equals("SKY_O11")){
            weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.weather29));
        }if(weatherState.get(14).toString().equals("SKY_O12")){
            weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.weather26));
        }if(weatherState.get(14).toString().equals("SKY_O13")){
            weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.weather27));
        }
        if(weatherState.get(14).toString().equals("SKY_O14")){
            weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.weather28));
        }


    }
    private void getWeatherValues(){
        humidity = (int)Double.parseDouble((weatherState.get(8).toString()));
        temp = (int) Double.parseDouble(weatherState.get(5).toString());
        airConditionValue = (int)Double.parseDouble(airCondition.get(0).toString());
        airConditonString = airCondition.get(1);

    }
    private void airProgressUI(){
        Log.d("TAG",airConditionValue+"");
        airProgress.setMax(350);
        if(airConditionValue<30){/*좋음*/
            airProgress.setMax(50);
            airProgress.setFinishedColor(Color.BLUE);
        }else if(airConditionValue<80){/*보통*/
            airProgress.setMax(90);
            airProgress.setFinishedColor(Color.GREEN);
        }else if(airConditionValue<120){/*약간나쁨*/
            airProgress.setMax(150);
            airProgress.setFinishedColor(Color.YELLOW);
        }else if(airConditionValue<200){/*나쁨*/
            airProgress.setMax(250);
            airProgress.setFinishedColor(Color.LTGRAY);
        }else{/*매우 나쁨*/
            airProgress.setMax(300);
            airProgress.setUnfinishedColor(Color.RED);
        }
        ProgressBarAnimation anim = new ProgressBarAnimation(airProgress, (float)0, (float)airConditionValue);
        anim.setDuration(1000);
        airProgress.startAnimation(anim);
        airProgress.setTextSize((float) 50.0);
        airProgress.setUnfinishedColor(Color.WHITE);
        airProgress.setTextColor(Color.BLACK);
        airProgress.setSuffixText("");
        outsideAirCondition.setText("대기질 "+airConditonString);
    }
    private void humProgressUI(){
        ProgressBarAnimation anim = new ProgressBarAnimation(humProgress, (float)0, (float)humidity);
        anim.setDuration(1000);
        humProgress.startAnimation(anim);
        humProgress.setTextColor(Color.BLACK);
        humProgress.setUnfinishedColor(Color.WHITE);
    }
    private void tempProgressUI(){
        ProgressBarAnimation anim = new ProgressBarAnimation(tempProgress, (float)0, (float)temp);
        anim.setDuration(1000);
        tempProgress.setSuffixText("°C");
        tempProgress.setFinishedColor(Color.argb(255,255,051,051));
        tempProgress.setUnfinishedColor(Color.WHITE);
        tempProgress.setTextColor(Color.BLACK);
        tempProgress.setMax(50);
        tempProgress.startAnimation(anim);
    }
    private void xmlIdConnection(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);
        homeIconView = (ImageView)findViewById(R.id.homeIconView);
        humProgress = (CircleProgress)findViewById(R.id.humProgress);
        tempProgress =(CircleProgress)findViewById(R.id.tempProgress);
        airProgress = (CircleProgress)findViewById(R.id.airCondition);
        weatherLocation = (TextView)findViewById(R.id.weatherLocation);
        weatherIcon = (ImageView) findViewById(R.id.weatherIcon);
        weatherTime = (TextView)findViewById(R.id.weatherTime);
        currentWeather =(TextView)findViewById(R.id.currentWeather);
        outsideAirCondition = (TextView)findViewById(R.id.outsideAir);
        mRecyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

    }
    private void createListener(){
        homeIconView.setOnClickListener(this);
        weatherLocation.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
    }
    private void weatherParseStart() {
        Thread t = new Thread(new Runnable() {/*Thread 빠지면 안됨.*/
            @Override
            public void run() {
                try {
                       /*weatherState index 설명*/
                    /*0:풍향(degree) 1:풍속(m/s) 2:강수형태코드 3:1시간 누적 강수량(or적설량) 4:하늘상태코드명 5:1시간현재기온 6:오늘의 최고기온 7:오늘의 최저기온
                    * 8:상대습도 9:낙뢰유무 10:발표시간
                     * 자세한 것은 weatherParse 클래스 주석 참고*/

                      /*sunSetRise index 설명*/
                     /*0:sunRise Time 1:sunSet Time*/

                     /*airCondition index 설명*/
                    /*농도(㎍/㎥) - 0~30: 좋음, 31~80: 보통, 81~120: 약간나쁨, 121~200: 나쁨, 201~300: 매우나쁨
                       /*등급 - 좋음, 보통, 약간나쁨, 나쁨, 매우나쁨*/
                    airCondition = myWeatherPerse.getAirCondition();
                    weatherState = myWeatherPerse.getWeather();
                    sunSetRise = myWeatherPerse.getSunsetrise();
                    int size = weatherState.size();
                    int size2 = airCondition.size();
                    int size3 = sunSetRise.size();
                    for (int i = 0; i < size3; i++)
                        Log.d("TAG", sunSetRise.get(i).toString());
                    for (int i = 0; i < size2; i++)
                        Log.d("TAG", airCondition.get(i).toString());
                    for (int i = 0; i < size; i++)
                        Log.d("TAG", weatherState.get(i).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.homeIconView||view.getId()==R.id.weatherLocation){
            Intent i = new Intent(MainActivity.this,MapActivity.class);
            startActivity(i);
            finish();
        }
        if (view.getId()==R.id.button1){

            //unregisterReceiver(receiver);
        }
        if(view.getId()==R.id.button2){
            Intent i = new Intent(MainActivity.this,ModeActivity.class);
            startActivity(i);
        }
        if (view.getId()==R.id.button3){
            Intent i = new Intent(MainActivity.this,RegistActivity.class);
            startActivity(i);
        }
    }
}