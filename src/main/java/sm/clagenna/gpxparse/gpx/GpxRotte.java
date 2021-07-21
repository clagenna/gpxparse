package sm.clagenna.gpxparse.gpx;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Getter;

public class GpxRotte implements IGpxGest {

  @SuppressWarnings("unused") private static final Logger s_log     = LogManager.getLogger(GpxRotte.class);
  // private static final String   CXP_RTE   = "gpx/rte";
  private static final String                             CXP_RTEPT = "gpx/rte/rtept";

  private static final String                             CXP_NOME  = "gpx/rte/name";

  @Getter private LocalDateTime                           tempo;
  @Getter private String                                  nome;

  private GpxRottaPunti                                   rottaPunti;
  private List<GpxRottaPunti>                             m_liRottaPunti;

  public GpxRotte() {
    //
  }

  @Override
  public void inizio(String p_xPath, Map<String, String> p_props) {
    if (p_xPath.equals(CXP_RTEPT)) {
      addRottaPunti();
      rottaPunti = (GpxRottaPunti) GpxFactory.getInst().get(p_xPath);
    }
    if (rottaPunti != null)
      rottaPunti.inizio(p_xPath, p_props);
  }

  @Override
  public void fine(String p_xPath, String p_text) {
    if (p_xPath.equals(CXP_NOME) || p_xPath.equals(CXP_RTEPT)) {
      nome = p_text;
      return;
    }
    if (rottaPunti != null)
      rottaPunti.fine(p_xPath, p_text);

  }

  private void addRottaPunti() {
    if (m_liRottaPunti == null)
      m_liRottaPunti = new ArrayList<GpxRottaPunti>();
    if (rottaPunti != null)
      m_liRottaPunti.add(rottaPunti);
  }

}
