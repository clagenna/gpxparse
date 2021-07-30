package sm.clagenna.gpxparse.swing;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.NumberFormatter;

import sm.clagenna.gpxparse.InsertGpxRpt;
import sm.clagenna.gpxparse.util.AppProperties;

public class MainFrame extends JFrame {

  /** long serialVersionUID */
  private static final long   serialVersionUID = 7395956565038302212L;
  private JPanel              contentPane;
  private JTextField          txGpxIn;
  private JTextField          txGpxOut;
  private JFormattedTextField txKmMin;
  private JCheckBox           chckbxNewCheckBox;
  private int                 m_nMinKm;
  private boolean             m_bLaunchBC;
  private File                m_fiIn;
  private File                m_fiOut;
  private JButton             btSalvaGPX;
  private boolean             m_bInUpdate;

  public static void main(String[] args) {

    try {
      // Set cross-platform Java L&F (also called "Metal")
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception l_e) {
      System.err.println("Set Look and Feel");
      l_e.printStackTrace();
      System.exit(1957);
    }

    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {
          MainFrame frame = new MainFrame();
          frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the frame.
   */
  public MainFrame() {
    initFrame();
    initData();
  }

  public void initFrame() {
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent p_e) {
        locWindowClosing(p_e);
      }
    });

    //    addComponentListener(new ComponentAdapter() {
    //      @Override
    //      public void componentResized(ComponentEvent e) {
    //        Component cmp = e.getComponent();
    //        int dimx = cmp.getWidth();
    //        int dimy = cmp.getHeight();
    //        System.out.printf("MainFrame.rezize(%d,%d)\n", dimx, dimy);
    //      }
    //    });

