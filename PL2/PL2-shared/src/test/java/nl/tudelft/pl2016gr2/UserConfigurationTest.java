package nl.tudelft.pl2016gr2;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;


/**
 *
 * <p>
 *  Not much to test, as it is merely a helper class with some constants and
 *  a way to always access the same Preferences globally.
 * </p>
 *
 */
public class UserConfigurationTest {

  @Test
  public void testInstance() {
    assertNotNull(UserConfiguration.preferences);
  }


}
