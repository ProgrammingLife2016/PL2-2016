package nl.tudelft.pl2016gr2.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * POJO for the Annotations found in the metadata.xlsx.
 *
 * <p>
 * One consideration is to make enums/actual classes for several properties to give more
 * functionality. An example is to have cyclic references to easily lookup other annotations with
 * the same property.
 * </p>
 */
public class MetaData {

  /**
   * This is a list of column names that are known to be categorical.
   *
   * <p>
   * In an ideal situation the Metadata class would also support this and the reader would be able
   * to read any sheet.
   * </p>
   */
  public static final String[] KNOWN_CATEGORICAL_COLUMNS = {
    "genotypic DST",
    "phenotypic DST",
    "lineage",
    "hiv status",
    "cohort",
    "study geographic district",
    "specimen type",
    "microscopy smear status",
    "dna isolation",
    "capreomycin",
    "ethambutol",
    "ethionamide",
    "isoniazid",
    "kanamycin",
    "pyrazinamide",
    "ofloxacin",
    "rifampin",
    "streptomycin",
    "digital spoligotype",
    "sex",};

  public String specimenId;

  public Integer age;

  public Sex sex;

  public Status hivStatus;

  public String cohort;

  public Date dateOfCollection;

  public String studyGeographicDistrict;

  // consider introducing an enum?
  public String specimenType;

  public Status microscopySmearStatus;

  public Isolation dnaIsolation;

  @SuppressWarnings("AbbreviationAsWordInName")
  public String phenotypicDSTPattern;

  // consider introducing an enum?
  public String capreomycin;

  // consider introducing an enum?
  public String ethambutol;

  // consider introducing an enum?
  public String ethionamide;

  // consider introducing an enum?
  public String isoniazid;

  // consider introducing an enum?
  public String kanamycin;

  // consider introducing an enum?
  public String pyrazinamide;

  // consider introducing an enum?
  public String ofloxacin;

  // consider introducing an enum?
  public String rifampin;

  // consider introducing an enum?
  public String streptomycin;

  public String digitalSpoligotype;

  // consider introducing an enum?
  public String lineage;

  // consider introducing an enum?
  @SuppressWarnings("AbbreviationAsWordInName")
  public String genotypicDSTPattern;

  // TODO: Tugela Ferry vs. non-Tugela Ferry XDR
  // A lot of n/a, also not clear what to name this property..
  public enum Sex {
    Male,
    Female,
    unknown
  }

  public enum Status {
    Positive,
    Negative,
    unknown
  }

  public enum Isolation {
    Single,
    NonSingle
  }

  @Override
  public String toString() {
    return String.format(
        "<%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s>",
        specimenId, age, sex, hivStatus, cohort,
        dateOfCollection, studyGeographicDistrict,
        specimenType, microscopySmearStatus, dnaIsolation,
        phenotypicDSTPattern, capreomycin, ethambutol,
        ethionamide, isoniazid, kanamycin, pyrazinamide,
        ofloxacin, rifampin, streptomycin, digitalSpoligotype,
        lineage, genotypicDSTPattern);
  }

  /**
   * Build a string containing all of the metadata in this file.
   *
   * @return a string containing all of the metadata in this file.
   */
  @SuppressWarnings("checkstyle:MethodLength")
  public String buildMetaDataString() {
    StringBuilder sb = new StringBuilder();
    sb.append("ID\t\t\t\t\t\t").append(specimenId).append('\n');
    sb.append("Specimen Type\t\t\t").append(specimenType).append('\n');
    sb.append("Lineage\t\t\t\t\t").append(lineage).append('\n');
    sb.append("Isolation\t\t\t\t\t").append(dnaIsolation).append('\n');
    sb.append("Age\t\t\t\t\t\t").append(age).append('\n');
    sb.append("Capreomycin\t\t\t\t").append(capreomycin).append('\n');
    sb.append("Cohort\t\t\t\t\t").append(cohort).append('\n');
    sb.append("Date of collection\t\t\t");
    if (dateOfCollection == null) {
      sb.append("Unknown").append('\n');
    } else {
      sb.append(new SimpleDateFormat("dd-MM-yyyy").format(dateOfCollection)).append('\n');
    }
    sb.append("Digital Spoligo type\t\t\t").append(digitalSpoligotype).append('\n');
    sb.append("Ethambutol\t\t\t\t").append(ethambutol).append('\n');
    sb.append("Ethionamide\t\t\t\t").append(ethionamide).append('\n');
    sb.append("Genotypic DST Pattern\t\t").append(genotypicDSTPattern).append('\n');
    sb.append("Phenotypic DST Pattern\t\t").append(phenotypicDSTPattern).append('\n');
    sb.append("HIV Status\t\t\t\t").append(hivStatus).append('\n');
    sb.append("Isoniazid\t\t\t\t\t").append(isoniazid).append('\n');
    sb.append("Kanamyoin\t\t\t\t").append(kanamycin).append('\n');
    sb.append("Microscopy Smear Status\t").append(microscopySmearStatus).append('\n');
    sb.append("Ofloxaoin\t\t\t\t\t").append(ofloxacin).append('\n');
    sb.append("Pyrazinamid\t\t\t\t").append(pyrazinamide).append('\n');
    sb.append("Rifampin\t\t\t\t\t").append(rifampin).append('\n');
    sb.append("Sex\t\t\t\t\t\t").append(sex).append('\n');
    sb.append("Specimen Type\t\t\t").append(specimenType).append('\n');
    sb.append("Rifampin\t\t\t\t\t").append(rifampin).append('\n');
    sb.append("Streptomycin\t\t\t\t").append(streptomycin).append('\n');
    sb.append("Study Geographic District\t").append(studyGeographicDistrict).append('\n');
    return sb.toString();
  }

  /**
   * This method take the correct field from the metaData class for a given name.
   *
   * <p>
   * See {@link #knownCategoricalColumns}, when the metadata+its reader would understand dynamic
   * columns, this method would be unnecessary.
   * </p>
   *
   * @param columnName the name of the column.
   * @return the value corresponding to the column.
   */
  @SuppressWarnings("checkstyle:MethodLength")
  public String getValueForColumnName(String columnName) {
    switch (columnName) {
      case "genotypic DST":
        return genotypicDSTPattern;
      case "phenotypic DST":
        return phenotypicDSTPattern;
      case "lineage":
        return lineage;
      case "hiv status":
        return hivStatus.name();
      case "cohort":
        return cohort;
      case "study geographic district":
        return studyGeographicDistrict;
      case "specimen type":
        return specimenType;
      case "microscopy smear status":
        return microscopySmearStatus.name();
      case "dna isolation":
        return dnaIsolation.name();
      case "capreomycin":
        return capreomycin;
      case "ethambutol":
        return ethambutol;
      case "ethionamide":
        return ethionamide;
      case "isoniazid":
        return isoniazid;
      case "kanamycin":
        return kanamycin;
      case "pyrazinamide":
        return pyrazinamide;
      case "ofloxacin":
        return ofloxacin;
      case "rifampin":
        return rifampin;
      case "streptomycin":
        return streptomycin;
      case "digital spoligotype":
        return digitalSpoligotype;
      case "sex":
        return sex.name();
      default:
        throw new RuntimeException("Undefined column in temp method");
    }
  }
}
