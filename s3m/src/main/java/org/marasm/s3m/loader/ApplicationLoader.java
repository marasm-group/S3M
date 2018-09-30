package org.marasm.s3m.loader;

import org.marasm.s3m.loader.application.ApplicationDescriptor;

import java.io.InputStream;
import java.io.OutputStream;

public interface ApplicationLoader {
    ApplicationDescriptor loadApp(InputStream input) throws Exception;

    void saveApp(OutputStream out, ApplicationDescriptor ad) throws Exception;
}
