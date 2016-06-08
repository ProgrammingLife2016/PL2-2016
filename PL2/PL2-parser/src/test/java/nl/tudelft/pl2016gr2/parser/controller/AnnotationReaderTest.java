package nl.tudelft.pl2016gr2.parser.controller;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class AnnotationReaderTest {

  @Test
  public void annoTest() throws FileNotFoundException {
    // System.out.println("Working Directory = " +
    // System.getProperty("user.dir"));
    InputStream io = new FileInputStream(new File("src/main/resources/decorationV5_20130412.gff"));
    AnnotationReader anno = new AnnotationReader(io);
    anno.read();
  }

}
