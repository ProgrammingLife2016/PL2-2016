package nl.tudelft.pl2016gr2.gui.javafxrunner;

import javafx.application.Platform;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Coppied from: http://awhite.blogspot.nl/2013/04/javafx-junit-testing.html This basic class runner
 * ensures that JavaFx is running and then wraps all the runChild() calls in a Platform.runLater().
 * runChild() is called for each test that is run. By wrapping each call in the Platform.runLater()
 * this ensures that the request is executed on the JavaFx thread.
 */
public class JavaFxJUnit4ClassRunner extends BlockJUnit4ClassRunner {

  /**
   * Constructs a new JavaFxJUnit4ClassRunner with the given parameters.
   *
   * @param clazz The class that is to be run with this Runner
   * @throws InitializationError Thrown by the BlockJUnit4ClassRunner in the super()
   */
  public JavaFxJUnit4ClassRunner(final Class<?> clazz) throws InitializationError {
    super(clazz);
    if (!isTravisBuild()) {
      JavaFxJUnit4Application.startJavaFx();
    }
  }

  @Override
  protected void runChild(final FrameworkMethod method, final RunNotifier notifier) {
    if (isTravisBuild()) {
      return;
    }
    final CountDownLatch latch = new CountDownLatch(1);
    Platform.runLater(() -> {
      JavaFxJUnit4ClassRunner.super.runChild(method, notifier);
      latch.countDown();
    });
    try {
      latch.await();
    } catch (InterruptedException ex) {
      Logger.getLogger(JavaFxJUnit4ClassRunner.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Check if this is a travis build. Travis can't open displays (when you try to you get the error:
   * "(java:2726): Gtk-WARNING **: cannot open display:"), so JavaFX tests must be skipped if this
   * is a travis build.
   *
   * @return if this is a travis build.
   */
  private static boolean isTravisBuild() {
    return "true".equals(System.getenv("TRAVIS"));
  }
}
