package checker;

import javassist.NotFoundException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static checker.DependencyChecker.checkDependencies;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DependencyCheckerTest {
    @Test
    public void ClassBWithoutDependencyTest() throws NotFoundException {
        String mainClassName = "com.jetbrains.internship2024.ClassB";
        List<String> jars = getJarsList("ModuleB-1.0.jar");
        boolean result = checkDependencies(mainClassName, jars);
        assertFalse(result);
    }

    @Test
    public void ClassBWithDependencyTest() throws NotFoundException {
        String mainClassName = "com.jetbrains.internship2024.ClassB";
        List<String> jars = getJarsList("ModuleA-1.0.jar", "ModuleB-1.0.jar");
        boolean result = checkDependencies(mainClassName, jars);
        assertTrue(result);
    }

    @Test
    public void ClassATest() throws NotFoundException {
        String mainClassName = "com.jetbrains.internship2024.ClassA";
        List<String> jars = getJarsList("ModuleA-1.0.jar");
        boolean result = checkDependencies(mainClassName, jars);
        assertTrue(result);
    }

    @Test
    public void SomeAnotherClassWithoutDependencyTest() throws NotFoundException {
        String mainClassName = "com.jetbrains.internship2024.SomeAnotherClass";
        List<String> jars = getJarsList("ModuleA-1.0.jar");
        boolean result = checkDependencies(mainClassName, jars);
        assertFalse(result);
    }

    @Test
    public void SomeAnotherClassWithDependencyTest() throws NotFoundException {
        String mainClassName = "com.jetbrains.internship2024.SomeAnotherClass";
        List<String> jars = getJarsList("ModuleA-1.0.jar", "commons-io-2.16.1.jar");
        boolean result = checkDependencies(mainClassName, jars);
        assertTrue(result);
    }

    @Test
    public void ClassB1Test() throws NotFoundException {
        String mainClassName = "com.jetbrains.internship2024.ClassB1";
        List<String> jars = getJarsList("ModuleB-1.0.jar");
        boolean result = checkDependencies(mainClassName, jars);
        assertTrue(result);
    }

    private List<String> getJarsList(String... jars) {
        return Stream.of(jars).map(str -> "src/test/java/checker/jars/" + str).toList();
    }
}
