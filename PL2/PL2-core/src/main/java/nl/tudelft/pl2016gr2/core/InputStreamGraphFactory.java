package nl.tudelft.pl2016gr2.core;

import nl.tudelft.pl2016gr2.model.graph.SequenceGraph;
import nl.tudelft.pl2016gr2.parser.controller.GfaReader;

import java.io.InputStream;

public class InputStreamGraphFactory implements GraphFactory {

  private final InputStream stream;

  public InputStreamGraphFactory(InputStream file) {
    this.stream = file;
  }

  @Override
  public SequenceGraph getGraph() {
    return getGfaReader().read();
  }

  /*package*/ GfaReader getGfaReader() {
    return new GfaReader(stream);
  }

}
