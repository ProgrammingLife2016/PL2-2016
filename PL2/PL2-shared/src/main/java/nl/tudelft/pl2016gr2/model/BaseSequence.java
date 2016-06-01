package nl.tudelft.pl2016gr2.model;

import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;

/**
 * This class represents a DNA sequence. It efficiently encodes the ATCG and N (unknown) bases as
 * bits in an array of integers, so fewer memory is needed than when using a simple String. The
 * stored bases are immutable (just like a String) and it is possible to retrieve a String of the
 * stored bases by calling the <code>getBaseSequence()</code> method.
 *
 * @author Faris
 */
public class BaseSequence {

  private static final int BITS_PER_BASE = 3;
  private static final int BITS_PER_INT = Integer.BYTES * 8;

  private static final int END_OF_BASES = 0b000;
  private static final int BASE_A = 0b001;
  private static final int BASE_T = 0b010;
  private static final int BASE_C = 0b011;
  private static final int BASE_G = 0b100;
  private static final int BASE_UNKNOWN = 0b101;

  @TestId(id = "bases")
  private final int[] bases;
  private final int amountOfBases;

  /**
   * Create a base sequence. The characters in the String may only have one of the following values:
   * A, T, C, G, N (otherwise an AssertionError will be thrown).
   *
   * @param bases the sequence of bases.
   */
  public BaseSequence(String bases) {
    this(bases.toCharArray(), 0, bases.length());
  }

  /**
   * Create a base sequence. This constructor is made for the parser (as the parser parses an array
   * of characters). The characters in the array between the startIndex and endIndex may only have
   * one of the following values: A, T, C, G, N (otherwise an AssertionError will be thrown).
   *
   * @param bases      an array containing the sequence of bases.
   * @param startIndex the index in the array where the bases start.
   * @param endIndex   the index in the array where the bases end.
   */
  public BaseSequence(char[] bases, int startIndex, int endIndex) {
    amountOfBases = endIndex - startIndex;
    int bitsNeeded = amountOfBases * BITS_PER_BASE;
    int intsNeeded = (int) Math.ceil(bitsNeeded / (double) BITS_PER_INT);
    this.bases = new int[intsNeeded];
    setBases(bases, startIndex, endIndex);
  }

  /**
   * Get the base sequence as a string.
   *
   * @return the base sequence as a string.
   */
  public String getBaseSequence() {
    StringBuilder sb = new StringBuilder();
    int i = 0;
    int encodedBase = getBase(i / BITS_PER_INT, i % BITS_PER_INT);
    while(encodedBase != END_OF_BASES) {
      sb.append(decode(encodedBase));
      i += BITS_PER_BASE;
      encodedBase = getBase(i / BITS_PER_INT, i % BITS_PER_INT);
    }
    return sb.toString();
  }

  /**
   * Get a single encoded base. If there is no base present at given index the END_OF_BASES value
   * will be returned to indicate that all bases have been read.
   *
   * @param arrayIndex the index in the array of the base.
   * @param bitIndex   the index of the starting bit of the base in the array.
   * @return the read encoded base.
   */
  private int getBase(int arrayIndex, int bitIndex) {
    if (bitIndex <= BITS_PER_INT - BITS_PER_BASE) {
      return (bases[arrayIndex] >> bitIndex) & 0b111;
    } else if (arrayIndex + 1 < bases.length) {
      int bits = (bases[arrayIndex] >>> bitIndex) & 0b111;
      int bitsLeftToCopy = BITS_PER_BASE - (BITS_PER_INT - bitIndex);
      for (int i = 0; i < bitsLeftToCopy; i++) {
        bits += (bases[arrayIndex + 1] & (1 << i)) << BITS_PER_BASE - bitsLeftToCopy;
      }
      return bits;
    }
    return END_OF_BASES;
  }

  /**
   * Encode the bases and put them into the array of integers.
   *
   * @param bases      an array containing the bases of the sequence.
   * @param startIndex the index in the array where the bases start.
   * @param endIndex   the index in the array where the bases end.
   */
  private void setBases(char[] bases, int startIndex, int endIndex) {
    for (int i = 0; i < endIndex - startIndex; i++) {
      char base = bases[startIndex + i];
      setBase((i * BITS_PER_BASE) / BITS_PER_INT, (i * BITS_PER_BASE) % BITS_PER_INT, encode(base));
    }
  }

  /**
   * Set a single encoded base.
   *
   * @param arrayIndex  the index in the array of the base.
   * @param bitIndex    the index of the starting bit of the base in the array.
   * @param encodedBase the encoded base.
   */
  private void setBase(int arrayIndex, int bitIndex, int encodedBase) {
    if (bitIndex <= BITS_PER_INT - BITS_PER_BASE) {
      bases[arrayIndex] += encodedBase << bitIndex;
    } else {
      int baseCopy = encodedBase;
      for (int i = bitIndex; i < BITS_PER_INT; i++) {
        bases[arrayIndex] += (baseCopy & 1) << i;
        baseCopy >>= 1;
      }
      bases[arrayIndex + 1] = baseCopy;
    }
  }

  /**
   * Encode a base.
   *
   * @param base the base to encode.
   * @return the encoded base.
   */
  private int encode(char base) {
    switch (base) {
      case 'C':
        return BASE_C;
      case 'G':
        return BASE_G;
      case 'A':
        return BASE_A;
      case 'T':
        return BASE_T;
      case 'N':
        return BASE_UNKNOWN;
      default:
        throw new AssertionError();
    }
  }

  /**
   * Decode a base.
   *
   * @param encodedBase the encoded base to decode.
   * @return the decoded base.
   */
  private char decode(int encodedBase) {
    switch (encodedBase) {
      case BASE_C:
        return 'C';
      case BASE_G:
        return 'G';
      case BASE_A:
        return 'A';
      case BASE_T:
        return 'T';
      case BASE_UNKNOWN:
        return 'N';
      default:
        throw new AssertionError();
    }
  }

  /**
   * Get the amount of bases in this sequence.
   *
   * @return the amount of bases in this sequence.
   */
  public int size() {
    return amountOfBases;
  }
}
