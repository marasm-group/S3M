package org.marasm.s3m.api;

import java.io.Serializable;
import java.util.List;

public interface S3MNode {

    List<S3MQueue> getInputQueues();

    void setInputQueues(List<S3MQueue> inputQueues);

    List<S3MQueue> getOutputQueues();

    void setOutputQueues(List<S3MQueue> outputQueues);

    List<Serializable> process(List<Serializable> input) throws Exception;

}
