package nl.tudelft.pl2016gr2.core;

import net.sourceforge.olduvai.treejuxtaposer.TreeParser;
import net.sourceforge.olduvai.treejuxtaposer.drawer.Tree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileTreeFactory implements TreeFactory {

  private File file;

  /**
   * Constructs a new factory, this constructor checks whether the file exists.
   *
   * @param file The file to read
   * @throws FileNotFoundException when file does not exist
   */
  public FileTreeFactory(File file) throws FileNotFoundException {
    this.file = file;
    if (!file.exists()) {
      throw new FileNotFoundException();
    }

  }

  @Override
  public Tree getTree() {
    final Reader reader;
    try {
      reader = new InputStreamReader(new FileInputStream(file));
    } catch (FileNotFoundException e) {
      throw new RuntimeException("File not found, even though we checked in the constructor");
    }
    BufferedReader br = new BufferedReader(reader);
    TreeParser tp = new TreeParser(br);

    Tree tree = tp.tokenize("Tree");

    try {
      reader.close();
    } catch (IOException ex) {
      Logger.getLogger(FileTreeFactory.class.getName()).log(Level.SEVERE, null, ex);
    }

    return tree;
  }
}
