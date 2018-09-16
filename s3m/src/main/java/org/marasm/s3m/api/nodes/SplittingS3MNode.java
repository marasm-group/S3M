package org.marasm.s3m.api.nodes;

import org.marasm.s3m.api.S3MNode;
import org.marasm.s3m.api.S3MQueue;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public abstract class SplittingS3MNode implements S3MNode {

    private S3MQueue inputQueue;
    private List<S3MQueue> outputQueues;

    @Override

    public List<S3MQueue> getInputQueues() {
        return Collections.singletonList(getInputQueue());
    }

    @Override
    public void setInputQueues(List<S3MQueue> inputQueues) {
        if (inputQueues.size() > 1) {
            throw new IllegalArgumentException("Too many input queues assigned to " + getClass().getName());
        }
        setInputQueue(inputQueues.get(0));
    }

    @Override
    public List<S3MQueue> getOutputQueues() {
        return outputQueues;
    }

    @Override
    public void setOutputQueues(List<S3MQueue> outputQueues) {
        if (outputQueues.size() > 2) {
            throw new IllegalArgumentException("Too many output queues assigned to " + getClass().getName());
        }
        this.outputQueues = outputQueues;
    }

    @Override
    public List<Serializable> process(List<Serializable> input) {
        return process(input.get(0));
    }

    public S3MQueue getInputQueue() {
        return inputQueue;
    }

    public void setInputQueue(S3MQueue inputQueue) {
        this.inputQueue = inputQueue;
    }

    public abstract List<Serializable> process(Serializable input);
}
