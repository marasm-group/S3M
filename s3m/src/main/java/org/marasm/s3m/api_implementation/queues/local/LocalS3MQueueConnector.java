package org.marasm.s3m.api_implementation.queues.local;

import org.marasm.s3m.api.S3MQueue;
import org.marasm.s3m.api_implementation.queues.MqServerConfig;
import org.marasm.s3m.api_implementation.queues.S3MQueueConnector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class LocalS3MQueueConnector implements S3MQueueConnector {

    Map<String, Queue<byte[]>> queues = new HashMap<>();

    @Override

    public void init(MqServerConfig serverConfig) throws Exception {
    }

    @Override
    public void connect() throws Exception {
    }

    @Override
    public void createQueue(S3MQueue queue) throws IOException {
        Queue<byte[]> q = new ConcurrentLinkedQueue<>();
        queues.put(getId(queue), q);
    }

    private String getId(S3MQueue queue) {
        return queue.getName() + ":" + queue.getId();
    }

    @Override
    public void deleteQueue(S3MQueue queue) throws IOException {
        queues.remove(getId(queue));
    }

    @Override
    public void put(S3MQueue queue, byte[] data) throws IOException {
        getQueue(queue).add(data);
    }

    @Override
    public int size(S3MQueue queue) throws IOException {
        return getQueue(queue).size();
    }

    private Queue<byte[]> getQueue(S3MQueue queue) {
        return queues.get(getId(queue));
    }

    @Override
    public void onReceivePolling(S3MQueue queue, Consumer<byte[]> receiver) throws Exception {
        byte[] peek = getQueue(queue).peek();
        if (peek == null) {
            return;
        }
        receiver.accept(peek);
    }
}
