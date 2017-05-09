package kr.ac.bist.iot_noti.Activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import kr.ac.bist.iot_noti.Adapter.NotificationData;
import kr.ac.bist.iot_noti.Adapter.NotificationDataAdaptor;
import kr.ac.bist.iot_noti.R;
import kr.ac.bist.iot_noti.Service.MyFirebaseMessagingService;
import kr.ac.bist.iot_noti.messaging.ConnManager;

public class ScrollingActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    public static RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<NotificationData> myDataset;
    private SwipeRefreshLayout reLayout;
    public static Context mContext;
    private BroadcastReceiver myReceiver;
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
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("kr.ac.bist.iot_noti.fcmNotification");
        refreshData();
        myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                    if(intent.getAction().equals("kr.ac.bist.iot_noti.fcmNotification")){
                        refreshData();
                    }
            }
        };
        registerReceiver(myReceiver, intentfilter);
        /*메모리 누출 방지*/
        //unregisterReceiver(myReceiver);

        /*메시지 버튼 동작*/
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    public void refreshData() {
        myDataset.clear();
        JSONArray array = null;
        try {
            array = parseJSONString(requestJSONString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(array == null) {
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
                    myDataset.add(0, new NotificationData( object.getString("name"), object.getString("createdAt")));
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
        conn.execute("GET",ConnManager.main_url+ConnManager.log_url);
        return conn.get();
    }
    public RecyclerView.Adapter getAdapter(){
        return  mAdapter;
    }
}
