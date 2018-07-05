package com.alexbaryzhikov.bakingtime.di.components;

import com.alexbaryzhikov.bakingtime.di.modules.StepFragmentModule;
import com.alexbaryzhikov.bakingtime.di.scopes.StepFragmentScope;
import com.alexbaryzhikov.bakingtime.ui.StepFragment;
import com.google.android.exoplayer2.SimpleExoPlayer;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = StepFragmentModule.class, dependencies = MainActivityComponent.class)
@StepFragmentScope
public interface StepFragmentComponent {

  void inject(StepFragment fragment);

  SimpleExoPlayer simpleExoPlayer();

  @Component.Builder
  interface Builder {

    Builder mainActivityComponent(MainActivityComponent component);

    @BindsInstance
    Builder fragment(StepFragment fragment);

    StepFragmentComponent build();
  }
}
