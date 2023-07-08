package ru.dpoliwhi.reviewresponsebot.utils.httputils;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

public class HttpResult {
    private final int statusCode;
    private final String body;
    private List<Header> headers;

    public HttpResult(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = new ArrayList<>();
    }

    public HttpResult(int statusCode, String body, List<Header> headers) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }

    public List<Header> getHeaders() {
        if (headers == null) {
            headers = new ArrayList<>();
        }
        return headers;
    }
}
