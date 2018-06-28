package com.alexbaryzhikov.bakingtime.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.alexbaryzhikov.bakingtime.R;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    // Add browse fragment if this is a first creation
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

  public void showFragment(Fragment fragment, String backStackName) {
    if (findViewById(R.id.detail_fragment_container) == null) {
      // Normal mode
      getSupportFragmentManager().beginTransaction()
          .addToBackStack(backStackName)
          .replace(R.id.fragment_container, fragment)
          .commit();
    } else {
      // Two panes mode
      if ("DetailFragment".equals(backStackName)) {
        getSupportFragmentManager().beginTransaction()
            .addToBackStack(backStackName)
            .remove(getSupportFragmentManager().findFragmentById(R.id.fragment_container))
            .add(R.id.detail_fragment_container, fragment)
            .commit();
      } else if ("StepFragment".equals(backStackName)) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.step_fragment_container, fragment)
            .commit();
      }
    }
  }

  public void showBackInActionBar() {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      boolean enabled = getSupportFragmentManager().getBackStackEntryCount() > 0;
      actionBar.setDisplayHomeAsUpEnabled(enabled);
    }
  }

  public void clearStepFragment() {
    if (findViewById(R.id.step_fragment_container) != null) {
      Fragment stepFragment = getSupportFragmentManager().findFragmentById(R.id.step_fragment_container);
      if (stepFragment != null) {
        getSupportFragmentManager().beginTransaction()
            .remove(stepFragment)
            .commit();
      }
    }
  }
}
