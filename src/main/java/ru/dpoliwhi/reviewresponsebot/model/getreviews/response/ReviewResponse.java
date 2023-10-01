package ru.dpoliwhi.reviewresponsebot.model.getreviews.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ReviewResponse {

    @JsonProperty("pagination_last_timestamp")
    private Long lastTimeStamp;

    @JsonProperty("pagination_last_uuid")
    private String lastUUID;

    List<Review> result;
}
