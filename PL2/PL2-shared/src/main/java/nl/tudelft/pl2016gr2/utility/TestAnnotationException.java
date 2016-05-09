package nl.tudelft.pl2016gr2.utility;

/**
 * An exception which is thrown when there is something wrong with the test annotation. This
 * exception is a runtime exception and isn't intended to be caught.
 *
 * @see RuntimeException
 *
 * @author Faris
 */
public class TestAnnotationException extends RuntimeException {

  /**
   * Creates a new instance of <code>NotAnnotatedException</code> without detail message.
   */
  public TestAnnotationException() {
  }

  /**
   * Constructs an instance of <code>NotAnnotatedException</code> with the specified detail message.
   *
   * @param msg the detail message.
   */
  public TestAnnotationException(String msg) {
    super(msg);
  }
}
