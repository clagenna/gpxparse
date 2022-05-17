package prova.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sm.clagenna.gpxparse.javafx.GpxParseMainApp;

public class ProvaRigaComando {
  @SuppressWarnings("unused")
  private static final Logger s_log = LogManager.getLogger(ProvaRigaComando.class);

  public ProvaRigaComando() {
    //
  }

  public static void main(String[] args) {
    GpxParseMainApp app = new GpxParseMainApp();
    app.initApp();
    app.doTheJob(args);
  }

}
