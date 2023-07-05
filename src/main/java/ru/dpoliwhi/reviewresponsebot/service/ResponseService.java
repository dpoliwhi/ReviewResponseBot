package ru.dpoliwhi.reviewresponsebot.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.dpoliwhi.reviewresponsebot.model.request.Filter;
import ru.dpoliwhi.reviewresponsebot.model.request.PageFilter;
import ru.dpoliwhi.reviewresponsebot.model.request.enums.InteractionStatus;
import ru.dpoliwhi.reviewresponsebot.model.response.ReviewResponse;
import ru.dpoliwhi.reviewresponsebot.utils.JsonUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class ResponseService {

    @Autowired
    private JsonUtils jsonUtils;

    private static final String OZON_REVIEWS_URL = "https://seller.ozon.ru/api/v3/review/list";

    public ReviewResponse getReviews() {
        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpPost request = new HttpPost(OZON_REVIEWS_URL);

            PageFilter pageFilter = new PageFilter();
            Filter filter = new Filter();
            List<InteractionStatus> interactionStatuses = new ArrayList<>();
            interactionStatuses.add(InteractionStatus.NOT_VIEWED);
            filter.setInteractionStatuses(interactionStatuses);
            pageFilter.setFilter(filter);



            StringEntity params = new StringEntity("");
            request.addHeader("content-type", "application/x-www-form-urlencoded");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
        } catch (Exception ex) {
        }
        return null;
    }
}
