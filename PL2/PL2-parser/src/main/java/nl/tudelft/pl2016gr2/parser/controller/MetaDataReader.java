package nl.tudelft.pl2016gr2.parser.controller;

import nl.tudelft.pl2016gr2.model.GenomeMap;
import nl.tudelft.pl2016gr2.model.MetaData;

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

public class MetaDataReader {

  private static final Logger logger = Logger.getLogger(MetaDataReader.class.getName());

  private final InputStream stream;

  public MetaDataReader(InputStream stream) {
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
   * @return A single {@link MetaData} object that the given row represents.
   */
  /*
   * There are many columns in the annotation file so this method is
   * exceptionally long, because it needs a number of method calls for each column.
   * As this number exceeds the MethodLength restriction of our CheckStyle
   * this method Suppresses its warnings.
   */
  @SuppressWarnings("checkstyle:MethodLength")
  private MetaData readRow(Row row) {
    Iterator<Cell> iterator = row.cellIterator();

    MetaData metaData = new MetaData();

    parseSpecimenId(metaData, iterator.next());
    parseAge(metaData, iterator.next());
    parseSex(metaData, iterator.next());
    parseHIVStatus(metaData, iterator.next());
    parseCohort(metaData, iterator.next());
    parseDateOfCollection(metaData, iterator.next());
    parseStudyGeographicDistrict(metaData, iterator.next());
    parseSpecimenType(metaData, iterator.next());
    parseMicroscopySmearStatus(metaData, iterator.next());
    iterator.next(); // swallow specimenId column
    parseDNAIsolation(metaData, iterator.next());
    parsePhenotypicDSTPattern(metaData, iterator.next());
    parseCapreomycin(metaData, iterator.next());
    parseEthambutol(metaData, iterator.next());
    parseEthionamide(metaData, iterator.next());
    parseIsoniazid(metaData, iterator.next());
    parseKanamycin(metaData, iterator.next());
    iterator.next(); // swallow specimenId column
    parsePyrazinamide(metaData, iterator.next());
    parseOfloxacin(metaData, iterator.next());
    parseRifampin(metaData, iterator.next());
    parseStreptomycin(metaData, iterator.next());
    parseDigitalSpoligotype(metaData, iterator.next());
    parseLineage(metaData, iterator.next());
    parseGenotypicDSTPattern(metaData, iterator.next());

    return metaData;
  }

  private void parseSpecimenId(MetaData metaData, Cell cell) {
    metaData.specimenId = cell.getStringCellValue();
  }

  private void parseAge(MetaData metaData, Cell cell) {
    if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
      metaData.age = (int)cell.getNumericCellValue();
    }
  }

