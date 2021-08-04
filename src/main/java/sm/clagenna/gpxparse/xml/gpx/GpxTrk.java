package sm.clagenna.gpxparse.xml.gpx;

import java.time.LocalDateTime;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Getter;
import sm.clagenna.gpxparse.util.AppUtils;
import sm.clagenna.gpxparse.util.Punto;

public class GpxTrk implements IGpxGest {

  @SuppressWarnings("unused")
  private static final Logger   s_log     = LogManager.getLogger(GpxTrk.class);
  private static final String   CXP_WPT   = "gpx/wpt";
  private static final String   CXP_TEMPO = "gpx/wpt/time";
  private static final String   CXP_NOME  = "gpx/wpt/name";

  @Getter private LocalDateTime tempo;
  @Getter private Punto         punto;
  @Getter private String        nome;

  public GpxTrk() {
    //
  }

  @Override
  public void inizio(String p_xPath, Map<String, String> p_props) {
    if (p_xPath.equals(CXP_WPT))
      setPunto(p_props);
  }

  @Override
  public void fine(String p_xPath, String p_text) {
    switch (p_xPath) {
      case CXP_TEMPO:
        tempo = AppUtils.parseTime(p_text);
        break;

      case CXP_NOME:
        nome = p_text;
        break;
    }

  }

  private void setPunto(Map<String, String> p_props) {
    double lat = Double.parseDouble(p_props.get("lat"));
    double lon = Double.parseDouble(p_props.get("lon"));
    punto = new Punto(lat, lon);
  }

}
