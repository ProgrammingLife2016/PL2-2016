package nl.tudelft.pl2016gr2.util;

/**
 * A pair contains two values, a left value and a right value.
 *
 * @author Faris
 * @param <L> the type of the left value of the pair.
 * @param <R> the type of the right value of the pair.
 */
public class Pair<L, R> {

  public final L left;
  public final R right;

  /**
   * Construct a pair.
   *
   * @param left  the left value.
   * @param right the right value.
   */
  public Pair(L left, R right) {
    this.left = left;
    this.right = right;
  }
}
