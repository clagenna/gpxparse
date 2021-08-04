package sm.clagenna.gpxparse.xml.gpx;

import java.util.Map;

public interface IGpxGest {
  void inizio(String p_xPath, Map<String, String> p_props);

  void fine(String p_xPath, String p_text);
}
