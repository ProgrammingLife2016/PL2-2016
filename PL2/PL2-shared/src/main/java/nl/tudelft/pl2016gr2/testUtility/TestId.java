package nl.tudelft.pl2016gr2.testUtility;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which makes it possible to give private methods and fields an ID
 * so they can be accessed by test classes using the AccessPrivate class.
 *
 * @author Faris
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface TestId {

	/**
	 * The ID of the field or method.
	 *
	 * @return the ID.
	 */
	public String id();
}
