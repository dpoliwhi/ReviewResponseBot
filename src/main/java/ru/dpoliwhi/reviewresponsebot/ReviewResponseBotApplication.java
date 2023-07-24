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
import ru.dpoliwhi.reviewresponsebot.service.ReviewStorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootApplication
public class ReviewResponseBotApplication implements CommandLineRunner {

    private final ReviewService reviewService;

    private final ReviewStorage reviewStorage;

    public ReviewResponseBotApplication(ReviewService reviewService, ReviewStorage reviewStorage) {
        this.reviewService = reviewService;
        this.reviewStorage = reviewStorage;
    }

    public static void main(String[] args) {
        SpringApplication.run(ReviewResponseBotApplication.class, args);

    }

    @Override
    public void run(String... args) throws Exception {
        PageFilter pageFilter = new PageFilter();
        Filter filter = new Filter();
        List<InteractionStatus> interactionStatuses = new ArrayList<>();
        interactionStatuses.add(InteractionStatus.ALL);
        filter.setInteractionStatuses(interactionStatuses);
        pageFilter.setFilter(filter);

        Sort sort = new Sort(SortBy.PUBLISHED_AT, SortDirection.DESC);
        pageFilter.setSort(sort);

//        pageFilter.setCompanyId("382276");
        pageFilter.setCompanyId("1213833");
        pageFilter.setCompanyType("seller");
        pageFilter.setWithCounters(false);
        pageFilter.setLastTimeStamp(new Date(0L).getTime());
        pageFilter.setLastUUID(null);

        String token = "__Secure-ab-group=85; __Secure-ext_xcid=e1a085422aa690220db7feeaa007c409; __Secure-user-id=70187620; x-o3-language=ru; __cf_bm=HKn3JEU0pvXz6MWkPZHHH2rQk4.IUNHvgF_awTRoQ4k-1690214542-0-AZY/Y/3RFdIbS0T+bJtddftIGQwvaGct+0k2daLHyGRpa8gnouy31Q0tTQ6hfEJkXjouQzPdsDMAE8q7IHBvdIg=; cf_clearance=Vp8hd5svizrjadF3halM6bPJPKtYtGgn3idiuMpyGYI-1690214544-0-0.2.1690214544; __Secure-access-token=3.70187620.1mXdI0UCTqWtg_iaPakq8A.85.l8cMBQAAAABkvqCQAAAAAKN3ZWKrNzkwNjkzODk5NjkAgJCg.20210628064922.20230724180224.z67FWQK8KOwq1ktIQ6BlCn4e6C1nm7bGdXlOJnDW8Ug; __Secure-refresh-token=3.70187620.1mXdI0UCTqWtg_iaPakq8A.85.l8cMBQAAAABkvqCQAAAAAKN3ZWKrNzkwNjkzODk5NjkAgJCg.20210628064922.20230724180224.jYxDnuMLj9EdLR3F8X0a1R_o-0UFWw05Mq1a_AQXasU; bacntid=2726135; contentId=1213833";
        reviewService.getReviews(pageFilter, token);

        log.error("REVIEW AMOUNT IN STORAGE: " + reviewStorage.getReviews().size());

        Map<Integer, Integer> ratings = reviewStorage.getRatings();
        ratings.forEach((key, value) -> log.atInfo().log(String.format("RATING %d: %d", key, value)));
    }
}
