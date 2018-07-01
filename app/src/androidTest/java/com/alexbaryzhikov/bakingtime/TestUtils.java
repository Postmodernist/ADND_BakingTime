package com.alexbaryzhikov.bakingtime;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class TestUtils {

  private static DisplayState displayState;

  public static DisplayState getDisplayState() {
    if (displayState == null) {
      Resources resources = InstrumentationRegistry.getTargetContext().getResources();
      boolean phone = resources.getConfiguration().smallestScreenWidthDp < 600;
      int orientation = resources.getConfiguration().orientation;
      boolean fullscreen = phone && orientation == ORIENTATION_LANDSCAPE;
      displayState = new DisplayState(phone, orientation, fullscreen);
    }
    return displayState;
  }

  public static final class DisplayState {
    final boolean phone;
    final int orientation;
    final boolean fullscreen;

    DisplayState(boolean phone, int orientation, boolean fullscreen) {
      this.phone = phone;
      this.orientation = orientation;
      this.fullscreen = fullscreen;
    }
  }

  public static void enqueueNormalResponse(MockWebServer server) {
    String filename = "baking_200_ok_response.json";
    try {
      server.enqueue(new MockResponse()
          .setResponseCode(200)
          .setBody(getStringFromFile(InstrumentationRegistry.getContext(), filename)));
    } catch (IOException e) {
      throw new RuntimeException("Failed to read from file " + filename, e);
    }
  }

  public static void enqueueErrorResponse(MockWebServer server) {
    server.enqueue(new MockResponse()
        .setResponseCode(404)
        .setBody("{}"));
  }

  private static String getStringFromFile(Context context, String filePath) throws IOException {
    final InputStream stream = context.getResources().getAssets().open(filePath);
    String ret = convertStreamToString(stream);
    stream.close();
    return ret;
  }

  private static String convertStreamToString(InputStream is) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      sb.append(line).append("\n");
    }
    reader.close();
    return sb.toString();
  }
}
