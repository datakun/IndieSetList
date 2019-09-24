package com.kimjunu.indiesetlist.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class FlingLinearLayout extends LinearLayout {
    private static final String TAG = "FlingLinearLayout";

    public FlingLinearLayout(Context context) {
        super(context);
    }

    public FlingLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlingLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG, "Touch");

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_UP:
                performClick();

                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
