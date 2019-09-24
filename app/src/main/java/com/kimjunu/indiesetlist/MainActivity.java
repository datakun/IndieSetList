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
import com.kimjunu.indiesetlist.model.VenueModel;
import com.kimjunu.indiesetlist.ui.main.ArtistFragment;
import com.kimjunu.indiesetlist.ui.main.DateFragment;
import com.kimjunu.indiesetlist.ui.main.VenueFragment;
import com.kimjunu.indiesetlist.ui.main.ViewPagerAdapter;

import org.joda.time.DateTime;

import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Bundle bundleDate = new Bundle();

        Bundle bundleArtist = new Bundle();

        Bundle bundleVenue = new Bundle();

        mDateFragment = DateFragment.newInstance(bundleDate);
        mArtistFragment = ArtistFragment.newInstance(bundleArtist);
        mVenueFragment = VenueFragment.newInstance(bundleVenue);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mDateFragment);
        adapter.addFragment(mArtistFragment);
        adapter.addFragment(mVenueFragment);
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
                    case R.id.navigation_date:
                        viewPager.setCurrentItem(0);

                        return true;
                    case R.id.navigation_artists:
                        viewPager.setCurrentItem(1);

                        return true;
                    case R.id.navigation_venues:
                        viewPager.setCurrentItem(2);

                        return true;
                }
                return false;
            }
        });

        // DB 데이터 조회
        FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        db.collection(App.ARGS_ARTISTS)
                .orderBy("name")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mArtistList.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ArtistModel artist = new ArtistModel();
                                artist.id = document.getId();
                                artist.name = document.get("name").toString();
                                mArtistList.add(artist);
                            }

                            if (mArtistFragment != null)
                                mArtistFragment.setArtistList(mArtistList);
                        } else {
                            Log.e(TAG, "Error getting artist documents.", task.getException());
                        }
                    }
                });

        db.collection(App.ARGS_VENUES)
                .orderBy("name")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mVenueList.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                VenueModel venue = new VenueModel();
                                venue.id = document.getId();
                                venue.name = document.get("name").toString();
                                mVenueList.add(venue);
                            }

                            if (mVenueFragment != null)
                                mVenueFragment.setVenueList(mVenueList);
                        } else {
                            Log.e(TAG, "Error getting venue documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (mDateFragment.isCalendarOpened()) {
            mDateFragment.onLayoutCalendarClicked();
        } else {
            super.onBackPressed();
        }
    }
}
