package sm.clagenna.gpxparse;

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
}
