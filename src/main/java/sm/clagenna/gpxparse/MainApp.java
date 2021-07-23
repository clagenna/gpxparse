package sm.clagenna.gpxparse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sm.clagenna.gpxparse.util.Punto;

public class MainApp {

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
  private static final DecimalFormat s_decFmt    = new DecimalFormat("0000");
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

  /** distanza in metri per inserire l'inserto */
  private int  m_distMin;
  private File m_fiIn;
  private File m_fiOut;

  public MainApp() {
    //
  }

  public MainApp(File fi, int distMin) {
    m_fiIn = fi;
    m_distMin = distMin;
  }

  public static void main(String[] args) {

    if (args.length < 1 || args[0].length() < 4) {
      System.out.println("Uso: GPX_file [dist-min-in-metri: def 2000]");
      System.exit(1957);
    }
    File fi = new File(args[0]);
    if ( !fi.exists()) {
      System.out.println("Non esiste il file " + args[0]);
      System.exit(1957);
    }
    int distMin = 2000;
    if (args.length > 1)
      distMin = Integer.parseInt(args[1]);
    if (distMin < 500) {
      System.out.println("Distanza minima troppo corta(min 500) :" + args[1]);
      System.exit(1957);
    }
    MainApp app = new MainApp(fi, distMin);
    app.doTheJob();
  }

  private void doTheJob() {
    creaFileOut();

    m_refPunto = null;
    String riga = null;
    int qtaRighe = 1;
    Lavoro curr = Lavoro.scriviRiga;
    m_ptClaudio = 1;
    System.out.printf("Genero %s\n", m_fiOut.getAbsolutePath());
    try (PrintWriter prt = new PrintWriter(m_fiOut)) {
      try (BufferedReader bur = new BufferedReader(new FileReader(m_fiIn))) {
        while ( (riga = bur.readLine()) != null) {
          boolean bMtch = false;
          System.out.printf("%d\r", qtaRighe++);
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
                if (distanza < m_distMin) {
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
      System.out.println("Scritto il file:" + m_fiOut.getAbsolutePath());
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

  private void creaFileOut() {
    String szFiOu = m_fiIn.getAbsolutePath();
    int n = szFiOu.lastIndexOf(".");
    if (n < 1) {
      System.out.println("Non trovo il suffisso GPX" + szFiOu);
      System.exit(1957);
    }
    String szExt = szFiOu.substring(n + 1);
    if (szExt == null || !szExt.toLowerCase().equals("gpx")) {
      System.out.println("Non è un file GPX: " + szFiOu);
      System.exit(1957);
    }
    szFiOu = szFiOu.substring(0, n) + "_CONWP.gpx";
    m_fiOut = new File(szFiOu);
  }

}
