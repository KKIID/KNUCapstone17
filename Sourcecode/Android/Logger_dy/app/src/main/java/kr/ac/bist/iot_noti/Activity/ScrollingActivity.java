package kr.ac.bist.iot_noti.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
    private int result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        myDataset = new ArrayList<>();
        mAdapter = new NotificationDataAdaptor(myDataset);
        mRecyclerView.setAdapter(mAdapter);

        permissionRequester(Manifest.permission.CAMERA);

        SharedPreferences image = getSharedPreferences("iotalarm",MODE_PRIVATE);
        if(image.contains("picturePath")) {
            ImageView view = (ImageView) findViewById(R.id.bar_img);
            view.setImageBitmap(BitmapFactory.decodeFile(image.getString("picturePath","")));
        }
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("kr.ac.bist.iot_noti.fcmNotification");
        refreshData();


        myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("kr.ac.bist.iot_noti.fcmNotification")) {
                    refreshData();
                }
            }
        };
        registerReceiver(myReceiver, intentfilter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (result == PermissionRequester.ALREADY_GRANTED) {
                    Log.d("RESULT", "권한이 이미 존재함.");
                    if (ActivityCompat.checkSelfPermission(ScrollingActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:010-1111-1111"));
                        startActivity(intent);
                    }
                } else if (result == PermissionRequester.NOT_SUPPORT_VERSION) {
                    Log.d("RESULT", "마쉬멜로우 이상 버젼 아님.");
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:010-1111-1111"));
                    startActivity(intent);
                } else if (result == PermissionRequester.REQUEST_PERMISSION) {
                    Log.d("RESULT", "요청함. 응답을 기다림.");
                    permissionRequester(Manifest.permission.CAMERA);
                    Log.d("permisson",result+"");
                }
            }
        });
    }

    private void permissionRequester(String permission){
        result = new PermissionRequester.Builder(ScrollingActivity.this)
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
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) { // 요청한 권한을 사용자가 "허용" 했다면...
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(ScrollingActivity.this, "권한요청을 허용했습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ScrollingActivity.this, "권한요청을 거부했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
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
                break;
            }
            case R.id.pic_setting: {
                int permission_result = new PermissionRequester.Builder(ScrollingActivity.this)
                        .setTitle("권한 요청")
                        .setMessage("권한을 요청합니다.")
                        .setPositiveButtonName("네")
                        .setNegativeButtonName("아니요.")
                        .create()
                        .request(Manifest.permission.READ_EXTERNAL_STORAGE, 1000, new PermissionRequester.OnClickDenyButtonListener() {
                            @Override
                            public void onClick(Activity activity) {
                                Log.d("xxx", "취소함.");
                            }
                        });
                if (result == PermissionRequester.ALREADY_GRANTED) {
                    Log.d("RESULT", "권한이 이미 존재함.");
                    if (ActivityCompat.checkSelfPermission(ScrollingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, RESULT_LOAD_IMAGE);
                    }
                } else if (result == PermissionRequester.NOT_SUPPORT_VERSION) {
                    Log.d("RESULT", "마쉬멜로우 이상 버젼 아님.");
                    Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                } else if (result == PermissionRequester.REQUEST_PERMISSION) {
                    Log.d("RESULT", "요청함. 응답을 기다림.");
                    permissionRequester(Manifest.permission.READ_EXTERNAL_STORAGE);
                    Log.d("permisson",result+"");
                }
                break;
            }
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
                                    finish();
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
    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
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

