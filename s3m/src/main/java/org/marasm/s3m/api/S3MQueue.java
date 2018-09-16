package org.marasm.s3m.api;

public interface S3MQueue {

    String getName();

    int getId();

    Class getMessageClass();

}
