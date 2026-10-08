// FormattersTest.java
// JUnit tests for the date and number formatting helpers
package com.rezashahwaz.skycast.util;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

public class FormattersTest {
   @Test
   public void formatsTimesAndDays() {
      Locale.setDefault(Locale.US);
      assertEquals("6:28 PM", Formatters.formatTime("2026-10-07T18:28"));
      assertEquals("6PM", Formatters.formatHour("2026-10-07T18:00"));
      assertEquals("Wed", Formatters.formatDayShort("2026-10-07"));
      assertEquals("Wednesday", Formatters.formatDayLong("2026-10-07"));
      assertEquals("not a date", Formatters.formatTime("not a date"));
   }

   @Test
   public void formatsNumbers() {
      assertEquals("65", Formatters.formatNumber(65.1));
      assertEquals("0", Formatters.formatNumber(-0.4));
      assertEquals("-3", Formatters.formatNumber(-2.6));
      assertEquals("--", Formatters.formatNumber(Double.NaN));
   }
}
