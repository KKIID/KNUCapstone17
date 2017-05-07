package kr.ac.bist.iot_noti.messaging;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by BIST120 on 2017-05-07.
 */
/*웹 서버로 Client Token을 보내기 위함.*/
public class HttpPost {
    private String resultStr="";
    public String HttpPostData(String phoneID, String token){
        try {
            URL url = new URL("http://bist.knu.ac.kr:9191/notis"/*Token 등록할 php 서버 주소*/);

            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setUseCaches(false);
            http.setDoInput(true);              //서버에서 읽기 모드
            http.setDoOutput(true);             //서버로 쓰기 모드
            http.setRequestMethod("POST");  //전송방식 POST

            /*서버로 값을 전송.*/
            StringBuffer buffer = new StringBuffer();
            buffer.append("name="+phoneID+"&key=").append(token);         //php 변수에 값 대입

            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(),"EUC-KR");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();

            InputStreamReader inputStream = new InputStreamReader(http.getInputStream(),"EUC-KR");
            BufferedReader reader = new BufferedReader(inputStream);
            StringBuilder builder = new StringBuilder();
            String str;
            while((str=reader.readLine())!=null){
                builder.append(str+"\n");
            }
            resultStr = builder.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            resultStr = "ERROR: Server is not connected.";
            Log.d("TAG", "ERROR: Server is not connected.");
            e.printStackTrace();
        }
        return resultStr;


    }
}
