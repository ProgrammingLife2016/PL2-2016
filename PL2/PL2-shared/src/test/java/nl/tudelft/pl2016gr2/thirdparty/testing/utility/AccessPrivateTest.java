package nl.tudelft.pl2016gr2.thirdparty.testing.utility;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * This class tests if the test utility classes work correctly.
 *
 * @author faris
 */
public class AccessPrivateTest {

  /**
   * Test call private method.
   */
  @Test
  public void privateMethodTest() {
    PrivateMemberClass instance = new PrivateMemberClass();
    int ret = AccessPrivate.callMethod("m_getNumber", PrivateMemberClass.class, instance);
    assertEquals(5, ret);
  }

  /**
   * Test call static private method.
   */
  @Test
  public void privateStaticMethodTest() {
    int ret = AccessPrivate.callMethod("m_getNumberStatic", PrivateMemberClass.class, null);
    assertEquals(6, ret);
  }

  /**
   * Test call method with parameter.
   */
  @Test
  public void privateStaticMethodWithParameterTest() {
    int ret = AccessPrivate.callMethod("m_returnNumber", PrivateMemberClass.class, null, 7, 8);
    assertEquals(15, ret);
  }

  /**
   * Test get private field.
   */
  @Test
  public void privateFieldAccessTest() {
    PrivateMemberClass instance = new PrivateMemberClass();
    int ret = AccessPrivate.getFieldValue("f_testVar", PrivateMemberClass.class, instance);
    assertEquals(456, ret);
  }

  /**
   * Test get private static field.
   */
  @Test
  public void privateStaticFieldAccessTest() {
    int ret = AccessPrivate.getFieldValue("f_staticTestVar", PrivateMemberClass.class, null);
    assertEquals(234, ret);
  }

  /**
   * Test set private field.
   */
  @Test
  public void setPrivateFieldTest() {
    PrivateMemberClass instance = new PrivateMemberClass();
    String privateChangebleVar = "f_changeableTestVar";
    AccessPrivate.setFieldValue(privateChangebleVar,
        PrivateMemberClass.class, instance, -1);
    assertEquals(-1, (int) AccessPrivate.getFieldValue(privateChangebleVar,
        PrivateMemberClass.class, instance));
  }

  /**
   * Test set private static field.
   */
  @Test
  public void setPrivateStaticFieldTest() {
    String privateChangebleVar = "f_changeableStaticTestVar";
    AccessPrivate.setFieldValue(privateChangebleVar,
        PrivateMemberClass.class, null, -2);
    assertEquals(-2, (int) AccessPrivate.getFieldValue(privateChangebleVar,
        PrivateMemberClass.class, null));
  }

  /**
   * Test correct exception when trying to set private final field.
   */
  @Test(expected = TestAnnotationException.class)
  public void setPrivateFinalFieldTest() {
    AccessPrivate.setFieldValue("f_testVar", PrivateMemberClass.class, null, -6);
  }

  /**
   * Test correct exception when asking for non-existent id.
   */
  @Test(expected = TestAnnotationException.class)
  public void annotationExceptionTest() {
    AccessPrivate.getFieldValue("NON_EXISTING_FIELD", PrivateMemberClass.class, null);
  }
}
