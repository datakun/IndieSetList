package com.kimjunu.indiesetlist;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kimjunu.indiesetlist.model.ArtistModel;
import com.kimjunu.indiesetlist.model.EventModel;
import com.kimjunu.indiesetlist.model.PerformModel;
import com.kimjunu.indiesetlist.model.VenueModel;
import com.kimjunu.indiesetlist.ui.main.ArtistFragment;
import com.kimjunu.indiesetlist.ui.main.DateFragment;
import com.kimjunu.indiesetlist.ui.main.VenueFragment;
import com.kimjunu.indiesetlist.ui.main.ViewPagerAdapter;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @BindView(R.id.nav_view)
    BottomNavigationView navView;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    MenuItem mPrevMenuItem = null;
    DateFragment mDateFragment;
    ArtistFragment mArtistFragment;
    VenueFragment mVenueFragment;

    ArrayList<EventModel> mEventList = new ArrayList<>();
    ArrayList<ArtistModel> mArtistList = new ArrayList<>();
    ArrayList<VenueModel> mVenueList = new ArrayList<>();
    ArrayList<PerformModel> mPerformList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Bundle bundleDate = new Bundle();

        Bundle bundleArtist = new Bundle();

        Bundle bundleVenue = new Bundle();

        mArtistFragment = ArtistFragment.newInstance(bundleArtist);
        mVenueFragment = VenueFragment.newInstance(bundleVenue);
        mDateFragment = DateFragment.newInstance(bundleDate);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mArtistFragment);
        adapter.addFragment(mVenueFragment);
        adapter.addFragment(mDateFragment);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mPrevMenuItem != null)
                    mPrevMenuItem.setChecked(false);
                else
                    navView.getMenu().getItem(0).setChecked(false);

                navView.getMenu().getItem(position).setChecked(true);
                mPrevMenuItem = navView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_artists:
                        viewPager.setCurrentItem(0);

                        return true;
                    case R.id.navigation_venues:
                        viewPager.setCurrentItem(1);

                        return true;
                    case R.id.navigation_date:
                        viewPager.setCurrentItem(2);

                        return true;
                }
                return false;
            }
        });

        // DB 데이터 조회
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 공연 정보 가져오기
        db.collection(App.ARGS_EVENTS)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mEventList.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                EventModel event = new EventModel();
                                event.id = document.getId();
                                HashMap<String, Object> venue = (HashMap<String, Object>) document.getData().get("venue");
                                event.venue.id = venue.get("id").toString();
                                event.venue.name = venue.get("name").toString();
                                ArrayList<HashMap<String, Object>> artists = (ArrayList<HashMap<String, Object>>) document.getData().get("artists");
                                for (HashMap<String, Object> item : artists) {
                                    ArtistModel artist = new ArtistModel();
                                    artist.id = item.get("id").toString();
                                    artist.name = item.get("name").toString();
                                    event.artists.add(artist);
                                }
                                event.date = new DateTime(document.getData().get("date"));

                                mEventList.add(event);
                            }

                            if (mDateFragment != null)
                                mDateFragment.setEventList(mEventList);
                        } else {
                            Log.e(TAG, "Error getting event documents.", task.getException());
                        }
                    }
                });

        // 모든 음악가 가져오기
//        db.collection(App.ARGS_ARTISTS)
//                .orderBy("name")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful() && task.getResult() != null) {
//                            mArtistList.clear();
//
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                ArtistModel artist = new ArtistModel();
//                                artist.id = document.getId();
//                                artist.name = document.get("name").toString();
//                                mArtistList.add(artist);
//                            }
//
//                            if (mArtistFragment != null)
//                                mArtistFragment.setArtistList(mArtistList);
//                        } else {
//                            Log.e(TAG, "Error getting artist documents.", task.getException());
//                        }
//                    }
//                });

        db.collection(App.ARGS_PERFORMANCE)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mArtistList.clear();
                            mVenueList.clear();
                            mPerformList.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // 연주 추가
                                HashMap<String, Object> artist_item = (HashMap<String, Object>) document.get("artist");
                                HashMap<String, Object> venue_item = (HashMap<String, Object>) document.get("venue");
                                HashMap<String, Object> video_item = (HashMap<String, Object>) document.get("video");
                                PerformModel perform = new PerformModel();
                                perform.id = document.getId();
                                perform.artist = artist_item.get("name").toString();
                                perform.date = document.get("date").toString();
                                perform.venue = venue_item.get("name").toString();
                                perform.videoId = video_item.get("id").toString();
                                perform.videoTitle = video_item.get("title").toString();

                                mPerformList.add(perform);

                                // 음악가 추가
                                ArtistModel artist = new ArtistModel();
                                artist.id = artist_item.get("id").toString();
                                artist.name = artist_item.get("name").toString();

                                boolean isFound = false;
                                for (ArtistModel model : mArtistList) {
                                    if (model.name.equals(artist.name)) {
                                        isFound = true;

                                        break;
                                    }
                                }

                                if (isFound == false)
                                    mArtistList.add(artist);

                                // 공연장 추가
                                VenueModel venue = new VenueModel();
                                venue.id = venue_item.get("id").toString();
                                venue.name = venue_item.get("name").toString();

                                isFound = false;
                                for (VenueModel model : mVenueList) {
                                    if (model.name.equals(venue.name)) {
                                        isFound = true;

                                        break;
                                    }
                                }


                                if (isFound == false)
                                    mVenueList.add(venue);
                            }

                            if (mArtistFragment != null) {
                                Collections.sort(mArtistList, new AscendingArtist());
                                mArtistFragment.setArtistList(mArtistList);
                                mArtistFragment.setPerformList(mPerformList);
                            }

                            if (mVenueFragment != null) {
                                Collections.sort(mVenueList, new AscendingVenue());
                                mVenueFragment.setVenueList(mVenueList);
                                mVenueFragment.setPerformList(mPerformList);
                            }
                        } else {
                            Log.e(TAG, "Error getting venue documents.", task.getException());
                        }
                    }
                });

        // 모든 공연장 가져오기
//        db.collection(App.ARGS_VENUES)
//                .orderBy("name")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful() && task.getResult() != null) {
//                            mVenueList.clear();
//
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                VenueModel venue = new VenueModel();
//                                venue.id = document.getId();
//                                venue.name = document.get("name").toString();
//                                mVenueList.add(venue);
//                            }
//
//                            if (mVenueFragment != null)
//                                mVenueFragment.setVenueList(mVenueList);
//                        } else {
//                            Log.e(TAG, "Error getting venue documents.", task.getException());
//                        }
//                    }
//                });
    }

    @Override
    public void onBackPressed() {
        if (mDateFragment.isCalendarOpened()) {
            mDateFragment.onLayoutCalendarClicked();
        } else {
            super.onBackPressed();
        }
    }

    class AscendingArtist implements Comparator<ArtistModel> {
        @Override
        public int compare(ArtistModel a, ArtistModel b) {
            return a.name.compareTo(b.name);
        }
    }

    class AscendingVenue implements Comparator<VenueModel> {
        @Override
        public int compare(VenueModel a, VenueModel b) {
            return a.name.compareTo(b.name);
        }
    }
}
