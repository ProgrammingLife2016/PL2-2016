package nl.tudelft.pl2016gr2.model;

import java.util.Date;

/**
 * POJO for the Annotations found in the metadata.xlsx.
 *
 * <p>
 *   One consideration is to make enums/actual classes for several
 *   properties to give more functionality. An example is to have
 *   cyclic references to easily lookup other annotations with the
 *   same property.
 * </p>
 */
public class Annotation {

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
}