package com.kimjunu.indiesetlist.ui.main;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.kimjunu.indiesetlist.R;
import com.kimjunu.indiesetlist.model.EventModel;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DateFragment extends Fragment {
    private static final String TAG = "DateFragment";

    @BindView(R.id.tvEmptyEvent)
    TextView tvEmptyEvent;

    @BindView(R.id.rvEvent)
    RecyclerView rvEvent;

    @BindView(R.id.layoutCalendar)
    ConstraintLayout layoutCalendar;

    @BindView(R.id.tvDate)
    TextView tvDate;

    @BindView(R.id.calendarView)
    CalendarView calendarView;

    EventAdapter mEventAdapter = null;

    RecyclerView.LayoutManager mLayoutManager = null;

    ArrayList<EventModel> mEventList = new ArrayList<>();

    public static DateFragment newInstance(Bundle bundle) {
        DateFragment fragment = new DateFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_date, container, false);

        ButterKnife.bind(this, root);

//        Bundle bundle = getArguments();
//
//        mEventList = bundle.getParcelableArrayList(App.ARGS_EVENT_LIST);

        rvEvent.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        rvEvent.setLayoutManager(mLayoutManager);

        mEventAdapter = new EventAdapter();
        rvEvent.setAdapter(mEventAdapter);

        calendarView.setOnDayClickListener(eventDay -> {
            DateTime datetime = new DateTime(eventDay.getCalendar().getTime());
            Log.e(TAG, datetime.toString());
            updateEventList(datetime);
        });

        updateEventList(new DateTime());

        return root;
    }

    public void setEventList(ArrayList<EventModel> itemList) {
        mEventList = itemList;

        // 달력 업데이트
        ArrayList<EventDay> events = new ArrayList<>();

        for (EventModel event : mEventList) {
            DateTime date = new DateTime(event.date);
            Calendar calendar = date.toCalendar(Locale.getDefault());
            events.add(new EventDay(calendar, R.drawable.ic_queue_music_black_24dp));
        }

        calendarView.setEvents(events);

        updateEventList(new DateTime());
    }

    private void updateEventList(DateTime selectedDate) {
        // 불필요한 시, 분, 초를 제거
        LocalDate sourceDate = selectedDate.toLocalDate();

        ArrayList<EventModel> eventList = new ArrayList<>();

        for (EventModel event : mEventList) {
            if (sourceDate.compareTo(event.date.toLocalDate()) == 0) {
                eventList.add(event);
            }
        }

        if (eventList.isEmpty()) {
            tvEmptyEvent.setVisibility(View.VISIBLE);
            rvEvent.setVisibility(View.INVISIBLE);
        } else {
            tvEmptyEvent.setVisibility(View.INVISIBLE);
            rvEvent.setVisibility(View.VISIBLE);
        }

        mEventAdapter.setEventList(eventList);
        mEventAdapter.notifyDataSetChanged();

        tvDate.setText(DateTimeFormat.forPattern("yyyy년 M월 d일").print(selectedDate));
    }

    public boolean isCalendarOpened() {
        return layoutCalendar.getVisibility() == View.VISIBLE;
    }

    @OnClick(R.id.fabCalendar)
    public void onFabCalendarClicked() {
        if (layoutCalendar.getVisibility() == View.GONE)
            layoutCalendar.setVisibility(View.VISIBLE);
        else
            layoutCalendar.setVisibility(View.GONE);
    }

    @OnClick(R.id.layoutCalendar)
    public void onLayoutCalendarClicked() {
        layoutCalendar.setVisibility(View.GONE);
    }
}
