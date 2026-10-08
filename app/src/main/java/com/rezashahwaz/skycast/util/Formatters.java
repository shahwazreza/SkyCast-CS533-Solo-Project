// Formatters.java
// Parses Open-Meteo date strings and formats times, days and numbers for display
package com.rezashahwaz.skycast.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Formatters {
   // API times are already in the city's local time, so parse and format
   // them in UTC to keep the wall-clock time unchanged
   private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

   private Formatters() { } // static methods only

   // "2026-10-07T18:28" -> "6:28 PM"
   public static String formatTime(String isoDateTime) {
      return reformat(isoDateTime, "yyyy-MM-dd'T'HH:mm", "h:mm a");
   }

   // "2026-10-07T18:00" -> "6PM", for chart labels
   public static String formatHour(String isoDateTime) {
      return reformat(isoDateTime, "yyyy-MM-dd'T'HH:mm", "ha");
   }

   // "2026-10-07" -> "Wed"
   public static String formatDayShort(String isoDate) {
      return reformat(isoDate, "yyyy-MM-dd", "EEE");
   }

   // "2026-10-07" -> "Wednesday"
   public static String formatDayLong(String isoDate) {
      return reformat(isoDate, "yyyy-MM-dd", "EEEE");
   }

   // device-local time a forecast was downloaded, e.g. "Oct 7, 4:45 PM"
   public static String formatTimestamp(long millis) {
      return new SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
         .format(new Date(millis));
   }

   // rounds a temperature or speed for display; "--" if missing
   public static String formatNumber(double value) {
      if (Double.isNaN(value))
         return "--";
      long rounded = Math.round(value);
      return String.valueOf(rounded == 0 ? 0 : rounded); // no "-0"
   }

   // parses text with one pattern and formats it with another; returns
   // the original text if it can't be parsed
   private static String reformat(String text, String inPattern,
      String outPattern) {
      if (text == null)
         return "";
      try {
         SimpleDateFormat in = new SimpleDateFormat(inPattern, Locale.US);
         in.setTimeZone(UTC);
         SimpleDateFormat out =
            new SimpleDateFormat(outPattern, Locale.getDefault());
         out.setTimeZone(UTC);
         return out.format(in.parse(text));
      }
      catch (ParseException e) {
         return text;
      }
   }
}
