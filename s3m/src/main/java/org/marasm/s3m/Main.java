package org.marasm.s3m;

import org.marasm.s3m.api_implementation.queues.MqServerConfig;
import org.marasm.s3m.loader.ApplicationLoader;
import org.marasm.s3m.loader.ApplicationRunner;
import org.marasm.s3m.loader.application.ApplicationDescriptor;
import org.marasm.s3m.loader.xml.XmlApplicationLoader;

import java.io.FileInputStream;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("S3M executable expected as argument....");
            System.exit(-1);
        }
        ApplicationLoader loader = new XmlApplicationLoader();
        ApplicationDescriptor applicationDescriptor = loader.loadApp(new FileInputStream(args[0]));
        ApplicationRunner applicationRunner = new ApplicationRunner();
        applicationRunner.run(applicationDescriptor, MqServerConfig.builder().host("localhost").build());
    }
}