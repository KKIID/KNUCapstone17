package kr.ac.knu.bist.wheather_parse.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import kr.ac.knu.bist.wheather_parse.AlarmManage.AlarmManage;
import kr.ac.knu.bist.wheather_parse.CardView.CardViewAdapter;
import kr.ac.knu.bist.wheather_parse.CardView.CardViewData;
import kr.ac.knu.bist.wheather_parse.Connection.ConnManager;
import kr.ac.knu.bist.wheather_parse.CustomNotification.CustomNotification;
import kr.ac.knu.bist.wheather_parse.DataRequest.weatherIO;
import kr.ac.knu.bist.wheather_parse.DataRequest.weatherItems;
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
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Button.OnClickListener {

    private weatherParse myWeatherParse = null;
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
    private SharedPreferences appPreferences;
    private String moduleName;
    private BroadcastReceiver broadcastReceiver;
    private weatherIO myweatherIO;
    private AlarmManage alarmManage=null;
    private weatherItems w;
    private long mLastClickTime;

   private CustomNotification customNotification;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xmlIdConnection();

        alarmManage = new AlarmManage();
        alarmManage.registAlarm(getApplicationContext());
        //저장된 IP 호출
        connManager = new ConnManager();
        connManager.setMain_url(appPreferences.getString("key_serverIP",""));

        myweatherIO = new weatherIO(getApplicationContext());

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

        /*받아 온 날씨 정보를 notification에 전달*/
        customNotification.showCustomNotification("",0,0,0,weatherParse.weatherIconUI(weatherState,sunSetRise));

        /*기기 목록을 가져오기 위해 필요한 부분*/
        myDataset = new ArrayList<>();
        mAdapter = new CardViewAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("kr.ac.bist.iot_noti.moduleRegist");
        refreshModuleList();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("kr.ac.bist.iot_noti.moduleRegist")){
                    refreshModuleList();
                }

            }
        };
        registerReceiver(broadcastReceiver, intentfilter);

    }
    public void refreshModuleList() {
        myDataset.clear();
        JSONArray array=null;
        try {
            array = parseJSONString(requestJSONString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (array == null) {
            final Handler mHandler = new Handler();
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setCancelable(false).setTitle("에러").setMessage("서버 응답 없음\n서버의 인터넷 연결 유무를 확인하세요").setPositiveButton("종료", new                                        DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                        }
                    });
                }
            });
            t.start();
        } else {
            for (int i = 0; i < array.length(); i++) {
                final JSONObject object;
                try {
                    final int temp = i;
                    object = array.getJSONObject(i);
                    moduleName = object.getString("name");
                    final int num = object.getInt("id");
                    int[] icons = {R.drawable.bulb, R.drawable.telev, R.drawable.airconditioner, R.drawable.alert};
                    int selected;
                    char code = '0';
                    switch (moduleName) {
                        case "전등":
                            selected = 0;
                            code = '0';
                            break;
                        case "텔레비전":
                            selected = 1;
                            code = '1';
                            break;
                        case "에어컨":
                            selected = 2;
                            code = 'a';
                            break;
                        case "선풍기":
                            selected = 3;
                            code = '8';
                            break;
                        case "오디오":
                            selected = 3;
                            code = '5';
                            break;
                        case "빔프로젝터":
                            selected = 3;
                            code = '1';
                            break;
                        default:
                            selected = 3;
                            code = '0';
                    }
                    final char finalCode = code;
                    myDataset.add(i,new CardViewData(moduleName, icons[selected],num, code, new View.OnClickListener() {/*중복 클릭을 방지하는 코드*/
                        @Override
                        public void onClick(View v) {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000){
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            String[] params = {"id", (temp + 1) + "", "status", "1", "code", finalCode + ""};
                            moduleOperate(moduleName, params);
                        }

                    }, new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            connManager = new ConnManager();
                            connManager.execute("DELETE",ConnManager.main_url+ConnManager.dev_url+myDataset.get(temp).id);
                            refreshModuleList();
                            Toast.makeText(getApplicationContext(),moduleName+" 삭제되었습니다.",Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    }));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mAdapter.notifyDataSetChanged();
        }

    }
    public void moduleOperate(String moduleName, String[] params){//String[] params  = {"id","1","status","1"};
        Toast.makeText(getApplicationContext(),moduleName+" 동작 중, 잠시 기다려 주세요.",Toast.LENGTH_SHORT).show();
        String result=null;
        connManager = new ConnManager();
        try {
            result = connManager.execute("PUT",ConnManager.main_url+ConnManager.dev_url,ConnManager.makeParams(params)).get();
            if(result.equals("IOException")|| result.equals("ProtocolException")||result.equals("MalformedURLException")) {
                Toast.makeText(getApplicationContext(), "동작 실패. IP설정을 확인해주세요.",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),moduleName+" 동작 완료.",Toast.LENGTH_SHORT).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    public void InitializingUI(){
        setSupportActionBar(toolbar);
        toolbarLayout.setTitle("  ");
        toolbar.setTitle(" ");
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        customNotification = new CustomNotification(this);

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
            }
            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            }
        }).setRationaleMessage("지도를 이용하기 위해서 권한이 필요합니다.").setPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION).
                check();

        new TedPermission(this).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
            }
            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            }
        }).setRationaleMessage("날씨 정보를 휴대폰 내부에 저장합니다.").setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE).
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
            Intent i = new Intent(this, SettingActivity.class);
            startActivity(i);

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
        myWeatherParse = new weatherParse(latitude, longitude);
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
        try {
            humidity = (int) Double.parseDouble((weatherState.get(8).toString()));
            temp = (int) Double.parseDouble(weatherState.get(5).toString());
            airConditionValue = (int) Double.parseDouble(airCondition.get(0).toString());
            airConditonString = airCondition.get(1);
        }catch (NullPointerException e){
            e.printStackTrace();/*알람매니저가 돌아가지 않아서 데이터가 없을 수 있음*/
            airConditionValue=0;
            airConditonString=" ";
            humidity=0;
            temp = 0;
        }

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
        button1 = (Button)findViewById(R.id.button111);
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
        appPreferences = PreferenceManager.getDefaultSharedPreferences(this);


    }
    private void createListener(){
        homeIconView.setOnClickListener(this);
        weatherLocation.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
    }
    private void weatherParseStart()  {
        /*weatherState index 설명*/
        /*0:풍향(degree) 1:풍속(m/s) 2:강수형태코드 3:1시간 누적 강수량(or적설량) 4:하늘상태코드명 5:1시간현재기온 6:오늘의 최고기온 7:오늘의 최저기온
         * 8:상대습도 9:낙뢰유무 10:발표시간
         * 자세한 것은 weatherParse 클래스 주석 참고*/
         /*sunSetRise index 설명*/
         /*0:sunRise Time 1:sunSet Time*/
         /*airCondition index 설명*/
         /*농도(㎍/㎥) - 0~30: 좋음, 31~80: 보통, 81~120: 약간나쁨, 121~200: 나쁨, 201~300: 매우나쁨
        /*등급 - 좋음, 보통, 약간나쁨, 나쁨, 매우나쁨*/
         /*데이터는 알람매니저에서 주기적으로 불러와준다.*/
        w = myweatherIO.weatherRead();/*저장되어 있는 데이터를 읽어온다.*/
        if(w!=null) {
            airCondition = w.getAirCondition();
            weatherState = w.getWeatherState();
            sunSetRise = w.getSunSetRise();
        }else{/*저장되어 있는 데이터가 없다면 우선 불러온다.*/
            try {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getSavedLocation();
                        myWeatherParse = new weatherParse(latitude,longitude);
                        try {
                            airCondition = myWeatherParse.getAirCondition();
                            weatherState = myWeatherParse.getWeather();
                            sunSetRise = myWeatherParse.getSunsetrise();
                            /*public weatherItems(int saveTime*//*데이터가 저장된 시간*//*,String longitude,String latitude, ArrayList<String> airCondition, ArrayList<String> weatherState,
                                ArrayList<String> sunSetRise, ArrayList<String> alertWeather, ArrayList<String> Laundary, ArrayList<String> UltraViolet, ArrayList<String> FeelTemp,
                                ArrayList<String> Discomfort, ArrayList<String> SkinProblem)*/
                            w = new weatherItems(Integer.parseInt(myWeatherParse.getWeather().get(10).substring(11, 13)), longitude, latitude,
                                    myWeatherParse.getAirCondition(), myWeatherParse.getWeather(), myWeatherParse.getSunsetrise(), myWeatherParse.getalertWeather(),
                                    myWeatherParse.getLaundary(),myWeatherParse.getUltraViolet(),myWeatherParse.getFeelTemp(),myWeatherParse.getDiscomfort(),myWeatherParse.getSkinProblem());

                            myweatherIO.weatherWrite(w);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                t.start();
                t.join();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.homeIconView||view.getId()==R.id.weatherLocation){
            Intent i = new Intent(MainActivity.this,MapActivity.class);
            startActivity(i);
            finish();
        }
        if (view.getId()==R.id.button111){

            new CustomNotification(this).showCustomNotification("히히",10,10,10,2130903040);
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


    public JSONArray parseJSONString(String string) throws JSONException {
        return new JSONArray(string);
    }
    public String requestJSONString() throws ExecutionException, InterruptedException {
        ConnManager conn = new ConnManager();
        conn.execute("GET", ConnManager.main_url + ConnManager.dev_url);
        return conn.get();
    }
}