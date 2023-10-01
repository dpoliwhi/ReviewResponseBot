package ru.dpoliwhi.reviewresponsebot.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewInfoToResponse {

    private String uuid;

    private String authorName;
}
