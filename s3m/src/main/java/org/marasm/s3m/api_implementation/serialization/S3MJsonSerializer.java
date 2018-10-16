package org.marasm.s3m.api_implementation.serialization;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.marasm.s3m.api.serialization.S3MSerializer;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

public class S3MJsonSerializer implements S3MSerializer {

    public static final char SEPARATOR = '\n';
    @SuppressWarnings("Annotator") //TODO: better regexp
    private static final Pattern DOUBLE_PATTERN = Pattern.compile(
            "[\\x00-\\x20]*[+-]?(NaN|Infinity|((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)" +
                    "([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|" +
                    "(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))" +
                    "[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*");
    final Gson gson = new Gson();

    private static boolean empty(final String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isInteger(String s) {
        return isInteger(s, 10);
    }

    public static boolean isInteger(String s, int radix) {
        if (empty(s)) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) {
                    return false;
                } else {
                    continue;
                }
            }
            if (Character.digit(s.charAt(i), radix) < 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isFloat(String s) {
        return DOUBLE_PATTERN.matcher(s).matches();
    }

    private static Class<?> getClass(String className, String json) {
        try {
            return Class.forName(className);
        } catch (Throwable ignored) {
        }
        if (json.startsWith("{")) {
            return HashMap.class;
        }
        if (json.startsWith("[")) {
            return ArrayList.class;
        }
        if (isInteger(json)) {
            return Long.class;
        }
        if (isFloat(json)) {
            return Double.class;
        }
        return String.class;
    }

    @Override
    public byte[] serialize(Serializable object) {
        if (object == null) {
            return null;
        }
        String json = gson.toJson(object);
        String className = object.getClass().getCanonicalName();
        return (className + SEPARATOR + json).getBytes();
    }

    @Override
    public <T> T deserialize(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        String className = null;
        String json = null;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == '\n') {
                className = new String(Arrays.copyOfRange(data, 0, i));
                json = new String(Arrays.copyOfRange(data, i + 1, data.length));
            }
        }
        if (empty(json)) {
            json = new String(data);
        }

        try {
            Class<?> type = getClass(className, json);
            Type typeToken = TypeToken.get(type).getType();
            return gson.fromJson(json, typeToken);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
