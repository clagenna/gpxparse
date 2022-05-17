package sm.clagenna.gpxparse.javafx;

import java.io.File;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Getter;
import sm.clagenna.gpxparse.InsertGpxRpt;
import sm.clagenna.gpxparse.ex.GpxException;
import sm.clagenna.gpxparse.util.AppProperties;
import sm.clagenna.gpxparse.util.AppUtils;
import sm.clagenna.gpxparse.util.ETipoWP;
import sm.clagenna.gpxparse.util.RigaComando;

public class GpxParseMainApp extends Application {
  private static final Logger            s_log            = LogManager.getLogger(GpxParseMainApp.class);

  private static final String            CSZ_MAIN_APP_CSS = "/sm/clagenna/gpxparse/javafx/styleMainApp2.css";
  @Getter private static GpxParseMainApp inst;
  @Getter private Stage                  primaryStage;

  private RigaComando                    cmd;

  @Override
  public void start(Stage pStage) throws Exception {
    primaryStage = pStage;
    initApp();

    URL url = getClass().getResource(GpxParseFxmlController.CSZ_FXMLNAME);
    if (url == null)
      url = getClass().getClassLoader().getResource(GpxParseFxmlController.CSZ_FXMLNAME);
    Parent radice = FXMLLoader.load(url);
    Scene scene = new Scene(radice, 725, 170);
    url = getClass().getResource(CSZ_MAIN_APP_CSS);
    if (url == null)
      url = getClass().getClassLoader().getResource(CSZ_MAIN_APP_CSS);
    scene.getStylesheets().add(url.toExternalForm());

    primaryStage.setScene(scene);
    primaryStage.show();
  }

  @Override
  public void stop() throws Exception {
    // System.out.println("MainAppFxml.stop()");
    AppProperties prop = AppProperties.getInst();
    prop.saveProperties();
    super.stop();
  }

  public static void main(String[] args) {
    if (args.length == 0) {
      Application.launch(args);
      return;
    }
    GpxParseMainApp app = new GpxParseMainApp();
    app.initApp();
    app.doTheJob(args);
    try {
      app.stop();
    } catch (Exception e) {
      s_log.error(e.getMessage());
    }
    System.exit(0);
  }

  public void initApp() {
    inst = this;
    AppProperties prop = new AppProperties();
    prop.openProperties();
    prop.setTipoWp(ETipoWP.ShapingPoint);
    prop.setKmMin(2000);
    prop.setLanciaBaseCamp(false);
  }

  public void doTheJob(String[] args) {
    cmd = new RigaComando();
    if (args.length == 0) {
      System.err.println("Non hai fornito argomenti");
      cmd.help();
      return;
    }

    if ( !cmd.parseOptions(args))
      return;
    AppProperties props = AppProperties.getInst();
    analizzaCmdLine();

    InsertGpxRpt igpt = new InsertGpxRpt(props);
    igpt.setTipowp(props.getTipoWp());
    igpt.setFxcntrl(null);
    igpt.setMetriMin(props.getKmMin());
    igpt.setFrame(null);
    
    
    igpt.setFileIn(props.getGpxIn());
    igpt.setFileOut(props.getGpxOut());
    
    igpt.doTheJob();

  }

  private void analizzaCmdLine() {

    AppProperties props = AppProperties.getInst();
    // GPX in
    File fiGpxIn = new File(cmd.getOption(RigaComando.CSZ_OPT_SRCGPX));
    props.setGpxIn(fiGpxIn);
    s_log.info("Analizzo il file in={}", fiGpxIn.getAbsolutePath());
    String sz = fiGpxIn.getAbsoluteFile().getParentFile().getAbsolutePath();
    props.setLastDir(sz);
    File fiGpxOut = null;
    try {
      fiGpxOut = AppUtils.creaFileOut(fiGpxIn);
      props.setGpxOut(fiGpxOut);
    } catch (GpxException e) {
      s_log.error(e.getMessage());
      return;
    }
    // GPX out
    sz = cmd.getOption(RigaComando.CSZ_OPT_DSTGPX);
    if (sz != null) {
      fiGpxOut = new File(sz);
      props.setGpxOut(fiGpxOut);
    }
    // minDist
    s_log.info("Genero il filo out={}", fiGpxOut.getAbsolutePath());

  }

}
