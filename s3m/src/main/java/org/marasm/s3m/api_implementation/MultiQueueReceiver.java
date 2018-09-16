package org.marasm.s3m.api_implementation;

public class MultiQueueReceiver {

    public final byte[][] data;
    final Object[] locks;

    MultiQueueReceiver(int size) {
        locks = new Object[size];
        data = new byte[size][];
    }

    void waitAllLocksAndDo() throws InterruptedException {
        for(int i = 0; i <locks.length; i++){
            synchronized (locks[i]) {
                locks[i].wait();
            }
        }
    }

    void notifyAllLocks() {
        for(int i = 0; i <locks.length; i++){
            synchronized (locks[i]) {
                locks[i].notify();
            }
        }
    }

    void receive(int queue, byte[] data) throws InterruptedException {
        synchronized (locks[queue]) {
            locks[queue].wait();
            this.data[queue] = data;
            locks[queue].notify();
        }
    }

}
