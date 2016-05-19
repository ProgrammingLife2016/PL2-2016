package nl.tudelft.pl2016gr2.core;


import nl.tudelft.pl2016gr2.model.OriginalGraph;
import nl.tudelft.pl2016gr2.parser.controller.GfaReader;

import java.io.File;

public class FileGraphFactory implements GraphFactory {

  private final File file;

  public FileGraphFactory(File file) {
    this.file = file;
  }

  @Override
  public OriginalGraph getGraph() {
    return getGfaReader().read();
  }

  /*package*/ GfaReader getGfaReader() {
    return new GfaReader(file);
  }

}
