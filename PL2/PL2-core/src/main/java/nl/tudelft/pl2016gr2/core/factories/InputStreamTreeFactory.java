package nl.tudelft.pl2016gr2.core.factories;

import net.sourceforge.olduvai.treejuxtaposer.TreeParser;
import net.sourceforge.olduvai.treejuxtaposer.drawer.Tree;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InputStreamTreeFactory implements TreeFactory {

  private final InputStream fileStream;

  /**
   * Constructs a new factory, this constructor checks whether the file exists.
   *
   * @param fileStream The file to read
   * @throws FileNotFoundException when file does not exist
   */
  public InputStreamTreeFactory(InputStream fileStream) throws FileNotFoundException {
    this.fileStream = fileStream;
  }

  /* package */ TreeParser getTreeParser(BufferedReader br) {
    return new TreeParser(br);
  }

  @Override
  public Tree getTree() {
    final Reader reader;
    reader = new InputStreamReader(fileStream);
    BufferedReader br = new BufferedReader(reader);
    TreeParser tp = getTreeParser(br);

    Tree tree = tp.tokenize("Tree");

    try {
      reader.close();
    } catch (IOException ex) {
      Logger.getLogger(InputStreamTreeFactory.class.getName()).log(Level.SEVERE, null, ex);
    }

    return tree;
  }
}
