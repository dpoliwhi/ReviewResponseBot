package ru.dpoliwhi.reviewresponsebot.model.response;

import lombok.Data;

import java.util.List;

@Data
public class ReviewResponse {
    List<Review> result;
}
