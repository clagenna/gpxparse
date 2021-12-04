package sm.clagenna.gpxparse.prove;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainProgBarAppFxml extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    URL url = getClass().getResource(ProgBarFxmlController.CSZ_FXMLNAME);
    Parent radice = FXMLLoader.load(url);
    Scene scene = new Scene(radice, 300, 400);

    //    scene.getStylesheets().add(getClass().getResource("sm/clagenna/cssfiles/styleProgBar.css").toExternalForm());

    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) {
    Application.launch(args);
  }
}
