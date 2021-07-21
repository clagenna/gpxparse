package prova.gpx;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;
import org.xml.sax.SAXException;

import sm.clagenna.gpxparse.util.AppProperties;
import sm.clagenna.gpxparse.xml.XmlHandler;

public class ProvaGpx {

  @Test
  public void doIt() throws ParserConfigurationException, SAXException, IOException {
    new AppProperties().openProperties();
    String szFile = "dati/2015-06-27 Sardegna.gpx";
    SAXParserFactory factory = SAXParserFactory.newInstance();

    SAXParser saxParser = factory.newSAXParser();
    XmlHandler hand = new XmlHandler();
    saxParser.parse(szFile, hand);

  }

}
