package sm.clagenna.gpxparse.xml.gpx;

import java.time.LocalDateTime;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Getter;
import sm.clagenna.gpxparse.util.AppUtils;
import sm.clagenna.gpxparse.util.Punto;

public class GpxMetadata implements IGpxGest {

  @SuppressWarnings("unused")
  private static final Logger   s_log     = LogManager.getLogger(GpxMetadata.class);
  private static final String   CXP_TEMPO = "gpx/metadata/time";
  private static final String   CXP_BOUND = "gpx/metadata/bounds";

  @Getter private LocalDateTime tempo;
  @Getter private Punto         maxbound;
  @Getter private Punto         minbound;

  public GpxMetadata() {
    // 
  }

  @Override
  public void inizio(String p_xPath, Map<String, String> p_props) {
    if (p_xPath.equals(CXP_BOUND))
      setBounds(p_props);

  }

  @Override
  public void fine(String p_xPath, String p_text) {
    if ( p_xPath.equals(CXP_TEMPO))
      tempo = AppUtils.parseTime(p_text);

  }

  private void setBounds(Map<String, String> p_props) {
    double lat = Double.parseDouble(p_props.get("maxlat"));
    double lon = Double.parseDouble(p_props.get("maxlon"));
    maxbound = new Punto(lat, lon);
    
    lat = Double.parseDouble(p_props.get("minlat"));
    lon = Double.parseDouble(p_props.get("minlon"));
    minbound = new Punto(lat, lon);
  }

}
