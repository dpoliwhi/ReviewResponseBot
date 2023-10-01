package ru.dpoliwhi.reviewresponsebot.service;

import lombok.Data;
import org.springframework.stereotype.Service;
import ru.dpoliwhi.reviewresponsebot.model.dto.ReviewInfoToResponse;
import ru.dpoliwhi.reviewresponsebot.model.dto.ReviewTelegramDto;
import ru.dpoliwhi.reviewresponsebot.model.getreviews.response.Review;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Service
public class ReviewStorage {

    private List<Review> reviews;

    private Map<Integer, Integer> ratings;

    private List<ReviewTelegramDto> negativeReviews;

    private List<ReviewInfoToResponse> reviewInfos;

    public ReviewStorage() {
        reviews = new ArrayList<>();

        ratings = new HashMap<>();
        fillRatingsMap();

        negativeReviews = new ArrayList<>();

        reviewInfos = new ArrayList<>();
    }

    public void fillRatingsMap() {
        ratings.clear();
        ratings.put(5, 0);
        ratings.put(4, 0);
        ratings.put(3, 0);
        ratings.put(2, 0);
        ratings.put(1, 0);
    }

    public void addReviews(List<Review> reviews) {
        this.reviews.addAll(reviews);
        for (Review review : reviews) {
            Integer rating = review.getRating();
            ratings.put(rating, ratings.get(review.getRating()) + 1);
            if (rating <= 3) {
                negativeReviews.add(new ReviewTelegramDto(review));
            } else {
                reviewInfos.add(new ReviewInfoToResponse(review.getUuid(), review.getAuthorName()));
            }
        }
    }
}
