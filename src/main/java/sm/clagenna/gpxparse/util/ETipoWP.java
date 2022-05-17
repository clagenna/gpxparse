package sm.clagenna.gpxparse.util;

public enum ETipoWP {

  ShapingPoint("Shaping Point"), //
  ViaPoint("Via Point");

  private String nome;

  private ETipoWP(String ds) {
    nome = ds;
  }

  public String getNome() {
    return nome;
  }

  public static ETipoWP parse(String sz) {
    if (sz == null)
      return null;
    ETipoWP ee = null;
    try {
      ee = ETipoWP.valueOf(sz);
    } catch (Exception e) {
      //
    }
    if (ee == null) {
      switch (sz) {
        case "via":
          ee = ViaPoint;
          break;
        case "shape":
          ee = ShapingPoint;
          break;
      }
    }
    return ee;
  }
}