    setBounds(100, 100, 654, 138);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new BorderLayout(0, 0));
    setContentPane(contentPane);

    JPanel panel = new JPanel();
    contentPane.add(panel, BorderLayout.CENTER);
    GridBagLayout gbl_panel = new GridBagLayout();
    gbl_panel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0 };
    gbl_panel.rowHeights = new int[] { 0, 0, 0, 0 };
    gbl_panel.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
    gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
    panel.setLayout(gbl_panel);

    JLabel lbCercaFile = new JLabel("GPX in");
    lbCercaFile.setToolTipText("Il file in ingresso da convertire");
    GridBagConstraints gbc_lbCercaFile = new GridBagConstraints();
    gbc_lbCercaFile.insets = new Insets(0, 0, 5, 5);
    gbc_lbCercaFile.anchor = GridBagConstraints.EAST;
    gbc_lbCercaFile.gridx = 0;
    gbc_lbCercaFile.gridy = 0;
    panel.add(lbCercaFile, gbc_lbCercaFile);

    txGpxIn = new JTextField();
    lbCercaFile.setLabelFor(txGpxIn);
    GridBagConstraints gbc_textField = new GridBagConstraints();
    gbc_textField.gridwidth = 3;
    gbc_textField.insets = new Insets(0, 0, 5, 5);
    gbc_textField.fill = GridBagConstraints.HORIZONTAL;
    gbc_textField.gridx = 1;
    gbc_textField.gridy = 0;
    panel.add(txGpxIn, gbc_textField);
    txGpxIn.setColumns(10);
    txGpxIn.getDocument().addDocumentListener(new DocumentListener() {

      @Override
      public void removeUpdate(DocumentEvent e) {
        locTxFinChanged();
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        locTxFinChanged();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        locTxFinChanged();
      }
    });

    JButton btnNewButton = new JButton("...");
    btnNewButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
          apriFileChooser();
        } finally {
          setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
      }
    });
    btnNewButton.setToolTipText("Apri la file dialog box");
    GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
    gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
    gbc_btnNewButton.gridx = 4;
    gbc_btnNewButton.gridy = 0;
    panel.add(btnNewButton, gbc_btnNewButton);

    JLabel lbKmMin = new JLabel("Km min");
    GridBagConstraints gbc_lbKmMin = new GridBagConstraints();
    gbc_lbKmMin.anchor = GridBagConstraints.EAST;
    gbc_lbKmMin.insets = new Insets(0, 0, 5, 5);
    gbc_lbKmMin.gridx = 0;
    gbc_lbKmMin.gridy = 1;
    panel.add(lbKmMin, gbc_lbKmMin);

    NumberFormat fmtNo = NumberFormat.getNumberInstance();
    NumberFormatter fmterNo = new NumberFormatter(fmtNo);
    fmterNo.setValueClass(Integer.class);
    fmterNo.setMinimum(500);
    fmterNo.setMaximum(5000);
    fmterNo.setAllowsInvalid(true);
    // commit value on each valid integer
    fmterNo.setCommitsOnValidEdit(true);

    txKmMin = new JFormattedTextField(fmterNo);
    m_nMinKm = 500;
    txKmMin.setValue(m_nMinKm);
    // txKmMin.setHorizontalAlignment(SwingConstants.RIGHT);
    lbKmMin.setLabelFor(txKmMin);
    GridBagConstraints gbc_txKmMin = new GridBagConstraints();
    gbc_txKmMin.insets = new Insets(0, 0, 5, 5);
    gbc_txKmMin.fill = GridBagConstraints.HORIZONTAL;
    gbc_txKmMin.gridx = 1;
    gbc_txKmMin.gridy = 1;
    panel.add(txKmMin, gbc_txKmMin);
    txKmMin.setColumns(10);

    txKmMin.getDocument().addDocumentListener(new DocumentListener() {

      @Override
      public void removeUpdate(DocumentEvent e) {
        locTxKmMinChanged();
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        locTxKmMinChanged();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        locTxKmMinChanged();
      }
    });

    chckbxNewCheckBox = new JCheckBox("Lancia BaseCamp");
    chckbxNewCheckBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        m_bLaunchBC = e.getStateChange() == ItemEvent.SELECTED;
      }
    });
    GridBagConstraints gbc_chckbxNewCheckBox = new GridBagConstraints();
    gbc_chckbxNewCheckBox.gridwidth = 3;
    gbc_chckbxNewCheckBox.insets = new Insets(0, 0, 5, 0);
    gbc_chckbxNewCheckBox.gridx = 2;
    gbc_chckbxNewCheckBox.gridy = 1;
    panel.add(chckbxNewCheckBox, gbc_chckbxNewCheckBox);

    JLabel lblNewLabel = new JLabel("GPX out");
    GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
    gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
    gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
    gbc_lblNewLabel.gridx = 0;
    gbc_lblNewLabel.gridy = 2;
    panel.add(lblNewLabel, gbc_lblNewLabel);

    txGpxOut = new JTextField();
    GridBagConstraints gbc_textField_1 = new GridBagConstraints();
    gbc_textField_1.gridwidth = 3;
    gbc_textField_1.insets = new Insets(0, 0, 0, 5);
    gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
    gbc_textField_1.gridx = 1;
    gbc_textField_1.gridy = 2;
    panel.add(txGpxOut, gbc_textField_1);
    txGpxOut.setColumns(10);

    btSalvaGPX = new JButton("Salva");
    btSalvaGPX.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        locSalvaClick();
      }
    });
    GridBagConstraints gbc_btSalva = new GridBagConstraints();
    gbc_btSalva.gridx = 4;
    gbc_btSalva.gridy = 2;
    panel.add(btSalvaGPX, gbc_btSalva);
  }

  private File apriFileChooser() {
    File retFi = null;
    JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    jfc.setDialogTitle("Scegli il direttorio da scannerizzare");
    jfc.setMultiSelectionEnabled(false);
    jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    AppProperties props = AppProperties.getInst();
    String sz = props.getLastDir();
    if (sz != null)
      jfc.setCurrentDirectory(new File(sz));
    int returnValue = 0;
    returnValue = jfc.showOpenDialog(this);
    if (returnValue == JFileChooser.APPROVE_OPTION) {
      retFi = jfc.getSelectedFile();
      String lastDir = retFi.getParentFile().getAbsolutePath();
      props.setLastDir(lastDir);
      retFi = settaFileIn(retFi);
      if (retFi != null)
        creaFileOut(retFi);
    }
    checkDati();
    return retFi;
  }

  private File settaFileIn(File p_fi) {
    String szFiin = p_fi.getAbsolutePath();
    int n = szFiin.lastIndexOf(".");
    if (n < 1) {
      String szMsg = "Non trovo il suffisso GPX" + szFiin;
      JOptionPane.showMessageDialog(this, szMsg);
      return null;
    }
    String szExt = szFiin.substring(n + 1);
    if (szExt == null || !szExt.toLowerCase().equals("gpx")) {
      String szMsg = "Non è un file GPX: " + szFiin;
      JOptionPane.showMessageDialog(this, szMsg);
      return null;
    }

    AppProperties props = AppProperties.getInst();
    props.setLastDir(p_fi.getAbsolutePath());
    txGpxIn.setText(p_fi.getAbsolutePath());
    m_fiIn = p_fi;
    return p_fi;
  }

  private void initData() {
    m_bInUpdate = true;
    try {
      AppProperties prop = new AppProperties();
      prop.openProperties();
      String sz = prop.getLastDir();
      if (sz != null) {
        txGpxIn.setText(sz);
        m_fiIn = new File(sz);
        creaFileOut(new File(sz));
      }
      checkDati();
    } finally {
      m_bInUpdate = false;
    }
  }

  private void creaFileOut(File p_fiIn) {
    String szFiOu = p_fiIn.getAbsolutePath();
    int n = szFiOu.lastIndexOf(".");
    if (n < 1) {
      System.out.println("Non trovo il suffisso GPX" + szFiOu);
      System.exit(1957);
    }
    String szExt = szFiOu.substring(n + 1);
    if (szExt == null || !szExt.toLowerCase().equals("gpx")) {
      String szMsg = "Non è un file GPX: " + szFiOu;
      JOptionPane.showMessageDialog(this, szMsg);
    }
    szFiOu = szFiOu.substring(0, n) + "_CONWP.gpx";
    m_fiOut = new File(szFiOu);
    txGpxOut.setText(szFiOu);
  }

  private void locTxFinChanged() {
    if (m_bInUpdate)
      return;
    String szFiIn = txGpxIn.getText();
    if (szFiIn == null || szFiIn.length() < 3) {
      m_fiIn = null;
      checkDati();
      return;
    }
    m_fiIn = new File(szFiIn);
    if ( !m_fiIn.exists()) {
      m_fiIn = null;
      checkDati();
      return;
    }
    creaFileOut(m_fiIn);
    checkDati();
  }

  protected void locTxKmMinChanged() {
    Object obj = txKmMin.getValue();
    if (obj instanceof Integer)
      m_nMinKm = ((Integer) obj).intValue();
    checkDati();
  }

  protected void locSalvaClick() {
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    try {
      InsertGpxRpt igpx = new InsertGpxRpt();
      igpx.doTheJob(m_nMinKm, m_fiIn, m_fiOut);
      if (m_bLaunchBC)
        lanciaBaseCamp();
    } finally {
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
  }

  private void lanciaBaseCamp() {
    System.out.println("MainFrame.lanciaBaseCamp con " + m_fiOut.getAbsolutePath());
    if (m_fiOut == null || !m_fiOut.exists()) {
      JOptionPane.showMessageDialog(this, "Non esiste il file GPX out, non lancio basecamp!");
      return;
    }
    try {
      Desktop dskt = Desktop.getDesktop();
      dskt.open(m_fiOut);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void checkDati() {
    boolean bOk = true;
    bOk &= (m_fiIn != null && m_fiIn.exists());
    if ( !bOk)
      System.out.println("file in non esiste");
    bOk &= (m_fiOut != null);
    if ( !bOk)
      System.out.println("non ho file out");
    bOk &= (m_nMinKm >= 500 && m_nMinKm <= 5000);
    if ( !bOk)
      System.out.println("Min Km non e valido");
    btSalvaGPX.setEnabled(bOk);
  }

  private void locWindowClosing(WindowEvent p_e) {
    AppProperties prop = AppProperties.getInst();
    prop.saveProperties();
    dispose();
  }

}
