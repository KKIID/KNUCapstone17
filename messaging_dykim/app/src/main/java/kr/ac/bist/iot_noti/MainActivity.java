package kr.ac.bist.iot_noti;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.ac.bist.iot_noti.messaging.HttpGet;
import kr.ac.bist.iot_noti.messaging.HttpPost;

public class MainActivity extends Activity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<MyData> myDataset;
    private SwipeRefreshLayout reLayout;
    private int i = 0;
    private static final String TAG = "MainActivity";
    private HttpPost httpPost;
    private HttpGet httpGet;
    private String token="";
    private String toastMsg="";
    private String getData="";
    public static Context mContext;
    public RecyclerView.Adapter getAdapter(){
        return  mAdapter;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        myDataset = new ArrayList<>();
        mAdapter = new MyAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);

        reLayout = (SwipeRefreshLayout) findViewById(R.id.reLayout);
        reLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { /* 당겨서 새로고침*/
                myDataset.clear();
                Thread getThread = new Thread(new Runnable() {    /*data를 불러오는 Thread*/
                    @Override
                    public void run() {
                        httpGet = new HttpGet();
                        getData = httpGet.requestHttpGet();
                        Log.d("TAG",getData+"//DATA");
                    }
                });
                getThread.start();
                try {
                    getThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                JSONArray array = null;
                try {
                    array = new JSONArray(getData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for(int i = 0; i < array.length(); i++) {
                    JSONObject object;
                    try {
                        object = array.getJSONObject(i);
                        myDataset.add(0,new MyData("시간"+ object.getString("createdAt") , "내용" + object.getString("name")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                mAdapter.notifyDataSetChanged();
                reLayout.setRefreshing(false);

            }
        });
        /* dy 추가*/

        token = FirebaseInstanceId.getInstance().getToken();

        // Log and toast
        String msg = getString(R.string.msg_token_fmt, token);
        Log.d(TAG, msg);
        /*Regist Token 어플 첫 실행 시에 수행하면 된다. -> 서버에서 중복처리 함.*/


        Thread thread = new Thread(new Runnable() {/*Token을 저장하는 thread main이 수행되면 무조건 실행*/
            @Override
            public void run() {
                httpPost = new HttpPost();
                toastMsg  =  httpPost.HttpPostData(Build.ID,token);
                Log.d("TAG","//"+toastMsg);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        thread.start();
        /*                                              */
        Toast.makeText(this, toastMsg,Toast.LENGTH_SHORT).show();


        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }

    }//onCreate
}


class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<MyData> myDataset;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView timeTextView;
        public TextView contentTextView;
        public ViewHolder(View view) {
            super(view);
            timeTextView = (TextView) view.findViewById(R.id.textTime);
            contentTextView = (TextView) view.findViewById(R.id.textContent);
        }
    }

    // Provide a suitable constructor (depends on the kind o
    // f dataset)
    public MyAdapter(ArrayList<MyData> myDataset) {
        this.myDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.timeTextView.setText(myDataset.get(position).textTime);
        holder.contentTextView.setText(myDataset.get(position).textContent);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return myDataset.size();
    }
}

class MyData {
    public String textTime;
    public String textContent;

    public MyData(String textTime, String textContent) {
        this.textTime = textTime;
        this.textContent = textContent;
    }
}