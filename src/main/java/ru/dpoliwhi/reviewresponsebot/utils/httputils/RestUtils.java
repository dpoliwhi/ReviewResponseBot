package ru.dpoliwhi.reviewresponsebot.utils.httputils;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import ru.dpoliwhi.reviewresponsebot.exceptions.ExternalServiceRequestError;
import ru.dpoliwhi.reviewresponsebot.utils.JsonUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class RestUtils {

    private final HttpFactory httpFactory;

    private final JsonUtils jsonUtils;

    @Autowired
    public RestUtils(HttpFactory httpFactory, JsonUtils jsonUtils) {
        this.httpFactory = httpFactory;
        this.jsonUtils = jsonUtils;
    }

    /**
     * Отправить post запрос
     * Content-type: json
     *
     * @return InputStream
     */
    public HttpResult postRequest(URIBuilder uriBuilder, String json, List<Header> headers) {

        HttpPost httpPost = getHttpPost(uriBuilder, headers);

        httpPost.setEntity(new StringEntity(json, org.apache.http.entity.ContentType.APPLICATION_JSON));

        return execute(httpPost);
    }


    /**
     * Отправить post запрос
     * Content-type: json, multipart/form-data
     *
     * @return InputStream
     */
    public HttpResult postRequest(URIBuilder uriBuilder, HttpEntity entity, List<Header> headers) {

        HttpPost httpPost = getHttpPost(uriBuilder, headers);

        httpPost.setEntity(entity);

        return execute(httpPost);
    }

    /**
     * Отправить post запрос
     * Content-type: urlencoded
     *
     * @return InputStream
     */
    public HttpResult postRequest(URIBuilder uriBuilder, List<NameValuePair> nvps, String charset, List<Header> headers) throws UnsupportedEncodingException {
        HttpPost httpPost = getHttpPost(uriBuilder, headers);
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, charset));
        return execute(httpPost);
    }

    /**
     * Отправить post запрос
     *
     * @return InputStream
     */
    public HttpResult postRequest(URIBuilder uriBuilder, String json) {
        return postRequest(uriBuilder, json, null);
    }

    /**
     * Отправить post запрос.
     *
     * @param uriBuilder      путь.
     * @param body            тело запроса.
     * @param headers         список заголовков запроса.
     * @param bodyContentType тип отправляемых данных.
     * @return ответ.
     */
    public HttpResult postRequest(URIBuilder uriBuilder, List<Header> headers, ContentType bodyContentType, String body) {
        HttpPost httpPost = getHttpPost(uriBuilder, headers);
        httpPost.setEntity(new StringEntity(body, bodyContentType));
        return execute(httpPost);
    }

    /**
     * Отправить get запрос
     *
     * @return InputStream
     */
    public HttpResult getRequest(URIBuilder uriBuilder, List<Header> headers) {

        HttpGet httpGet = getHttpGet(uriBuilder);

        if (headers != null) {
            headers.forEach(x -> httpGet.addHeader(x.getName(), x.getValue()));
        }
        return execute(httpGet);
    }

    /**
     * Отправить get запрос
     *
     * @return InputStream
     */
    public HttpResult getRequest(URIBuilder uriBuilder) {
        return getRequest(uriBuilder, null);
    }

    /**
     * Получить url
     */
    public URIBuilder getURIBuilder(String uri) {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(uri);
        } catch (URISyntaxException e) {
            throw new ExternalServiceRequestError(e.getMessage());
        }
        return uriBuilder;
    }

    /**
     * Конвертация ответа в виде json в нужную сущность
     */
    public <T> T mapResponse(HttpResult response, TypeReference<T> type) {
        String content = getContent(response);
        return jsonUtils.fromJson(content, type);
    }

    /**
     * Получить заголовки по умолчанию
     */
    public List<Header> getDefaultHeader() {
        return Collections.singletonList(new BasicHeader(HttpHeaders.USER_AGENT, "JVM/8/0"));
    }

    /**
     * Метод определения успешного статуса ответа.
     *
     * @param httpResponse ответ.
     * @return true, если статус ОК, иначе false.
     */
    public static boolean isHttpStatusOk(HttpResult httpResponse) {
        return httpResponse != null
                && httpResponse.getStatusCode() == HttpStatus.SC_OK;
    }

    /**
     * Метод проверки типа данных тела http сообщения.
     *
     * @param httpEntity          сущность http сообщения.
     * @param expectedContentType ожидаемый тип данных.
     * @return true, если тип данных соответствует ожидаемому, иначе false.
     */
    public static boolean checkHttpEntityContentType(HttpEntity httpEntity, ContentType expectedContentType) {
        ContentType entityContentType = ContentType.get(httpEntity);
        return Objects.equals(entityContentType, expectedContentType);
    }

    private HttpPost getHttpPost(URIBuilder uriBuilder, List<Header> headers) {
        HttpPost httpPost;
        try {
            httpPost = httpFactory.createHttpPost(uriBuilder);
        } catch (URISyntaxException e) {
            throw new ExternalServiceRequestError(e.getMessage());
        }

        if (headers != null) {
            headers.forEach(httpPost::addHeader);
        }

        return httpPost;
    }

    private HttpResult execute(HttpUriRequest request) {
        try (CloseableHttpResponse response = HttpClients.createDefault().execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String body = null;
            if (HttpStatus.SC_NO_CONTENT != statusCode) {
                body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
            Header[] allHeaders = response.getAllHeaders();
            return new HttpResult(statusCode, body, Arrays.asList(allHeaders));
        } catch (IOException e) {
            throw new ExternalServiceRequestError(e.getMessage());
        }
    }

    private HttpGet getHttpGet(URIBuilder uriBuilder) {
        HttpGet httpGet;
        try {
            httpGet = httpFactory.createHttpGet(uriBuilder);
        } catch (URISyntaxException e) {
            throw new ExternalServiceRequestError(e.getMessage());
        }
        return httpGet;
    }

    private String getContent(HttpResult response) {
        String content = response.getBody();

        int statusCode = response.getStatusCode();
        if (statusCode != Response.Status.OK.getStatusCode()) {
            Map<String, String> messageMap = jsonUtils.fromJson(content, new TypeReference<Map<String, String>>() {
            });
            String errorMessage = messageMap.get("message");
            if (StringUtils.isNotBlank(errorMessage)) {
                throw new ExternalServiceRequestError(messageMap, statusCode);
            }
            throw new ExternalServiceRequestError(messageMap, statusCode);
        }

        return content;
    }

    public List<Header> getAuthHeader(String token) {
        List<Header> result = new ArrayList<>();
        result.add(new BasicHeader("Cookie", token));
        return result;
    }
}
