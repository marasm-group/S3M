package org.marasm.s3m;

import java.io.File;

public class Configuration {
    public static String HOME = System.getenv("S3M_HOME");
    public static String CURRENT_DIR = System.getProperty("user.dir");

    public static String LIB_FOLDER = HOME + File.pathSeparator + "lib";
}
