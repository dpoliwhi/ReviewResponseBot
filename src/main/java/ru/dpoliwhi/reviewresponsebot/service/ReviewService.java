package ru.dpoliwhi.reviewresponsebot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicHeader;
import org.springframework.stereotype.Service;
import ru.dpoliwhi.reviewresponsebot.exceptions.ExternalServiceRequestError;
import ru.dpoliwhi.reviewresponsebot.model.request.Filter;
import ru.dpoliwhi.reviewresponsebot.model.request.PageFilter;
import ru.dpoliwhi.reviewresponsebot.model.request.Sort;
import ru.dpoliwhi.reviewresponsebot.model.request.enums.InteractionStatus;
import ru.dpoliwhi.reviewresponsebot.model.request.enums.SortBy;
import ru.dpoliwhi.reviewresponsebot.model.request.enums.SortDirection;
import ru.dpoliwhi.reviewresponsebot.model.response.ReviewResponse;
import ru.dpoliwhi.reviewresponsebot.utils.JsonUtils;
import ru.dpoliwhi.reviewresponsebot.utils.httputils.HttpResult;
import ru.dpoliwhi.reviewresponsebot.utils.httputils.RestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    public PageFilter getFilter(String companyId) {
        PageFilter pageFilter = new PageFilter();
        Filter filter = new Filter();
        List<InteractionStatus> interactionStatuses = new ArrayList<>();
        interactionStatuses.add(InteractionStatus.ALL);
        filter.setInteractionStatuses(interactionStatuses);
        pageFilter.setFilter(filter);

        Sort sort = new Sort(SortBy.PUBLISHED_AT, SortDirection.DESC);
        pageFilter.setSort(sort);

//        pageFilter.setCompanyId("382276");
        pageFilter.setCompanyId(companyId);
        pageFilter.setCompanyType("seller");
        pageFilter.setWithCounters(false);
        pageFilter.setLastTimeStamp(new Date(0L).getTime());
        pageFilter.setLastUUID(null);

        return pageFilter;
    }

    public void getReviews(PageFilter filter, String token) {
        URIBuilder uriBuilder = restUtils.getURIBuilder(OZON_REVIEWS_URL);
        List<Header> headers = getAuthHeader(token);

        ReviewResponse reviews = null;
        filter.setLastUUID("");

        while (true) {
            String pageFilterJson = jsonUtils.toJson(filter);

            try {
                HttpResult response = restUtils.postRequest(uriBuilder, pageFilterJson, headers);
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

    private List<Header> getAuthHeader(String token) {
        List<Header> result = new ArrayList<>();
        result.add(new BasicHeader("Cookie", token));
        return result;
    }

    public int getCountOfReviews() {
        return reviewStorage.getReviews().size();
    }

    public Map<Integer, Integer> getRatings() {
        return reviewStorage.getRatings();
    }
}
