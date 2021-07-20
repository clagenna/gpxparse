package sm.clagenna.gpxparse.util;

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Punto implements Serializable {

  /** long serialVersionUID */
  private static final long   serialVersionUID = 455535321314118649L;
  private static final Logger s_log            = LogManager.getLogger(Punto.class);
  // Degrees, minutes, and seconds (DMS): 41°24'12.2"N 2°10'26.5"E
  private static final String CSZ_DMS_NEA = "([0-9]+).([0-9]+).([0-9.]+).N([0-9]+).([0-9]+).([0-9.]+).E";
  // Degrees, minutes, and seconds (DMS): N41°24'12.2" E2°10'26.5"
  private static final String CSZ_DMS_BNE = "N([0-9]+).([0-9]+).([0-9.]+).E([0-9]+).([0-9]+).([0-9.]+).";
  // Degrees, minutes, and seconds (DMS): 2°10'26.5"E 41°24'12.2"N
  private static final String CSZ_DMS_ENA = "([0-9]+).([0-9]+).([0-9.]+).E([0-9]+).([0-9]+).([0-9.]+).N";
  // Degrees, minutes, and seconds (DMS): E2°10'26.5" N41°24'12.2"
  private static final String CSZ_DMS_BEN = "E([0-9]+).([0-9]+).([0-9.]+).N([0-9]+).([0-9]+).([0-9.]+).";
  // Decimal degrees (DD): E2.17403, N41.40338 
  private static final String CSZ_DEC_ENA = "([0-9.]+)E,([0-9.]+)N";
  // Decimal degrees (DD): 2.17403E, 41.40338N, 
  private static final String CSZ_DEC_BEN = "E([0-9.]+),N([0-9.]+)";
  // Decimal degrees (DD): 41.40338N, 2.17403E 
  private static final String CSZ_DEC_NEA = "([0-9.]+)N,([0-9.]+)E";
  // Decimal degrees (DD): N41.40338, E2.17403,  
  private static final String CSZ_DEC_BNE = "E([0-9.]+),N([0-9.]+)";
  // Decimal degrees (DD): 41.40338, 2.17403
  private static final String CSZ_DD = "([0-9.]+),([0-9.]+)";

  
  private enum EGpxFmt { dms_nea, dms_bne, dms_ena, dms_ben,  dd_ne };
  
  
  
  private double              lat;
  private double              lon;
  private double              latR;
  private double              lonR;

  public Punto() {
    //
  }

  public Punto(String p_sz) {

  }

  public void parse(String p_sz) {

  }

  public double getLat() {
    return lat;
  }

  public void setLat(double lat) {
    this.lat = lat;
  }

  public double getLon() {
    return lon;
  }

  public void setLon(double lon) {
    this.lon = lon;
  }

  public double getLatR() {
    return latR;
  }

  public void setLatR(double latR) {
    this.latR = latR;
  }

  public double getLonR() {
    return lonR;
  }

  public void setLonR(double lonR) {
    this.lonR = lonR;
  }

}
