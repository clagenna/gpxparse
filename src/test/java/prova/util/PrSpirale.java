package prova.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class PrSpirale {
  double      dPi = 2. * Math.PI;

  List<Coord> m_li;

  @Test
  public void provalo() {
    int vv = 1;
    m_li = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
      int qta = i > 0 ? 8 * i : 1;
      double rot = dPi / qta;
      double rotx = 0;
      for (int k = 0; k < qta; k++) {
        double x = i * Math.sin(rotx);
        double y = i * Math.cos(rotx);
        double segnoX = Math.signum(x);
        double segnoY = Math.signum(y);
        int ix = (int) Math.round(Math.abs(x) * segnoX);
        int iy = (int) Math.round(Math.abs(y) * segnoY);
        Coord coo = new Coord(vv, ix, iy);
        System.out.printf("%d)\t%d\t%d\t%.2f\t%.2f\n", vv, ix, iy, x, y);
        if (m_li.contains(coo)) {
          System.out.println(coo);
        } else
          m_li.add(coo);
        vv++;
        rotx += rot;
      }
    }
  }
}
