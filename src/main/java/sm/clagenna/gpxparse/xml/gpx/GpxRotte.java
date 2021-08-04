package sm.clagenna.gpxparse.xml.gpx;

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

  /**
   * Deve convertire un punto gpxx:rpt in<br/>
   * <code>... xmlsnip....</code>
   * 
   * <pre>
   * ------------------------------------------------------------------
   *         &lt;/gpxx:RoutePointExtension&gt;
          &lt;/extensions&gt;
        &lt;/rtept&gt;
        &lt;rtept lat="43.929970264434814" lon="12.324879169464111"&gt;
          &lt;time&gt;2021-07-22T13:04:09Z&lt;/time&gt;
          &lt;name&gt;Strada Provinciale Marecchia e Strada&lt;/name&gt;
          &lt;sym&gt;Waypoint&lt;/sym&gt;
          &lt;extensions&gt;
            &lt;trp:ShapingPoint /&gt;
            &lt;gpxx:RoutePointExtension&gt;
   * ------------------------------------------------------------------
   * </pre>
   */
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
