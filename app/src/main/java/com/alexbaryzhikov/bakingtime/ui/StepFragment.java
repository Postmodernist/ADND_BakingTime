package com.alexbaryzhikov.bakingtime.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alexbaryzhikov.bakingtime.BakingApp;
import com.alexbaryzhikov.bakingtime.R;
import com.alexbaryzhikov.bakingtime.datamodel.view.StepItem;
import com.alexbaryzhikov.bakingtime.di.components.DaggerStepFragmentComponent;
import com.alexbaryzhikov.bakingtime.viewmodel.RecipeViewModel;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

/** Cooking step details */
public class StepFragment extends Fragment {

  @BindView(R.id.player_view) PlayerView playerView;
  @BindView(R.id.no_video) TextView noVideo;
  TextView instructions;
  Button prevStep;
  Button nextStep;

  @Inject RecipeViewModel viewModel;
  @Inject MainActivity mainActivity;
  @Inject SimpleExoPlayer exoPlayer;
  @Inject ExtractorMediaSource.Factory extractorsFactory;
  @Inject StepPlayerEventListener playerEventListener;

  private Disposable disposable;
  private int orientation;
  private int systemVisibility;

  public StepFragment() {
    // Required empty public constructor
  }

  @Override
  public void onAttach(Context context) {
    setupDagger(context);
    super.onAttach(context);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_step, container, false);
    ButterKnife.bind(this, view);
    orientation = mainActivity.getResources().getConfiguration().orientation;
    if (orientation == ORIENTATION_PORTRAIT) {
      instructions = view.findViewById(R.id.instructions);
      prevStep = view.findViewById(R.id.prev_step);
      nextStep = view.findViewById(R.id.next_step);
    }
    setupFragment();
    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (disposable != null) {
      disposable.dispose();
    }
    releasePlayer();
    restoreSystemUi();
  }

  private void setupDagger(Context context) {
    DaggerStepFragmentComponent.builder()
        .appComponent(BakingApp.getAppComponent(context))
        .fragment(this)
        .build()
        .inject(this);
  }

  private void setupFragment() {
    if (orientation == ORIENTATION_PORTRAIT) {
      // Show action bar back button
      mainActivity.showBackInActionBar();
      // Subscribe
      disposable = viewModel.getStepStream().subscribe(this::renderStepPort);
      viewModel.onStep(0);
      // Setup buttons
      prevStep.setOnClickListener(v -> viewModel.onStep(-1));
      nextStep.setOnClickListener(v -> viewModel.onStep(1));
    } else {
      hideSystemUi();
      // Subscribe
      disposable = viewModel.getStepStream().subscribe(this::renderStepLand);
      viewModel.onStep(0);
    }
  }

  private void hideSystemUi() {
    ActionBar actionBar = mainActivity.getSupportActionBar();
    if (actionBar != null) {
      actionBar.hide();
    }
    if (Build.VERSION.SDK_INT >= 19) {
      View decorView = mainActivity.getWindow().getDecorView();
      systemVisibility = decorView.getSystemUiVisibility();
      decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
          | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
          | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
  }

  private void restoreSystemUi() {
    ActionBar actionBar = mainActivity.getSupportActionBar();
    if (actionBar != null) {
      actionBar.show();
    }
    if (Build.VERSION.SDK_INT >= 19) {
      View decorView = mainActivity.getWindow().getDecorView();
      decorView.setSystemUiVisibility(systemVisibility);
    }
  }

  private void renderStepPort(StepItem stepItem) {
    instructions.setText(stepItem.getDescription());
    prevStep.setVisibility(stepItem.isFirst() ? View.INVISIBLE : View.VISIBLE);
    nextStep.setVisibility(stepItem.isLast() ? View.INVISIBLE : View.VISIBLE);
    if (TextUtils.isEmpty(stepItem.getVideoUrl())) {
      playerView.setVisibility(View.INVISIBLE);
      noVideo.setVisibility(View.VISIBLE);
      exoPlayer.stop();
    } else {
      playerView.setVisibility(View.VISIBLE);
      noVideo.setVisibility(View.INVISIBLE);
      initPlayer(Uri.parse(stepItem.getVideoUrl()));
    }
  }

  private void renderStepLand(StepItem stepItem) {
    if (TextUtils.isEmpty(stepItem.getVideoUrl())) {
      playerView.setVisibility(View.INVISIBLE);
      noVideo.setVisibility(View.VISIBLE);
      exoPlayer.stop();
    } else {
      playerView.setVisibility(View.VISIBLE);
      noVideo.setVisibility(View.INVISIBLE);
      initPlayer(Uri.parse(stepItem.getVideoUrl()));
    }
  }

  private void initPlayer(Uri videoUri) {
    if (playerView.getPlayer() == null) {
      playerEventListener.setPlayerView(playerView);
      exoPlayer.addListener(playerEventListener);
      playerView.setPlayer(exoPlayer);
    }
    exoPlayer.setPlayWhenReady(false);
    MediaSource mediaSource = extractorsFactory.createMediaSource(videoUri);
    exoPlayer.prepare(mediaSource);
  }

  private void releasePlayer() {
    if (exoPlayer == null) {
      return;
    }
    exoPlayer.stop();
    exoPlayer.release();
    exoPlayer = null;
  }
}
