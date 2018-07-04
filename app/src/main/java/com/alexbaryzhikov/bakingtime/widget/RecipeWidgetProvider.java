package com.alexbaryzhikov.bakingtime.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.alexbaryzhikov.bakingtime.R;
import com.alexbaryzhikov.bakingtime.ui.MainActivity;

import static com.alexbaryzhikov.bakingtime.widget.WidgetConfigureActivity.deletePrefs;
import static com.alexbaryzhikov.bakingtime.widget.WidgetConfigureActivity.loadNamePref;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

  public static void updateAppWidget(@NonNull Context context, @NonNull AppWidgetManager appWidgetManager,
                                     final int appWidgetId, @NonNull final CharSequence name) {
    // Construct the RemoteViews object
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);

    // Set widget header text
    views.setTextViewText(R.id.appwidget_header, name);

    // Set ListWidgetService intent to act as the adapter for ListView
    Intent adapterIntent = new Intent(context, ListWidgetService.class);
    Uri data = Uri.fromParts("widget", "app-widget-id", String.valueOf(appWidgetId));
    adapterIntent.setData(data);
    views.setRemoteAdapter(R.id.appwidget_list, adapterIntent);

    // Set list item onClick intent template
    Intent activityIntent = new Intent(context, MainActivity.class);
    PendingIntent pendingIntent = TaskStackBuilder.create(context)
        .addNextIntentWithParentStack(activityIntent)
        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    views.setPendingIntentTemplate(R.id.appwidget_list, pendingIntent);

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views);
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    // There may be multiple widgets active, so update all of them
    for (int appWidgetId : appWidgetIds) {
      final String name = loadNamePref(context, appWidgetId);
      updateAppWidget(context, appWidgetManager, appWidgetId, name);
    }
  }

  @Override
  public void onDeleted(Context context, int[] appWidgetIds) {
    // Delete the preferences associated with the deleted widgets
    for (int appWidgetId : appWidgetIds) {
      deletePrefs(context, appWidgetId);
    }
  }
}
