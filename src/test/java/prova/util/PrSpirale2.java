package prova.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class PrSpirale2 {
  final double        dPi = 2. * Math.PI;
  final int           SZX = 20;
  final int           SZY = 20;

  List<Coord>         m_li;

  List<StringBuilder> rappr;

  @Before
  public void init() {
    m_li = new ArrayList<>();

    rappr = new ArrayList<>();
    String sz = " ".repeat(4);
    sz = sz.repeat(SZX);
    for (int i = 0; i < SZY; i++)
      rappr.add(new StringBuilder(sz));
  }

  @Test
  public void provalo2() {
    Coord coo = new Coord(1, 0, 0);
    Coord incr = new Coord(1, 1, 0);
    m_li = new ArrayList<>();
    m_li.add(coo);
    rappresenta(coo);
    coo = coo.add(incr);
    for (int i = 1; i < 4; i++) {
      int qtaInRect = i > 0 ? 8 * i : 0;
      int quarto = qtaInRect / 4;
      int quadrante = 0;
      int curr = (int) Math.round((double) quarto / 2f);
      coo = new Coord(coo.a(), i, -1);
      for (int k = 0; k < qtaInRect; k++) {
        System.out.println(coo);
        rappresenta(coo);
        if (m_li.contains(coo)) {
          System.out.println(coo);
        } else
          m_li.add(coo);
        // System.out.printf("%d)\t%d\t%d\n", coo.a, coo.x, coo.y);
        if (curr++ >= quarto) {
          quadrante++;
          curr = 1;
        }
        incr = quadrante(quadrante);
        coo = coo.add(incr);
      }
    }
  }

  private void rappresenta(Coord p_prox) {
    int posx = SZX / 2 + p_prox.x() * 4;
    int posy = SZY / 2 - p_prox.y();
    String sz = String.format("%4d", p_prox.a());
    StringBuilder rig = rappr.get(posy);
    rig = rig.replace(posx, posx + 4, sz);
  }

  private Coord quadrante(int quadrante) {
    Coord quadr;
    switch (quadrante) {
      case 0:
        quadr = new Coord(1, 0, 1);
        break;
      case 1:
        quadr = new Coord(1, -1, 0);
        break;
      case 2:
        quadr = new Coord(1, 0, -1);
        break;
      case 3:
        quadr = new Coord(1, 1, 0);
        break;
      default:
        quadr = new Coord(1, 1, 0);
        break;
    }
    return quadr;
  }

  @Override
  public String toString() {
    StringBuilder sz = new StringBuilder("");
    for (StringBuilder bl : rappr)
      sz.append(bl.toString()).append("\n");
    return sz.toString();
  }
}
