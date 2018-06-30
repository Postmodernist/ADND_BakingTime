package com.alexbaryzhikov.bakingtime;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.alexbaryzhikov.bakingtime.di.components.ApplicationComponent;
import com.alexbaryzhikov.bakingtime.ui.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringContains.containsString;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UiTests {

  private static final String[] recipes = {"Nutella Pie", "Brownies", "Yellow Cake", "Cheesecake"};

  private static final String ingredients0 = "• 2 cups of Graham Cracker crumbs\n" +
      "• 6 tbs. of unsalted butter, melted\n" +
      "• 0.5 cups of granulated sugar\n" +
      "• 1.5 tsp. of salt\n" +
      "• 5 tbs. of vanilla\n" +
      "• 1 kg of Nutella or other chocolate-hazelnut spread\n" +
      "• 500 g of Mascapone Cheese(room temperature)\n" +
      "• 1 cup of heavy cream(cold)\n" +
      "• 4 oz of cream cheese(softened)";

  private static final String[] steps0 = {
      "Recipe Introduction",
      "Starting prep",
      "Prep the cookie crust.",
      "Press the crust into baking form.",
      "Start filling prep",
      "Finish filling prep",
      "Finishing Steps"};

  private static final RecyclerViewMatcher cookingStepsMatcher =
      RecyclerViewMatcher.byId(R.id.cooking_steps);

  @Rule
  public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

  private IdlingResource idlingResource;

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
  public void broseFragmentExistsOnStart() {
    onView(withId(R.id.recipe_list))
        .check(matches(isDisplayed()));
  }

  @Test
  public void broseFragmentShowsValidRecipes() {
    for (int i = 0; i < 4; i++) {
      onView(withId(R.id.recipe_list))
          .perform(RecyclerViewActions.scrollToPosition(i));
      onView(withText(recipes[i]))
          .check(matches(isDisplayed()));
    }
  }

  @Test
  public void broseFragmentClickExpandButtonExpandsIngredients() {
    final RecyclerViewMatcher recipeListMatcher = RecyclerViewMatcher.byId(R.id.recipe_list);

    onView(recipeListMatcher.atPositionOnView(0, R.id.expand_button))
        .perform(click());
    onView(recipeListMatcher.atPositionOnView(0, R.id.recipe_ingredients))
        .check(matches(withText(ingredients0)));
  }

  @Test
  public void broseFragmentClickRecipeItemOpensDetailFragment() {
    onView(withId(R.id.recipe_list))
        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    onView(withId(R.id.cooking_steps))
        .check(matches(isDisplayed()));
  }

  @Test
  public void detailFragmentShowValidIngredientsAndCookingSteps() {
    // Open first recipe details
    onView(withId(R.id.recipe_list))
        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

    // Check list content
    onView(cookingStepsMatcher.atPositionOnView(0, R.id.recipe_ingredients))
        .check(matches(withText(ingredients0)));
    for (int i = 0; i < 7; i++) {
      onView(withId(R.id.cooking_steps))
          .perform(RecyclerViewActions.scrollToPosition(i + 1));
      onView(cookingStepsMatcher.atPositionOnView(i + 1, R.id.step_description))
          .check(matches(withText(containsString(steps0[i]))));
    }
  }

  @Test
  public void detailFragmentWhenOpenedIntroIsSelected() {
    // Open first recipe details
    onView(withId(R.id.recipe_list))
        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

    // Check selection
    onView(cookingStepsMatcher.atPositionOnView(1, R.id.selection_mark))
        .check(matches(isDisplayed()));
  }

  @Test
  public void detailFragmentClickStepChangesSelection() {
    // Open first recipe details
    onView(withId(R.id.recipe_list))
        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

    // Select 2nd step
    onView(cookingStepsMatcher.atPosition(2))
        .perform(click());

    // Press back if in phone mode
    if (activityTestRule.getActivity().findViewById(R.id.step_fragment_container) == null) {
      Espresso.pressBack();
    }

    // Check selection
    onView(cookingStepsMatcher.atPositionOnView(1, R.id.selection_mark))
        .check(matches(not(isDisplayed())));
    onView(cookingStepsMatcher.atPositionOnView(2, R.id.selection_mark))
        .check(matches(isDisplayed()));
  }

  @Test
  public void detailFragmentClickStepOpensStepFragment() {
    // Open first recipe details
    onView(withId(R.id.recipe_list))
        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

    // Select 1st step
    onView(cookingStepsMatcher.atPosition(1))
        .perform(click());

    // Check if player view is visible
    onView(withId(R.id.player_view)).
        check(matches(isDisplayed()));
  }
}
