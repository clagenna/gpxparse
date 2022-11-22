package sm.clagenna.gpxparse.javafx;

import java.awt.Desktop;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.InputMethodEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import sm.clagenna.gpxparse.TaskIGpxFactory;
import sm.clagenna.gpxparse.ex.GpxException;
import sm.clagenna.gpxparse.util.AppProperties;
import sm.clagenna.gpxparse.util.AppUtils;
import sm.clagenna.gpxparse.util.ETipoWP;

public class GpxParseFxmlController implements Initializable {
  /**
   * Nel fxml ci deve essere la specifica:<br/>
   * <code>fx:controller="sm.clagenna...GpxParseFxmlController"</code>
   */
  public static final String         CSZ_FXMLNAME      = "GpxParse.fxml";
  private static int                 N_METRIMIN        = 100;
  private static int                 N_METRIMAX        = 10_000;
  private static String              IMAGE_EDITING_ICO = "basecamp.ico";
  private static final DecimalFormat s_fmt             = new DecimalFormat("#,##0");

  @FXML
  private TextField                  txGpxIn;
  @FXML
  private TextField                  txKmMin;
  @FXML
  private TextField                  txGpxOut;
  @FXML
  private JFXButton                  btCercaGpx;
  @FXML
  private CheckBox                   ckLanciaBaseCamp;
  @FXML
  private ChoiceBox<ETipoWP>         cbTipoWp;
  @FXML
  private JFXButton                  btSalva;
  @FXML
  private Label                      lbProgrBar;
  private double                     qtaRighe;
  // private double             progress;
  @FXML
  private ProgressBar                progBar;

