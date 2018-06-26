package com.alexbaryzhikov.bakingtime.di.modules;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.LayoutManager;

import com.alexbaryzhikov.bakingtime.di.scopes.BrowseFragmentScope;
import com.alexbaryzhikov.bakingtime.ui.BrowseFragment;
import com.alexbaryzhikov.bakingtime.ui.DetailFragment;
import com.alexbaryzhikov.bakingtime.ui.MainActivity;
import com.alexbaryzhikov.bakingtime.ui.BrowseAdapter.RecipeClickCallback;
import com.alexbaryzhikov.bakingtime.viewmodel.RecipeViewModel;
import com.alexbaryzhikov.bakingtime.viewmodel.RecipeViewModelFactory;

import dagger.Module;
import dagger.Provides;

@Module
public class BrowseFragmentModule {

  @Provides
  LayoutManager provideLayoutManager(Context context) {
    return new LinearLayoutManager(context);
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
        viewModel.setDetail(position);
        mainActivity.showFragment(detailFragment, "detail_fragment");
      }
    };
  }

  @Provides
  DetailFragment provideDetailFragment() {
    return new DetailFragment();
  }

  @Provides
  Context provideContext(BrowseFragment fragment) {
    return fragment.getContext();
  }

  @Provides
  MainActivity provideMainActivity(BrowseFragment fragment) {
    return (MainActivity) fragment.getActivity();
  }
}
