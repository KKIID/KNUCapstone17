package kr.ac.knu.bist.wheather_parse.DataRequest;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by BIST120 on 2017-06-17.
 */

public class weatherIO implements Serializable/*날씨 정보를 입출력하기 위한 객체*/ {
    private weatherItems myWeatherItems;
    private FileInputStream fis=null;
    private FileOutputStream fos = null;
    private ObjectInputStream ois=null;
    private ObjectOutputStream oos=null;
    private String fileName = "weatherdat.dat";
    private Context context;
    private File file;
    public weatherIO(Context context){
        this.context = context;
        fileName = context.getFilesDir().getPath().toString()+"weatherdat.dat";
        file = new File(fileName);
    }

    public Boolean IsReadable(){
        return file.exists();
    }

    public weatherItems weatherRead(){
        try {
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            myWeatherItems = (weatherItems) ois.readObject();
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return myWeatherItems;
    }
    public void weatherWrite(weatherItems items){
        Log.d("TAG","SAVE");
        try {
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(items);
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}