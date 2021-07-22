package prova.gpx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import sm.clagenna.gpxparse.util.Punto;

public class LeggiScrivi {

  private static final String        CSZ_FILIN   = "dati/2015-06-27 Sardegna.gpx";
  private static final String        CSZ_FILOU   = "dati/2015-06-27 Sardegna_AFTER.gpx";

  /** distanza in metri per inserire l'inserto */
  private static double              s_maxDist   = 1_200;

  private static final String        CSZ_ENDGPXX = "</gpxx:rpt>";
  private static final String        CSZ_INNESTO =                                                                         //
      "</gpxx:RoutePointExtension>\r\n"                                                                                    //
          + "          </extensions>\r\n"                                                                                  //
          + "        </rtept>\r\n"                                                                                         //
          + "        <rtept %s>\r\n"                                                                                       //
          + "          <time>2021-07-22T13:04:09Z</time>\r\n"                                                              //
          + "          <name>PtClaudio%s</name>\r\n"                                                                       //
          + "          <sym>Waypoint</sym>\r\n"                                                                            //
          + "          <extensions>\r\n"                                                                                   //
          + "            <trp:ShapingPoint />\r\n"                                                                         //
          + "            <gpxx:RoutePointExtension>\r\n";

  private Pattern                    patrtept    = Pattern.compile("[^<]*<rtept lat=\"([0-9.]+)\" lon=\"([0-9.]+)\".*");
  private Pattern                    patxxrpt    = Pattern.compile("[^<]*<gpxx:rpt lat=\"([0-9.]+)\" lon=\"([0-9.]+)\".*");
  private Punto                      m_refPunto;
  private Punto                      m_finalPunto;
  private static final DecimalFormat s_decFmt    = new DecimalFormat("000");
  private int                        m_ptClaudio;

  enum Lavoro {
    scanFile, //
    foundRTEPT, //
    foundGPXXRPT, //
    cercaFineGPXXRPT, //
    scriviNuovoRTEPT, //
    //
    scriviRiga
  }

  @Test
  public void doTheJob() {
    m_refPunto = null;
    String riga = null;
    Lavoro curr = Lavoro.scriviRiga;
    m_ptClaudio = 1;

    try (PrintWriter prt = new PrintWriter(new File(CSZ_FILOU))) {
      try (BufferedReader bur = new BufferedReader(new FileReader(new File(CSZ_FILIN)))) {
        while ( (riga = bur.readLine()) != null) {
          boolean bMtch = false;
          Matcher mtch = patrtept.matcher(riga);
          bMtch = mtch.find();
          if ( !bMtch) {
            mtch = patxxrpt.matcher(riga);
            bMtch = mtch.find();
            if (bMtch)
              curr = Lavoro.foundGPXXRPT;
          } else
            curr = Lavoro.foundRTEPT;
          boolean bLoop = false;
          // inizio del lavoro
          do {
            bLoop = true;
            switch (curr) {

              case scanFile:
                // !! non serve !!!
                curr = Lavoro.scriviRiga;
                break;

              case foundRTEPT:
                decodificaStartPoint(riga, mtch);
                curr = Lavoro.scriviRiga;
                break;

              case foundGPXXRPT:
                decodificaGpxxRpt(riga, mtch);
                boolean bCeEnd = riga.trim().endsWith("/>");
                double distanza = m_refPunto.distance(m_finalPunto);
                if (distanza < s_maxDist) {
                  curr = Lavoro.scriviRiga;
                  break;
                }
                if (bCeEnd) {
                  curr = Lavoro.scriviNuovoRTEPT;
                  break;
                }
                curr = Lavoro.cercaFineGPXXRPT;
                break;

              case cercaFineGPXXRPT:
                prt.println(riga);
                if (riga.contains(CSZ_ENDGPXX))
                  curr = Lavoro.scriviNuovoRTEPT;
                else
                  bLoop = false;
                break;

              case scriviNuovoRTEPT:
                String szDD = m_finalPunto.getDD();
                String szSeq = s_decFmt.format(m_ptClaudio++);
                riga = String.format(CSZ_INNESTO, szDD, szSeq);
                curr = Lavoro.scriviRiga;
                m_refPunto = m_finalPunto;
                break;

              case scriviRiga:
                prt.println(riga);
                bLoop = false;
                break;

              default:
                break;
            }
          } while (bLoop);

        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void decodificaStartPoint(String riga, Matcher mtch) {
    double lat = Double.parseDouble(mtch.group(1));
    double lon = Double.parseDouble(mtch.group(2));
    m_refPunto = new Punto(lat, lon);
  }

  private void decodificaGpxxRpt(String riga, Matcher mtch) {
    double lat = Double.parseDouble(mtch.group(1));
    double lon = Double.parseDouble(mtch.group(2));
    m_finalPunto = new Punto(lat, lon);
  }

}
