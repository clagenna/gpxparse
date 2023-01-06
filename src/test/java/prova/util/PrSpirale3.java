package prova.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class PrSpirale3 {
  final int           SZX = 20;
  final int           SZY = 20;

  double              dPi = 2. * Math.PI;
  List<Coord>         m_li;
  Coord               coo;
  Coord               incr;

  List<StringBuilder> rappr;

  @Before
  public void init() {
    m_li = new ArrayList<>();
    coo = new Coord(1, 0, 0);
    incr = new Coord(1, 1, 0);
    // metto spiare 0 al centro (0,0)
    m_li.add(coo);

    rappr = new ArrayList<>();
    String sz = " ".repeat(4);
    sz = sz.repeat(SZX);
    for (int i = 0; i < SZY; i++)
      rappr.add(new StringBuilder(sz));
  }

  @Test
  public void provalo3() {
    for (int i = 1; i < 100; i++) {
      Coord prox = calcolaCoord(i);
      rappresenta(prox);
      m_li.add(prox);
    }
  }

  private void rappresenta(Coord p_prox) {
    int posx = SZX / 2 + p_prox.x() * 4;
    int posy = SZY / 2 - p_prox.y();
    String sz = String.format("%4d", p_prox.a());
    StringBuilder rig = rappr.get(posy);
    rig = rig.replace(posx, posx + 4, sz);
  }

  private Coord calcolaCoord(int p_i) {
    Coord ret = new Coord(1, 0, 0);
    if (p_i < 1)
      return ret;
    int cerchio = 0;
    int qtapo = 0;
    while ( (qtapo = qtaPosOccup(cerchio + 1)) < p_i)
      cerchio++;
    int qtaInCerchio = 8 * cerchio;
    int poRimasti = qtapo - p_i;
    int quarti = qtaInCerchio / 4;
    ret = new Coord(qtapo, cerchio, 0);
    for (int k = 0; k < poRimasti; k++) {
      int qud = (int) Math.floor( (k + quarti / 2) / quarti);
      incr = quadrante(qud);
      ret = ret.add(incr);
    }
    return ret;
  }

  private int qtaPosOccup(int p_cerchio) {
    int ret = 1;
    for (int k = 1; k <= p_cerchio; k++)
      ret += 8 * k;
    return ret;
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

}
