package org.marasm.s3m.api_implementation.queues.rabbitmq;

import com.rabbitmq.client.*;
import org.marasm.s3m.api.S3MQueue;
import org.marasm.s3m.api_implementation.queues.MqServerConfig;
import org.marasm.s3m.api_implementation.queues.S3MQueueConnector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class RabbitMqS3MConnector implements S3MQueueConnector {

    private ConnectionFactory factory = new ConnectionFactory();
    private Connection connection;
    private Channel channel;

    private Map<S3MQueue, DefaultConsumer> consumers = new HashMap<>();

    @Override
    public void init(MqServerConfig serverConfig) throws Exception {
        factory.setHost(serverConfig.getHost());
    }

    @Override
    public void connect() throws Exception {
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.addShutdownListener(cause -> {
            System.out.println(cause);
        });
    }

    @Override
    public void createQueue(S3MQueue queue) throws IOException {
        channel.queueDeclare(getRemoteId(queue), false, false, false, null);
    }

    private String getRemoteId(S3MQueue queue) {
        return queue.getName() + ":" + queue.getId();
    }

    @Override
    public void deleteQueue(S3MQueue queue) throws IOException {
        channel.queueDeleteNoWait(getRemoteId(queue), false, false);
    }

    @Override
    public void put(S3MQueue queue, byte[] data) throws IOException {
        channel.basicPublish("", getRemoteId(queue), null, data);
    }

    @Override
    public void onReceivePolling(S3MQueue queue, Consumer<byte[]> receiver) throws Exception {
        boolean autoAck = false;
        GetResponse response = channel.basicGet(getRemoteId(queue), autoAck);
        if (response == null) {
            // No message retrieved.
        } else {
            AMQP.BasicProperties props = response.getProps();
            byte[] body = response.getBody();
            long deliveryTag = response.getEnvelope().getDeliveryTag();
            receiver.accept(body);
            channel.basicAck(deliveryTag, false); // acknowledge receipt of the message
        }
    }

    private com.rabbitmq.client.Consumer getConsumer(S3MQueue queue, Object lock, Consumer<byte[]> receiver) {
        return consumers.computeIfAbsent(queue, k -> new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) {
                receiver.accept(body);
            }
        });
    }

}
