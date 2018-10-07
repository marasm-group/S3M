package org.marasm.s3m.api_implementation.serialization;

import org.marasm.s3m.api.serialization.S3MSerializer;

import java.io.*;

public class S3MBinarySerializer implements S3MSerializer {

    @Override
    public byte[] serialize(Serializable serializable) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(serializable);
            out.flush();
            return bos.toByteArray();
        } catch (IOException ignored) {
        }
        return null;
    }

    @Override
    public <T> T deserialize(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try (ObjectInput in = new ObjectInputStream(bis)) {
            T o = (T) in.readObject();
            return o;
        } catch (IOException | ClassNotFoundException ignored) {
        }
        return null;
    }
}
