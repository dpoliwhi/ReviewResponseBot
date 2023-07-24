package ru.dpoliwhi.reviewresponsebot.model.dto;

import lombok.Data;
import ru.dpoliwhi.reviewresponsebot.model.response.Review;
import ru.dpoliwhi.reviewresponsebot.model.response.ReviewText;

@Data
public class ReviewTelegramDto {

    private String productName;

    private Integer rating;

    private String authorName;

    private ReviewText reviewText;

    public ReviewTelegramDto(Review review) {
        this.productName = review.getProduct().getTitle();
        this.rating = review.getRating();
        this.authorName = review.getAuthorName();
        this.reviewText = review.getText();
    }
}
