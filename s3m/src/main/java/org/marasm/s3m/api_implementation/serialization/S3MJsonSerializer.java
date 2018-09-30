package org.marasm.s3m.api_implementation.serialization;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.marasm.s3m.api.serialization.S3MSerializer;

import java.io.Serializable;
import java.lang.reflect.Type;

public class S3MJsonSerializer implements S3MSerializer {

    final Gson gson = new Gson();

    @Override
    public byte[] serialize(Serializable object) {
        return gson.toJson(object).getBytes();
    }

    @Override
    public Serializable deserialize(byte[] data, Class clazz) {
        Type typeToken = TypeToken.get(clazz).getType();
        try {
            return gson.fromJson(new String(data), typeToken);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
