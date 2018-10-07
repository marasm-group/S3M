package org.marasm.s3m.api_implementation.examples;

public class PrimeNumbers {

    /*private RabbitMqS3MQueue errorQueue = new RabbitMqS3MQueue("ERROR", 0, ErrorMessage.class);
    private RemoteS3MQueue in = RabbitMqS3MQueue.builder().id(1).name("in").messageClass(HashMap.class).build();
    private RemoteS3MQueue in_js = RabbitMqS3MQueue.builder().id(1).name("in").messageClass(HashMap.class).build();
    private RemoteS3MQueue processing_js = RabbitMqS3MQueue.builder().id(1).name("processing").messageClass(HashMap.class).build();
    private RemoteS3MQueue primes = RabbitMqS3MQueue.builder().id(1).name("primes").messageClass(Long.class).build();
    private RemoteS3MQueue primes_js = RabbitMqS3MQueue.builder().id(1).name("primes").messageClass(HashMap.class).build();
    private MqServerConfig serverConfig = MqServerConfig.builder().host("localhost").build();
    private S3MSerializer serializer = new S3MJsonSerializer();

    @Test
    @SneakyThrows
    public void main() {
        DispatchQueue.get("Prodicer").async(this::producerThread);
        DispatchQueue.get("Mapper").async(() -> jsProcessorThread("" +
                "function process (input) {\n" +
                "    var result = {\n" +
                "        number: input[0].number,\n" +
                "        current: Math.floor(Math.sqrt(input[0].number))\n" +
                "    };\n" +
                "    return [result];\n" +
                "}", Collections.singletonList(in_js), Collections.singletonList(processing_js)));
        DispatchQueue.get("Processor").async(() -> jsProcessorThread("" +
                "function process (input) {\n" +
                "    var context = input[0];    \n" +
                "    if(context.current === 0 || context.current === 1) {\n" +
                "        return [null, context.number];\n" +
                "    }    \n" +
                "    if(context.number % context.current === 0) {\n" +
                "        return [];\n" +
                "    } else {\n" +
                "        context.current--;\n" +
                "        return[context, null];\n" +
                "    }\n" +
                "}", Collections.singletonList(processing_js), Arrays.asList(processing_js, primes_js)));
        DispatchQueue.get("Consumer").async(this::consumerThread);
        Thread.sleep(100000);
    }

    @SneakyThrows
    private void producerThread() {
        S3MNode supplier = new SupplierS3MNode(new Supplier<List<Serializable>>() {
            private long i = 1;

            @Override
            public List<Serializable> get() {
                return Collections.singletonList(new HashMap<>(Collections.singletonMap("number", i++)));
            }
        });


        RabbitMqS3MConnector queueConnector = new RabbitMqS3MConnector();
        queueConnector.init(serverConfig);
        queueConnector.connect();
        queueConnector.createQueue(in);
        queueConnector.createQueue(errorQueue);
        NodeRunner.builder()
                .serializer(serializer)
                .errorQueue(errorQueue)
                .errorQueueConnector(queueConnector)
                .outputQueues(Collections.singletonList(in))
                .outputQueuesConnector(queueConnector)
                .node(supplier)
                .throttlingQueueSize(5)
                .build().runLoop();
    }

    @SneakyThrows
    private void consumerThread() {
        S3MNode consumer = new ConsumerS3MNode(
                input -> {
                    if (input == null || input.isEmpty() || input.get(0) == null) {
                        return;
                    }
                    long prime = (long) input.get(0);
                    System.out.println(prime + " is prime");
                });


        RabbitMqS3MConnector queueConnector = new RabbitMqS3MConnector();
        queueConnector.init(serverConfig);
        queueConnector.connect();
        queueConnector.createQueue(primes);
        queueConnector.createQueue(errorQueue);
        NodeRunner.builder()
                .serializer(serializer)
                .errorQueue(errorQueue)
                .errorQueueConnector(queueConnector)
                .inputQueues(Collections.singletonList(primes))
                .inputQueuesConnector(queueConnector)
                .node(consumer)
                .build().runLoop();
    }

    @SneakyThrows
    private void jsProcessorThread(String jsCode, Collection<S3MQueue> in, Collection<S3MQueue> out) {
        S3MNode processor = new JavaScriptS3MNode(jsCode);

        RabbitMqS3MConnector queueConnector = new RabbitMqS3MConnector();
        queueConnector.init(serverConfig);
        queueConnector.connect();
        ArrayList<S3MQueue> allQueues = new ArrayList<>(in.size() + out.size());
        allQueues.addAll(in);
        allQueues.addAll(out);
        for (S3MQueue s3MQueue : allQueues) {
            queueConnector.createQueue(s3MQueue);
        }
        queueConnector.createQueue(errorQueue);
        NodeRunner.builder()
                .serializer(serializer)
                .errorQueue(errorQueue)
                .errorQueueConnector(queueConnector)
                .inputQueues(new ArrayList<>(in))
                .inputQueuesConnector(queueConnector)
                .outputQueues(new ArrayList<>(out))
                .outputQueuesConnector(queueConnector)
                .node(processor)
                .build().runLoop();
    }*/
}
