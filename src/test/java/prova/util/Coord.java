package prova.util;

public record Coord(int a, int x, int y) {
  @Override
  public boolean equals(Object other) {
    boolean bRet = false;
    if ( ! (other instanceof Coord))
      return bRet;
    Coord oth = (Coord) other;
    bRet = oth.x == x && oth.y == y;
    return bRet;
  }

  public Coord add(Coord co) {
    return new Coord(co.a + a(), x + co.x(), y + co.y());
  }
}
