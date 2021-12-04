package sm.clagenna.gpxparse.prove;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class ProgBarFxmlController implements Initializable {
  /**
   * Nel fxml ci deve essere la specifica:<br/>
   * <code>fx:controller="sm.clagenna...ProgBarFxmlController"</code>
   */
  public static final String CSZ_FXMLNAME = "ProgBar.fxml";

  @FXML private Button       btStart;
  @FXML private Button       btStop;
  @FXML private Label        lbProgBar;
  @FXML private ProgressBar  myProgBar;
  private double             progress;

  @FXML
  void btStartClick(ActionEvent event) {
    if (progress < 1)
      progress += 0.1;
    formatProgBar();
  }

  private void formatProgBar() {
    myProgBar.setProgress(progress);
    String sz = String.format("%.0f %%", progress * 100.);
    lbProgBar.setText(sz);
  }

  @FXML
  void btStopClick(ActionEvent event) {
    if (progress > 0)
      progress -= 0.1;
    formatProgBar();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    myProgBar.setStyle("-fx-accent: red;");
  }

}
