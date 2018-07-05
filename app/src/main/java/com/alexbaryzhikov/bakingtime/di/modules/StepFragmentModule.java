package com.alexbaryzhikov.bakingtime.di.modules;

import android.arch.lifecycle.ViewModelProviders;

import com.alexbaryzhikov.bakingtime.di.scopes.StepFragmentScope;
import com.alexbaryzhikov.bakingtime.ui.MainActivity;
import com.alexbaryzhikov.bakingtime.viewmodel.RecipeViewModel;
import com.alexbaryzhikov.bakingtime.viewmodel.RecipeViewModelFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class StepFragmentModule {

  @Provides
  @StepFragmentScope
  RecipeViewModel provideRecipeViewModel(MainActivity mainActivity, RecipeViewModelFactory factory) {
    return ViewModelProviders.of(mainActivity, factory).get(RecipeViewModel.class);
  }

  @Provides
  SimpleExoPlayer provideSimpleExoPlayer(MainActivity context, TrackSelector trackSelector) {
    return ExoPlayerFactory.newSimpleInstance(context, trackSelector);
  }

  @Provides
  @StepFragmentScope
  TrackSelector provideTrackSelector(BandwidthMeter bandwidthMeter) {
    return new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
  }

  @Provides
  @StepFragmentScope
  BandwidthMeter provideBandwidthMeter() {
    return new DefaultBandwidthMeter();
  }

  @Provides
  @StepFragmentScope
  ExtractorMediaSource.Factory provideExtractorMediaSourceFactory(DefaultHttpDataSourceFactory factory) {
    return new ExtractorMediaSource.Factory(factory);
  }

  @Provides
  @StepFragmentScope
  DefaultHttpDataSourceFactory provideDefaultHttpDataSourceFactory(@Named("UserAgent") String userAgent) {
    return new DefaultHttpDataSourceFactory(userAgent);
  }

  @Provides
  @StepFragmentScope
  @Named("UserAgent")
  String provideUserAgent(MainActivity context) {
    return Util.getUserAgent(context, "BakingTime");
  }

}
