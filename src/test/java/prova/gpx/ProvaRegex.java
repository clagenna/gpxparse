package prova.gpx;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import sm.clagenna.gpxparse.util.Punto;

public class ProvaRegex {
  private static final String[] CSZ_ARR = { // 
      "    <rtept lat=\"43.962100865319371\" lon=\"12.467174045741558\">",   //
      "      <time>2015-06-19T15:38:26Z</time>",                                                                   //
      "      <name>B01 Strada Di San Michele</name>",                                                              //
      "      <cmt>Strada Di San Michele 200",                                                                      //
      "47899, Serravalle, SMR</cmt>",                                                                              //
      "      <desc>Strada Di San Michele 200",                                                                     //
      "47899, Serravalle, SMR</desc>",                                                                             //
      "      <sym>Flag, Blue</sym>",                                                                               //
      "      <extensions>",                                                                                        //
      " <gpxx:rpt lat=\"43.929948806762695\" lon=\"12.324857711791992\" />",                                       //
      " <gpxx:rpt lat=\"43.929777145385742\" lon=\"12.324600219726563\">",                                         //
      "  <gpxx:Subclass>030067465B0103AC16001F002C002B3E1A00</gpxx:Subclass>",                                     //
      " </gpxx:rpt>,",                                                                                             //
      "<gpxx:rpt lat=\"43.929777145385742\" lon=\"12.324600219726563\" />",                                        //
      " <gpxx:rpt lat=\"43.929347991943359\" lon=\"12.324128150939941\" >",                                        //
      " <gpxx:Subclass>030067465B01E8AB16001F002C003C3E0D00</gpxx:Subclass>",                                      //
      "</gpxx:rpt>",                                                                                               // 
      "  <gpxx:rpt lat=\"43.929133415222168\" lon=\"12.323870658874512\" />",                                      //
      "          <gpxx:rpt lat=\"43.928961753845215\" lon=\"12.32365608215332\">"                                  //
  };

  private Pattern               pat2    = Pattern.compile("[^<]*<gpxx:rpt lat=\"([0-9.]+)\" lon=\"([0-9.]+)\".*");

  @Test
  public void provalo() {
    Punto lstPu = null;
    for (String sz : CSZ_ARR) {
      Matcher mtch = pat2.matcher(sz);
      if (mtch.find()) {
        double lat = Double.parseDouble(mtch.group(1));
        double lon = Double.parseDouble(mtch.group(2));
        Punto pu = new Punto(lat, lon);
        System.out.printf("%s\t%s\n", pu.getDD(), sz);
        if (lstPu != null)
          System.out.printf("Distanza dal prec=%.2f m\n", lstPu.distance(pu));
        lstPu = pu;
      } else {
        System.out.println("non tratto:" + sz);
      }
    }

  }

}
