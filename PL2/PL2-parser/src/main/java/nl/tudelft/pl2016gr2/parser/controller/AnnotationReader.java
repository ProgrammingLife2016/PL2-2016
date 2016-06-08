package nl.tudelft.pl2016gr2.parser.controller;

import nl.tudelft.pl2016gr2.model.Annotation;
import nl.tudelft.pl2016gr2.model.GenomeMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class reads an annotation file.
 *
 * @author Faris
 */
public class AnnotationReader {

  private static final Logger LOGGER = Logger.getLogger(AnnotationReader.class.getName());
  private final InputStream fileStream;
  private ArrayList<Annotation> annotations;

  /**
   * Creates a reader object and reads the gfa data from the filename.
   *
   * @param fileStream the file to read.
   */
  public AnnotationReader(InputStream fileStream) {
    this.fileStream = fileStream;
  }

  /**
   * Read the annotation file.
   *
   * @return The read set of annotations.
   */
  public Collection<Annotation> read() {
    if (annotations == null) {
      annotations = annotations = new ArrayList<>();
      GenomeMap genomeMap = GenomeMap.getInstance();
      try (BufferedReader br = new BufferedReader(new InputStreamReader(fileStream))) {
        String line;
        while ((line = br.readLine()) != null) {
          Annotation annotation = parseLine(line);
          if (annotation != null && genomeMap.containsGenome(annotation.sequenceId)) {
            annotations.add(annotation);
          }
        }
      } catch (IOException ex) {
        Logger.getLogger(AnnotationReader.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return annotations;
  }

  /**
   * Parse a line of the annotation file.
   *
   * @param line the line to parse.
   * @return the read annotation, or null if an invalid annotation has been read.
   */
  private Annotation parseLine(String line) {
    String[] parts = line.split("\t");
    if (parts.length < 9) {
      LOGGER.log(Level.WARNING, "The following read line does not contain the expected 9 "
          + "substrings split by tabs: {0}", line);
      return null;
    }
    return new Annotation(parts);
  }
}
