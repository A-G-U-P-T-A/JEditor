package org.node.model;

import java.lang.reflect.*;
import java.util.*;
import java.io.File;
import java.net.URL;
import java.util.stream.Collectors;

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

    public static Node createNodeFromClass(Class<?> cls) {
        Node node = new Node(cls.getSimpleName(), Node.NodeType.FUNCTION, null);
        
        // Constructor pins
        Constructor<?>[] constructors = cls.getConstructors();
        if (constructors.length > 0) {
            // Use first public constructor
            Constructor<?> constructor = constructors[0];
            node.addInputPin(new Pin("new", Pin.PinType.FLOW, "Create", true));
            for (Parameter param : constructor.getParameters()) {
                node.addInputPin(new Pin(
                    param.getName(),
                    convertTypeToPinType(param.getType()),
                    param.getName(),
                    true
                ));
            }
            node.addOutputPin(new Pin("instance", Pin.PinType.OBJECT, cls.getSimpleName(), false));
        }

        // Method pins
        for (Method method : getRelevantMethods(cls)) {
            if (!method.getName().startsWith("get") && !method.getName().startsWith("set")) {
                node.addInputPin(new Pin(
                    method.getName() + "_exec",
                    Pin.PinType.FLOW,
                    method.getName(),
                    true
                ));
                
                // Method parameters as input pins
                for (Parameter param : method.getParameters()) {
                    node.addInputPin(new Pin(
                        method.getName() + "_" + param.getName(),
                        convertTypeToPinType(param.getType()),
                        param.getName(),
                        true
                    ));
                }

                // Return value as output pin
                if (method.getReturnType() != void.class) {
                    node.addOutputPin(new Pin(
                        method.getName() + "_return",
                        convertTypeToPinType(method.getReturnType()),
                        "Return",
                        false
                    ));
                }
            }
        }

        // Field pins
        for (Field field : getRelevantFields(cls)) {
            if (!Modifier.isFinal(field.getModifiers())) {
                node.addInputPin(new Pin(
                    "set_" + field.getName(),
                    convertTypeToPinType(field.getType()),
                    field.getName(),
                    true
                ));
            }
            node.addOutputPin(new Pin(
                "get_" + field.getName(),
                convertTypeToPinType(field.getType()),
                field.getName(),
                false
            ));
        }

        return node;
    }
}
