package ru.dpoliwhi.reviewresponsebot.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReviewResponse {

    @JsonProperty("company_id")
    private String companyId;

    @JsonProperty("company_type")
    private String companyType;

    @JsonProperty("parent_comment_id")
    private int parentCommentId;

    @JsonProperty("review_uuid")
    private String reviewUuid;

    @JsonProperty("text")
    private String text;
}
