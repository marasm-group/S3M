package org.marasm.s3m;

import org.marasm.s3m.api_implementation.queues.S3MQueueConnector;
import org.marasm.s3m.api_implementation.queues.local.LocalS3MQueueConnector;

import java.io.File;

public class Configuration {
    public static String HOME = System.getenv("S3M_HOME");
    public static String CURRENT_DIR = System.getProperty("user.dir");

    public static String LIB_FOLDER = HOME + File.pathSeparator + "lib";

    private static Class<? extends S3MQueueConnector> defaultConnector = LocalS3MQueueConnector.class;
}
