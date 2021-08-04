package sm.clagenna.gpxparse.xml.gpx;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sm.clagenna.gpxparse.util.AppProperties;

public class GpxFactory {

  private static final Logger          s_log = LogManager.getLogger(GpxFactory.class);
  private static GpxFactory            s_inst;
  private static Map<String, Class<?>> s_mapClasses;

  public GpxFactory() {
    if (s_inst != null) {
      s_log.error("GpxFactory gia istanziato !");
      throw new UnsupportedOperationException("GpxFactory gia istanziato !");
    }
    s_inst = this;
    init();
  }

  private void init() {
    s_mapClasses = new HashMap<String, Class<?>>();
    Properties prop = AppProperties.getInst().getProperties();
    String szPackage = getClass().getPackageName();
    for (Object ob : prop.keySet()) {
      String key = ob.toString();
      if ( !key.startsWith("gpx/"))
        continue;
      String szClass = prop.getProperty(key);
      String szClsName = szPackage + "." + szClass;
      try {
        getClass();
        Class<?> cls = Class.forName(szClsName);
        s_mapClasses.put(key, cls);
      } catch (ClassNotFoundException e) {
        String szMsg = String.format("La classe %s non trovata !", szClass);
        s_log.error(szMsg);
        throw new UnsupportedOperationException(szMsg);
      }
    }
  }

  public static GpxFactory getInst() {
    return s_inst;
  }

  public IGpxGest get(String p_xPath) {
    IGpxGest ret = null;
    Class<?> cls = null;
    try {
      if (s_mapClasses.containsKey(p_xPath)) {
        cls = s_mapClasses.get(p_xPath);
        ret = (IGpxGest) cls.getDeclaredConstructor().newInstance();
      }
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
        | NoSuchMethodException | SecurityException e) {
      String szMsg = String.format("La classe %s non instanziabile !!", cls.getSimpleName());
      s_log.error(szMsg);
      throw new UnsupportedOperationException(szMsg, e);
    }
    return ret;
  }

}
