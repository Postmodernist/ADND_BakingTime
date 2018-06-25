package com.alexbaryzhikov.bakingtime.ui;

import com.alexbaryzhikov.bakingtime.di.scopes.StepFragmentScope;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;

import javax.inject.Inject;

@StepFragmentScope
public class StepPlayerEventListener implements Player.EventListener {

  private PlayerView playerView;

  @Inject
  StepPlayerEventListener() {
  }

  public void setPlayerView(PlayerView playerView) {
    this.playerView = playerView;
  }

  @Override
  public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

  }

  @Override
  public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

  }

  @Override
  public void onLoadingChanged(boolean isLoading) {

  }

  @Override
  public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
    if (playerView == null) {
      return;
    }
    if (playbackState == Player.STATE_READY && playWhenReady) {
      playerView.hideController();
    }
  }

  @Override
  public void onRepeatModeChanged(int repeatMode) {

  }

  @Override
  public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

  }

  @Override
  public void onPlayerError(ExoPlaybackException error) {

  }

  @Override
  public void onPositionDiscontinuity(int reason) {

  }

  @Override
  public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

  }

  @Override
  public void onSeekProcessed() {

  }
}
