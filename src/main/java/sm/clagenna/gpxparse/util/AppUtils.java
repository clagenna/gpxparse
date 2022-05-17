package sm.clagenna.gpxparse.util;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import sm.clagenna.gpxparse.ex.GpxException;

public class AppUtils {
  public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

  public static LocalDateTime parseTime(String p_text) {
    LocalDateTime dateTime = LocalDateTime.parse(p_text, formatter);
    return dateTime;
  }

  public static File creaFileOut(File p_fiIn) throws GpxException {
    String szFiOu = p_fiIn.getAbsolutePath();
    AppProperties props = AppProperties.getInst();
    int n = szFiOu.lastIndexOf(".");
    if (n < 1) {
      String szMsg = "Non trovo il suffisso GPX" + szFiOu;
      throw new GpxException(szMsg);
    }
    String szExt = szFiOu.substring(n + 1);
    if (szExt == null || !szExt.toLowerCase().equals("gpx")) {
      String szMsg = "Non è un file GPX: " + szFiOu;
      throw new GpxException(szMsg);
    }
    szFiOu = szFiOu.substring(0, n);
    String sufx = "";
    switch (props.getTipoWp()) {
      case ShapingPoint:
        sufx = "_SHAPWP.gpx";
        break;
      case ViaPoint:
        sufx = "_VIAWP.gpx";
        break;
    }
    File fiOut = new File(szFiOu + sufx);
    int k = 1;
    while (fiOut.exists()) {
      fiOut = new File(szFiOu + "_" + k++ + sufx);
    }
    props.setGpxOut(fiOut);
    return fiOut;
  }

}
