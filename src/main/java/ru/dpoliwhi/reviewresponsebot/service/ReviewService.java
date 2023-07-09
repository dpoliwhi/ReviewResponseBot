package ru.dpoliwhi.reviewresponsebot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.springframework.stereotype.Service;
import ru.dpoliwhi.reviewresponsebot.exceptions.ExternalServiceRequestError;
import ru.dpoliwhi.reviewresponsebot.model.request.PageFilter;
import ru.dpoliwhi.reviewresponsebot.model.response.ReviewResponse;
import ru.dpoliwhi.reviewresponsebot.utils.JsonUtils;
import ru.dpoliwhi.reviewresponsebot.utils.httputils.HttpResult;
import ru.dpoliwhi.reviewresponsebot.utils.httputils.RestUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ReviewService {
    private static final String OZON_REVIEWS_URL = "https://seller.ozon.ru/api/v3/review/list";

    private final RestUtils restUtils;
    private final JsonUtils jsonUtils;

    public ReviewService(RestUtils restUtils, JsonUtils jsonUtils) {
        this.restUtils = restUtils;
        this.jsonUtils = jsonUtils;
    }

    public ReviewResponse getReviews(PageFilter filter, String token) {
        HttpClient httpClient = HttpClientBuilder.create().build();

        URIBuilder uriBuilder = restUtils.getURIBuilder(OZON_REVIEWS_URL);

        String pageFilterJson = jsonUtils.toJson(filter);
        log.atInfo().log("PageFilter: {}", pageFilterJson);

        ReviewResponse reviews = null;
        try {
            HttpResult response = restUtils.postRequest(uriBuilder, pageFilterJson, getAuthHeader(token));
            int status = response.getStatusCode(); //TODO обработать статус 403 401, прокинуть ошибку выше на запрос нового токена
            String body = response.getBody();
            reviews = restUtils.mapResponse(response, new TypeReference<ReviewResponse>() {});
        } catch (ExternalServiceRequestError e) {
            log.warn("OZON server is unavailable");
            log.error(e.getMessage(), e);
        }

        return reviews;
    }


    public List<Header> getAuthHeader(String token) {
        List<Header> result = new ArrayList<>();
        result.add(new BasicHeader("Cookie", token));
        return result;
    }

}