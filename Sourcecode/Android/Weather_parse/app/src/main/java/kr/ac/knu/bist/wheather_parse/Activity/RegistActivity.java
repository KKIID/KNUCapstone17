package kr.ac.knu.bist.wheather_parse.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import kr.ac.knu.bist.wheather_parse.ModuleRegist.moduleAdapter;
import kr.ac.knu.bist.wheather_parse.ModuleRegist.moduleItem;
import kr.ac.knu.bist.wheather_parse.R;

public class RegistActivity extends AppCompatActivity {

    private ListView moduleListView;
    private ArrayList<moduleItem> moduleItems;
    private moduleAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        getSupportActionBar().setTitle("기기 추가");
        moduleListView = (ListView)findViewById(R.id.moduleList);
        setModuleList();
        moduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                createCheckDialog(moduleItems.get(i).getMoudleName());
                /*서버에 모듈을 등록하도록 만들어야 함.*/
            }
        });

    }

    public void createCheckDialog(String moduleName){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("기기 추가").setMessage(moduleName+"을(를) 추가하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                /*서버와 통신하여 기기를 등록하도록 해야함.*/
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
        moduleItems.add(new moduleItem(R.drawable.alert,"비상스위치"));

        Log.d("TAG", moduleItems.get(0).getMoudleName());

        adapter = new moduleAdapter(this,R.layout.module_items,moduleItems);
        Log.d("TAG",adapter.getItem(0).get(0).getMoudleName());

        moduleListView.setAdapter(adapter);
    }
}