package com.alexbaryzhikov.bakingtime.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

/** Cooking step details */
public class StepFragment extends Fragment {

  private static final String KEY_RECIPE_POSITION = "recipe-position";
  private static final String KEY_STEP_POSITION = "step-position";

  @BindView(R.id.player_view) PlayerView playerView;
  @BindView(R.id.no_video) TextView noVideo;
  @BindView(R.id.instructions) TextView instructions;
  @BindView(R.id.prev_step) Button prevStep;
  @BindView(R.id.next_step) Button nextStep;

  @Inject RecipeViewModel viewModel;
  @Inject SimpleExoPlayer exoPlayer;
  @Inject ExtractorMediaSource.Factory extractorsFactory;
  @Inject StepPlayerEventListener playerEventListener;

  private Disposable disposable;
  private int stepPosition;

  public StepFragment() {
    // Required empty public constructor
  }

  static StepFragment forStep(int recipePosition, int stepPosition) {
    StepFragment fragment = new StepFragment();
    Bundle args = new Bundle();
    args.putInt(KEY_RECIPE_POSITION, recipePosition);
    args.putInt(KEY_STEP_POSITION, stepPosition);
    fragment.setArguments(args);
    return fragment;
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
  }

  private void setupDagger(Context context) {
    DaggerStepFragmentComponent.builder()
        .appComponent(BakingApp.getAppComponent(context))
        .fragment(this)
        .build()
        .inject(this);
  }

  private void setupFragment() {
    // Get fragment arguments
    Bundle args = getArguments();
    if (args == null || !args.containsKey(KEY_RECIPE_POSITION) || !args.containsKey(KEY_STEP_POSITION)) {
      throw new AssertionError("Arguments expected");
    }
    final int recipePosition = args.getInt(KEY_RECIPE_POSITION);
    stepPosition = args.getInt(KEY_STEP_POSITION);
    // Subscribe
    disposable = viewModel.getStepStream().subscribe(this::renderStep);
    viewModel.onStep(recipePosition, stepPosition);
    // Setup buttons
    prevStep.setOnClickListener(v -> viewModel.onStep(recipePosition, --stepPosition));
    nextStep.setOnClickListener(v -> viewModel.onStep(recipePosition, ++stepPosition));
  }

  private void renderStep(StepItem stepItem) {
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
