package ru.dpoliwhi.reviewresponsebot.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class PageFilter {

    private Filter filter;

    private Sort sort;

    @JsonProperty("company_id")
    private String companyId;

    @JsonProperty("company_type")
    private String companyType;

    @JsonProperty("with_counters")
    private Boolean withCounters;

    @JsonProperty("pagination_last_timestamp")
    private Long lastTimeStamp;

    @JsonProperty("pagination_last_uuid")
    private String lastUUID;
}
