package sm.clagenna.gpxparse;

import java.io.File;

import javafx.concurrent.Task;

import lombok.Getter;
import lombok.Setter;
import sm.clagenna.gpxparse.javafx.GpxParseFxmlController;
import sm.clagenna.gpxparse.swing.MainFrame;

public class TaskIGpxFactory {

  /** distanza in metri per inserire l'inserto */
  @Getter @Setter private int                    metriMin;
  @Getter @Setter private File                   fileIn;
  @Getter @Setter private File                   fileOut;
  @Getter @Setter private MainFrame              frame;
  @Getter @Setter private GpxParseFxmlController fxcntrl;

  public TaskIGpxFactory() {
    // 
  }

  public TaskIGpxFactory(GpxParseFxmlController ctrl, int metri, File fin, File fout) {
    System.out.println("TaskIGpxFactory.TaskIGpxFactory()");
    setFxcntrl(ctrl);
    setMetriMin(metri);
    setFileIn(fin);
    setFileOut(fout);
  }

  public Task<Void> get() throws InterruptedException {
    System.out.println("TaskIGpxFactory.get()");
    return new Task<Void>() {

      @Override
      protected Void call() throws InterruptedException {
        System.out.println("TaskIGpxFactory.get().new Task() {...}.call()");
        InsertGpxRpt igpt = new InsertGpxRpt(fxcntrl);
        igpt.doTheJob(metriMin, fileIn, fileOut);
        return null;
      }
    };
  }

}
