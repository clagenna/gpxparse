package sm.clagenna.gpxparse.xml.gpx;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Getter;
import sm.clagenna.gpxparse.util.AppUtils;
import sm.clagenna.gpxparse.util.Punto;

@SuppressWarnings("unused")
public class GpxRottaPunti implements IGpxGest {

  private static final Logger   s_log           = LogManager.getLogger(GpxRottaPunti.class);
  private static final String   CXP_NOME        = "gpx/rte/name";

  private static final String   CXP_RTEPT       = "gpx/rte/rtept";
  private static final String   CXP_RTEPT_name  = "gpx/rte/rtept/name";
  private static final String   CXP_RTEPT_time  = "gpx/rte/rtept/time";
  private static final String   CXP_RTEPT_cmt   = "gpx/rte/rtept/cmt";
  private static final String   CXP_RTEPT_punti = "gpx/rte/rtept/extensions/gpxx:RoutePointExtension/gpxx:rpt";

  @Getter private Punto         punto;
  @Getter private LocalDateTime tempo;
  @Getter private String        nome;
  @Getter private String        commento;

  private List<Punto>           m_punti;

  @Override
  public void inizio(String p_xPath, Map<String, String> p_props) {
    double lat = 0;
    double lon = 0;
    switch (p_xPath) {
      case CXP_RTEPT:
        m_punti = new ArrayList<Punto>();
        lat = Double.parseDouble(p_props.get("lat"));
        lon = Double.parseDouble(p_props.get("lon"));
        punto = new Punto(lat, lon);
        return;

      case CXP_RTEPT_punti:
        lat = Double.parseDouble(p_props.get("lat"));
        lon = Double.parseDouble(p_props.get("lon"));
        Punto pu = new Punto(lat, lon);
        m_punti.add(pu);
        return;

    }
  }

  @Override
  public void fine(String p_xPath, String p_text) {
    switch (p_xPath) {
      case CXP_RTEPT_name:
        nome = p_text;
        break;
      case CXP_RTEPT_time:
        tempo = AppUtils.parseTime(p_text);
        break;
      case CXP_RTEPT_cmt:
        commento = p_text;
        break;
    }

  }

}
