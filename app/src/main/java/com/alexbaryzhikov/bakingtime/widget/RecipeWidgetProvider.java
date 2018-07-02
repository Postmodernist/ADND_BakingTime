package com.alexbaryzhikov.bakingtime.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.alexbaryzhikov.bakingtime.R;
import com.alexbaryzhikov.bakingtime.ui.MainActivity;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

  static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                              int appWidgetId) {

    CharSequence widgetHeader = "Nutella Pie";
    // Construct the RemoteViews object
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);

    // Set widget header text
    views.setTextViewText(R.id.appwidget_header, widgetHeader);

    // Set ListWidgetService intent to act as the adapter for ListView
    Intent adapterIntent = new Intent(context, ListWidgetService.class);
    views.setRemoteAdapter(R.id.appwidget_list, adapterIntent);

    // Set MainActivity intent to launch when clicked
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
      updateAppWidget(context, appWidgetManager, appWidgetId);
    }
  }
}

