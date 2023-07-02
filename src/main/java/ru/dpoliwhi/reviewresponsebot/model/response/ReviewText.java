package ru.dpoliwhi.reviewresponsebot.model.response;

import lombok.Data;

@Data
public class ReviewText {
    private String positive;
    private String negative;
    private String comment;
}
