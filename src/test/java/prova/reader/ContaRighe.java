package prova.reader;

import java.io.FileReader;
import java.io.LineNumberReader;

import org.junit.Test;

public class ContaRighe {

  @Test
  public void doTheJob() {
    String szFile = "dati/2015-06-27 Sardegna.gpx";
    @SuppressWarnings("unused")
    String riga = null;
    long result = 0;
    try (FileReader input = new FileReader(szFile); LineNumberReader count = new LineNumberReader(input);) {
      while ( (riga = count.readLine()) != null) {
      }
      result = count.getLineNumber() + 1; // +1 because line index starts at 0
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("ContaRighe = " + result);
  }

}
