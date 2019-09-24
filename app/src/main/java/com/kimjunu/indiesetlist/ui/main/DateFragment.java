package com.kimjunu.indiesetlist.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kimjunu.indiesetlist.App;
import com.kimjunu.indiesetlist.R;
import com.kimjunu.indiesetlist.model.EventModel;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DateFragment extends Fragment implements CalendarView.OnDateChangeListener {
    private static final String TAG = "DateFragment";

    @BindView(R.id.rvEvent)
    RecyclerView rvEvent;

    @BindView(R.id.calendarView)
    CalendarView calendarView;

    @BindView(R.id.layoutCalendar)
    ConstraintLayout layoutCalendar;

    @BindView(R.id.tvDate)
    TextView tvDate;

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

        calendarView.setOnDateChangeListener(this);

        updateEventList(new DateTime());

        return root;
    }

    public void setEventList(ArrayList<EventModel> itemList) {
        mEventList = itemList;

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

        mEventAdapter.setEventList(eventList);
        mEventAdapter.notifyDataSetChanged();

        tvDate.setText(DateTimeFormat.forPattern("yyyy년 M월 d일").print(selectedDate));
    }

    public boolean isCalendarOpened() {
        return layoutCalendar.getVisibility() == View.VISIBLE;
    }

    @OnClick(R.id.fabCalendar)
    public void onFabCalendarClicked() {
        layoutCalendar.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.layoutCalendar)
    public void onLayoutCalendarClicked() {
        layoutCalendar.setVisibility(View.GONE);
    }

    @Override
    public void onSelectedDayChange(CalendarView calendarView, int year, int month, int dayOfMonth) {
        DateTime datetime = new DateTime(year + "-" + (month + 1) + "-" + dayOfMonth);
        updateEventList(datetime);
    }
}
