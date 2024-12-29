package org.node.model;

import java.lang.reflect.*;
import java.util.*;
import java.io.File;
import java.net.URL;
import java.util.stream.Collectors;
import javafx.geometry.Point2D;

public class ClassScanner {
    private static final Set<String> EXCLUDED_PACKAGES = Set.of(
        "sun.", "com.sun.", "java.awt", "javax.swing"
    );

    public static List<Class<?>> scanJavaUtilClasses() {
        List<Class<?>> classes = new ArrayList<>();
        try {
            // Get the java.util package URL
            String packageName = "java.util";
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);

            // Get built-in java.util classes
            classes.addAll(Arrays.asList(
                ArrayList.class, LinkedList.class, HashMap.class, 
                HashSet.class, TreeMap.class, TreeSet.class,
                Stack.class, Vector.class, Properties.class,
                Collections.class, Arrays.class, List.class,
                Map.class, Set.class, Queue.class
            ));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes.stream()
            .filter(cls -> !cls.isAnonymousClass())
            .filter(cls -> !Modifier.isPrivate(cls.getModifiers()))
            .filter(cls -> !cls.getName().contains("$"))
            .collect(Collectors.toList());
    }

    public static List<Method> getRelevantMethods(Class<?> cls) {
        return Arrays.stream(cls.getDeclaredMethods())
            .filter(method -> !method.isSynthetic())
            .filter(method -> !Modifier.isPrivate(method.getModifiers()))
            .filter(method -> !method.getName().contains("$"))
            .collect(Collectors.toList());
    }

    public static List<Field> getRelevantFields(Class<?> cls) {
        return Arrays.stream(cls.getDeclaredFields())
            .filter(field -> !field.isSynthetic())
            .filter(field -> !Modifier.isPrivate(field.getModifiers()))
            .filter(field -> !field.getName().contains("$"))
            .collect(Collectors.toList());
    }

    public static Pin.PinType convertTypeToPinType(Class<?> type) {
        if (type == boolean.class || type == Boolean.class) {
            return Pin.PinType.BOOLEAN;
        } else if (type == int.class || type == long.class || type == float.class || 
                   type == double.class || type == byte.class || type == short.class ||
                   Number.class.isAssignableFrom(type)) {
            return Pin.PinType.NUMBER;
        } else if (type == String.class || type == char.class || type == Character.class) {
            return Pin.PinType.STRING;
        } else if (type.isArray() || Collection.class.isAssignableFrom(type)) {
            return Pin.PinType.ARRAY;
        } else {
            return Pin.PinType.OBJECT;
        }
    }

    public static Node createMethodNode(Method method, Point2D position) {
        String methodName = method.getName();
        Class<?> cls = method.getDeclaringClass();
        Node node = new Node(methodName, Node.NodeType.FUNCTION, position);
        
        // Add execution input pin
        node.addInputPin(new Pin("exec_in", Pin.PinType.FLOW, "Exec", true));
        
        // Add 'this' pin for instance methods
        if (!Modifier.isStatic(method.getModifiers())) {
            node.addInputPin(new Pin("this", Pin.PinType.OBJECT, cls.getSimpleName(), true));
        }
        
        // Add parameter pins
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            node.addInputPin(new Pin(
                param.getName() != null ? param.getName() : "arg" + i,
                convertTypeToPinType(param.getType()),
                param.getType().getSimpleName(),
                true
            ));
        }
        
        // Add execution output pin
        node.addOutputPin(new Pin("exec_out", Pin.PinType.FLOW, "Exec", false));
        
        // Add return value pin if not void
        if (method.getReturnType() != void.class) {
            node.addOutputPin(new Pin(
                "return",
                convertTypeToPinType(method.getReturnType()),
                method.getReturnType().getSimpleName(),
                false
            ));
        }
        
        return node;
    }

    public static Node createConstructorNode(Constructor<?> constructor, Point2D position) {
        Class<?> cls = constructor.getDeclaringClass();
        Node node = new Node("Create " + cls.getSimpleName(), Node.NodeType.FUNCTION, position);
        
        // Add execution input pin
        node.addInputPin(new Pin("exec_in", Pin.PinType.FLOW, "Exec", true));
        
        // Add parameter pins
        Parameter[] parameters = constructor.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            node.addInputPin(new Pin(
                param.getName() != null ? param.getName() : "arg" + i,
                convertTypeToPinType(param.getType()),
                param.getType().getSimpleName(),
                true
            ));
        }
        
        // Add execution output pin
        node.addOutputPin(new Pin("exec_out", Pin.PinType.FLOW, "Exec", false));
        
        // Add instance output pin
        node.addOutputPin(new Pin("instance", Pin.PinType.OBJECT, cls.getSimpleName(), false));
        
        return node;
    }
}
