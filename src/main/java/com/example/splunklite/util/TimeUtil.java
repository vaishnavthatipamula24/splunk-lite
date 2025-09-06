package com.example.splunklite.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TimeUtil {
  public static Instant parse(String s){
    if (s == null || s.isBlank() || s.equals("now")) return Instant.now();
    if (s.startsWith("now-")) {
      String rest = s.substring(4);
      long n = Long.parseLong(rest.substring(0, rest.length()-1));
      char u = rest.charAt(rest.length()-1);
      return switch (u) {
        case 's' -> Instant.now().minus(n, ChronoUnit.SECONDS);
        case 'm' -> Instant.now().minus(n, ChronoUnit.MINUTES);
        case 'h' -> Instant.now().minus(n, ChronoUnit.HOURS);
        case 'd' -> Instant.now().minus(n, ChronoUnit.DAYS);
        default -> Instant.parse(s);
      };
    }
    return Instant.parse(s);
  }
}
