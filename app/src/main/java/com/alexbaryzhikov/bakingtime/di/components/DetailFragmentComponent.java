package com.alexbaryzhikov.bakingtime.di.components;

import android.support.v7.widget.RecyclerView.LayoutManager;

import com.alexbaryzhikov.bakingtime.di.modules.DetailFragmentModule;
import com.alexbaryzhikov.bakingtime.di.scopes.DetailFragmentScope;
import com.alexbaryzhikov.bakingtime.ui.DetailFragment;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = DetailFragmentModule.class, dependencies = ApplicationComponent.class)
@DetailFragmentScope
public interface DetailFragmentComponent {

  void inject(DetailFragment fragment);

  LayoutManager layoutManager();

  @Component.Builder
  interface Builder {

    Builder appComponent(ApplicationComponent component);

    @BindsInstance
    Builder fragment(DetailFragment fragment);

    DetailFragmentComponent build();
  }
}
