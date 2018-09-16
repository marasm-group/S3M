package org.marasm.s3m.api.nodes;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.marasm.s3m.api.S3MNode;
import org.marasm.s3m.api.S3MQueue;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@ToString
@AllArgsConstructor
public abstract class UnaryS3MNode implements S3MNode {

    private S3MQueue inputQueue;
    private S3MQueue outputQueue;

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
        return Collections.singletonList(getOutputQueue());
    }

    @Override
    public void setOutputQueues(List<S3MQueue> outputQueues) {
        if (outputQueues.size() > 1) {
            throw new IllegalArgumentException("Too many output queues assigned to " + getClass().getName());
        }
        setOutputQueue(outputQueues.get(0));
    }

    @Override
    public List<Serializable> process(List<Serializable> input) {
        return Collections.singletonList(process(input.get(0)));
    }

    public S3MQueue getInputQueue() {
        return inputQueue;
    }

    public void setInputQueue(S3MQueue inputQueue) {
        this.inputQueue = inputQueue;
    }

    public S3MQueue getOutputQueue() {
        return outputQueue;
    }

    public void setOutputQueue(S3MQueue outputQueue) {
        this.outputQueue = outputQueue;
    }

    public abstract Serializable process(Serializable input);

}