  @Getter
  @Setter
  private File                       lastDir;
  private File                       m_fiIn;
  private File                       m_fiOut;
  private int                        m_nMinKm;
  private boolean                    m_bLaunchBC;
  private ETipoWP                    m_tipowp;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    AppProperties prop = AppProperties.getInst();
    m_tipowp = ETipoWP.ShapingPoint;
    cbTipoWp.getItems().addAll(ETipoWP.values());
    cbTipoWp.getSelectionModel().select(m_tipowp);
    cbTipoWp.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        ETipoWP wp = ETipoWP.values()[newValue.intValue()];
        m_tipowp = wp;
        try {
          m_fiOut = AppUtils.creaFileOut(m_fiIn);
          txGpxOut.setText(m_fiOut.getAbsolutePath());
        } catch (GpxException e) {
          messageDialog(AlertType.ERROR, e.getMessage());
        }
      }
    });

    String sz = prop.getLastDir();
    if (sz != null)
      setLastDir(new File(sz));

    //    if (sz != null) {
    //      settaFileIn(new File(sz));
    //      try {
    //        m_fiOut = AppUtils.creaFileOut(m_fiIn);
    //        txGpxOut.setText(m_fiOut.getAbsolutePath());
    //      } catch (GpxException e) {
    //        messageDialog(AlertType.ERROR, e.getMessage());
    //      }
    //    }
    m_bLaunchBC = false;
    Integer k = prop.getKmMin();
    if (k != null && k > 0)
      settaKmMin(k);
    else
      settaKmMin(1500);
    // txKmMin.setText(String.valueOf(m_nMinKm));

    chekDati();
    txKmMin.textProperty().addListener(new ChangeListener<String>() {

      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        testKmMinIsNumerico(observable, oldValue, newValue);
      }
    });
    txKmMin.focusedProperty().addListener((obs, oldval, newval) -> { if ( !newval ) txKmMinLostFocus();});

    Stage mainstage = GpxParseMainApp.getInst().getPrimaryStage();
    InputStream stre = getClass().getResourceAsStream(IMAGE_EDITING_ICO);
    if (stre == null)
      stre = getClass().getClassLoader().getResourceAsStream(IMAGE_EDITING_ICO);
    if (stre != null) {
      Image ico = new Image(stre);
      mainstage.getIcons().add(ico);
    }
    mainstage.setTitle("Inseritore di Way Point in files GPX");

    // progress = 0f;
  }

  @FXML
  void btCercaClick(ActionEvent event) {
    Stage stage = GpxParseMainApp.getInst().getPrimaryStage();
    FileChooser filChoose = new FileChooser();
    // imposto la dir precedente (se c'è)
    AppProperties props = AppProperties.getInst();
    String sz = props.getLastDir();
    if (sz != null) {
      File fi = new File(sz);
      if (fi.exists())
        filChoose.setInitialDirectory(fi);
    }
    filChoose.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("GPX files", "*.gpx"));
    System.out.println("GpxParseFxmlController.btCercaClick() init dir=" + filChoose.getInitialDirectory());
    File fileScelto = filChoose.showOpenDialog(stage);
    if (fileScelto != null) {
      String lastDir = fileScelto.getParentFile().getAbsolutePath();
      setLastDir(new File(lastDir));
      props.setLastDir(lastDir);
      fileScelto = settaFileIn(fileScelto);
      // creaFileOut(fileScelto);
      try {
        m_fiOut = AppUtils.creaFileOut(fileScelto);
        txGpxOut.setText(m_fiOut.getAbsolutePath());
        chekDati();
      } catch (GpxException e) {
        messageDialog(AlertType.ERROR, e.getMessage());
      }

    } else {
      System.out.println("Non hai scelto nulla !!");
      messageDialog(AlertType.WARNING, "Non hai scelto nessun file!");
    }
  }

  @FXML
  void onEnterFileIn(ActionEvent event) {
    String szGpx = txGpxIn.getText();
    if (szGpx == null || szGpx.length() < 3) {
      messageDialog(AlertType.ERROR, String.format("Il file \"%s\" non è valido", szGpx));
      return;
    }
    File fi = null;
    try {
      fi = new File(szGpx);
      if ( !fi.exists()) {
        messageDialog(AlertType.ERROR, String.format("Non trovo il file \"%s\" oppure non è valido", szGpx));
        return;
      }
    } catch (Exception e) {
      messageDialog(AlertType.ERROR, String.format("Non trovo il file \"%s\" oppure non è valido", szGpx));
    }
    if (fi != null) {
      settaFileIn(fi);
      // creaFileOut(fi);
      try {
        m_fiOut = AppUtils.creaFileOut(fi);
        txGpxOut.setText(m_fiOut.getAbsolutePath());
      } catch (GpxException e) {
        messageDialog(AlertType.ERROR, e.getMessage());
      }
    }
  }

  protected void testKmMinIsNumerico(ObservableValue<? extends String> observable, String oldValue, String newValue) {
    // System.out.printf("testKmMinIsNumerico(old=%s, new=%s)\n", oldValue, newValue);
    if ( !newValue.matches("\\d*(\\.\\d*)*")) {
      txKmMin.setText(oldValue);
    } 
  }
  
  private void txKmMinLostFocus() {
    System.out.println("txKmMin\tLostFocus()");
    onEnterKmMin(null);
  }

  @FXML
  void onEnterKmMin(ActionEvent event) {
    String szKm = txKmMin.getText();
    System.out.printf("onEnterKmMin(kmMin=%s)\n", szKm);
    if (szKm == null || szKm.length() < 2)
      return;

    try {
      szKm = szKm.replace(".", "");
      Integer ii = Integer.parseInt(szKm);
      settaKmMin(ii);
      chekDati();
    } catch (Exception e) {
      messageDialog(AlertType.ERROR, e.getMessage());
    }
  }

  @FXML
  void metriMinChanged(InputMethodEvent event) {
    System.out.println("GpxParseFxmlController.metriMinChanged()event=" + event.toString());
  }

  private File settaFileIn(File p_fi) {
    return settaFileIn(p_fi, true);
  }

  private File settaFileIn(File p_fi, boolean p_setTx) {
    String szFiin = p_fi.getAbsolutePath();
    int n = szFiin.lastIndexOf(".");
    if (n < 1) {
      String szMsg = "Non trovo il suffisso GPX" + szFiin;
      messageDialog(AlertType.WARNING, szMsg);
      return null;
    }
    String szExt = szFiin.substring(n + 1);
    if (szExt == null || !szExt.toLowerCase().equals("gpx")) {
      String szMsg = "Non è un file GPX: " + szFiin;
      messageDialog(AlertType.WARNING, szMsg);
      return null;
    }
    AppProperties props = AppProperties.getInst();
    props.setLastDir(p_fi.getParent());
    if (p_setTx)
      txGpxIn.setText(p_fi.getAbsolutePath());
    m_fiIn = p_fi;
    chekDati();
    return p_fi;
  }

  private void settaKmMin(int p_v) {
    if (p_v > N_METRIMIN && p_v <= N_METRIMAX) {
      m_nMinKm = p_v;
      String sz = s_fmt.format(p_v);
      txKmMin.setText(sz);
    } else {
      String sz = String.format("I metri devono stare entro i %d min e %d max ", N_METRIMIN, N_METRIMAX);
      messageDialog(AlertType.WARNING, sz);
    }
  }

  private void messageDialog(AlertType typ, String p_msg) {
    Alert alert = new Alert(typ);
    switch (typ) {
      case INFORMATION:
        alert.setTitle("Informa");
        alert.setHeaderText("Ok !");
        break;
      case WARNING:
        alert.setTitle("Attenzione");
        alert.setHeaderText("Fai Attenzione !");
        break;
      case ERROR:
        alert.setTitle("Errore !");
        alert.setHeaderText("Ahi ! Ahi !");
        break;
      default:
        break;
    }
    alert.setContentText(p_msg);
    alert.showAndWait();
  }
  //
  //  private void creaFileOut(File p_fiIn) {
  //    String szFiOu = p_fiIn.getAbsolutePath();
  //    int n = szFiOu.lastIndexOf(".");
  //    if (n < 1) {
  //      System.out.println("Non trovo il suffisso GPX" + szFiOu);
  //      System.exit(1957);
  //    }
  //    String szExt = szFiOu.substring(n + 1);
  //    if (szExt == null || !szExt.toLowerCase().equals("gpx")) {
  //      String szMsg = "Non è un file GPX: " + szFiOu;
  //      messageDialog(AlertType.ERROR, szMsg);
  //      return;
  //    }
  //    szFiOu = szFiOu.substring(0, n);
  //    String sufx = "";
  //    switch (m_tipowp) {
  //      case ShapingPoint:
  //        sufx = "_SHAPWP.gpx";
  //        break;
  //      case ViaPoint:
  //        sufx = "_VIAWP.gpx";
  //        break;
  //    }
  //    m_fiOut = new File(szFiOu + sufx);
  //    int k = 1;
  //    while (m_fiOut.exists()) {
  //      m_fiOut = new File(szFiOu + "_" + k++ + sufx);
  //    }
  //    txGpxOut.setText(m_fiOut.getAbsolutePath());
  //  }

  @FXML
  public void onEnter(ActionEvent ae) {
    String szPath = txGpxIn.getText();
    settaFileIn(new File(szPath), false);
  }

  @FXML
  void ckLanciaBaseCampClick(ActionEvent event) {
    m_bLaunchBC = ckLanciaBaseCamp.isScaleShape();
  }

  @FXML
  void btSalvaClick(ActionEvent event) {
    qtaRighe = contaRighe(m_fiIn);
    settaProgBar(0);

    //    InsertGpxRpt igpx = new InsertGpxRpt(this);
    //    igpx.doTheJob(m_nMinKm, m_fiIn, m_fiOut);
    //    if (m_bLaunchBC)
    //      lanciaBaseCamp();

    TaskIGpxFactory factory = new TaskIGpxFactory();
    factory.setTipowp(m_tipowp);
    factory.setFxcntrl(this);
    factory.setMetriMin(m_nMinKm);
    factory.setFileIn(m_fiIn);
    factory.setFileOut(m_fiOut);
    try {
      Task<Void> runjob = factory.get();
      runjob.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

        @Override
        public void handle(WorkerStateEvent event) {
          terminatoJob();
        }
      });
      runjob.setOnFailed(new EventHandler<WorkerStateEvent>() {

        @Override
        public void handle(WorkerStateEvent event) {
          abortitoJob();
        }
      });
      // new Thread(runjob).start();
      Platform.runLater(runjob);
    } catch (InterruptedException e) {
      messageDialog(AlertType.ERROR, e.getMessage());
    }

  }

  protected void terminatoJob() {
    // System.out.println("GpxParseFxmlController.terminatoJob()");
    settaProgBar((int) qtaRighe);
    if (m_bLaunchBC)
      lanciaBaseCamp();
    messageDialog(AlertType.INFORMATION, "Creato il file:" + m_fiOut.getAbsolutePath() //
        + "\nricordati di **ri-calcolare** in BaseCamp il nuovo percorso !");
  }

  protected void abortitoJob() {
    System.out.println("GpxParseFxmlController.abortitoJob()");
    settaProgBar(0);
  }

  private long contaRighe(File p_fi) {
    @SuppressWarnings("unused")
    String riga = null;
    long result = 0;
    try (FileReader input = new FileReader(p_fi); LineNumberReader count = new LineNumberReader(input);) {
      while ( (riga = count.readLine()) != null) {
        result = count.getLineNumber() + 1; // +1 because line index starts at 0
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  private void lanciaBaseCamp() {
    System.out.println("MainFrame.lanciaBaseCamp con " + m_fiOut.getAbsolutePath());
    if (m_fiOut == null || !m_fiOut.exists()) {
      // JOptionPane.showMessageDialog(this, "Non esiste il file GPX out, non lancio basecamp!");
      Alert alerta = new Alert(AlertType.WARNING);
      alerta.setTitle(m_fiOut.getAbsolutePath());
      alerta.setContentText("Non esiste il file GPX out, non lancio basecamp!");
      alerta.show();
      return;
    }
    try {
      Desktop dskt = Desktop.getDesktop();
      dskt.open(m_fiOut);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void chekDati() {
    boolean bOk = true;
    bOk &= (m_fiIn != null && m_fiIn.exists());
    if ( !bOk)
      System.out.println("file in non esiste");
    bOk &= (m_fiOut != null);
    if ( !bOk)
      System.out.println("non ho file out");
    bOk &= (m_nMinKm >= N_METRIMIN && m_nMinKm <= N_METRIMAX);
    if ( !bOk)
      System.out.println("Min Km non e valido");
    btSalva.setDisable( !bOk);
  }

  public void settaProgBar(int qta) {
    // System.out.printf("GpxParseFxmlController.settaProgBar(%d)\n", qta);
    if (qta == 0) {
      lbProgrBar.setText("0 %");
      progBar.setStyle("-fx-accent: blue;");
      progBar.setProgress(0);
      return;
    }
    double dbl = qta / (qtaRighe != 0 ? qtaRighe : 100f);
    String sz = String.format("%.0f %%", dbl * 100F);
    progBar.setProgress(dbl);
    lbProgrBar.setText(sz);
    if (qta >= qtaRighe) {
      progBar.setStyle("-fx-accent: forestgreen;");
    }
  }

}
