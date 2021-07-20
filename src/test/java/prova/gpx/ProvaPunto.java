package prova.gpx;

import org.junit.Test;

import sm.clagenna.gpxparse.util.Punto;

public class ProvaPunto {

  private static String s_arrTest[] = { //
      " 41°24'12.2\"N 2°10'26.5\"E", //
      " N41°24'12.2\" E2°10'26.5\"", //

      " 2°10'26.5\"E 41°24'12.2\"N ", //
      " E2°10'26.5\" N41°24'12.2\" ", //

      "2.17403E, 41.40338N", //
      "E2.17403, N41.40338", //

      "N41.40338, E2.17403", //
      "41.40338N, 2.17403E" //

  };

  @Test
  public void doIt() {
    Punto rif = new Punto(41.40338F, 2.17403F);
    Punto rif2 = new Punto(41.40338134765625F, 2.174030065536499F);
    double dist = rif.distance(rif2);
    System.out.printf("Fra rif e rif2  dist=%.6f\n", dist);

    for (String sz : s_arrTest) {
      Punto pu = new Punto();
      pu.parse(sz);
      dist = rif.distance(pu);
      if (dist > 1) {
        System.out.printf("Errore Punto %s,  dist=%.6f\n", sz, dist);
        pu.parse(sz);
      }
      System.out.printf(" per %s \t = %s\n", sz, pu.toString());
    }

  }

}
