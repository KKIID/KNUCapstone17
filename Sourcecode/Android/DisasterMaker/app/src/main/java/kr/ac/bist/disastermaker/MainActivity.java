package kr.ac.bist.disastermaker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button send;
    private EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send = (Button)findViewById(R.id.send);
        text = (EditText)findViewById(R.id.edit);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = text.getText().toString();
                ConnManager manager = new ConnManager();
                manager.execute(value);
            }
        });
    }
}
