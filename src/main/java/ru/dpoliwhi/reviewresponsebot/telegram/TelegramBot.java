package ru.dpoliwhi.reviewresponsebot.telegram;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.dpoliwhi.reviewresponsebot.config.BotConfig;
import ru.dpoliwhi.reviewresponsebot.model.request.PageFilter;
import ru.dpoliwhi.reviewresponsebot.registration.AuthInfo;
import ru.dpoliwhi.reviewresponsebot.service.ReviewService;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final String GET_COMPANY_ID_MESSAGE = "Введи id компании";
    private final String GET_TOKEN_MESSAGE = "Введи токен";

    private AuthInfo authInfo;

    private boolean isStarted = false;

    private final BotConfig botConfig;

    private ReviewService reviewService;

    @Autowired
    public TelegramBot(BotConfig botConfig, ReviewService reviewService, AuthInfo authInfo) {
        this.botConfig = botConfig;
        this.reviewService = reviewService;
        this.authInfo = authInfo;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "Привет, " + name + "!" + "\n" +
                "этот бот позволяет получить отзывы о товарах в твоем магазине OzonSeller";
        // TODO добавить вводный текст
        // TODO добавить аналитику????
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (!isStarted && !messageText.equals("/start")) {
                sendMessage(chatId, "Присылай команду /start");
            } else if (messageText.equals("/start")) {
                authInfo.clearAuthInfo();
                isStarted = true;
                startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                sendMessage(chatId, GET_COMPANY_ID_MESSAGE);
            } else if (authInfo.isEmpty()) {
                authInfo.setSellerId(messageText);
                sendMessage(chatId, GET_TOKEN_MESSAGE);

//                authInfo.requestToken();
            }
            else if (authInfo.getSellerId() != null && authInfo.getToken() == null) {
                authInfo.setToken(messageText);
                sendMessage(chatId, "Твои данные получены, давай начинать!");

                log.atInfo().log("company_ID: " + authInfo.getSellerId());
                log.atInfo().log("TOKEN: " + authInfo.getToken());

                sendMessage(chatId, "Получаем твои отзывы...");
                getReviews(chatId);
            } else {
                sendMessage(chatId, "Ты уже зарегистрировался");
            }
        }
    }

    private void getReviews(long chatId) {
        PageFilter pageFilter = reviewService.getFilter(authInfo.getSellerId());
        reviewService.getReviews(pageFilter, authInfo.getToken());

        int countOfReviews = reviewService.getCountOfReviews();
        sendMessage(chatId, "Получено " + countOfReviews + " отзывов");

        Map<Integer, Integer> ratings = reviewService.getRatings();
        StringBuilder ratingsString = new StringBuilder();
        ratings.forEach((key, value) -> ratingsString.append(String.format("RATING %d: %d%n", key, value)));
        sendMessage(chatId, ratingsString.toString());
    }
}
