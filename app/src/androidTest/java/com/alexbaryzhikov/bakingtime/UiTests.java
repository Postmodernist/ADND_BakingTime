package com.alexbaryzhikov.bakingtime;

import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.alexbaryzhikov.bakingtime.api.RecipeApiConstants;
import com.alexbaryzhikov.bakingtime.ui.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import okhttp3.mockwebserver.MockWebServer;

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

  private static final String RECIPE0_INGREDIENTS = "• 2 cups of Graham Cracker crumbs\n" +
      "• 6 tbs. of unsalted butter, melted\n" +
      "• 0.5 cups of granulated sugar\n" +
      "• 1.5 tsp. of salt\n" +
      "• 5 tbs. of vanilla\n" +
      "• 1 kg of Nutella or other chocolate-hazelnut spread\n" +
      "• 500 g of Mascapone Cheese(room temperature)\n" +
      "• 1 cup of heavy cream(cold)\n" +
      "• 4 oz of cream cheese(softened)";

  private static final String[] RECIPE0_STEPS = {
      "Recipe Introduction",
      "Starting prep",
      "Prep the cookie crust.",
      "Press the crust into baking form.",
      "Start filling prep",
      "Finish filling prep",
      "Finishing Steps"};

  private static final String RECIPE0_STEP2_DESCRIPTION = "2. Whisk the graham cracker crumbs, 50 grams (1/4 cup) of sugar, and 1/2 teaspoon of salt together in a medium bowl. Pour the melted butter and 1 teaspoon of vanilla into the dry ingredients and stir together until evenly mixed.";

  private static final RecyclerViewMatcher cookingStepsMatcher = RecyclerViewMatcher.byId(R.id.cooking_steps);

  @Rule
  public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

  private IdlingResource idlingResource;
  private MockWebServer server;

  @Before
  public void registerIdlingResource() {
    idlingResource = activityTestRule.getActivity().getMainActivityComponent().simpleIdlingResource();
    IdlingRegistry.getInstance().register(idlingResource);
  }

  @Before
  public void setupMockWebServer() throws IOException {
    server = new MockWebServer();
    server.start();
    RecipeApiConstants.BASE_URL = server.url("/").toString();
  }

  @After
  public void shutdownMockWebServer() throws IOException {
    server.shutdown();
  }

  @After
  public void unregisterIdlingResource() {
    if (idlingResource != null) {
      IdlingRegistry.getInstance().unregister(idlingResource);
    }
  }

  @Test
  public void broseFragmentExistsOnStart() {
    TestUtils.enqueueNormalResponse(server);
    activityTestRule.launchActivity(new Intent());

    onView(withId(R.id.recipe_list))
        .check(matches(isDisplayed()));
  }

  @Test
  public void browseFragmentHandlesErrorResponse() {
    TestUtils.enqueueErrorResponse(server);
    activityTestRule.launchActivity(new Intent());

    // Check refresh button visibility
    onView(withId(R.id.refresh_button))
        .check(matches(isDisplayed()));

    TestUtils.enqueueNormalResponse(server);

    // Click refresh button
    onView(withId(R.id.refresh_button))
        .perform(click());

    // Check recipes list visibility
    onView(withId(R.id.recipe_list))
        .check(matches(isDisplayed()));
  }

  @Test
  public void broseFragmentShowsValidRecipes() {
    TestUtils.enqueueNormalResponse(server);
    activityTestRule.launchActivity(new Intent());

    for (int i = 0; i < 4; i++) {
      onView(withId(R.id.recipe_list))
          .perform(RecyclerViewActions.scrollToPosition(i));
      onView(withText(recipes[i]))
          .check(matches(isDisplayed()));
    }
  }

  @Test
  public void broseFragmentClickExpandButtonShowsIngredients() {
    final RecyclerViewMatcher recipeListMatcher = RecyclerViewMatcher.byId(R.id.recipe_list);

    TestUtils.enqueueNormalResponse(server);
    activityTestRule.launchActivity(new Intent());

    onView(recipeListMatcher.atPositionOnView(0, R.id.expand_button))
        .perform(click());
    onView(recipeListMatcher.atPositionOnView(0, R.id.recipe_ingredients))
        .check(matches(withText(RECIPE0_INGREDIENTS)));
  }

  @Test
  public void broseFragmentClickRecipeItemOpensDetailFragment() {
    TestUtils.enqueueNormalResponse(server);
    activityTestRule.launchActivity(new Intent());

    onView(withId(R.id.recipe_list))
        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    onView(withId(R.id.cooking_steps))
        .check(matches(isDisplayed()));
  }

  @Test
  public void detailFragmentShowValidIngredientsAndCookingSteps() {
    TestUtils.enqueueNormalResponse(server);
    activityTestRule.launchActivity(new Intent());

    // Open first recipe details
    onView(withId(R.id.recipe_list))
        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

    // Check list content
    onView(cookingStepsMatcher.atPositionOnView(0, R.id.recipe_ingredients))
        .check(matches(withText(RECIPE0_INGREDIENTS)));
    for (int i = 0; i < 7; i++) {
      onView(withId(R.id.cooking_steps))
          .perform(RecyclerViewActions.scrollToPosition(i + 1));
      onView(cookingStepsMatcher.atPositionOnView(i + 1, R.id.step_description))
          .check(matches(withText(containsString(RECIPE0_STEPS[i]))));
    }
  }

  @Test
  public void detailFragmentWhenOpenedIntroIsSelected() {
    TestUtils.enqueueNormalResponse(server);
    activityTestRule.launchActivity(new Intent());

    // Open first recipe details
    onView(withId(R.id.recipe_list))
        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

    // Check selection
    onView(cookingStepsMatcher.atPositionOnView(1, R.id.selection_mark))
        .check(matches(isDisplayed()));
  }

  @Test
  public void detailFragmentClickStepChangesSelection() {
    TestUtils.enqueueNormalResponse(server);
    activityTestRule.launchActivity(new Intent());

    // Open first recipe details
    onView(withId(R.id.recipe_list))
        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

    // Select 2nd step
    onView(cookingStepsMatcher.atPosition(2))
        .perform(click());

    // Press back if in phone mode
    if (TestUtils.getDisplayState().phone) {
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
    TestUtils.enqueueNormalResponse(server);
    activityTestRule.launchActivity(new Intent());

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

  @Test
  public void stepFragmentDisplaysValidDescription() {
    if (TestUtils.getDisplayState().fullscreen) {
      return;
    }

    TestUtils.enqueueNormalResponse(server);
    activityTestRule.launchActivity(new Intent());

    // Open recipe details
    onView(withId(R.id.recipe_list))
        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

    // Select step
    onView(cookingStepsMatcher.atPosition(3))
        .perform(click());

    // Check text
    onView(withId(R.id.instructions))
        .check(matches(withText(RECIPE0_STEP2_DESCRIPTION)));
  }

  @Test
  public void stepFragmentHidesPrevAndNextButtons() {
    if (TestUtils.getDisplayState().fullscreen) {
      return;
    }

    TestUtils.enqueueNormalResponse(server);
    activityTestRule.launchActivity(new Intent());

    // Open recipe details
    onView(withId(R.id.recipe_list))
        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

    // Select step
    onView(withId(R.id.cooking_steps))
        .perform(RecyclerViewActions.scrollToPosition(1));
    onView(cookingStepsMatcher.atPosition(1))
        .perform(click());

    // Check buttons visibility
    onView(withId(R.id.prev_step))
        .check(matches(not(isDisplayed())));
    onView(withId(R.id.next_step))
        .check(matches(isDisplayed()));

    // Press back if in phone mode
    if (TestUtils.getDisplayState().phone) {
      Espresso.pressBack();
    }

    // Select step
    onView(withId(R.id.cooking_steps))
        .perform(RecyclerViewActions.scrollToPosition(2));
    onView(cookingStepsMatcher.atPosition(2))
        .perform(click());

    // Check buttons visibility
    onView(withId(R.id.prev_step))
        .check(matches(isDisplayed()));
    onView(withId(R.id.next_step))
        .check(matches(isDisplayed()));

    // Press back if in phone mode
    if (TestUtils.getDisplayState().phone) {
      Espresso.pressBack();
    }

    // Select step
    onView(withId(R.id.cooking_steps))
        .perform(RecyclerViewActions.scrollToPosition(7));
    onView(cookingStepsMatcher.atPosition(7))
        .perform(click());

    // Check buttons visibility
    onView(withId(R.id.prev_step))
        .check(matches(isDisplayed()));
    onView(withId(R.id.next_step))
        .check(matches(not(isDisplayed())));
  }

  @Test
  public void stepFragmentNextButtonSwitchSteps() {
    if (TestUtils.getDisplayState().fullscreen) {
      return;
    }

    TestUtils.enqueueNormalResponse(server);
    activityTestRule.launchActivity(new Intent());

    // Open recipe details
    onView(withId(R.id.recipe_list))
        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

    // Select step
    onView(withId(R.id.cooking_steps))
        .perform(RecyclerViewActions.scrollToPosition(2));
    onView(cookingStepsMatcher.atPosition(2))
        .perform(click());

    // Click next
    onView(withId(R.id.next_step))
        .perform(click());

    // Check content changed
    onView(withId(R.id.instructions))
        .check(matches(withText(RECIPE0_STEP2_DESCRIPTION)));

    // Press back if in phone mode
    if (TestUtils.getDisplayState().phone) {
      Espresso.pressBack();
    }

    // Check selection marker
    onView(cookingStepsMatcher.atPositionOnView(2, R.id.selection_mark))
        .check(matches(not(isDisplayed())));
    onView(cookingStepsMatcher.atPositionOnView(3, R.id.selection_mark))
        .check(matches(isDisplayed()));
  }

  @Test
  public void stepFragmentPrevButtonSwitchSteps() {
    if (TestUtils.getDisplayState().fullscreen) {
      return;
    }

    TestUtils.enqueueNormalResponse(server);
    activityTestRule.launchActivity(new Intent());

    // Open recipe details
    onView(withId(R.id.recipe_list))
        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

    // Select step
    onView(withId(R.id.cooking_steps))
        .perform(RecyclerViewActions.scrollToPosition(4));
    onView(cookingStepsMatcher.atPosition(4))
        .perform(click());

    // Click next
    onView(withId(R.id.prev_step))
        .perform(click());

    // Check content changed
    onView(withId(R.id.instructions))
        .check(matches(withText(RECIPE0_STEP2_DESCRIPTION)));

    // Press back if in phone mode
    if (TestUtils.getDisplayState().phone) {
      Espresso.pressBack();
    }

    // Check selection marker
    onView(cookingStepsMatcher.atPositionOnView(4, R.id.selection_mark))
        .check(matches(not(isDisplayed())));
    onView(cookingStepsMatcher.atPositionOnView(3, R.id.selection_mark))
        .check(matches(isDisplayed()));
  }

  @Test
  public void stepFragmentHandlesAbsenceOfVideo() {
    TestUtils.enqueueNormalResponse(server);
    activityTestRule.launchActivity(new Intent());

    // Open recipe details
    onView(withId(R.id.recipe_list))
        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

    // Select step
    onView(withId(R.id.cooking_steps))
        .perform(RecyclerViewActions.scrollToPosition(2));
    onView(cookingStepsMatcher.atPosition(2))
        .perform(click());

    onView(withId(R.id.no_video))
        .check(matches(isDisplayed()));
  }
}
