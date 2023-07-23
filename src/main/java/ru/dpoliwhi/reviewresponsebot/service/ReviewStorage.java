package ru.dpoliwhi.reviewresponsebot.service;

import org.springframework.stereotype.Service;
import ru.dpoliwhi.reviewresponsebot.model.dto.ReviewTelegramDto;
import ru.dpoliwhi.reviewresponsebot.model.response.Review;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewStorage {

    private List<Review> reviews = new ArrayList<>();

    private int rating_5 = 0;
    private int rating_4 = 0;
    private int rating_3 = 0;
    private int rating_2 = 0;
    private int rating_1 = 0;

    private List<ReviewTelegramDto> negativeReviews = new ArrayList<>();

}
