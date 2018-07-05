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

import com.alexbaryzhikov.bakingtime.R;
import com.alexbaryzhikov.bakingtime.datamodel.view.PlayerState;
import com.alexbaryzhikov.bakingtime.datamodel.view.StepItem;
import com.alexbaryzhikov.bakingtime.di.components.DaggerStepFragmentComponent;
import com.alexbaryzhikov.bakingtime.di.components.StepFragmentComponent;
import com.alexbaryzhikov.bakingtime.viewmodel.RecipeViewModel;
import com.google.android.exoplayer2.Player;
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
  @Inject ExtractorMediaSource.Factory extractorsFactory;
  @Inject StepPlayerEventListener playerEventListener;

  private StepFragmentComponent fragmentComponent;
  private SimpleExoPlayer exoPlayer;
  private Disposable disposable;
  private int systemVisibility;
  private boolean uiHidden = false;
  private boolean playerUnwired = true;

  public StepFragment() {
    // Required empty public constructor
  }

  @Override
  public void onAttach(Context context) {
    setupDagger();
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
  public void onResume() {
    super.onResume();
    StepItem stepItem = viewModel.getStepItemCache();
    if (stepItem != null) {
      initPlayer(stepItem);
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    releasePlayer();
  }

  @Override
  public void onStop() {
    super.onStop();
    releasePlayer();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (disposable != null) {
      disposable.dispose();
    }
    restoreSystemUi();
  }

  private void setupDagger() {
    if (getActivity() == null) {
      throw new AssertionError();
    }
    fragmentComponent = DaggerStepFragmentComponent.builder()
        .mainActivityComponent(((MainActivity) getActivity()).getMainActivityComponent())
        .fragment(this)
        .build();
    fragmentComponent.inject(this);
  }

  private void setupFragment() {
    // Setup buttons
    if (prevStep != null && nextStep != null) {
      prevStep.setOnClickListener(v -> viewModel.emitPrevStep());
      nextStep.setOnClickListener(v -> viewModel.emitNextStep());
    }
    // Find out if we're in fullscreen mode
    boolean phone = mainActivity.getResources().getConfiguration().smallestScreenWidthDp < 600;
    int orientation = mainActivity.getResources().getConfiguration().orientation;
    final boolean fullscreen = phone && orientation == ORIENTATION_LANDSCAPE;
    // Subscribe
    disposable = viewModel.getStepStream()
        .subscribe(stepItem -> {
          if (fullscreen) {
            setupFullscreenUi(stepItem);
          } else {
            setupDefaultUi(stepItem);
          }
          initPlayer(stepItem);
        });
  }

  private void setupDefaultUi(@NonNull StepItem stepItem) {
    mainActivity.setTitle(stepItem.getRecipeName());
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
    } else {
      playerView.setVisibility(View.VISIBLE);
      noVideo.setVisibility(View.INVISIBLE);
    }
  }

  private void setupFullscreenUi(@NonNull StepItem stepItem) {
    hideSystemUi();
    if (TextUtils.isEmpty(stepItem.getVideoUrl())) {
      playerView.setVisibility(View.INVISIBLE);
      noVideo.setVisibility(View.VISIBLE);
    } else {
      playerView.setVisibility(View.VISIBLE);
      noVideo.setVisibility(View.INVISIBLE);
    }
  }

  private void initPlayer(@NonNull StepItem stepItem) {
    // If nothing to play just stop the player and return
    if (TextUtils.isEmpty(stepItem.getVideoUrl()) && exoPlayer != null) {
      exoPlayer.stop();
      return;
    }
    // Get player instance
    if (exoPlayer == null) {
      exoPlayer = fragmentComponent.simpleExoPlayer();
    }
    // Wire up player, player view and player event listener
    if (playerUnwired) {
      playerEventListener.setPlayerView(playerView);
      exoPlayer.addListener(playerEventListener);
      playerView.setPlayer(exoPlayer);
      playerUnwired = false;
    }
    // Resolve player state
    PlayerState state = viewModel.getPlayerState();
    if (state == null || state.getRecipeId() != stepItem.getRecipeId()
        || state.getStepId() != stepItem.getStepId()
        || state.getPlaybackState() == Player.STATE_ENDED) {
      state = new PlayerState(stepItem.getRecipeId(), stepItem.getStepId(), Player.STATE_IDLE, 0, false);
      viewModel.setPlayerState(state);
    }
    // Restore playback
    exoPlayer.setPlayWhenReady(state.isPlayWhenReady());
    Uri videoUri = Uri.parse(stepItem.getVideoUrl());
    MediaSource mediaSource = extractorsFactory.createMediaSource(videoUri);
    exoPlayer.prepare(mediaSource);
    exoPlayer.seekTo(state.getPositionMs());
  }

  private void releasePlayer() {
    if (exoPlayer == null) {
      return;
    }
    // Save player state
    PlayerState oldState = viewModel.getPlayerState();
    final int recipeId = oldState.getRecipeId();
    final int stepId = oldState.getStepId();
    final int playbackState = exoPlayer.getPlaybackState();
    final long positionMs = exoPlayer.getCurrentPosition();
    final boolean playWhenReady = exoPlayer.getPlayWhenReady();
    PlayerState state = new PlayerState(recipeId, stepId, playbackState, positionMs, playWhenReady);
    viewModel.setPlayerState(state);
    // Release player
    exoPlayer.stop();
    exoPlayer.release();
    exoPlayer = null;
    playerUnwired = true;
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
