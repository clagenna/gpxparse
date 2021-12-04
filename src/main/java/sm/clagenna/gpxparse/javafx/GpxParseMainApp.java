package sm.clagenna.gpxparse.javafx;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import lombok.Getter;
import sm.clagenna.gpxparse.util.AppProperties;

public class GpxParseMainApp extends Application {

  private static final String        CSZ_MAIN_APP_CSS = "/sm/clagenna/gpxparse/javafx/styleMainApp2.css";
  @Getter private static GpxParseMainApp inst;
  @Getter private Stage              primaryStage;

  @Override
  public void start(Stage pStage) throws Exception {
    inst = this;
    primaryStage = pStage;
    
   AppProperties prop = new AppProperties();
   prop.openProperties();

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
    System.out.println("MainAppFxml.stop()");
    AppProperties prop = AppProperties.getInst();

    //    prop.setPropVal(AppProperties.CSZ_PROP_DIMFRAME_X, String.format("%.0f", primaryStage.getWidth()));
    //    prop.setPropVal(AppProperties.CSZ_PROP_DIMFRAME_Y, String.format("%.0f", primaryStage.getHeight()));
    //    prop.setPropVal(AppProperties.CSZ_PROP_POSFRAME_X, String.format("%.0f", primaryStage.getX()));
    //    prop.setPropVal(AppProperties.CSZ_PROP_POSFRAME_Y, String.format("%.0f", primaryStage.getY()));

    prop.saveProperties();

    super.stop();
  }

  public static void main(String[] args) {
    Application.launch(args);
  }
}
