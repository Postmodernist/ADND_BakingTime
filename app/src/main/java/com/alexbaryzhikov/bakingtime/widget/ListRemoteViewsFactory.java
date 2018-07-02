package com.alexbaryzhikov.bakingtime.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.alexbaryzhikov.bakingtime.R;
import com.alexbaryzhikov.bakingtime.ui.MainActivity;

public class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

  private final Context context;

  ListRemoteViewsFactory(Context context) {
    this.context = context;
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
    CharSequence ingredients = context.getString(R.string.appwidget_ingredients);
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_item);
    views.setTextViewText(R.id.appwidget_ingredients, ingredients);

    // TODO On click open recipe detail fragment
    Intent fillInIntent = new Intent();
    fillInIntent.putExtra(MainActivity.EXTRA_RECIPE_ID, 0);
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
