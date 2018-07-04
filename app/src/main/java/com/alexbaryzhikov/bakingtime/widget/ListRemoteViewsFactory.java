package com.alexbaryzhikov.bakingtime.widget;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.alexbaryzhikov.bakingtime.R;
import com.alexbaryzhikov.bakingtime.ui.MainActivity;

import static com.alexbaryzhikov.bakingtime.widget.WidgetConfigureActivity.loadIdPref;
import static com.alexbaryzhikov.bakingtime.widget.WidgetConfigureActivity.loadIngredientsPref;

public class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

  private final Context context;
  private final int appWidgetId;

  ListRemoteViewsFactory(@NonNull Context context, final int appWidgetId) {
    this.context = context;
    this.appWidgetId = appWidgetId;
  }

  @Override
  public void onCreate() {
  }

  @Override
  public void onDataSetChanged() {
  }

  @Override
  public void onDestroy() {
  }

  @Override
  public int getCount() {
    return 1;
  }

  @Override
  public RemoteViews getViewAt(int position) {
    // Set ingredients
    CharSequence ingredients = loadIngredientsPref(context, appWidgetId);
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget_item);
    views.setTextViewText(R.id.appwidget_ingredients, ingredients);
    // Fill in onClick intent
    int recipeId = loadIdPref(context, appWidgetId);
    Intent fillInIntent = new Intent();
    fillInIntent.putExtra(MainActivity.KEY_RECIPE_ID, recipeId);
    views.setOnClickFillInIntent(R.id.appwidget_item_container, fillInIntent);
    return views;
  }

  @Override
  public RemoteViews getLoadingView() {
    return null;
  }

  @Override
  public int getViewTypeCount() {
    return 1;
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  @Override
  public boolean hasStableIds() {
    return true;
  }
}
