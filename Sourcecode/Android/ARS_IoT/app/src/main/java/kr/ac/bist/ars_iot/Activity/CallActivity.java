package kr.ac.bist.ars_iot.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import kr.ac.bist.ars_iot.Connection.ConnManager;
import kr.ac.bist.ars_iot.Engine.ARSEngine;
import kr.ac.bist.ars_iot.Engine.SpeakerEngine;
import kr.ac.bist.ars_iot.R;

public class CallActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView call_ctrl;
    private Button call_1, call_2, call_3, call_4, call_5, call_6, call_7, call_8, call_9, call_0, call_s, call_n;
    private String btn_buffer = "";
    private ImageButton call_exit;
    ARSEngine arsEngine;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_call);
        preSetting();
        arsEngine = new ARSEngine();
        arsEngine.nextStep();
    }

    @Override
    public void onClick(View v) {
        if(arsEngine!=null)
            arsEngine.muteEngine();

        switch(v.getId()) {
            case R.id.call_1:
                append("1");
                btn_buffer+="1";
                break;
            case R.id.call_2:
                append("2");
                btn_buffer+="2";
                break;
            case R.id.call_3:
                append("3");
                btn_buffer+="3";
                break;
            case R.id.call_4:
                append("4");
                btn_buffer+="4";
                break;
            case R.id.call_5:
                append("5");
                btn_buffer+="5";
                break;
            case R.id.call_6:
                append("6");
                btn_buffer+="6";
                break;
            case R.id.call_7:
                append("7");
                btn_buffer+="7";
                break;
            case R.id.call_8:
                append("8");
                btn_buffer+="8";
                break;
            case R.id.call_9:
                append("9");
                btn_buffer+="9";
                break;
            case R.id.call_0:
                append("0");
                btn_buffer+="0";
                break;
            case R.id.call_s:
                append("*");
                break;
            case R.id.call_n:
                append("#");
                arsEngine.doStep(btn_buffer);
                btn_buffer = "";
                break;
            case R.id.call_exit:
                finish();
                break;
        }
    }

    public void append(String s) {
        String text = call_ctrl.getText() + s;
        String ellipsizeStr = TextUtils.ellipsize(text, call_ctrl.getPaint(), call_ctrl .getWidth(), TextUtils.TruncateAt.START).toString();
        call_ctrl.setText(ellipsizeStr);
    }
    public void preSetting() {
        call_ctrl = (TextView)findViewById(R.id.call_ctrl);
        call_1 = (Button)findViewById(R.id.call_1);
        call_2 = (Button)findViewById(R.id.call_2);
        call_3 = (Button)findViewById(R.id.call_3);
        call_4 = (Button)findViewById(R.id.call_4);
        call_5 = (Button)findViewById(R.id.call_5);
        call_6 = (Button)findViewById(R.id.call_6);
        call_7 = (Button)findViewById(R.id.call_7);
        call_8 = (Button)findViewById(R.id.call_8);
        call_9 = (Button)findViewById(R.id.call_9);
        call_0 = (Button)findViewById(R.id.call_0);
        call_s = (Button)findViewById(R.id.call_s);
        call_n = (Button)findViewById(R.id.call_n);
        call_exit = (ImageButton) findViewById(R.id.call_exit);

        call_1.setOnClickListener(this);
        call_2.setOnClickListener(this);
        call_3.setOnClickListener(this);
        call_4.setOnClickListener(this);
        call_5.setOnClickListener(this);
        call_6.setOnClickListener(this);
        call_7.setOnClickListener(this);
        call_8.setOnClickListener(this);
        call_9.setOnClickListener(this);
        call_0.setOnClickListener(this);
        call_s.setOnClickListener(this);
        call_n.setOnClickListener(this);
        call_exit.setOnClickListener(this);
    }
}
