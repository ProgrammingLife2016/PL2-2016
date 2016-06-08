package nl.tudelft.pl2016gr2.parser.controller;

import nl.tudelft.pl2016gr2.model.Annotation;
import nl.tudelft.pl2016gr2.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnnotationReader {

  private InputStream fileStream;
  private HashSet<String> properties = new HashSet<>();

  public AnnotationReader(InputStream inputStream) {
    this.fileStream = inputStream;
  }

  public void read() {
    try {
      parse();
    } catch (IOException ex) {
      Logger.getLogger(GfaReader.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private void parse() throws IOException {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(fileStream))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] tabSplitted = line.split("\t");
        if (tabSplitted.length < 9) {
          Logger.getLogger(GfaReader.class.getName()).log(Level.WARNING, null,
              "Line layout not recognised (shorter than expected): " + line);
        }
        Annotation annotation = new Annotation();
        String refGenome = tabSplitted[0];
        String unkownNull = tabSplitted[1];
        String tag = tabSplitted[2];
        String firstInt = tabSplitted[3];
        String secondInt = tabSplitted[4];
        String firstDouble = tabSplitted[5];
        String plusMinus = tabSplitted[6];
        String point = tabSplitted[7];
        extractProperties(tabSplitted[8]);
        if (tabSplitted.length >= 10) {
          Logger.getLogger(AnnotationReader.class.getName()).log(Level.WARNING, null,
              "Line layout not recognised (longer than expected): " + line);
        }
      }
      System.out.println(properties);
    }
  }
  
  private void extractProperties(String information) {
    String[] semicolonDelimited = information.split(";");
    Pair<String, String> tuple = splitOnEqualSign(semicolonDelimited[0]);
    if (!tuple.left.equals("calhounClass")) {
      Logger.getLogger(AnnotationReader.class.getName()).log(Level.WARNING, null,
          "calhounClass expected but was: " + tuple.left);
    }
    Pair<String, String> tuple2 = splitOnEqualSign(semicolonDelimited[1]);
    if (!tuple.left.equals("Name")) {
      Logger.getLogger(AnnotationReader.class.getName()).log(Level.WARNING, null,
          "Name expected but was: " + tuple.left);
    }
    Pair<String, String> tuple3 = splitOnEqualSign(semicolonDelimited[2]);
    if (!tuple.left.equals("ID")) {
      Logger.getLogger(AnnotationReader.class.getName()).log(Level.WARNING, null,
          "ID expected but was: " + tuple.left);
    }
    Pair<String, String> tuple4 = splitOnEqualSign(semicolonDelimited[3]);
    if (!tuple.left.equals("displayName")) {
      Logger.getLogger(AnnotationReader.class.getName()).log(Level.WARNING, null,
          "displayName expected but was: " + tuple.left);
    }
    
    
  }
  
  private Pair<String, String> splitOnEqualSign(String property) {
    String[] propertySplit = property.split("=");
    properties.add(propertySplit[0]);
    return new Pair<>(propertySplit[0], propertySplit[1]);
  }

}
