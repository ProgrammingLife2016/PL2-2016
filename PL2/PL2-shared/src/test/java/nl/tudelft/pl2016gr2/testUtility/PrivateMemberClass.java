package nl.tudelft.pl2016gr2.testUtility;

/**
 * This class is for testing if we are correctly able to access it's private members.
 *
 * @author Faris
 */
public class PrivateMemberClass {

    @TestId(id = "f_testVar")
    private final int testVar = 456;
    @TestId(id = "f_staticTestVar")
    private final static int staticTestVar = 234;
    @TestId(id = "f_changeableTestVar")
    private int changeableTestVar = 0;
    @TestId(id = "f_changeableStaticTestVar")
    private static int changeableStaticTestVar = 0;

    /**
     * A private method whitch returns 5.
     *
     * @return the number 5.
     */
    @TestId(id = "m_getNumber")
    private int getNumber() {
        return 5;
    }

    /**
     * A private static method whitch returns 6.
     *
     * @return the number 6.
     */
    @TestId(id = "m_getNumberStatic")
    private static int getNumberStatic() {
        return 6;
    }

    /**
     * a private static method which returns the first + second parameter.
     *
     * @param number1 a number.
     * @param number2 a number.
     * @return the first + second parameter.
     */
    @TestId(id = "m_returnNumber")
    private static int returnNumber(int number1, int number2) {
        return number1 + number2;
    }
}
