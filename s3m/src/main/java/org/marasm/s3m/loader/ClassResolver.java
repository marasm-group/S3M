package org.marasm.s3m.loader;

import lombok.SneakyThrows;
import org.marasm.s3m.api.S3MNode;
import org.marasm.s3m.loader.application.NodeDescriptor;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class ClassResolver {

    static Map<String, Class<S3MNode>> aliases = new HashMap<>();
    private Map<String, ClassLoader> classLoaders = new HashMap<>();

    private JarLoader jarLoader;

    public ClassResolver() {
        classLoaders.put(null, ClassLoader.getSystemClassLoader());
        jarLoader = new JarLoader(this);
    }

    public Class<S3MNode> get(NodeDescriptor nd) {
        loadClassLoader(nd.getJar());
        return get(nd.getJar(), nd.getAClass());
    }

    @SneakyThrows
    public Class get(String jar, String name) {
        ClassLoader classLoader = loadClassLoader(jar);
        return Class.forName(name, true, classLoader);
    }

    private ClassLoader loadClassLoader(String jar) {
        ClassLoader classLoader = classLoaders.computeIfAbsent(jar, this::getClassLoader);
        return classLoader;
    }

    @SneakyThrows
    private ClassLoader getClassLoader(String jar) {
        return new URLClassLoader(new URL[]{new File(jar).toURL()});
    }

    @SneakyThrows
    public void registerClass(Class<S3MNode> clazz) {
        String alias = clazz.newInstance().alias();
        if (alias != null) {
            aliases.put(alias, clazz);
        }
    }
}
