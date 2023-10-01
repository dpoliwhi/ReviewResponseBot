package ru.dpoliwhi.reviewresponsebot.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.dpoliwhi.reviewresponsebot.model.dto.ReviewInfoToResponse;
import ru.dpoliwhi.reviewresponsebot.utils.JsonUtils;
import ru.dpoliwhi.reviewresponsebot.utils.httputils.RestUtils;

import java.util.List;

@Slf4j
@Service
public class ResponseService {

    private static final String OZON_RESPONSE_URL = "https://seller.ozon.ru/api/review/comment/create";

    private final RestUtils restUtils;

    private final JsonUtils jsonUtils;

    private final ReviewStorage reviewStorage;

    @Autowired
    public ResponseService(RestUtils restUtils, JsonUtils jsonUtils, ReviewStorage reviewStorage) {
        this.restUtils = restUtils;
        this.jsonUtils = jsonUtils;
        this.reviewStorage = reviewStorage;
    }

    public void sendResponses(String token) {
        URIBuilder uriBuilder = restUtils.getURIBuilder(OZON_RESPONSE_URL);
        List<Header> headers = restUtils.getAuthHeader(token);

        List<ReviewInfoToResponse> reviewInfos = reviewStorage.getReviewInfos();
//        for (ReviewInfoToResponse reviewInfo : reviewInfos) {
//            HttpResult response = restUtils.postRequest(uriBuilder, pageFilterJson, headers);
//
//        }
//
//
//        while (true) {
//            String pageFilterJson = jsonUtils.toJson(filter);
//
//            try {
//                HttpResult response = restUtils.postRequest(uriBuilder, pageFilterJson, headers);
//                int status = response.getStatusCode(); //TODO обработать статус 403 401, прокинуть ошибку выше на запрос нового токена
//                reviews = restUtils.mapResponse(response, new TypeReference<ReviewResponse>() {});
//
//                reviewStorage.addReviews(reviews.getResult());
//
//                if (StringUtils.isBlank(reviews.getLastUUID())) {
//                    break;
//                }
//
//                filter.setLastTimeStamp(reviews.getLastTimeStamp());
//                filter.setLastUUID(reviews.getLastUUID());
//            } catch (ExternalServiceRequestError e) {
//                log.warn("OZON server is unavailable");
//                log.error(e.getMessage(), e);
//            }
//        }
    }

}
