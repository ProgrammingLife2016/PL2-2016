package nl.tudelft.pl2016gr2.parser.controller;

import nl.tudelft.pl2016gr2.model.Annotation;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnnotationReader {

  private static final Logger logger = Logger.getLogger(AnnotationReader.class.getName());

  private final InputStream stream;

  public AnnotationReader(InputStream stream) {
    this.stream = stream;
  }

  /**
   * This method reads a single row of the sheet.
   *
   * <p>
   *   This method will subsequently call various methods in this class
   *   to parse a single column, on a whole parsing a whole line.
   * </p>
   * @param row the row to parse.
   * @return A single {@link Annotation} object that the given row represents.
   */
  /*
   * There are many columns in the annotation file so this method is
   * exceptionally long, because it needs a number of method calls for each column.
   * As this number exceeds the MethodLength restriction of our CheckStyle
   * this method Suppresses its warnings.
   */
  @SuppressWarnings("checkstyle:MethodLength")
  private Annotation readRow(Row row) {
    Iterator<Cell> iterator = row.cellIterator();

    Annotation annotation = new Annotation();

    parseSpecimenId(annotation, iterator.next());
    parseAge(annotation, iterator.next());
    parseSex(annotation, iterator.next());
    parseHIVStatus(annotation, iterator.next());
    parseCohort(annotation, iterator.next());
    parseDateOfCollection(annotation, iterator.next());
    parseStudyGeographicDistrict(annotation, iterator.next());
    parseSpecimenType(annotation, iterator.next());
    parseMicroscopySmearStatus(annotation, iterator.next());
    iterator.next(); // swallow specimenId column
    parseDNAIsolation(annotation, iterator.next());
    parsePhenotypicDSTPattern(annotation, iterator.next());
    parseCapreomycin(annotation, iterator.next());
    parseEthambutol(annotation, iterator.next());
    parseEthionamide(annotation, iterator.next());
    parseIsoniazid(annotation, iterator.next());
    parseKanamycin(annotation, iterator.next());
    iterator.next(); // swallow specimenId column
    parsePyrazinamide(annotation, iterator.next());
    parseOfloxacin(annotation, iterator.next());
    parseRifampin(annotation, iterator.next());
    parseStreptomycin(annotation, iterator.next());
    parseDigitalSpoligotype(annotation, iterator.next());
    parseLineage(annotation, iterator.next());
    parseGenotypicDSTPattern(annotation, iterator.next());

    return annotation;
  }

  private void parseSpecimenId(Annotation annotation, Cell cell) {
    annotation.specimenId = cell.getStringCellValue();
  }

  private void parseAge(Annotation annotation, Cell cell) {
    if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
      annotation.age = (int)cell.getNumericCellValue();
    }
  }

  private void parseSex(Annotation annotation, Cell cell) {
    if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
      annotation.sex = Annotation.Sex.valueOf(cell.getStringCellValue());
    } else {
      annotation.sex = Annotation.Sex.unknown;
    }
  }

  @SuppressWarnings("AbbreviationAsWordInName")
  private void parseHIVStatus(Annotation annotation, Cell cell) {
    annotation.hivStatus = Annotation.Status.valueOf(cell.getStringCellValue());
  }

  private void parseCohort(Annotation annotation, Cell cell) {
    annotation.cohort = cell.getStringCellValue();
  }

  private void parseDateOfCollection(Annotation annotation, Cell cell) {
    if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC && HSSFDateUtil.isCellDateFormatted(cell)) {
      annotation.dateOfCollection = cell.getDateCellValue();
    } else {
      logger.log(Level.WARNING, String.format(
              "Species %s with dateCell %s doesn't represent a date?",
              annotation.specimenId,
              cell.toString())
      );
    }
  }

  private void parseStudyGeographicDistrict(Annotation annotation, Cell cell) {
    annotation.studyGeographicDistrict = cell.getStringCellValue();
  }

  private void parseSpecimenType(Annotation annotation, Cell cell) {
    annotation.specimenType = cell.getStringCellValue();
  }

  private void parseMicroscopySmearStatus(Annotation annotation, Cell cell) {
    annotation.microscopySmearStatus = Annotation.Status.valueOf(cell.getStringCellValue());
  }

  @SuppressWarnings("AbbreviationAsWordInName")
  private void parseDNAIsolation(Annotation annotation, Cell cell) {
    switch (cell.getStringCellValue()) {
      case "single colony":
        annotation.dnaIsolation = Annotation.Isolation.Single;
        break;
      case "non-single colony":
        annotation.dnaIsolation = Annotation.Isolation.NonSingle;
        break;
      default:
        throw new RuntimeException("DNA isolation not \"single colony\" or \"non-single colony\"");
    }
  }

  @SuppressWarnings("AbbreviationAsWordInName")
  private void parsePhenotypicDSTPattern(Annotation annotation, Cell cell) {
    annotation.phenotypicDSTPattern = cell.getStringCellValue();
  }

  private void parseCapreomycin(Annotation annotation, Cell cell) {
    annotation.capreomycin = cell.getStringCellValue();
  }

  private void parseEthambutol(Annotation annotation, Cell cell) {
    annotation.ethambutol = cell.getStringCellValue();
  }

  private void parseEthionamide(Annotation annotation, Cell cell) {
    annotation.ethionamide = cell.getStringCellValue();
  }

  private void parseIsoniazid(Annotation annotation, Cell cell) {
    annotation.isoniazid = cell.getStringCellValue();
  }

  private void parseKanamycin(Annotation annotation, Cell cell) {
    annotation.kanamycin = cell.getStringCellValue();
  }

  private void parsePyrazinamide(Annotation annotation, Cell cell) {
    annotation.pyrazinamide = cell.getStringCellValue();
  }

  private void parseOfloxacin(Annotation annotation, Cell cell) {
    annotation.ofloxacin = cell.getStringCellValue();
  }

  private void parseRifampin(Annotation annotation, Cell cell) {
    annotation.rifampin = cell.getStringCellValue();
  }

  private void parseStreptomycin(Annotation annotation, Cell cell) {
    annotation.streptomycin = cell.getStringCellValue();
  }

  private void parseDigitalSpoligotype(Annotation annotation, Cell cell) {
    annotation.digitalSpoligotype = cell.getStringCellValue();
  }

  private void parseLineage(Annotation annotation, Cell cell) {
    annotation.lineage = cell.getStringCellValue();
  }

  @SuppressWarnings("AbbreviationAsWordInName")
  private void parseGenotypicDSTPattern(Annotation annotation, Cell cell) {
    annotation.genotypicDSTPattern = cell.getStringCellValue();
  }

  /**
   * Reads the file given in the constructor and returns a list of {@link Annotation}s.
   *
   * @return The list.
   * @throws IOException on io erros
   * @throws InvalidFormatException when the given file is not the proper format.
   */
  public List<Annotation> read() throws IOException, InvalidFormatException {
    Workbook wb = WorkbookFactory.create(stream);
    Sheet sheet = wb.getSheetAt(wb.getActiveSheetIndex());
    List<Annotation> out = new ArrayList<>();
    long startTime = System.currentTimeMillis();

    for (Row row : sheet) {
      // skip "header" rows
      // Also makes sure that the row isn't empty
      // in our first given file the last row is non-null but
      // does not actually contain data.
      Cell firstCell = row.getCell(row.getFirstCellNum());
      if (firstCell.getCellType() == Cell.CELL_TYPE_STRING
          && !firstCell.getStringCellValue().equals("Specimen ID")) {
        out.add(readRow(row));
      }
    }
    long stopTime = System.currentTimeMillis();
    logger.log(Level.INFO, String.format("Took %d milliseconds to read %d annotations",
        stopTime - startTime,
        out.size()));
    return out;

  }

}
