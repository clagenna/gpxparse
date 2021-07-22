package sm.clagenna.gpxparse.util;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Getter;

public class Punto implements Serializable {

  /** long serialVersionUID */
  private static final long   serialVersionUID = 455535321314118649L;
  private static final Logger s_log            = LogManager.getLogger(Punto.class);
  // Degrees, minutes, and seconds (DMS): 41°24'12.2"N 2°10'26.5"E
  private static final String CSZ_DMS_NEA      = "([0-9]+)[^.]([0-9]+).([0-9.]+).N([0-9]+)[^.]([0-9]+).([0-9.]+).E";
  // Degrees, minutes, and seconds (DMS): N41°24'12.2" E2°10'26.5"
  private static final String CSZ_DMS_BNE      = "N([0-9]+)[^.]([0-9]+).([0-9.]+).E([0-9]+)[^.]([0-9]+).([0-9.]+).";
  // Degrees, minutes, and seconds (DMS): 2°10'26.5"E 41°24'12.2"N
  private static final String CSZ_DMS_ENA      = "([0-9]+)[^.]([0-9]+).([0-9.]+).E([0-9]+)[^.]([0-9]+).([0-9.]+).N";
  // Degrees, minutes, and seconds (DMS): E2°10'26.5" N41°24'12.2"
  private static final String CSZ_DMS_BEN      = "E([0-9]+)[^.]([0-9]+).([0-9.]+).N([0-9]+)[^.]([0-9]+).([0-9.]+).";

  // Decimal degrees (DD): E2.17403, N41.40338
  private static final String CSZ_DEC_ENA      = "([0-9.]+)E,([0-9.]+)N";
  // Decimal degrees (DD): 2.17403E, 41.40338N,
  private static final String CSZ_DEC_BEN      = "E([0-9.]+),N([0-9.]+)";
  // Decimal degrees (DD): 41.40338N, 2.17403E
  private static final String CSZ_DEC_NEA      = "([0-9.]+)N,([0-9.]+)E";
  // Decimal degrees (DD): N41.40338, E2.17403,
  private static final String CSZ_DEC_BNE      = "N([0-9.]+),E([0-9.]+)";
  // Decimal degrees (DD): 41.40338, 2.17403
  private static final String CSZ_DD           = "([0-9.]+),([0-9.]+)";
  
  private static final DecimalFormat s_decFmt = new DecimalFormat("##.000000000000000");

  private enum EGpxFmt {
    dms_nea, dms_bne, dms_ena, dms_ben, dec_ben, dec_ena, dec_nea, dec_bne, dd
  };

  /** eath radius at equator ( in meter) */
  @SuppressWarnings("unused") private static double s_earthRadius_equa = 6_378_100F;
  @SuppressWarnings("unused") private static double s_earthRadius_mean = 6_371_008.7714F;
  @SuppressWarnings("unused") private static double s_earthRadius_pola = 6_356_800F;

  private static final Map<EGpxFmt, Pattern>        s_map;

  @Getter private double                            lat;
  @Getter private double                            lon;
  @Getter private double                            latR;
  @Getter private double                            lonR;

  static {
    s_map = new HashMap<Punto.EGpxFmt, Pattern>();
    s_map.put(EGpxFmt.dms_nea, Pattern.compile(CSZ_DMS_NEA));
    s_map.put(EGpxFmt.dms_bne, Pattern.compile(CSZ_DMS_BNE));
    s_map.put(EGpxFmt.dms_ena, Pattern.compile(CSZ_DMS_ENA));
    s_map.put(EGpxFmt.dms_ben, Pattern.compile(CSZ_DMS_BEN));
    s_map.put(EGpxFmt.dec_ena, Pattern.compile(CSZ_DEC_ENA));
    s_map.put(EGpxFmt.dec_ben, Pattern.compile(CSZ_DEC_BEN));
    s_map.put(EGpxFmt.dec_nea, Pattern.compile(CSZ_DEC_NEA));
    s_map.put(EGpxFmt.dec_bne, Pattern.compile(CSZ_DEC_BNE));
    s_map.put(EGpxFmt.dd, Pattern.compile(CSZ_DD));

  }

  public Punto() {
    //
  }

  public Punto(double p_lat, double p_lon) {
    set(p_lat, p_lon, false);
  }

