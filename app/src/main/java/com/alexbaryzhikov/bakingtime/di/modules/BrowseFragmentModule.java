package com.alexbaryzhikov.bakingtime.di.modules;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.LayoutManager;

import com.alexbaryzhikov.bakingtime.di.scopes.BrowseFragmentScope;
import com.alexbaryzhikov.bakingtime.ui.BrowseFragment;
import com.alexbaryzhikov.bakingtime.ui.MainActivity;
import com.alexbaryzhikov.bakingtime.ui.RecipeAdapter;
import com.alexbaryzhikov.bakingtime.viewmodel.RecipeViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class BrowseFragmentModule {

  @Provides
  @BrowseFragmentScope
  RecipeAdapter provideRecipeAdapter() {
    return new RecipeAdapter();
  }

  @Provides
  @BrowseFragmentScope
  LayoutManager provideLayoutManager(Context context) {
    return new LinearLayoutManager(context);
  }

  @Provides
  @BrowseFragmentScope
  RecipeViewModel provideRecipeVeiwModel(MainActivity mainActivity) {
    return ViewModelProviders.of(mainActivity).get(RecipeViewModel.class);
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
