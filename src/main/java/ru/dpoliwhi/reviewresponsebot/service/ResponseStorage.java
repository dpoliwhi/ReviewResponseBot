package ru.dpoliwhi.reviewresponsebot.service;

import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.dpoliwhi.reviewresponsebot.repositories.ResponseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class ResponseStorage {

    private List<String> responses;

    private final Random random = new Random();

    private ResponseRepository responseRepository;

    @Autowired
    public ResponseStorage(ResponseRepository responseRepository) {
        this.responseRepository = responseRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    private void init() {
        responses = new ArrayList<>();
        responseRepository.findAll().forEach(r -> responses.add(r.getResponseText()));
    }

    public String getRandomResponse() {
        return responses.get((random.nextInt(responses.size())));
    }
}
