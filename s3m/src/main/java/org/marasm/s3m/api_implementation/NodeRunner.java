package org.marasm.s3m.api_implementation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.marasm.s3m.api.S3MNode;
import org.marasm.s3m.api.S3MQueue;
import org.marasm.s3m.api.serialization.S3MSerializer;
import org.marasm.s3m.api_implementation.queues.S3MQueueConnector;
import org.marasm.s3m.api_implementation.serialization.S3MJsonSerializer;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
public class NodeRunner {

    @Builder.Default
    public boolean run = true;
    private S3MNode node;
    @Builder.Default
    private List<S3MQueue> inputQueues = new ArrayList<>();
    private S3MQueueConnector inputQueuesConnector;
    @Builder.Default
    private List<S3MQueue> outputQueues = new ArrayList<>();
    private S3MQueueConnector outputQueuesConnector;
    private S3MQueue errorQueue;
    private S3MQueueConnector errorQueueConnector;
    @Builder.Default
    private S3MSerializer serializer = new S3MJsonSerializer();
    @Builder.Default
    private int pollingInterval = 0;
    @Builder.Default
    private long throttlingQueueSize = 100; //TODO: find the best default value
    @Builder.Default
    private long throttlingDelay = 100; //TODO: find the best default value
    @Builder.Default
    private boolean throttling = true;
    private Set<String> inputQueueNames = Collections.emptySet();

    public void runLoop() {
        inputQueueNames = inputQueues.stream().map(S3MQueue::getName).collect(Collectors.toSet());
        while (run) {
            ArrayList<byte[]> params = new ArrayList<>(inputQueues.size());
            try {
                runLoopIteration(params);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                try {
                    errorQueueConnector.put(errorQueue, createErrorObject(node, throwable, params));
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private byte[] createErrorObject(S3MNode node, Throwable throwable, ArrayList<byte[]> params) {
        String nodeClass = node.getClass().getCanonicalName();
        List<String> inputQueuesNames = new ArrayList<>(inputQueues.size());
        List<Serializable> inputParams = new ArrayList<>(params.size());
        for (int i = 0; i < inputQueues.size(); i++) {
            S3MQueue queue = inputQueues.get(i);
            inputQueuesNames.add(queue.getName() + ":" + queue.getId());
            if (i < params.size()) {
                byte[] data = params.get(i);
                inputParams.add(new String(data));
            }
        }
        List<String> outputQueuesNames = new ArrayList<>(inputQueues.size());
        for (S3MQueue queue : outputQueues) {
            outputQueuesNames.add(queue.getName() + ":" + queue.getId());
        }
        ErrorMessage obj = ErrorMessage.builder()
                .nodeClass(nodeClass)
                .inputQueues(inputQueuesNames)
                .inputs(inputParams)
                .outputQueues(outputQueuesNames)
                .error(ErrorMessage.ThrowableInfo.builder().throwable(throwable).build())
                .build();
        return serializer.serialize(obj);
    }

    private void receive(int i, Consumer<List<byte[]>> receiver, List<byte[]> params) throws Throwable {
        if (i != 0) {
            final Throwable[] ex = {null};
            final int index = i - 1;
            inputQueuesConnector.onReceivePolling(inputQueues.get(index), data -> {
                params.add(index, data);
                try {
                    receive(index, receiver, params);
                } catch (Throwable throwable) {
                    ex[0] = throwable;
                }
            });
            if (ex[0] != null) {
                throw ex[0];
            }
        } else {
            receiver.accept(params);
        }
    }

    private void runLoopIteration(List<byte[]> params) throws Throwable {
        while (outputThrottling() || inputThrottling()) {
            //System.out.println("Will slow down due to a jam in output queues"); //TODO: log instead
            Thread.sleep(throttlingDelay);
        }
        if (inputQueues.isEmpty()) {
            processByNode(Collections.emptyList());
        } else {
            receive(inputQueues.size(), inputData -> {
                processByNode(inputData);
                Thread.sleep(pollingInterval);
            }, params);
        }
    }

    private boolean inputThrottling() throws IOException {
        if (throttling) {
            for (S3MQueue queue : inputQueues) {
                if (outputQueuesConnector.size(queue) <= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean outputThrottling() throws IOException {
        if (throttling) {
            for (S3MQueue queue : outputQueues) {
                if (!(inputQueueNames.contains(queue.getName())) &&
                        outputQueuesConnector.size(queue) > throttlingQueueSize) {
                    return true;
                }
            }
        }
        return false;
    }

    private void processByNode(List<byte[]> inputData) throws Exception {
        List<byte[]> output = node.process(inputData);
        for (int i = 0; i < outputQueues.size(); i++) {
            final S3MQueue queue = outputQueues.get(i);
            if (output.size() <= i) {
                break;
            }
            final byte[] data = output.get(i);
            if (data != null) {
                outputQueuesConnector.put(queue, data);
            }
        }
    }


    private interface Consumer<T> {
        void accept(T object) throws Throwable;
    }

}
