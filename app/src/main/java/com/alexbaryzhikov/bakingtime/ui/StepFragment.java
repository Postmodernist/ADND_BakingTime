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

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

/** Cooking step details */
public class StepFragment extends Fragment {

  @BindView(R.id.player_view) PlayerView playerView;
  @BindView(R.id.no_video) TextView noVideo;
  @Nullable @BindView(R.id.instructions) TextView instructions;
  @Nullable @BindView(R.id.prev_step) Button prevStep;
  @Nullable @BindView(R.id.next_step) Button nextStep;

  @Inject RecipeViewModel viewModel;
  @Inject MainActivity mainActivity;
  @Inject SimpleExoPlayer exoPlayer;
  @Inject ExtractorMediaSource.Factory extractorsFactory;
  @Inject StepPlayerEventListener playerEventListener;

  private Disposable disposable;
  private int systemVisibility;
  private boolean uiHidden = false;

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
    mainActivity.showBackInActionBar();
    View view = inflater.inflate(R.layout.fragment_step, container, false);
    ButterKnife.bind(this, view);
    setupFragment();
    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (disposable != null) {
      disposable.dispose();
    }
    restoreSystemUi();
    releasePlayer();
  }

  private void setupDagger(Context context) {
    if (getActivity() == null) {
      throw new AssertionError();
    }
    DaggerStepFragmentComponent.builder()
        .mainActivityComponent(((MainActivity) getActivity()).getMainActivityComponent())
        .fragment(this)
        .build()
        .inject(this);
  }

  public void setupFragment() {
    boolean phone = mainActivity.getResources().getConfiguration().smallestScreenWidthDp < 600;
    int orientation = mainActivity.getResources().getConfiguration().orientation;
    boolean fullscreen = phone && orientation == ORIENTATION_LANDSCAPE;
    if (fullscreen) {
      // Subscribe
      disposable = viewModel.getStepStream()
          .doOnNext(stepItem -> mainActivity.setTitle(stepItem.getRecipeName()))
          .subscribe(this::renderStepFullscreen);
    } else {
      // Setup buttons
      if (prevStep != null && nextStep != null) {
        prevStep.setOnClickListener(v -> viewModel.emitPrevStep());
        nextStep.setOnClickListener(v -> viewModel.emitNextStep());
      }
      // Subscribe
      disposable = viewModel.getStepStream()
          .doOnNext(stepItem -> mainActivity.setTitle(stepItem.getRecipeName()))
          .subscribe(this::renderStepDefault);
    }
  }

  private void renderStepDefault(StepItem stepItem) {
    if (instructions != null) {
      instructions.setText(stepItem.getDescription());
    }
    if (prevStep != null && nextStep != null) {
      prevStep.setVisibility(stepItem.isFirst() ? View.INVISIBLE : View.VISIBLE);
      nextStep.setVisibility(stepItem.isLast() ? View.INVISIBLE : View.VISIBLE);
    }
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

  private void renderStepFullscreen(StepItem stepItem) {
    hideSystemUi();
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

  private void hideSystemUi() {
    uiHidden = true;
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
    if (!uiHidden) {
      return;
    }
    ActionBar actionBar = mainActivity.getSupportActionBar();
    if (actionBar != null) {
      actionBar.show();
    }
    if (Build.VERSION.SDK_INT >= 19) {
      View decorView = mainActivity.getWindow().getDecorView();
      decorView.setSystemUiVisibility(systemVisibility);
    }
  }
}
