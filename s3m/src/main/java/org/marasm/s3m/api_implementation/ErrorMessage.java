package org.marasm.s3m.api_implementation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ErrorMessage implements Serializable {
    private String nodeClass;
    private List<Serializable> inputs;
    private List<String> inputQueues;
    private List<String> outputQueues;
    private ThrowableInfo error;

    public static String getStacktrace(Throwable t) {
        StringWriter stacktrace = new StringWriter();
        t.printStackTrace(new PrintWriter(stacktrace));
        return stacktrace.toString();
    }

    @Builder
    @AllArgsConstructor
    public static class ThrowableInfo implements Serializable {
        String stacktrace;
        String message;
        String exception;
        @Builder.Default
        String platform = "Java " + System.getProperty("java.version");

        public static class ThrowableInfoBuilder {
            public ThrowableInfoBuilder throwable(Throwable throwable) {
                return this
                        .stacktrace(getStacktrace(throwable))
                        .message(throwable.getMessage())
                        .exception(throwable.getClass().getCanonicalName());
            }
        }
    }
}
