package kr.ac.bist.iot_noti.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import kr.ac.bist.iot_noti.Adapter.NotificationData;
import kr.ac.bist.iot_noti.Adapter.NotificationDataAdaptor;
import kr.ac.bist.iot_noti.Permission.PermissionRequester;
import kr.ac.bist.iot_noti.R;
import kr.ac.bist.iot_noti.messaging.ConnManager;

public class ScrollingActivity extends AppCompatActivity {
    private static int RESULT_LOAD_IMAGE = 1;
    private RecyclerView mRecyclerView;
    public static RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<NotificationData> myDataset;
    public static Context mContext;
    private BroadcastReceiver myReceiver;
    SharedPreferences appPreferences;
    private Toolbar toolbar;
    private CollapsingToolbarLayout appBarLayout;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        toolbar.setTitle( appPreferences.getString("key_appname","SmartHome 알리미"));
        setSupportActionBar(toolbar);
        mContext = this;
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        myDataset = new ArrayList<>();
        mAdapter = new NotificationDataAdaptor(myDataset);
        mRecyclerView.setAdapter(mAdapter);
        new TedPermission(ScrollingActivity.this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {

                        if (ActivityCompat.checkSelfPermission(ScrollingActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ) {
                            return;
                        }
                    }
                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                    }
                })
                .setDeniedMessage("문자를 보내기 위해서는 권한이 필요합니다\n\n[설정] > [권한]에서 문자 권한을 켜주세요")
                .setPermissions(Manifest.permission.SEND_SMS)
                .check();

        SharedPreferences image = getSharedPreferences("iotalarm", MODE_PRIVATE);
        if (image.contains("picturePath")) {
            ImageView view = (ImageView) findViewById(R.id.bar_img);
            view.setImageBitmap(BitmapFactory.decodeFile(image.getString("picturePath", "")));
        }
        setUser();
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("kr.ac.bist.iot_noti.fcmNotification");
        intentfilter.addAction("kr.ac.bist.iot_noti.settingChange");


        refreshData();


        myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("kr.ac.bist.iot_noti.fcmNotification")) {
                    refreshData();
                }
                if(intent.getAction().equals("kr.ac.bist.iot_noti.settingChange")){
                    appBarLayout.setTitle(appPreferences.getString("key_appname",""));
                }
            }
        };
        try {
            registerReceiver(myReceiver, intentfilter);
        }catch (Exception e){
            e.printStackTrace();
        }
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TedPermission(ScrollingActivity.this)
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+appPreferences.getString("key_userNbr","")));
                                if (ActivityCompat.checkSelfPermission(ScrollingActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog();

            }
        });
    }

    public boolean setUser() {
        String noti = "";
        String[] keys = {"key_serverIP","key_userName","key_userName2","key_userNbr","key_userAddress"};
        String[] vals = {"홈 서버 IP주소", "사용자 이름", "보호대상자 이름", "응급전화번호", "보호대상자 주소"};
        boolean done = true;
        for(int i = 0; i<keys.length; i++) {
            if (!appPreferences.contains(keys[i])) {
                noti += vals[i]+"\n";
                done = false;
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(ScrollingActivity.this);
        if(!done) {
            builder.setCancelable(false).setTitle("설정이 완료되지 않았습니다.").setMessage(noti).setPositiveButton("설정창", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(ScrollingActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                    finish();
                }
            }).show();
        }
        return done;
    }
    public void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ScrollingActivity.this);
        builder.setCancelable(false).setTitle("신고").setMessage("보호자께서 입력하신 아래 주소로 앰뷸런스를 호출합니다.\n<"+appPreferences.getString("key_userAddress","")+">\n\n"+ getResources().getString(R.string.alarm)).setPositiveButton("신고", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //문자 메세지 발송 부
                SmsManager mSmsManager = SmsManager.getDefault();
                ArrayList<PendingIntent> sentIntent = new ArrayList<PendingIntent>();
                sentIntent.add(0,PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_SENT_ACTION"), 0));
                ArrayList<PendingIntent> deliveredIntent = new ArrayList<PendingIntent>();
                deliveredIntent.add(0,PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_DELIVERED_ACTION"), 0));
                Log.d("TAG", myDataset.size()+"////"+myDataset.get(0).textContent);
                ArrayList<String> arrayList= new ArrayList<String>();
                if(myDataset.size()!=0) {
                    String alarmData = myDataset.get(0).textContent;
                    //응급상황이 발생하였습니다. 신고자 : 홍길동 요구조자 :홍길동 주소 : 대구광역시 북구 대학로80 경북대학교
                    arrayList = mSmsManager.divideMessage(myDataset.get(0).textContent+" 발생하였습니다."
                            + "\n신고자:" + appPreferences.getString("key_userName", "") + "\n요구조자:" + appPreferences.getString("key_userName2", "")+"\n"
                            +"주소:" +appPreferences.getString("key_userAddress",""));
                    mSmsManager.sendMultipartTextMessage("01089159171",null,arrayList,sentIntent,deliveredIntent);

                }else{
                    //응급상황이 발생하였습니다. 신고자 : 홍길동 요구조자 :홍길동
                    arrayList = mSmsManager.divideMessage("응급상황이 발생하였습니다."
                            + "\n신고자:" + appPreferences.getString("key_userName", "") + "\n요구조자:" + appPreferences.getString("key_userName2", "")
                            +"주소:" +appPreferences.getString("key_userAddress",""));
                    mSmsManager.sendMultipartTextMessage("01089159171",null,arrayList,sentIntent,deliveredIntent);
                }
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        //ToolBar Menu Icon 생성
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_settings: {
                Intent intent = new Intent(ScrollingActivity.this, SettingsActivity.class);
                startActivity(intent);
                //finish();
                break;
            }
            case R.id.pic_setting: {
                new TedPermission(this)
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(i, RESULT_LOAD_IMAGE);
                            }
                            @Override
                            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                            }
                        })
                        .setDeniedMessage("사진을 설정하기 위해서는 권한이 필요합니다\n\n[설정] > [권한]에서 저장공간 권한을 켜주세요")
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .check();
                }
                break;
            }
        return super.onOptionsItemSelected(item);
    }
    public void refreshData() {
        myDataset.clear();
        JSONArray array = null;
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(ScrollingActivity.this);
                            builder.setCancelable(false).setTitle("에러").setMessage("서버 응답 없음\n서버의 인터넷 연결 유무를 확인하세요").setPositiveButton("종료", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(ScrollingActivity.this, SettingsActivity.class);
                                    startActivity(intent);
                                    dialog.dismiss();
                                    finish();
                                }
                            }).show();
                        }
                    });
                }
            });
            t.start();
        } else {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object;
                try {
                    object = array.getJSONObject(i);
                    myDataset.add(0, new NotificationData(object.getString("name"), object.getString("createdAt")));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }
    public JSONArray parseJSONString(String string) throws JSONException {
        return new JSONArray(string);
    }
    public String requestJSONString() throws ExecutionException, InterruptedException {
        ConnManager conn = new ConnManager();
        conn.execute("GET", ConnManager.main_url + ConnManager.log_url);
        return conn.get();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            ImageView view = (ImageView) findViewById(R.id.bar_img);
            view.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            SharedPreferences image = getSharedPreferences("iotalarm",MODE_PRIVATE);
            SharedPreferences.Editor editor = image.edit();
            editor.putString("picturePath",picturePath);
            editor.commit();
        }
    }
}

