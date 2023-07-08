package ru.dpoliwhi.reviewresponsebot.exceptions;

import com.google.common.base.Joiner;
import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.Map;

public class ExternalServiceRequestError extends RuntimeException {

    private static final String ERROR_MESSAGE = "Ошибка при отправке запроса на сторонний сервис";
    private final Map<String, String> messageMap;
    private final String error;
    private final int statusCode;

    public ExternalServiceRequestError(String s) {
        super(s);
        error = ERROR_MESSAGE;
        statusCode = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        messageMap = null;
    }

    public ExternalServiceRequestError(Map<String, String> messageMap, int statusCode) {
        this.error = ERROR_MESSAGE;
        this.messageMap = messageMap;
        this.statusCode = statusCode;
    }

    public ExternalServiceRequestError(int statusCode) {
        this(Collections.emptyMap(), statusCode);
    }

    public String getError() {
        return error;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage() {

        if (messageMap != null) {
            return "{" + Joiner.on(", ").withKeyValueSeparator("=").join(messageMap) + "}";
        }

        return super.getMessage();
    }

    @Override
    public String toString() {
        return "ExternalServiceRequestError{" +
                "messageMap=" + messageMap +
                ", statusCode=" + statusCode +
                '}';
    }
}
