package sm.clagenna.gpxparse.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Getter;
import sm.clagenna.gpxparse.ex.GpxException;

public class RigaComando {

  private static final Logger s_log            = LogManager.getLogger(RigaComando.class);
  public static final String  CSZ_OPT_SRCGPX   = "gpxIn";
  private static final String CSZ_SHT_SRCGPX   = "f";
  public static final String  CSZ_OPT_DSTGPX   = "gpxOut";
  private static final String CSZ_SHT_DSTGPX   = "o";
  public static final String  CSZ_OPT_TIPOWP   = "tipoWp";
  private static final String CSZ_SHT_TIPOWP   = "t";
  public static final String  CSZ_OPT_DISTMIN  = "distWp";
  private static final String CSZ_SHT_DISTMIN  = "d";
  public static final String  CSZ_OPT_BASECAMP = "baseC";
  private static final String CSZ_SHT_BASECAMP = "b";
  // l'istanza singleton
  private static RigaComando  s_inst;

  private Options             s_options;
  private CommandLine         s_cmdLine;
  @Getter private Path        pthSrc;
  @Getter private Path        pthDst;

  public RigaComando() {
    creaOptions();
    s_inst = this;
  }

  public static RigaComando getInst() {
    return s_inst;
  }

  public void creaOptions() {
    s_options = new Options();
    //    final boolean WITH_ARGS = true;
    //    final boolean NO_ARGS = false;

    Option op; // = new Option(CSZ_OPT_SRCGPX, WITH_ARGS, "il GPX sorgente per la scansione");
    // op.setRequired(true);
    op = Option.builder(CSZ_SHT_SRCGPX) //
        .longOpt(CSZ_OPT_SRCGPX) //
        .argName(CSZ_OPT_SRCGPX) //
        .hasArg() //
        .required() //
        .desc("il GPX sorgente per la scansione") //
        .build();
    s_options.addOption(op);

    op = Option.builder(CSZ_SHT_DSTGPX) //
        .longOpt(CSZ_OPT_DSTGPX) //
        .argName(CSZ_OPT_DSTGPX) //
        .hasArg() //
        .desc("il path GPX di destinazione") //
        .build();
    s_options.addOption(op);

    // op = new Option(CSZ_OPT_TIPOWP, WITH_ARGS, "Tipo di waypoint da inserire (via,shape)");
    op = Option.builder(CSZ_SHT_TIPOWP) //
        .longOpt(CSZ_OPT_TIPOWP) //
        .argName(CSZ_OPT_TIPOWP) //
        .hasArg() //
        .desc("Tipo di waypoint da inserire (via,shape)") //
        .build();
    s_options.addOption(op);

    // op = new Option(CSZ_OPT_DISTMIN, WITH_ARGS, "Distanza (in metri) minima tra i Way point (min 500m)");
    op = Option.builder(CSZ_SHT_DISTMIN) //
        .longOpt(CSZ_OPT_DISTMIN) //
        .argName(CSZ_OPT_DISTMIN) //
        .hasArg() //
        .desc("Distanza (in metri) minima tra i Way point (min 500m)") //
        .build();
    s_options.addOption(op);

    // op = new Option(CSZ_OPT_BASECAMP, NO_ARGS, "Se Lancio finale di BaseCamp");
    op = Option.builder(CSZ_SHT_BASECAMP) //
        .longOpt(CSZ_OPT_BASECAMP) //
        .argName(CSZ_OPT_BASECAMP) //
        .desc("Se Lancio finale di BaseCamp") //
        .build();
    s_options.addOption(op);

  }

  public boolean parseOptions(String[] args) {
    CommandLineParser prs = new DefaultParser();
    if (args.length == 0) {
      help();
      return false;
    }

    try {
      s_cmdLine = prs.parse(s_options, args);
      controllaOptions();
    } catch (GpxException | ParseException e) {
      s_log.error(e);
      help();
      return false;
    }
    s_log.debug("MainApp.main() src=" + s_cmdLine.getOptionValue(CSZ_OPT_SRCGPX));
    return true;
  }

  public void controllaOptions() throws GpxException {
    // opt  gpxIn
    if ( !s_cmdLine.hasOption(CSZ_OPT_SRCGPX))
      throw new GpxException("Non hai specificato il dir sorgente");
    String sz = s_cmdLine.getOptionValue(CSZ_OPT_SRCGPX);
    pthSrc = Paths.get(sz);
    if ( !Files.exists(pthSrc, LinkOption.NOFOLLOW_LINKS))
      throw new GpxException("Non esiste il file " + sz);
    // opt gpxOut
    try {
      if (s_cmdLine.hasOption(CSZ_OPT_DSTGPX)) {
        sz = s_cmdLine.getOptionValue(CSZ_OPT_DSTGPX);
        pthDst = Paths.get(sz);
        if (Files.isSameFile(pthSrc, pthDst)) {
          throw new GpxException("In e out sono stesso file " + sz);
        }
      }
    } catch (IOException e) {
      throw new GpxException("Errore determinazione dei direttori:" + e.getMessage(), e);
    }
    // distPw
    sz = s_cmdLine.getOptionValue(CSZ_OPT_DISTMIN);
    if (sz != null) {
      Integer ii = null;
      try {
        ii = Integer.parseInt(sz);
      } catch (NumberFormatException e) {
        throw new GpxException(sz + " - non e' un valore numerico");
      }
      if (ii < 1_000 || ii > 20_000)
        throw new GpxException(sz + " - non sta nel range da 1.000 a 20.000");
    }
    // opt tipoWp
    sz = s_cmdLine.getOptionValue(CSZ_OPT_TIPOWP);
    if (sz != null) {
      ETipoWP ee = ETipoWP.parse(sz);
      if (ee == null)
        throw new GpxException(sz + " - non e' un tipo di way point (via,shape)");
    }
  }

  public CommandLine getCommandLine() {
    return s_cmdLine;
  }

  public boolean isOption(String p_sz) {
    return getCommandLine().hasOption(p_sz);
  }

  public String getOption(String p_sz) {
    return getCommandLine().getOptionValue(p_sz);
  }

  public Integer getOptionInt(String p_sz) {
    String sz = getCommandLine().getOptionValue(p_sz);
    Integer ii = null;
    if (sz != null)
      ii = Integer.valueOf(sz);
    return ii;
  }

  public void help() {
    HelpFormatter hlp = new HelpFormatter();
    hlp.printHelp("GPXParse", s_options, true);
  }

}