  private void parseSex(MetaData metaData, Cell cell) {
    if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
      metaData.sex = MetaData.Sex.valueOf(cell.getStringCellValue());
    } else {
      metaData.sex = MetaData.Sex.unknown;
    }
  }

  @SuppressWarnings("AbbreviationAsWordInName")
  private void parseHIVStatus(MetaData metaData, Cell cell) {
    metaData.hivStatus = MetaData.Status.valueOf(cell.getStringCellValue());
  }

  private void parseCohort(MetaData metaData, Cell cell) {
    metaData.cohort = cell.getStringCellValue();
  }

  private void parseDateOfCollection(MetaData metaData, Cell cell) {
    if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC && HSSFDateUtil.isCellDateFormatted(cell)) {
      metaData.dateOfCollection = cell.getDateCellValue();
    } else {
      logger.log(Level.WARNING, String.format(
              "Species %s with dateCell %s doesn't represent a date?",
              metaData.specimenId,
              cell.toString())
      );
    }
  }

  private void parseStudyGeographicDistrict(MetaData metaData, Cell cell) {
    metaData.studyGeographicDistrict = cell.getStringCellValue();
  }

  private void parseSpecimenType(MetaData metaData, Cell cell) {
    metaData.specimenType = cell.getStringCellValue();
  }

  private void parseMicroscopySmearStatus(MetaData metaData, Cell cell) {
    metaData.microscopySmearStatus = MetaData.Status.valueOf(cell.getStringCellValue());
  }

  @SuppressWarnings("AbbreviationAsWordInName")
  private void parseDNAIsolation(MetaData metaData, Cell cell) {
    switch (cell.getStringCellValue()) {
      case "single colony":
        metaData.dnaIsolation = MetaData.Isolation.Single;
        break;
      case "non-single colony":
        metaData.dnaIsolation = MetaData.Isolation.NonSingle;
        break;
      default:
        throw new RuntimeException("DNA isolation not \"single colony\" or \"non-single colony\"");
    }
  }

  @SuppressWarnings("AbbreviationAsWordInName")
  private void parsePhenotypicDSTPattern(MetaData metaData, Cell cell) {
    metaData.phenotypicDSTPattern = cell.getStringCellValue();
  }

  private void parseCapreomycin(MetaData metaData, Cell cell) {
    metaData.capreomycin = cell.getStringCellValue();
  }

  private void parseEthambutol(MetaData metaData, Cell cell) {
    metaData.ethambutol = cell.getStringCellValue();
  }

  private void parseEthionamide(MetaData metaData, Cell cell) {
    metaData.ethionamide = cell.getStringCellValue();
  }

  private void parseIsoniazid(MetaData metaData, Cell cell) {
    metaData.isoniazid = cell.getStringCellValue();
  }

  private void parseKanamycin(MetaData metaData, Cell cell) {
    metaData.kanamycin = cell.getStringCellValue();
  }

  private void parsePyrazinamide(MetaData metaData, Cell cell) {
    metaData.pyrazinamide = cell.getStringCellValue();
  }

  private void parseOfloxacin(MetaData metaData, Cell cell) {
    metaData.ofloxacin = cell.getStringCellValue();
  }

  private void parseRifampin(MetaData metaData, Cell cell) {
    metaData.rifampin = cell.getStringCellValue();
  }

  private void parseStreptomycin(MetaData metaData, Cell cell) {
    metaData.streptomycin = cell.getStringCellValue();
  }

  private void parseDigitalSpoligotype(MetaData metaData, Cell cell) {
    metaData.digitalSpoligotype = cell.getStringCellValue();
  }

  private void parseLineage(MetaData metaData, Cell cell) {
    metaData.lineage = cell.getStringCellValue();
  }

  @SuppressWarnings("AbbreviationAsWordInName")
  private void parseGenotypicDSTPattern(MetaData metaData, Cell cell) {
    metaData.genotypicDSTPattern = cell.getStringCellValue();
  }

  /**
   * Reads the file given in the constructor and returns a list of {@link MetaData}s.
   *
   * @return The list.
   * @throws IOException on io erros
   * @throws InvalidFormatException when the given file is not the proper format.
   */
  public List<MetaData> read() throws IOException, InvalidFormatException {
    Workbook wb = WorkbookFactory.create(stream);
    Sheet sheet = wb.getSheetAt(wb.getActiveSheetIndex());
    List<MetaData> out = new ArrayList<>();
    long startTime = System.currentTimeMillis();

    GenomeMap genomeMap = GenomeMap.getInstance();
    for (Row row : sheet) {
      Cell firstCell = row.getCell(row.getFirstCellNum());
      if (firstCell.getCellType() == Cell.CELL_TYPE_STRING
          && !firstCell.getStringCellValue().equals("Specimen ID")) {
        MetaData metaData = readRow(row);
        if (genomeMap.getId(metaData.specimenId) != null) {
          out.add(metaData);
        }
      }
    }
    long stopTime = System.currentTimeMillis();
    logger.log(Level.INFO, String.format("Took %d milliseconds to read %d annotations",
        stopTime - startTime,
        out.size()));
    return out;
  }

}
