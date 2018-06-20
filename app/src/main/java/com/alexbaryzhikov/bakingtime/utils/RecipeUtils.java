package com.alexbaryzhikov.bakingtime.utils;

public final class RecipeUtils {

  private RecipeUtils() {  // Utility class
  }

  public static String smartValueOf(Double d, String measure) {
    String quantity = d % 1 == 0 ? String.valueOf(d.intValue()) : String.valueOf(d);
    return quantity + humanReadableMeasure(measure, d.compareTo(1.0) == 0);
  }

  private static String humanReadableMeasure(String measure, boolean single) {
    switch (measure) {
      case "CUP": return single ? " cup of" : " cups of";
      case "TBLSP": return " tbs. of";
      case "TSP": return " tsp. of";
      case "G": return " g of";
      case "K": return " kg of";
      case "OZ": return " oz of";
      case "UNIT": return "";
      default: return " " + measure + " of";
    }
  }
}
