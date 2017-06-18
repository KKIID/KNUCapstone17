package kr.ac.knu.bist.wheather_parse.Etc;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import com.github.lzyzsd.circleprogress.CircleProgress;

/**
 * Created by BIST120 on 2017-05-25.
 */

public class ProgressBarAnimation extends Animation {
    private CircleProgress progressBar;
    private float from;
    private float  to;

    public ProgressBarAnimation(CircleProgress progressBar, float from, float to) {
        super();
        this.progressBar = progressBar;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        progressBar.setProgress((int) value);
    }

}