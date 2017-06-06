package kr.ac.knu.bist.wheather_parse.CardView;

import android.view.View;

/**
 * Created by BIST120 on 2017-05-26.
 */

public class CardViewData {
    public String text;
    public int id;
    public int img;
    public char code;
    public View.OnClickListener listner;
    public View.OnLongClickListener longlistener;


    public CardViewData(String text, int img, int id, char code,View.OnClickListener listener, View.OnLongClickListener longlistener ) {
        this.text = text;
        this.img = img;
        this.id = id;
        this.code = code;
        this.listner = listener;
        this.longlistener = longlistener;
    }
}
