package nl.tudelft.pl2016gr2.parser.controller;

import nl.tudelft.pl2016gr2.model.Annotation;
import nl.tudelft.pl2016gr2.model.GenomeMap;
import nl.tudelft.pl2016gr2.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for reading a gff file.
 * @author Cas
 *
 */
public class AnnotationReader {

  private InputStream fileStream;
  private GenomeMap genomeMap;
  private ArrayList<Annotation> annotations = new ArrayList<>();

  /**
   * Creates an AnnotationReader for the inputstream specified.
   * @param inputStream the file we want to read.
   */
  public AnnotationReader(InputStream inputStream) {
    this.fileStream = inputStream;
    this.genomeMap = GenomeMap.getInstance();
  }

  /**
   * Reads the gff file containing annotations.
   * @return The list of annotations in the gff file.
   */
  public ArrayList<Annotation> read() {
    try {
      return parse();
    } catch (IOException ex) {
      Logger.getLogger(AnnotationReader.class.getName()).log(Level.SEVERE, null, ex);
      return null;
    }
  }

  /**
   * Creates a BufferedReader and parses every line of the file.
   * 
   * @return The list of annotations in the file.
   * @throws IOException from the BufferedReader.
   */
  private ArrayList<Annotation> parse() throws IOException {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(fileStream))) {
      String line;
      while ((line = br.readLine()) != null) {
        Annotation annotation = readLine(line);
        if (annotation != null) {
          annotations.add(annotation);
        }
      }
      return annotations;
    }
  }
  
  /**
   * Parser a line of the gff file
   * @param line A full line of the gff file
   * @return An annotation object with the information of the file.
   */
  private Annotation readLine(String line) {
    String[] tabSplitted = line.split("\t");
    if (tabSplitted.length != 9) {
      Logger.getLogger(AnnotationReader.class.getName()).log(Level.WARNING,
          "Line layout not recognised (other length than expected): " + line);
      return null;
    }
    String refGenome = tabSplitted[0];
    String unknownNull = tabSplitted[1];
    String tag = tabSplitted[2];
    int firstInt = Integer.parseInt(tabSplitted[3]);
    int secondInt = Integer.parseInt(tabSplitted[4]);
    double firstDouble = Double.parseDouble(tabSplitted[5]);
    String plusMinus = tabSplitted[6];
    String point = tabSplitted[7];
    Annotation annotation = new Annotation(refGenome, unknownNull, tag, firstInt, secondInt,
        firstDouble, plusMinus, point);
    extractProperties(tabSplitted[8], annotation);
    if (genomeMap.containsGenome(refGenome)) {
      return annotation;
    } else {
      return null;
    }
  }

  /**
   * This method adds properties of some annotation contained 
   * in the gff file to an annotation.
   * @param information The String with all properties
   * @param annotation The annotation to which these properties have to be added.
   */
  private void extractProperties(String information, Annotation annotation) {
    String[] semicolonDelimited = information.split(";");
    for (String str : semicolonDelimited) {
      annotation.addAttribute(parseProperty(str));
    }
  }

  /**
   * Splits the property on a '=' and takes the value.
   * @param property the full property
   * @return the value of the property
   */
  private Pair<String, String> parseProperty(String property) {
    String[] propertySplit = property.split("=");
    return new Pair<String, String>(propertySplit[0], propertySplit[1]);
  }

}
