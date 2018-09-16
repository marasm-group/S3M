package org.marasm.s3m.api_implementation.queues;

import org.marasm.s3m.api.S3MQueue;

import java.io.IOException;
import java.util.function.Consumer;

public interface S3MQueueConnector {
    void init(MqServerConfig serverConfig) throws Exception;

    void connect() throws Exception;

    void createQueue(S3MQueue queue) throws IOException;

    void deleteQueue(S3MQueue queue) throws IOException;

    void put(S3MQueue queue, byte[] data) throws IOException;

    void onReceivePolling(S3MQueue queue, Consumer<byte[]> receiver) throws Exception;

}
