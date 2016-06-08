package nl.tudelft.pl2016gr2.parser.controller;

import nl.tudelft.pl2016gr2.model.Annotation;
import nl.tudelft.pl2016gr2.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnnotationReader {

  private InputStream fileStream;
  private HashSet<String> properties = new HashSet<>();
  private ArrayList<Annotation> annotations = new ArrayList<>();

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
      int count = 0;
      while ((line = br.readLine()) != null && count < 30) {
        count++;
        String[] tabSplitted = line.split("\t");
        if (tabSplitted.length < 9) {
          Logger.getLogger(GfaReader.class.getName()).log(Level.WARNING,
              "Line layout not recognised (shorter than expected): " + line);
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
        if (tabSplitted.length >= 10) {
          Logger.getLogger(AnnotationReader.class.getName()).log(Level.WARNING, null,
              "Line layout not recognised (longer than expected): " + line);
        }
        annotations.add(annotation);
      }
      System.out.println(annotations);
      System.out.println(properties);
    }
  }

  private void extractProperties(String information, Annotation annotation) {
    String[] semicolonDelimited = information.split(";");
    Pair<String, String> tuple = splitOnEqualSign(semicolonDelimited[0]);
    Pair<String, String> tuple2 = splitOnEqualSign(semicolonDelimited[1]);
    Pair<String, String> tuple3 = splitOnEqualSign(semicolonDelimited[2]);
    Pair<String, String> tuple4 = splitOnEqualSign(semicolonDelimited[3]);
    annotation.setProperties(tuple2.right, tuple4.right, tuple.right, tuple3.right);
  }

  private Pair<String, String> splitOnEqualSign(String property) {
    String[] propertySplit = property.split("=");
    properties.add(propertySplit[0]);
    return new Pair<>(propertySplit[0], propertySplit[1]);
  }

}
