package org.marasm.s3m.loader;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.marasm.s3m.Configuration;
import org.marasm.s3m.api.S3MNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@AllArgsConstructor
public class JarLoader {
    private ClassResolver classResolver;

    @SneakyThrows
    public void loadjar(String name) {
        if (name.startsWith("/")) {
            loadJarAt(name);
        } else if (name.startsWith("./")) {
            loadJarAt(Configuration.CURRENT_DIR + File.pathSeparator + name.substring(2));
        } else {
            String libsPath = Configuration.LIB_FOLDER + File.pathSeparator + name;
            File parent = new File(libsPath).getParentFile();
            if (parent.exists()) {
                loadJarAt(libsPath);
            }
            throw new FileNotFoundException(name + " not found");
        }
    }

    private void loadJarAt(String path) {
        try {
            File file = new File(path);
            JarFile jar = null;
            jar = new JarFile(path);
            Enumeration<JarEntry> entries = jar.entries();
            URL jarfile = new URL("jar", "", "file:" + file.getAbsolutePath() + "!/");
            URLClassLoader cl = URLClassLoader.newInstance(new URL[]{jarfile});
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory() || !entry.getName().endsWith(".class")) {
                    continue;
                }
                String className = entry.getName().substring(0, entry.getName().length() - 6);
                className = className.replace('/', '.');
                Class c = cl.loadClass(className);
                if (S3MNode.class.isAssignableFrom(c)) {
                    classResolver.registerClass(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
