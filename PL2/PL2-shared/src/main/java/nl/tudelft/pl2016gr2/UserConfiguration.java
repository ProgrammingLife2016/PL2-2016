package nl.tudelft.pl2016gr2;

import java.util.prefs.Preferences;

public class UserConfiguration {

  public static final String USER_CONFIG_TREE_PATH = "lastOpenedTreePath";
  public static final String USER_CONFIG_GRAPH_PATH = "lastOpenedGraphPath";

  public static final Preferences preferences =
      Preferences.userNodeForPackage(UserConfiguration.class);


  /**
   * Private constructor to prevent instantiation.
   */
  private UserConfiguration() {
  }

}
