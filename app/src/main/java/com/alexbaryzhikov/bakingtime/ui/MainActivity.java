package com.alexbaryzhikov.bakingtime.ui;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alexbaryzhikov.bakingtime.R;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Add recipes list fragment if this is a first creation
    if (savedInstanceState == null) {
      BrowseFragment fragment = new BrowseFragment();
      getSupportFragmentManager().beginTransaction()
          .add(R.id.fragment_container, fragment)
          .commit();
    }
  }

  @Override
  public boolean onSupportNavigateUp() {
    getSupportFragmentManager().popBackStack();
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(false);
    }
    return true;
  }

  public void showBackInActionBar() {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      boolean enabled = getSupportFragmentManager().getBackStackEntryCount() > 0;
      actionBar.setDisplayHomeAsUpEnabled(enabled);
    }
  }

  public void showRecipeDetails(int recipeId, int position) {
    DetailFragment fragment = DetailFragment.forRecipe(recipeId, position);
    getSupportFragmentManager().beginTransaction()
        .addToBackStack("recipe")
        .replace(R.id.fragment_container, fragment)
        .commit();
  }
}
