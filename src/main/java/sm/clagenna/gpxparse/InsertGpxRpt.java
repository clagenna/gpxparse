package sm.clagenna.gpxparse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.concurrent.Task;

import lombok.Getter;
import lombok.Setter;
import sm.clagenna.gpxparse.javafx.GpxParseFxmlController;
import sm.clagenna.gpxparse.swing.MainFrame;
import sm.clagenna.gpxparse.util.Punto;

public class InsertGpxRpt extends Task<Void> {

  private static final String CSZ_ENDGPXX       = "</gpxx:rpt>";
  private static final String CSZ_INNESTO       =                                                                         //
      "            </gpxx:RoutePointExtension>\r\n"                                                                       //
          + "          </extensions>\r\n"                                                                                 //
          + "        </rtept>\r\n"                                                                                        //
          + "        <rtept %s>\r\n"                                                                                      //
          + "          <time>2021-07-22T13:04:09Z</time>\r\n"                                                             //
          + "          <name>wpCla%s</name>\r\n"                                                                          //
          + "          <sym>Waypoint</sym>\r\n"                                                                           //
          + "          <extensions>\r\n"                                                                                  //
          + "            <trp:ShapingPoint />\r\n"                                                                        //
          + "            <gpxx:RoutePointExtension>\r\n";

  private static final String CSZ_INNESTO_VIAPT =                                                                         //  
      "        </gpxx:RoutePointExtension>\r\n"                                                                           //
          + "      </extensions>\r\n"                                                                                     //
          + "    </rtept>\r\n"                                                                                            //
          + "    <rtept %s>\r\n"                                                                                          //
          + "      <time>2022-02-18T17:03:29Z</time>\r\n"                                                                 //
          + "      <name>wpCla%s</name>\r\n"                                                                              //
          + "      <cmt>wpCla%s</cmt>\r\n"                                                                                //
          + "      <desc>ins dopo %s m wpCla%s</desc>\r\n"                                                                //
          + "      <sym>Waypoint</sym>\r\n"                                                                               //
          + "      <extensions>\r\n"                                                                                      //
          + "        <trp:ViaPoint>\r\n"                                                                                  //
          + "          <trp:CalculationMode>FasterTime</trp:CalculationMode>\r\n"                                         //
          + "          <trp:ElevationMode>Standard</trp:ElevationMode>\r\n"                                               //
          + "        </trp:ViaPoint>\r\n"                                                                                 //
          + "        <gpxx:RoutePointExtension>\r\n";

  private static final int    MAXITER           = 5000;
  private static final String CSZ_CODS          = "0123456789abcdefjhijklmnopqrstuvwxyz";

  private Pattern             patrtept          = Pattern.compile("[^<]*<rtept lat=\"([0-9.]+)\" lon=\"([0-9.]+)\".*");
  private Pattern             patxxrpt          = Pattern.compile("[^<]*<gpxx:rpt lat=\"([0-9.]+)\" lon=\"([0-9.]+)\".*");
  private Punto               m_refPunto;
  private Punto               m_finalPunto;
  // private static final DecimalFormat s_decFmt    = new DecimalFormat("0000");
  private int                 m_ptClaudio;

  enum Lavoro {
    scanFile, //
    foundRTEPT, //
    foundGPXXRPT, //
    cercaFineGPXXRPT, //
    scriviNuovoRTEPT, //
    //
    scriviRiga
  }

  //  public static final int                        N_SHAPING_PT = 1;
  //  public static final int                        N_VIA_PT     = 2;

  /** distanza in metri per inserire l'inserto */
  @Getter @Setter private int                    metriMin;
  @Getter @Setter private File                   fileIn;
  @Getter @Setter private File                   fileOut;
  @Getter @Setter private MainFrame              frame;
  @Getter @Setter private GpxParseFxmlController fxcntrl;
  @Getter @Setter private ETipoWP                tipowp;

  public InsertGpxRpt(MainFrame mainFrame) {
    setFrame(mainFrame);
    setTipowp(ETipoWP.ShapingPoint);
  }

  public InsertGpxRpt(GpxParseFxmlController gpxParseFxmlController) {
    setFxcntrl(gpxParseFxmlController);
    setTipowp(ETipoWP.ShapingPoint);
  }

  public void doTheJob(int p_distMin, File p_fiIn, File p_fiOut) {
    metriMin = p_distMin;
    fileIn = p_fiIn;
    fileOut = p_fiOut;
    doTheJob();
  }

  public void doTheJob() {
    System.out.println("InsertGpxRpt.doTheJob() TipoWP=" + tipowp);
    m_refPunto = null;
    String riga = null;
    int qtaRighe = 1;
    Lavoro curr = Lavoro.scriviRiga;
    m_ptClaudio = 1;
    System.out.printf("Genero %s\n", fileOut.getAbsolutePath());
    try (PrintWriter prt = new PrintWriter(fileOut)) {
      try (BufferedReader bur = new BufferedReader(new FileReader(fileIn))) {
        System.out.println("Aperto " + fileIn.getAbsolutePath());
        while ( (riga = bur.readLine()) != null) {
          boolean bMtch = false;
          if ( (qtaRighe++ % 13) == 0) {
            aggiornaProgBar(qtaRighe);
            // System.out.printf("%d\r", qtaRighe);
          }
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
                if (distanza < metriMin) {
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
                // String szSeq = s_decFmt.format(m_ptClaudio++);
                String szSeq = traduci(m_ptClaudio++);
                riga = getWayPoint(szDD, szSeq);
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
      System.out.println("Scritto il file:" + fileOut.getAbsolutePath());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private String getWayPoint(String szGeoCoord, String szWpName) {
    String riga = "";
    switch (tipowp) {

      case ShapingPoint:
        riga = String.format(CSZ_INNESTO, szGeoCoord, szWpName);
        break;

      case ViaPoint:
        riga = String.format(CSZ_INNESTO_VIAPT, szGeoCoord, szWpName, szWpName, String.valueOf(metriMin), szWpName);
        break;
    }
    return riga;
  }

  private void aggiornaProgBar(int qtaRighe) {
    if (frame != null)
      frame.settaProgBar(qtaRighe);
    if (fxcntrl != null)
      fxcntrl.settaProgBar(qtaRighe);
  }

  private String traduci(int p_val) {
    int kc = CSZ_CODS.length();
    String sz = "";
    int resto;
    LocalDateTime dt = LocalDateTime.now();
    int val = 0;
    val = dt.getHour();
    val = val * 60 + dt.getMinute();
    val = val * 60 + dt.getSecond();
    val = val * MAXITER;
    val += p_val;

    while (val != 0) {
      resto = val % kc;
      val = val / kc;
      sz = CSZ_CODS.charAt(resto) + sz;
    }
    return sz;
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

  @Override
  protected Void call() throws Exception {
    doTheJob();
    return null;
  }

}
