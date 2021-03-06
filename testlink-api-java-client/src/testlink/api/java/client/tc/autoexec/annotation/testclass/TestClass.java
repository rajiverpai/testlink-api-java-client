/*
 * Used same structure as JUnit
 *
 * This class was taken from JUnit and modified to breakdown the class
 * in a way needed for processing TestLink annotated class. How the methods
 * are used for running test is very different between JUnit and the
 * Test Plan Prepare Interface.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package testlink.api.java.client.tc.autoexec.annotation.testclass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Wraps a class to be run, providing method validation and annotation searching
 */
public class TestClass {
	private final Class<?> fClass;

	private Map<Class<?>, List<TestClassMethod>> fMethodsForAnnotations= new HashMap<Class<?>, List<TestClassMethod>>();
	private Map<Class<?>, List<TestClassField>> fFieldsForAnnotations= new HashMap<Class<?>, List<TestClassField>>();

	/**
	 * Creates a {@code TestClass} wrapping {@code klass}. Each time this
	 * constructor executes, the class is scanned for annotations, which can be
	 * an expensive process (we hope in future JDK's it will not be.) Therefore,
	 * try to share instances of {@code TestClass} where possible.
	 */
	public TestClass(Class<?> klass) {
		fClass= klass;
		if (klass != null && klass.getConstructors().length > 1)
			throw new IllegalArgumentException(
					"Test class can only have one constructor");

		for (Class<?> eachClass : getSuperClasses(fClass)) {
			for (Method eachMethod : eachClass.getDeclaredMethods())
				addToAnnotationLists(new TestClassMethod(eachMethod), fMethodsForAnnotations);
			for (Field eachField : eachClass.getDeclaredFields())
				addToAnnotationLists(new TestClassField(eachField), fFieldsForAnnotations);
		}
	}

	private void addToAnnotationLists(TestClassMember member, Map map) {
		for (Annotation each : member.getAnnotations()) {
			Class<? extends Annotation> type= each.annotationType();
			List<TestClassMember> members= getAnnotatedMembers(map, type);
			if (member.isShadowedBy(members))
				return;
			if (runsTopToBottom(type))
				members.add(0, member);
			else
				members.add(member);
		}
	}

	/**
	 * Returns, efficiently, all the non-overridden methods in this class and
	 * its superclasses that are annotated with {@code annotationClass}.
	 */
	public List<TestClassMethod> getAnnotatedMethods(
			Class<? extends Annotation> annotationClass) {
		return getAnnotatedMembers(fMethodsForAnnotations, annotationClass);
	}

	/**
	 * Returns, efficiently, all the non-overridden fields in this class and
	 * its superclasses that are annotated with {@code annotationClass}.
	 */
	public List<TestClassField> getAnnotatedFields(Class<? extends Annotation> annotationClass) {
		return getAnnotatedMembers(fFieldsForAnnotations, annotationClass);
	}

	private <T> List<T> getAnnotatedMembers(Map<Class<?>, List<T>> map,
			Class<? extends Annotation> type) {
		if (!map.containsKey(type))
			map.put(type, new ArrayList<T>());
		return map.get(type);
	}

	private boolean runsTopToBottom(Class<? extends Annotation> annotation) {
		return annotation.equals("Before.class")
				|| annotation.equals("BeforeClass.class");
	}

	private List<Class<?>> getSuperClasses(Class<?> testClass) {
		ArrayList<Class<?>> results= new ArrayList<Class<?>>();
		Class<?> current= testClass;
		while (current != null) {
			results.add(current);
			current= current.getSuperclass();
		}
		return results;
	}

	/**
	 * Returns the underlying Java class.
	 */
	public Class<?> getJavaClass() {
		return fClass;
	}

	/**
	 * Returns the class's name.
	 */
	public String getName() {
		if (fClass == null)
			return "null";
		return fClass.getName();
	}

	/**
	 * Returns the annotations on this class
	 */
	public Annotation[] getAnnotations() {
		if (fClass == null)
			return new Annotation[0];
		return fClass.getAnnotations();
	}
}
