package com.kimjunu.indiesetlist;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.kimjunu.indiesetlist.common.Util;
import com.kimjunu.indiesetlist.model.PerformModel;
import com.kimjunu.indiesetlist.ui.custom.FlingLinearLayout;
import com.kimjunu.indiesetlist.ui.main.PerformAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class PerformListActivity extends YouTubeBaseActivity
        implements YouTubePlayer.OnFullscreenListener, YouTubePlayer.OnInitializedListener {
    private static final String TAG = "PerformListActivity";
    private static final int RECOVERY_DIALOG_REQUEST = 1;

    @BindView(R.id.tvTitle)
    TextView tvTitle;

    @BindView(R.id.rvPerform)
    RecyclerView rvPerform;

    @BindView(R.id.layoutPerformList)
    LinearLayout layoutPerformList;

    @BindView(R.id.layoutVideo)
    FlingLinearLayout layoutVideo;

    @BindView(R.id.ivClose)
    ImageView ivClose;

    @BindView(R.id.youTubePlayerView)
    YouTubePlayerView youTubePlayerView;

    SectionedRecyclerViewAdapter sectionAdapter = null;
    RecyclerView.LayoutManager layoutManager = null;

    YouTubePlayer youTubePlayer = null;

    boolean mIsFullscreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        ButterKnife.bind(this);

        Intent intent = getIntent();

        String eventTitle = intent.getStringExtra(App.ARGS_TITLE);

        tvTitle.setText(eventTitle);

        HashMap<String, ArrayList<PerformModel>> performList = (HashMap<String, ArrayList<PerformModel>>) intent.getSerializableExtra(App.ARGS_PERFORM_LIST);

        rvPerform.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(PerformListActivity.this);
        rvPerform.setLayoutManager(layoutManager);

        sectionAdapter = new SectionedRecyclerViewAdapter();

        for (String artist : performList.keySet()) {
            ArrayList<PerformModel> performs = performList.get(artist);

            PerformAdapter artistSection = new PerformAdapter(PerformListActivity.this, artist, performs);

            artistSection.setOnPerformSelectListener(new PerformAdapter.OnPerformSelectListener() {
                @Override
                public void onPerformSelected(PerformModel perform) {
                    Log.e(TAG, perform.videoId + ", " + perform.videoTitle);

                    if (youTubePlayer != null)
                        youTubePlayer.loadVideo(perform.videoId);

                    if (layoutVideo.getVisibility() != View.VISIBLE) {
                        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                            layoutVideo.setTranslationY(layoutVideo.getHeight());

                        layoutVideo.setVisibility(View.VISIBLE);
                    }

                    if (layoutVideo.getTranslationY() > 0)
                        layoutVideo.animate().translationY(0).setDuration(App.ANIMATION_DURATION_MILLIS);
                }
            });

            sectionAdapter.addSection(artistSection);
        }

        rvPerform.setAdapter(sectionAdapter);

        layoutVideo.setVisibility(View.INVISIBLE);

        youTubePlayerView.initialize(Developer.ANDROID_KEY, this);

        layout();

        checkYouTubeApi();
    }

    @Override
    protected void onDestroy() {
        if (youTubePlayer != null)
            youTubePlayer.release();

        super.onDestroy();
    }

    private void checkYouTubeApi() {
        YouTubeInitializationResult errorReason = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(this);
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else if (errorReason != YouTubeInitializationResult.SUCCESS) {
            String errorMessage = String.format(getString(R.string.msg_error_player), errorReason.toString());
            Util.showToast(this, errorMessage);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST)
            recreate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        layout();
    }

    @Override
    public void onFullscreen(boolean isFullscreen) {
        mIsFullscreen = isFullscreen;

        layout();
    }

    private void layout() {
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        layoutPerformList.setVisibility(mIsFullscreen ? View.GONE : View.VISIBLE);
        ivClose.setVisibility(isPortrait ? View.VISIBLE : View.GONE);

        if (mIsFullscreen) {
            layoutVideo.setTranslationY(0); // Reset any translation that was applied in portrait.
            setLayoutSize(youTubePlayerView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            setLayoutSizeAndGravity(layoutVideo, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.TOP | Gravity.LEFT);
        } else {
            setLayoutSize(layoutVideo, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            setLayoutSize(youTubePlayerView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            setLayoutSizeAndGravity(layoutVideo, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
        }
    }

    private static void setLayoutSize(View view, int width, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }

    private static void setLayoutSizeAndGravity(View view, int width, int height, int gravity) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        params.gravity = gravity;
        view.setLayoutParams(params);
    }

    @OnClick(R.id.ivClose)
    public void closeVideo() {
        if (youTubePlayer != null)
            youTubePlayer.pause();

        layoutVideo.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean restored) {
        youTubePlayer = player;
        youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
        youTubePlayer.setOnFullscreenListener(this);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        youTubePlayer = null;
    }
}
