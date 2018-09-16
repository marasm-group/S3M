package org.marasm.s3m.api.nodes;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.marasm.s3m.api.S3MNode;
import org.marasm.s3m.api.S3MQueue;

import java.io.Serializable;
import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class SupplierS3MNode implements S3MNode {

    private List<S3MQueue> ouputQueues;
    @NonNull
    private Supplier<List<Serializable>> supplier;

    @Override
    public List<S3MQueue> getInputQueues() {
        return null;
    }

    @Override
    public void setInputQueues(List<S3MQueue> inputQueues) {
        throw new UnsupportedOperationException(getClass().getName() + " does not support input");
    }

    @Override
    public List<S3MQueue> getOutputQueues() {
        return ouputQueues;
    }

    @Override
    public void setOutputQueues(List<S3MQueue> outputQueues) {
        this.ouputQueues = outputQueues;
    }

    @Override
    public List<Serializable> process(List<Serializable> input) throws Exception {
        return supplier.get();
    }

}
