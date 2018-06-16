package com.alexbaryzhikov.bakingtime.ui;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.alexbaryzhikov.bakingtime.BakingApp;
import com.alexbaryzhikov.bakingtime.R;
import com.alexbaryzhikov.bakingtime.di.components.ApplicationComponent;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class BrowseFragmentTest {

  private static final String RECIPE_NAME = "Brownies";
  private IdlingResource idlingResource;

  @Rule
  public ActivityTestRule<MainActivity> activityTestRule =
      new ActivityTestRule<>(MainActivity.class);

  @Before
  public void registerIdlingResource() {
    ApplicationComponent appComponent = BakingApp.getAppComponent(activityTestRule.getActivity());
    idlingResource = appComponent.idlingResource();
    IdlingRegistry.getInstance().register(idlingResource);
  }

  @After
  public void unregisterIdlingResource() {
    if (idlingResource != null) {
      IdlingRegistry.getInstance().unregister(idlingResource);
    }
  }

  @Test
  public void checkRecipeListExists() {
    onView(withId(R.id.recipe_list))
        .check(matches(isDisplayed()));
  }

  @Test
  public void checkRecipesListContent() {
    onView(withId(R.id.recipe_list))
        .perform(actionOnItemAtPosition(1, click()));

    onView(withText(RECIPE_NAME))
        .check(matches(isDisplayed()));
  }
}
