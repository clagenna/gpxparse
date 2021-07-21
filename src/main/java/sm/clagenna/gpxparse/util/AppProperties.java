package sm.clagenna.gpxparse.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Getter;
import lombok.Setter;

public class AppProperties {

  private static final Logger        s_log         = LogManager.getLogger(AppProperties.class);
  private static final String        CSZ_PROP_FILE = "GpxParse.properties";
  private static AppProperties       s_inst;

  @Getter @Setter private Properties properties;
  @Getter @Setter private File       propertyFile;

  public AppProperties() {
    if (AppProperties.s_inst != null)
      throw new UnsupportedOperationException("AppProperties gia' istanziato");
    AppProperties.s_inst = this;
  }

  public static AppProperties getInst() {
    return s_inst;
  }

  public void openProperties() {
    if (propertyFile == null)
      setPropertyFile(new File(AppProperties.CSZ_PROP_FILE));

    AppProperties.s_log.info("Apro il file properties {}", propertyFile.getAbsolutePath());
    properties = new Properties();
    if ( !propertyFile.exists()) {
      AppProperties.s_log.error("Il file di properties {} non esiste", AppProperties.CSZ_PROP_FILE);
      return;
    }
    try (InputStream is = new FileInputStream(propertyFile)) {
      properties = new Properties();
      properties.load(is);
      setPropertyFile(propertyFile);
    } catch (IOException e) {
      e.printStackTrace();
      AppProperties.s_log.error("Errore apertura property file: {}", propertyFile.getAbsolutePath(), e);
    }

  }

  public void saveProperties() {
    try (OutputStream output = new FileOutputStream(propertyFile)) {
      properties.store(output, null);
      AppProperties.s_log.info("Salvo property file {}", propertyFile.getAbsolutePath());
    } catch (IOException e) {
      e.printStackTrace();
      AppProperties.s_log.error("Errore scrittura property file: {}", propertyFile.getAbsolutePath(), e);
    }
  }

  public String getPropVal(String p_key) {
    String szRet = null;
    if (properties != null)
      szRet = properties.getProperty(p_key);
    return szRet;
  }

  public void setPropVal(String p_key, String p_val) {
    if (properties != null)
      if (p_val != null)
        properties.setProperty(p_key, p_val);
  }

  public void setPropVal(String p_key, int p_val) {
    setPropVal(p_key, String.valueOf(p_val));
  }

  public int getPropIntVal(String p_key) {
    Integer ii = Integer.valueOf(0);
    String sz = getPropVal(p_key);
    if (sz != null)
      ii = Integer.decode(sz);
    return ii.intValue();
  }

  public boolean getBooleanPropVal(String p_key) {
    boolean bRet = false;
    String sz = getPropVal(p_key);
    if (sz == null)
      return bRet;
    sz = sz.toLowerCase();
    switch (sz) {
      case "vero":
      case "true":
      case "yes":
      case "y":
      case "t":
      case "1":
        bRet = true;
        break;
    }
    return bRet;
  }

  public void setBooleanPropVal(String p_key, boolean bVal) {
    setPropVal(p_key, Boolean.valueOf(bVal).toString());
  }

}
