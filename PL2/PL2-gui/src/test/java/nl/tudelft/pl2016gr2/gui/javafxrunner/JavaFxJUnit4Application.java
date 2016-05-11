package nl.tudelft.pl2016gr2.gui.javafxrunner;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Coppied from: http://awhite.blogspot.nl/2013/04/javafx-junit-testing.html This is the application
 * which starts JavaFx. It is controlled through the startJavaFx() method.
 */
public class JavaFxJUnit4Application extends Application {

  /**
   * The lock that guarantees that only one JavaFX thread will be started.
   */
  private static final ReentrantLock LOCK = new ReentrantLock();

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
          JavaFxJUnit4Application.launch();
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
    started.set(Boolean.TRUE);
  }
}
