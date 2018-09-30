package org.marasm.s3m.loader;

import lombok.SneakyThrows;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class ClassResolver {

    private Map<String, ClassLoader> classLoader = new HashMap<>();

    public ClassResolver() {
        classLoader.put(null, ClassLoader.getSystemClassLoader());
    }

    public Class get(String name) {
        return get(null, name);
    }

    @SneakyThrows
    public Class get(String jar, String name) {
        ClassLoader classLoader = this.classLoader.computeIfAbsent(jar, this::getClassLoader);
        return Class.forName(name, true, classLoader);
    }

    @SneakyThrows
    private ClassLoader getClassLoader(String jar) {
        return new URLClassLoader(new URL[]{new File(jar).toURL()});
    }
}
