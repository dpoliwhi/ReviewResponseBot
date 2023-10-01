package ru.dpoliwhi.reviewresponsebot.model.getreviews.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.dpoliwhi.reviewresponsebot.model.getreviews.request.enums.SortBy;
import ru.dpoliwhi.reviewresponsebot.model.getreviews.request.enums.SortDirection;

@Data
@AllArgsConstructor
public class Sort {
    @JsonProperty("sort_by")
    private SortBy sortBy;

    @JsonProperty("sort_direction")
    private SortDirection sortDirection;
}
