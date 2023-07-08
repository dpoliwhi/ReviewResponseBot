package ru.dpoliwhi.reviewresponsebot.utils.httputils;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

@Component
public class HttpFactory {

    public HttpOptions createHttpOptions() {
        return new HttpOptions();
    }

    public HttpDelete createHttpDelete() {
        return new HttpDelete();
    }

    public HttpPut createHttpPut(HttpEntity entity) {
        HttpPut put = new HttpPut();
        put.setEntity(entity);
        return put;
    }

    public HttpHead createHttpHead() {
        return new HttpHead();
    }

    public HttpPost createHttpPost(HttpEntity entity) {
        HttpPost post = new HttpPost();
        post.setEntity(entity);
        return post;
    }

    public HttpGet createHttpGet() {
        return new HttpGet();
    }

    public HttpGet createHttpGet(URIBuilder builder) throws URISyntaxException {
        return new HttpGet(builder.build());
    }

    public HttpPost createHttpPost(URIBuilder builder) throws URISyntaxException {
        return new HttpPost(builder.build());
    }
}
