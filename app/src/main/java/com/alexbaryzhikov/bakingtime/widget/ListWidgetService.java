package com.alexbaryzhikov.bakingtime.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class ListWidgetService extends RemoteViewsService {

  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent) {
    final String appWidgetId = intent.getData() != null ? intent.getData().getFragment() : "";
    if (!appWidgetId.isEmpty()) {
      return new ListRemoteViewsFactory(this.getApplicationContext(), Integer.valueOf(appWidgetId));
    }
    throw new IllegalStateException("Expected AppWidgetId");
  }
}
