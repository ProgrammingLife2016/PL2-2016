package nl.tudelft.pl2016gr2.test.utility;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Access a private method or field which is annotated with the {@link TestId} annotation.
 * Uniqueness of a requested test ID will always be checked whenever a method or field is accessed.
 * It is possible to access non-private (for example protected) fields/methods too, although this
 * isn't the intended purpose of this class. <br>
 * If the requested ID is not present in the specified class, or if the ID is not unique then a
 * {@link TestAnnotationException} is thrown.
 *
 * @author Faris
 */
public final class AccessPrivate {

  /**
   * Don't let anyone instantiate this class.
   */
  private AccessPrivate() {
  }

  /**
   * Call the method with the specified id of the specified object.
   *
   * @param id     the annotated id of the method.
   * @param cl     the class of the method.
   * @param obj    the object the method is called on. Can be null if the method is static.
   * @param params the parameters of the method.
   * @return the returned value of the method.
   */
  public static Object callMethod(String id, Class cl, Object obj,
      Object... params) {
    Method method = getMethod(cl, id);
    try {
      method.setAccessible(true);
      Object res = method.invoke(obj, params);
      method.setAccessible(false);
      return res;
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      Logger.getLogger(
          AccessPrivate.class.getName()).log(Level.SEVERE, null, ex);
    }
    throw new TestAnnotationException("An error occured while accessing the"
        + " method with test id: " + id + ".");
  }

  /**
   * Get the method with the specified id and assure the id is unique.
   *
   * @param cl the class of the method.
   * @param id the id of the method.
   * @return the method with the specified id.
   */
  private static Method getMethod(Class cl, String id) {
    Method foundMethod = null;
    int foundMethods = 0;

    for (Method method : cl.getDeclaredMethods()) {
      if (checkAnnotated(method.getAnnotationsByType(TestId.class), id)) {
        foundMethod = method;
        ++foundMethods;
      }
    }
    if (foundMethods != 1) {
      throw createUniquenessException(cl, id, foundMethods);
    }
    return foundMethod;
  }

  /**
   * Get the value of the specified field of the specified object.
   *
   * @param id  the annotated id of the field.
   * @param cl  the class of the field.
   * @param obj the object which contains the field. Can be null if the field is static.
   * @return the returned value of the field.
   */
  public static Object getFieldValue(String id, Class cl, Object obj) {
    try {
      Field field = AccessPrivate.getField(cl, id);
      field.setAccessible(true);
      Object res = field.get(obj);
      field.setAccessible(false);
      return res;
    } catch (IllegalArgumentException | IllegalAccessException ex) {
      Logger.getLogger(AccessPrivate.class.getName()).log(Level.SEVERE,
          null, ex);
    }
    return null;
  }

  /**
   * Set the value of the specified field of the specified object.
   *
   * @param id    the annotated id of the field.
   * @param cl    the class of the field.
   * @param obj   the object which contains the field. Can be null if the field is static.
   * @param value the new value of the field.
   */
  public static void setFieldValue(String id, Class cl, Object obj,
      Object value) {
    try {
      Field field = AccessPrivate.getField(cl, id);
      if (Modifier.isFinal(field.getModifiers())) {
        throw new TestAnnotationException("Trying to set the value of "
            + "the final field with id: " + id + ".");
      }
      field.setAccessible(true);
      field.set(obj, value);
      field.setAccessible(false);
    } catch (IllegalArgumentException | IllegalAccessException ex) {
      Logger.getLogger(AccessPrivate.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Get the field of the specified object. Also assure that the found field is unique.
   *
   * @param cl the class of the field.
   * @param id the annotated id of the field.
   * @return the returned value of the field.
   */
  private static Field getField(Class cl, String id) {
    Field foundField = null;
    int foundFields = 0;

    for (Field field : cl.getDeclaredFields()) {
      if (checkAnnotated(field.getAnnotationsByType(TestId.class), id)) {
        foundField = field;
        ++foundFields;
      }
    }
    if (foundFields != 1) {
      throw createUniquenessException(cl, id, foundFields);
    }
    return foundField;
  }

  /**
   * Create an exception according to the amount of times an id was found (either the id was not
   * found, or the id was found too many times).
   *
   * @param id    the id.
   * @param found the amount of times the id was found.
   * @return an appropriate exception.
   */
  private static TestAnnotationException createUniquenessException(Class cl,
      String id,
      int found) {
    assert found != 1;
    switch (found) {
      case 0:
        return new TestAnnotationException("The requested id: " + id
            + " was not found inclass: " + cl.toString() + ".");
      default:
        return new TestAnnotationException("The requested id: " + id
            + " is not unique. It was found " + found
            + " times in class: " + cl.toString() + ".");
    }
  }

  /**
   * Check if the list of annotations contains a TestId annotation with the right id.
   *
   * @param annotations the annotations.
   * @param id          the id of the test annotation.
   * @return if the list of annotations contains a TestId annotation with the right id.
   */
  private static boolean checkAnnotated(Annotation[] annotations, String id) {
    for (Annotation annotation : annotations) {
      if (annotation.annotationType().equals(TestId.class)) {
        return ((TestId) annotation).id().equals(id);
      }
    }
    return false;
  }
}
