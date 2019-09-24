package com.kimjunu.indiesetlist.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.CalendarView;

public class FlingCalendarView extends CalendarView {


    public FlingCalendarView(Context context) {
        super(context);
    }

    public FlingCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlingCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FlingCalendarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;

            case MotionEvent.ACTION_UP:
                performClick();

                return true;
        }

        return false;
    }

    @Override
    public boolean performClick() {
        super.performClick();

        return true;
    }
}
