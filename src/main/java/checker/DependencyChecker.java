package checker;

import java.io.File;
import java.util.*;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class DependencyChecker {


    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Usage: <main-class> <jar-path1> ... <jar-pathN>");
            System.exit(1);
        }

        String mainClassName = args[0];
        List<String> jarPaths = List.of(args).subList(1, args.length);

        try {
            boolean allDependenciesFound = checkDependencies(mainClassName, jarPaths);
            if (allDependenciesFound) {
                System.out.println("All dependencies were found.");
            } else {
                System.out.println("Missing dependencies detected.");
            }
        } catch (Exception e) {
            System.out.println("Error during dependency checking: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean checkDependencies(String mainClassName, List<String> jarPaths) throws NotFoundException {
        ClassPool pool = new ClassPool();
        Set<String> checkedClasses = new HashSet<>();
        LinkedList<String> classQueue = new LinkedList<>();

        addJarFilesToPool(jarPaths, pool);

        classQueue.add(mainClassName);

        while (!classQueue.isEmpty()) {
            String className = classQueue.poll();
            if (!containsClassDependency(className, checkedClasses, classQueue, pool)) {
                return false;
            }
        }

        return true;
    }


    public static boolean containsClassDependency(String className, Set<String> checkedClasses, LinkedList<String> classQueue, ClassPool pool) {
        if (!needToCheckClassDependency(className, checkedClasses)) {
            return true;
        }
        checkedClasses.add(className);
        try {
            CtClass ctClass = pool.get(className);
            String[] dependencies = ctClass.getRefClasses().toArray(new String[0]);

            for (String dependency : dependencies) {
                if (needToCheckClassDependency(dependency, checkedClasses)) {
                    classQueue.add(dependency);
                }
            }
        } catch (NotFoundException e) {
            System.out.println("Missing dependency: " + className);
            return false;
        }
        return true;
    }

    public static void addJarFilesToPool(List<String> jarPaths, ClassPool pool) throws NotFoundException {
        for (String jarPath : jarPaths) {
            File jarFile = new File(jarPath);
            if (jarFile.exists() && jarFile.isFile()) {
                pool.appendClassPath(jarPath);
            } else {
                System.out.println("JAR file not found: " + jarPath);
            }
        }
    }

    public static boolean needToCheckClassDependency(String className, Set<String> checkedClasses) {
        return !checkedClasses.contains(className) && !isStandardLibraryClass(className);
    }

    public static boolean isStandardLibraryClass(String className) {
        return className.startsWith("java.") || className.startsWith("javax.")
                || className.startsWith("jdk.") || className.startsWith("sun.");
    }


}