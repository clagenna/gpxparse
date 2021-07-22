package sm.clagenna.gpxparse.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlHandler extends DefaultHandler {

  private static final Logger s_log = LogManager.getLogger(XmlHandler.class);

  private LinkedList<String>  m_stack;
  private List<String>        m_liXpaths;
  private String              m_txChars;

  public XmlHandler() {
    init();
  }

  private void init() {
    //
  }

  @Override
  public void startDocument() throws SAXException {
    m_stack = new LinkedList<>();
    m_liXpaths = new ArrayList<>();
  }

  @Override
  public void endDocument() throws SAXException {
    if (m_liXpaths == null)
      return;
    System.out.println("--- elenco xPaths  -----");
    m_liXpaths//
        .stream() //
        .sorted() //
        .forEach(System.out::println);
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    m_txChars = null;
    m_stack.push(qName);
    String szXp = getXpath();
    if ( !m_liXpaths.contains(szXp))
      m_liXpaths.add(szXp);
    Map<String, String> attrs = parseAttributes(attributes);

  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    m_txChars = new String(ch, start, length);
    m_txChars = m_txChars.replaceAll("\\n", "");
    m_txChars = m_txChars.replaceAll("\\r", "");
    m_txChars = m_txChars.trim();
    if (m_txChars.length() == 0)
      m_txChars = null;
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    //
    System.out.printf("endElement(%s)=%s\n", qName, m_txChars);
    m_stack.pop();
  }

  private String getXpath() {
    Iterator<String> iter = m_stack.descendingIterator();
    String sz = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iter, 0), false) //
        .collect(Collectors.joining("/"));
    return sz;
  }

  private Map<String, String> parseAttributes(Attributes attributes) {
    int k = 0;
    if (attributes != null)
      k = attributes.getLength();
    Map<String, String> map = new HashMap<String, String>();
    if (k == 0)
      return map;
    for (int i = 0; i < k; i++) {
      String ob = attributes.getLocalName(i);
      String vv = attributes.getValue(i);
      map.put(ob, vv);
    }
    return map;
  }

}
