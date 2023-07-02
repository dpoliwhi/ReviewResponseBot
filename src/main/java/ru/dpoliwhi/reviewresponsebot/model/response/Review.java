package ru.dpoliwhi.reviewresponsebot.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.dpoliwhi.reviewresponsebot.model.request.enums.InteractionStatus;

import java.util.Date;

@Data
public class Review {

    private String id;

    private String sku;

    private ReviewText text;

    @JsonProperty("published_at")
    private Date publishedAt;

    private Integer rating;

    @JsonProperty("interaction_status")
    private InteractionStatus interactionStatus;

    @JsonProperty("comments_amount")
    private Integer commentsAmount;

    @JsonProperty("likes_amount")
    private Integer likesAmount;

    @JsonProperty("dislikes_amount")
    private Integer dislikesAmount;

    @JsonProperty("author_name")
    private String authorName;

    @JsonProperty("photos_count")
    private Integer photosCount;

    @JsonProperty("videos_count")
    private Integer videosCount;

    @JsonProperty("comments_count")
    private Integer commentsCount;

    private String uuid;

    private Product product;
}
