package com.alexbaryzhikov.bakingtime.di.components;

import com.alexbaryzhikov.bakingtime.di.modules.BrowseFragmentModule;
import com.alexbaryzhikov.bakingtime.di.scopes.BrowseFragmentScope;
import com.alexbaryzhikov.bakingtime.ui.BrowseFragment;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = BrowseFragmentModule.class, dependencies = ApplicationComponent.class)
@BrowseFragmentScope
public interface BrowseFragmentComponent {

  void inject(BrowseFragment fragment);

  @Component.Builder
  interface Builder {

    Builder appComponent(ApplicationComponent component);

    @BindsInstance
    Builder fragment(BrowseFragment fragment);

    BrowseFragmentComponent build();
  }

}