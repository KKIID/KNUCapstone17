package kr.ac.bist.ars_iot.Engine;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

/**
 * Created by Bist on 2017-05-21.
 */

public class SpeakerEngine extends AsyncTask<String,Void,Void> {
    private HttpURLConnection connection;
    private final String API_URL = "https://openapi.naver.com/v1/voice/tts.bin";
    private final String APPKEY = "yhHz6QLKYv_abcCfQzrs";
    private final String APP_PW = "Dxj4hxcppJ";
    File f;
    MediaPlayer player;

    @Override
    protected Void doInBackground(String... params) {
        try {
            connection = (HttpURLConnection)new URL(API_URL).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("X-Naver-Client-Id", APPKEY);
            connection.setRequestProperty("X-Naver-Client-Secret", APP_PW);

            String postParams = "speaker=mijin&speed=0&text=" + URLEncoder.encode(params[0],"UTF-8");
            connection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = connection.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                InputStream is = connection.getInputStream();
                int read = 0;
                byte[] bytes = new byte[1024];
                // 랜덤한 이름으로 mp3 파일 생성
                String tempname = Long.valueOf(new Date().getTime()).toString();
                f = File.createTempFile(tempname,".mp3");
                OutputStream outputStream = new FileOutputStream(f);
                while ((read =is.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                is.close();
                player = new MediaPlayer();
                try {
                    player.setDataSource(f.getAbsolutePath());
                    player.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                player.start();
                Log.d("path",f.getAbsolutePath());
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                System.out.println(response.toString());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public void stop_speaker() {
        if(player!=null && player.isPlaying())
            player.stop();
    }
}


