package nl.tudelft.pl2016gr2.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PairTest {

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Test
  public void pairTest() {
    String str = "Test";
    int integer = 8;
    Pair pair = new Pair(str, integer);
    assertEquals(str, pair.left);
    assertEquals(integer, pair.right);
  }

}
