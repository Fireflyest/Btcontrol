package com.fireflyest.btcontrol.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class AnimProgress extends ProgressBar {
    public AnimProgress(Context context) {
        super(context);
    }

    public AnimProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AnimProgress(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public synchronized void setProgress(int progress) {

        super.setProgress(progress);
    }
}
