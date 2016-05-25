package nl.tudelft.pl2016gr2.util;

/**
 * Represents the ability to copy (clone) itself.
 * <p>
 * This class is a custom implementation of the Cloneable interface in the Java API.
 * </p>
 * <p>
 * This class implements the <i>factory method design pattern</i>.
 * The copy method returns a concrete implementation of T.
 * </p>
 *
 * @author Wouter Smit
 */
public interface Copyable<T> {

  /**
   * Creates a copy of class as is relevant to the concrete implementation.
   *
   * @return An exact copy of the class
   */
  T copy();
}
