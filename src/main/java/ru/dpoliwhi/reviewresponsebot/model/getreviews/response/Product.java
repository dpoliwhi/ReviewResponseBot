package ru.dpoliwhi.reviewresponsebot.model.getreviews.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Product {

    private String title;

    private String url;

    @JsonProperty("offer_id")
    private String offerId;

    @JsonProperty("company_info")
    private Info companyInfo;

    @JsonProperty("brand_info")
    private Info brandInfo;

    @JsonProperty("cover_image")
    private String coverImage;
}
