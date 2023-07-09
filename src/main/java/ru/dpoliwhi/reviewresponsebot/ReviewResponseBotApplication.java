package ru.dpoliwhi.reviewresponsebot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.dpoliwhi.reviewresponsebot.model.request.Filter;
import ru.dpoliwhi.reviewresponsebot.model.request.PageFilter;
import ru.dpoliwhi.reviewresponsebot.model.request.Sort;
import ru.dpoliwhi.reviewresponsebot.model.request.enums.InteractionStatus;
import ru.dpoliwhi.reviewresponsebot.model.request.enums.SortBy;
import ru.dpoliwhi.reviewresponsebot.model.request.enums.SortDirection;
import ru.dpoliwhi.reviewresponsebot.service.ReviewService;
import ru.dpoliwhi.reviewresponsebot.utils.JsonUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@SpringBootApplication
public class ReviewResponseBotApplication implements CommandLineRunner {

    private final ReviewService reviewService;
    private final JsonUtils jsonUtils;

    public ReviewResponseBotApplication(ReviewService reviewService, JsonUtils jsonUtils) {
        this.reviewService = reviewService;
        this.jsonUtils = jsonUtils;
    }

    public static void main(String[] args) {
        SpringApplication.run(ReviewResponseBotApplication.class, args);

    }

    @Override
    public void run(String... args) throws Exception {
        PageFilter pageFilter = new PageFilter();
        Filter filter = new Filter();
        List<InteractionStatus> interactionStatuses = new ArrayList<>();
        interactionStatuses.add(InteractionStatus.PROCESSED);
        filter.setInteractionStatuses(interactionStatuses);
        pageFilter.setFilter(filter);

        Sort sort = new Sort(SortBy.PUBLISHED_AT, SortDirection.DESC);
        pageFilter.setSort(sort);

        pageFilter.setCompanyId("382276");
        pageFilter.setCompanyType("seller");
        pageFilter.setWithCounters(false);
        pageFilter.setLastTimeStamp(new Date(0L));
        pageFilter.setLastUUID(null);

        String token = "__Secure-ab-group=85; __Secure-ext_xcid=e1a085422aa690220db7feeaa007c409; __Secure-user-id=70187620; bacntid=925310; contentId=382276; __cf_bm=iAP0eHO9ekDmFDcjrhXryrj3RmePAywJlGrSt3.FR1U-1688920150-0-AcqDn3oBzckBiVwI6zFcdngdibxfc6y67msiXxPBeROzYlFZRpwGbH3Sf0QxFBfqUKkescmc0Dtc8fkjQxhEZtjpCHsZ8u4xv0WFik5shoOB; __Secure-access-token=3.70187620.1mXdI0UCTqWtg_iaPakq8A.85.l8cMBQAAAABkquBWAAAAAKN3ZWKrNzkwNjkzODk5NjkAgJCg.20210628064922.20230709182910.aIBCgTG5_NeDrfEpUzA9ZT7D5o_ASrc-r3fQ449OyC4; __Secure-refresh-token=3.70187620.1mXdI0UCTqWtg_iaPakq8A.85.l8cMBQAAAABkquBWAAAAAKN3ZWKrNzkwNjkzODk5NjkAgJCg.20210628064922.20230709182910.evjS3c1H2liKT6gbdMUZNc8uIo0XqpLYvhkNovbX4a0; x-o3-language=ru";

        reviewService.getReviews(pageFilter, token);
    }
}
