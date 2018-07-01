package com.alexbaryzhikov.bakingtime.di.modules;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.alexbaryzhikov.bakingtime.di.scopes.BrowseFragmentScope;
import com.alexbaryzhikov.bakingtime.ui.BrowseAdapter.RecipeClickCallback;
import com.alexbaryzhikov.bakingtime.ui.BrowseFragment;
import com.alexbaryzhikov.bakingtime.ui.DetailFragment;
import com.alexbaryzhikov.bakingtime.ui.MainActivity;
import com.alexbaryzhikov.bakingtime.viewmodel.RecipeViewModel;
import com.alexbaryzhikov.bakingtime.viewmodel.RecipeViewModelFactory;

import dagger.Module;
import dagger.Provides;

@Module
public class BrowseFragmentModule {

  @Provides
  LayoutManager provideLayoutManager(MainActivity context) {
    boolean phone = context.getResources().getConfiguration().smallestScreenWidthDp < 600;
    return phone ? new LinearLayoutManager(context) :
        new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
  }

  @Provides
  @BrowseFragmentScope
  RecipeViewModel provideRecipeViewModel(MainActivity mainActivity, RecipeViewModelFactory factory) {
    return ViewModelProviders.of(mainActivity, factory).get(RecipeViewModel.class);
  }

  @Provides
  @BrowseFragmentScope
  RecipeClickCallback provideRecipeClickCallback(MainActivity mainActivity, BrowseFragment browseFragment,
                                                 RecipeViewModel viewModel, DetailFragment detailFragment) {
    return position -> {
      if (browseFragment.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
        viewModel.emitDetail(position);
        mainActivity.showFragment(detailFragment, "DetailFragment");
      }
    };
  }

  @Provides
  DetailFragment provideDetailFragment() {
    return new DetailFragment();
  }
}
