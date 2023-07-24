package ru.dpoliwhi.reviewresponsebot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.utils.URIBuilder;
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

    private final ReviewStorage reviewStorage;

    public ReviewService(RestUtils restUtils, JsonUtils jsonUtils, ReviewStorage reviewStorage) {
        this.restUtils = restUtils;
        this.jsonUtils = jsonUtils;
        this.reviewStorage = reviewStorage;
    }

    public void getReviews(PageFilter filter, String token) {
        URIBuilder uriBuilder = restUtils.getURIBuilder(OZON_REVIEWS_URL);
        List<Header> headers = getAuthHeader(token);

        ReviewResponse reviews = null;
        filter.setLastUUID("");

        while (true) {
            String pageFilterJson = jsonUtils.toJson(filter);

            try {
                HttpResult response = restUtils.postRequest(uriBuilder, pageFilterJson, getAuthHeader(token));
                int status = response.getStatusCode(); //TODO обработать статус 403 401, прокинуть ошибку выше на запрос нового токена
                reviews = restUtils.mapResponse(response, new TypeReference<ReviewResponse>() {});

                reviewStorage.addReviews(reviews.getResult());

                if (StringUtils.isBlank(reviews.getLastUUID())) {
                    break;
                }

                filter.setLastTimeStamp(reviews.getLastTimeStamp());
                filter.setLastUUID(reviews.getLastUUID());
            } catch (ExternalServiceRequestError e) {
                log.warn("OZON server is unavailable");
                log.error(e.getMessage(), e);
            }
        }
    }


    public List<Header> getAuthHeader(String token) {
        List<Header> result = new ArrayList<>();
        result.add(new BasicHeader("Cookie", token));
        return result;
    }

}
