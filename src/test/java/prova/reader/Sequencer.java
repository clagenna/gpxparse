package prova.reader;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;

import org.junit.Test;

public class Sequencer {
  private static final int    MAXITER  = 5000;
  private static final String CSZ_CODS = "0123456789abcdefjhijklmnopqrstuvwxyz";
  private static Random       rnd      = new Random( (new Date()).getTime());

  @Test
  public void provalo() {
    for (int i = 0; i < 10; i++) {
      int val = rnd.nextInt(MAXITER);
      String sz = traduci(val);
      System.out.printf("val=%d, cod=%s\n", val, sz);
    }
  }

  private String traduci(int p_val) {
    int kc = CSZ_CODS.length();
    String sz = "";
    int resto;
    LocalDateTime dt = LocalDateTime.now();
    int val = 0;
    val = dt.getHour();
    val = val * 60 + dt.getMinute();
    val = val * 60 + dt.getSecond();
    val = val * MAXITER;
    val += p_val;

    while (val != 0) {
      resto = val % kc;
      val = val / kc;
      sz = CSZ_CODS.charAt(resto) + sz;
    }
    return sz;
  }

}
