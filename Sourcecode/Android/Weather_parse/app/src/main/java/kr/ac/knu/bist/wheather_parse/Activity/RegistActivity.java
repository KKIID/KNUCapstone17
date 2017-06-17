package kr.ac.knu.bist.wheather_parse.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import kr.ac.knu.bist.wheather_parse.Connection.ConnManager;
import kr.ac.knu.bist.wheather_parse.ModuleRegist.moduleAdapter;
import kr.ac.knu.bist.wheather_parse.ModuleRegist.moduleItem;
import kr.ac.knu.bist.wheather_parse.R;

public class RegistActivity extends AppCompatActivity {

    private ListView moduleListView;
    private ArrayList<moduleItem> moduleItems;
    private moduleAdapter adapter;
    private ConnManager connManager;
    private SharedPreferences appPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        getSupportActionBar().setTitle("기기 추가");
        appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        moduleListView = (ListView)findViewById(R.id.moduleList);
        setModuleList();
        moduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                createCheckDialog(moduleItems.get(i).getMoudleName(), i);

            }
        });

    }

    public void createCheckDialog(String moduleName, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("기기 추가").setMessage(moduleName+"을(를) 추가하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //기기 등록
                /*connManager = new ConnManager();
                String[] params  = {"id","1","status","1"};
                connManager.execute("PUT",ConnManager.main_url+ConnManager.dev_url,ConnManager.makeParams(params));
                Toast.makeText(getApplicationContext(),"에어컨 동작 완료.",Toast.LENGTH_SHORT).show();*/
                /*서버와 통신하여 기기를 등록하도록 해야함.*/
                 /*서버에 모듈을 등록하도록 만들어야 함.*/
                connManager = new ConnManager();
                String[] string = new String[0];
                try {
                    string = new String[]{"name", URLEncoder.encode(moduleItems.get(position).getMoudleName(),"UTF-8")};
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                connManager.setMain_url(appPreferences.getString("key_serverIP",""));
                connManager.execute("POST", ConnManager.main_url + ConnManager.dev_url, ConnManager.makeParams(string));
                Toast.makeText(getApplicationContext(),"기기 등록",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("kr.ac.bist.iot_noti.moduleRegist");
                sendBroadcast(intent);
                finish();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public void setModuleList(){
        moduleItems = new ArrayList<>();

        moduleItems.add(new moduleItem(R.drawable.bulb,"전등"));
        moduleItems.add(new moduleItem(R.drawable.telev,"텔레비전"));
        moduleItems.add(new moduleItem(R.drawable.airconditioner,"에어컨"));
        moduleItems.add(new moduleItem(R.drawable.fan,"선풍기"));
        moduleItems.add(new moduleItem(R.drawable.beam,"빔프로젝터"));
        moduleItems.add(new moduleItem(R.drawable.camera,"카메라"));
        moduleItems.add(new moduleItem(R.drawable.audio,"오디오"));


        adapter = new moduleAdapter(this,R.layout.module_items,moduleItems);

        moduleListView.setAdapter(adapter);
    }
}