  public Punto(double p_lat, double p_lon, boolean p_bRad) {
    set(p_lat, p_lon, p_bRad);
  }

  public Punto(String p_sz) {
    parse(p_sz);
  }

  public void set(double p_lat, double p_lon, boolean p_bRad) {
    if (p_bRad) {
      setLatR(p_lat);
      setLonR(p_lon);
    } else {
      setLat(p_lat);
      setLon(p_lon);
    }
  }

  public void parse(String p_sz) {
    boolean bFound = false;
    String szCoor = p_sz.replaceAll(" ", "");
    for (EGpxFmt ef : EGpxFmt.values()) {
      Pattern pat = s_map.get(ef);
      Matcher mtch = pat.matcher(szCoor);
      if ( !mtch.matches())
        continue;
      bFound = true;
      int k = 1;
      double dd = 0;
      switch (ef) {
        case dms_nea:
        case dms_bne:
          dd = Double.parseDouble(mtch.group(k++));
          dd += Double.parseDouble(mtch.group(k++)) / 60F;
          dd += Double.parseDouble(mtch.group(k++)) / 3600F;
          setLat(dd);
          dd = Double.parseDouble(mtch.group(k++));
          dd += Double.parseDouble(mtch.group(k++)) / 60F;
          dd += Double.parseDouble(mtch.group(k++)) / 3600F;
          setLon(dd);
          return;
        case dms_ena:
        case dms_ben:
          dd = Double.parseDouble(mtch.group(k++));
          dd += Double.parseDouble(mtch.group(k++)) / 60F;
          dd += Double.parseDouble(mtch.group(k++)) / 3600F;
          setLon(dd);
          dd = Double.parseDouble(mtch.group(k++));
          dd += Double.parseDouble(mtch.group(k++)) / 60F;
          dd += Double.parseDouble(mtch.group(k++)) / 3600F;
          setLat(dd);
          return;
        case dec_ben:
        case dec_ena:
          dd = Double.parseDouble(mtch.group(k++));
          setLon(dd);
          dd = Double.parseDouble(mtch.group(k++));
          setLat(dd);
          return;
        case dec_nea:
        case dec_bne:
        case dd:
          dd = Double.parseDouble(mtch.group(k++));
          setLat(dd);
          dd = Double.parseDouble(mtch.group(k++));
          setLon(dd);
          return;
      }
    }
    if ( !bFound)
      s_log.error("Non ho interpretato {}", p_sz);
  }

  public double distance(Punto p_pu) {
    double dd = 0;
    // Haversine formula
    double dlon = p_pu.getLonR() - lonR;
    double dlat = p_pu.getLatR() - latR;
    double a = Math.pow(Math.sin(dlat / 2), 2) //
        + Math.cos(latR) * Math.cos(p_pu.getLatR()) //
            * Math.pow(Math.sin(dlon / 2), 2);

    double c = 2 * Math.asin(Math.sqrt(a));
    dd = (c * s_earthRadius_mean);
    return dd;
  }

  public double dist2(Punto p_pu) {
    double theta = lonR - p_pu.getLonR();
    double dist = Math.sin(latR) * Math.sin(p_pu.getLatR()) //
        + Math.cos(latR) * Math.cos(p_pu.getLatR()) //
            * Math.cos(theta);
    dist = Math.acos(dist);
    dist = Math.toDegrees(dist);
    dist = dist * 60 * 1.1515;
    dist = dist * 1609.344;
    return (dist);
  }

  public void setLat(double p_lat) {
    lat = p_lat;
    latR = lat / 180f * Math.PI;
  }

  public void setLon(double p_lon) {
    lon = p_lon;
    lonR = lon / 180f * Math.PI;
  }

  public void setLatR(double p_latR) {
    latR = p_latR;
    lat = latR / Math.PI * 180F;
  }

  public void setLonR(double p_lonR) {
    lonR = p_lonR;
    lon = lonR / Math.PI * 180F;
  }

  public String getDD() {
    String szLat = s_decFmt.format(lat).replace(",", ".");
    String szLon = s_decFmt.format(lon).replace(",", ".");
    String sz = String.format("lat=\"%s\" lon=\"%s\"", szLat, szLon);
    return sz;
  }

  @Override
  public String toString() {
    String sz = String.format("N%.8f E%.8f", lat, lon);
    return sz;
  }

}
