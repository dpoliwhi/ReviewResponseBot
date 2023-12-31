package ru.dpoliwhi.reviewresponsebot.model.getreviews.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.dpoliwhi.reviewresponsebot.model.getreviews.request.enums.InteractionStatus;

import java.util.List;

@Data
public class Filter {
    @JsonProperty("interaction_status")
    private List<InteractionStatus> interactionStatuses;
}
