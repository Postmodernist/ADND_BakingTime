package com.alexbaryzhikov.bakingtime.di.components;

import com.alexbaryzhikov.bakingtime.di.modules.StepFragmentModule;
import com.alexbaryzhikov.bakingtime.di.scopes.StepFragmentScope;
import com.alexbaryzhikov.bakingtime.ui.StepFragment;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = StepFragmentModule.class, dependencies = MainActivityComponent.class)
@StepFragmentScope
public interface StepFragmentComponent {

  void inject(StepFragment fragment);

  @Component.Builder
  interface Builder {

    Builder mainActivityComponent(MainActivityComponent component);

    @BindsInstance
    Builder fragment(StepFragment fragment);

    StepFragmentComponent build();
  }
}
