package ru.dpoliwhi.reviewresponsebot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.dpoliwhi.reviewresponsebot.repositories.ResponseRepository;
import ru.dpoliwhi.reviewresponsebot.repositories.enities.Response;

@Service
public class AdminService {

    private final String ADMIN_PASSWORD = "0009";

    private ResponseRepository responseRepository;

    @Autowired
    public AdminService(ResponseRepository responseRepository) {
        this.responseRepository = responseRepository;
    }

    public boolean loginAdmin(String password) {
        return password.equals(ADMIN_PASSWORD);
    }

    public void addNewReviewToDB(String reviewText) {
        Response review = new Response();
        review.setResponseText(reviewText);
        responseRepository.save(review);
    }
}
