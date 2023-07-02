package ru.dpoliwhi.reviewresponsebot.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.dpoliwhi.reviewresponsebot.model.request.enums.SortBy;
import ru.dpoliwhi.reviewresponsebot.model.request.enums.SortDirection;

@Data
public class Sort {
    @JsonProperty("sort_by")
    private SortBy sortBy;

    @JsonProperty("sort_direction")
    private SortDirection sortDirection;
}
