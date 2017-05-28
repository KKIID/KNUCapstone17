package kr.ac.knu.bist.wheather_parse.CardView;

import android.view.View;

/**
 * Created by BIST120 on 2017-05-26.
 */

public class CardViewData {
    public String text;
    public int img;
    public View.OnClickListener listner;

    public CardViewData(String text, int img, View.OnClickListener listener) {
        this.text = text;
        this.img = img;
        this.listner = listener;
    }
}
