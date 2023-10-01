package ru.dpoliwhi.reviewresponsebot.model.getreviews.response;

import lombok.Data;

@Data
public class ReviewText {
    private String positive;
    private String negative;
    private String comment;
}
