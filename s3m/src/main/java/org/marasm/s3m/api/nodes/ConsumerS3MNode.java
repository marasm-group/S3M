package org.marasm.s3m.api.nodes;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.marasm.s3m.api.S3MNode;
import org.marasm.s3m.api.S3MQueue;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class ConsumerS3MNode implements S3MNode {

    private List<S3MQueue> inputQueues;
    @NonNull
    private Consumer<List<Serializable>> consumer;

    @Override
    public List<S3MQueue> getInputQueues() {
        return inputQueues;
    }

    @Override
    public void setInputQueues(List<S3MQueue> inputQueues) {
        this.inputQueues = inputQueues;
    }

    @Override
    public List<S3MQueue> getOutputQueues() {
        return null;
    }

    @Override
    public void setOutputQueues(List<S3MQueue> outputQueues) {
        throw new UnsupportedOperationException(getClass().getName() + " does not support output");
    }

    @Override
    public List<Serializable> process(List<Serializable> input) throws Exception {
        consumer.accept(input);
        return null;
    }
}
