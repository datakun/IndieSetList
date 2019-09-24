package com.kimjunu.indiesetlist.ui.main;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kimjunu.indiesetlist.App;
import com.kimjunu.indiesetlist.PerformListActivity;
import com.kimjunu.indiesetlist.R;
import com.kimjunu.indiesetlist.model.ArtistModel;
import com.kimjunu.indiesetlist.model.EventModel;
import com.kimjunu.indiesetlist.model.PerformModel;
import com.nex3z.flowlayout.FlowLayout;

import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private static final String TAG = "EventAdapter";

    ArrayList<EventModel> mEventList = new ArrayList<>();

    Context mContext = null;

    public class EventViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cardEvent)
        CardView cardEvent;

        @BindView(R.id.tvDate)
        TextView tvDate;

        @BindView(R.id.tvVenue)
        TextView tvVenue;

        @BindView(R.id.layoutArtists)
        FlowLayout layoutArtists;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.cardEvent)
        public void onCardEventClicked() {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                // 연주 목록 가져오기
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection(App.ARGS_PERFORMANCE)
                        .whereEqualTo("event", mEventList.get(position).id)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                HashMap<String, ArrayList<PerformModel>> performList = new HashMap<>();

                                if (task.isSuccessful() && task.getResult() != null) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        // 연주 모델 만들기
                                        PerformModel perform = new PerformModel();
                                        perform.id = document.getId();
                                        perform.artist = ((HashMap<String, Object>) document.getData().get("artist")).get("name").toString();
                                        perform.date = document.get("date").toString();
                                        perform.venue = ((HashMap<String, Object>) document.getData().get("venue")).get("name").toString();
                                        HashMap<String, Object> video = ((HashMap<String, Object>) document.getData().get("video"));
                                        perform.videoId = video.get("id").toString();
                                        perform.videoTitle = video.get("title").toString();

                                        ArrayList<PerformModel> performs = null;
                                        if (performList.containsKey(perform.artist) == false) {
                                            // 새로운 음악가면 음악가 추가
                                            performs = new ArrayList<>();

                                            performList.put(perform.artist, performs);
                                        } else {
                                            // 기존 음악가면 음악가 가져오기
                                            performs = performList.get(perform.artist);
                                        }

                                        // 연주 추가
                                        performs.add(perform);
                                    }
                                } else {
                                    Log.e(TAG, "Error getting documents.", task.getException());
                                }

                                String eventTitle = tvDate.getText().toString() + ", " + tvVenue.getText().toString();
                                Intent intent = new Intent(mContext, PerformListActivity.class);
                                intent.putExtra(App.ARGS_TITLE, eventTitle);
                                intent.putExtra(App.ARGS_PERFORM_LIST, performList);

                                mContext.startActivity(intent);
                            }
                        });
            }
        }
    }

    public void setEventList(ArrayList<EventModel> eventList) {
        mEventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);

        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventModel event = mEventList.get(position);

        holder.tvDate.setText(DateTimeFormat.forPattern("yyyy년 M월 d일").print(event.date));
        holder.tvVenue.setText(event.venue.name);
        holder.layoutArtists.removeAllViews();

        for (ArtistModel artist : event.artists) {
            TextView tvArtist = new TextView(mContext);
            tvArtist.setText(artist.name);
            holder.layoutArtists.addView(tvArtist);
        }
    }

    @Override
    public int getItemCount() {
        return mEventList.size();
    }
}
