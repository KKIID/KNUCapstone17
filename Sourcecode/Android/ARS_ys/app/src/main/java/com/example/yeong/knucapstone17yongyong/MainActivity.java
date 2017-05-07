package com.example.yeong.knucapstone17yongyong;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView dial_text;
    Intent int_call;
    Button dial_1, dial_2, dial_3, dial_4, dial_5, dial_6, dial_7, dial_8, dial_9, dial_0, dial_s, dial_n, dial_call, dial_del;
    int dial_length=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        dial_call = (Button)findViewById(R.id.dial_call);
        dial_del = (Button)findViewById(R.id.dial_del);

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

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.dial_1:
                dial_text.append("1");
                dial_length++;
                break;
            case R.id.dial_2:
                dial_text.append("2");
                dial_length++;
                break;
            case R.id.dial_3:
                dial_text.append("3");
                dial_length++;
                break;
            case R.id.dial_4:
                dial_text.append("4");
                dial_length++;
                break;
            case R.id.dial_5:
                dial_text.append("5");
                dial_length++;
                break;
            case R.id.dial_6:
                dial_text.append("6");
                dial_length++;
                break;
            case R.id.dial_7:
                dial_text.append("7");
                dial_length++;
                break;
            case R.id.dial_8:
                dial_text.append("8");
                dial_length++;
                break;
            case R.id.dial_9:
                dial_text.append("9");
                dial_length++;
                break;
            case R.id.dial_0:
                dial_text.append("0");
                dial_length++;
                break;
            case R.id.dial_s:
                dial_text.append("*");
                dial_length++;
                break;
            case R.id.dial_n:
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
                int_call = new Intent(getApplicationContext(),CallActivity.class);
                int_call.putExtra("phone",dial_text.getText().toString());
                startActivity(int_call);
        }

        if (dial_length==4 || dial_length==9) {
            String temp = dial_text.getText().toString().substring(dial_length-1,dial_length);
            dial_text.setText(dial_text.getText().toString().substring(0,dial_length-1)+"-"+temp);
            dial_length++;
        }
    }
}
