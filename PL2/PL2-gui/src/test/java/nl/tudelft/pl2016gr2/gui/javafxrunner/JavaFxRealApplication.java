package nl.tudelft.pl2016gr2.gui.javafxrunner;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.tudelft.pl2016gr2.gui.view.RootLayoutController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Coppied from: http://awhite.blogspot.nl/2013/04/javafx-junit-testing.html This is the application
 * which starts JavaFx. It is controlled through the startJavaFx() method.
 */
public class JavaFxRealApplication extends Application {

  /**
   * The lock that guarantees that only one JavaFX thread will be started.
   */
  private static final ReentrantLock LOCK = new ReentrantLock();
  
  public static Stage primaryStage;
  public static RootLayoutController rootLayout;

  /**
   * Started flag.
   */
  private static final AtomicBoolean started = new AtomicBoolean();

  /**
   * Start JavaFx.
   */
  public static void startJavaFx() {
    try {
      LOCK.lock();
      if (!started.get()) {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
          JavaFxRealApplication.launch();
        });
        while (!started.get()) {
          Thread.yield();
        }
      }
    } finally {
      LOCK.unlock();
    }
  }

  /**
   * An empty start method.
   *
   * @param stage the stage.
   */
  @Override
  public final void start(final Stage stage) {
    primaryStage = stage;
    rootLayout = RootLayoutController.loadView();
    Scene scene = new Scene(rootLayout.getPane());
    primaryStage.setScene(scene);
    
    started.set(Boolean.TRUE);
  }
}
