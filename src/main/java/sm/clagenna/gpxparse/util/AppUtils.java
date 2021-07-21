package sm.clagenna.gpxparse.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AppUtils {
  public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

  public static LocalDateTime parseTime(String p_text) {
    LocalDateTime dateTime = LocalDateTime.parse(p_text, formatter);
    return dateTime;
  }

}
