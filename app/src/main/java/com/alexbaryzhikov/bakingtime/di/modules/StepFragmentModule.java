package com.alexbaryzhikov.bakingtime.di.modules;

import android.arch.lifecycle.ViewModelProviders;

import com.alexbaryzhikov.bakingtime.di.scopes.StepFragmentScope;
import com.alexbaryzhikov.bakingtime.ui.MainActivity;
import com.alexbaryzhikov.bakingtime.ui.StepFragment;
import com.alexbaryzhikov.bakingtime.viewmodel.RecipeViewModel;
import com.alexbaryzhikov.bakingtime.viewmodel.RecipeViewModelFactory;

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
  MainActivity provideMainActivity(StepFragment fragment) {
    return (MainActivity) fragment.getActivity();
  }
}
