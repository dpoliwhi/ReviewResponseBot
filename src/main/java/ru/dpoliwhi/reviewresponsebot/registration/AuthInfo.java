package ru.dpoliwhi.reviewresponsebot.registration;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.dpoliwhi.reviewresponsebot.utils.JsonUtils;
import ru.dpoliwhi.reviewresponsebot.utils.httputils.RestUtils;

@Data
@Log4j2
@Component
public class AuthInfo {

    private static final String OZON_TOKEN_REQUEST_URL = "https://seller.ozon.ru/api/ozon-id/request-token";

    private String sellerId;

    private String token;

    private final RestUtils restUtils;

    private final JsonUtils jsonUtils;

    public AuthInfo(RestUtils restUtils, JsonUtils jsonUtils) {
        this.restUtils = restUtils;
        this.jsonUtils = jsonUtils;
    }

    public void clearAuthInfo() {
        sellerId = null;
        token = null;
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(sellerId) && StringUtils.isEmpty(token);

    }
}
