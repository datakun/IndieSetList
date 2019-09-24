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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kimjunu.indiesetlist.App;
import com.kimjunu.indiesetlist.PerformListActivity;
import com.kimjunu.indiesetlist.R;
import com.kimjunu.indiesetlist.model.ArtistModel;
import com.kimjunu.indiesetlist.model.PerformModel;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {
    private static final String TAG = "EventAdapter";

    ArrayList<PerformModel> mPerformList = new ArrayList<>();
    ArrayList<ArtistModel> mArtistList = new ArrayList<>();

    Context mContext = null;

    public class ArtistViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cardArtist)
        CardView cardArtist;

        @BindView(R.id.tvArtist)
        TextView tvArtist;

        @BindView(R.id.tvEvent1)
        TextView tvEvent1;

        @BindView(R.id.tvEvent2)
        TextView tvEvent2;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.cardArtist)
        public void onCardArtistClicked() {
            final int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                // 아티스트 공연 목록 가져오기
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection(App.ARGS_PERFORMANCE)
                        .whereEqualTo("artist", mArtistList.get(position))
                        .orderBy("date", Query.Direction.DESCENDING)
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

                                String artistTitle = mArtistList.get(position).name;
                                Intent intent = new Intent(mContext, PerformListActivity.class);
                                intent.putExtra(App.ARGS_TITLE, artistTitle);
                                intent.putExtra(App.ARGS_PERFORM_LIST, performList);

                                mContext.startActivity(intent);
                            }
                        });
            }
        }
    }

    public void setPerformList(ArrayList<PerformModel> itemList) {
        mPerformList = itemList;
    }

    public void setArtistList(ArrayList<ArtistModel> itemList) {
        mArtistList = itemList;
    }

    @NonNull
    @Override
    public ArtistAdapter.ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist, parent, false);

        return new ArtistAdapter.ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistAdapter.ArtistViewHolder holder, int position) {
        ArtistModel artist = mArtistList.get(position);

        holder.tvArtist.setText(artist.name);

        int recentCount = 2;
        for (PerformModel model : mPerformList) {
            if (model.artist.equals(artist.name)) {
                recentCount--;

                if (recentCount == 1)
                    holder.tvEvent1.setText(model.videoTitle);
                else if (recentCount == 0)
                    holder.tvEvent2.setText(model.videoTitle);
            }

            if (recentCount == 0)
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mArtistList.size();
    }
}
