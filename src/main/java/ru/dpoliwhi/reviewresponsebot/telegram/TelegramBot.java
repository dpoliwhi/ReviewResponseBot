package ru.dpoliwhi.reviewresponsebot.telegram;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.dpoliwhi.reviewresponsebot.config.BotConfig;
import ru.dpoliwhi.reviewresponsebot.model.request.PageFilter;
import ru.dpoliwhi.reviewresponsebot.model.request.enums.InteractionStatus;
import ru.dpoliwhi.reviewresponsebot.registration.AuthInfo;
import ru.dpoliwhi.reviewresponsebot.service.ReviewService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final String GET_COMPANY_ID_MESSAGE = "Введи id компании";
    private final String GET_TOKEN_MESSAGE = "Введи токен";
    private final String NEW_REVIEWS = "new";
    private final String VIEWED_REVIEWS = "viewed";

    private AuthInfo authInfo;

    private boolean isStarted = false;
    private boolean isRegistered = false;
    private boolean isReviewsReceived = false;

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
        createAndSendMessage(chatId, answer);
    }

    private void sendMessage(SendMessage message, Long chatId, String textToSend) {
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void createAndSendMessage(Long chatId, String textToSend, InlineKeyboardMarkup markup) {
        SendMessage message = new SendMessage();
        message.setReplyMarkup(markup);
        sendMessage(message, chatId, textToSend);
    }

    private void createAndSendMessage(Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        sendMessage(message, chatId, textToSend);
    }



    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (!isStarted) {
                if (!messageText.equals("/start")) {
                    createAndSendMessage(chatId, "Присылай команду /start");
                } else {
                    startBot(update);
                }
            } else {
                if (!isRegistered) {
                    registration(chatId, messageText);
                } else {
                    if (!isReviewsReceived) {
                        getReviews(update);
                    }
                }


                if (messageText.equals("/start")) {
                    isStarted = false;
                    isRegistered = false;
                    onUpdateReceived(update);
//                    startBot(update);
                }
            }



//            if (!isStarted && !messageText.equals("/start")) {
//                createAndSendMessage(chatId, "Присылай команду /start");
//            } else if (messageText.equals("/start")) {
//                authInfo.clearAuthInfo();
//                isStarted = true;
//                startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
//                createAndSendMessage(chatId, GET_COMPANY_ID_MESSAGE);
//            } else if (authInfo.isEmpty()) {
//                authInfo.setSellerId(messageText);
//                createAndSendMessage(chatId, GET_TOKEN_MESSAGE);
//
////                authInfo.requestToken();
//            } else if (authInfo.getSellerId() != null && authInfo.getToken() == null) {
//                authInfo.setToken(messageText);
//                createAndSendMessage(chatId, "Твои данные получены, давай начинать!");
//
//                log.atInfo().log("company_ID: " + authInfo.getSellerId());
//                log.atInfo().log("TOKEN: " + authInfo.getToken());
//
//                chooseReviewsFacet(chatId);
////                createAndSendMessage(chatId, "Получаем твои отзывы...");
////                getReviews(chatId);
//            } else {
//                createAndSendMessage(chatId, "Ты уже зарегистрировался");
//            }
        }
    }

    private void startBot(Update update) {
        long chatId = update.getMessage().getChatId();
        authInfo.clearAuthInfo();
        isRegistered = false;
        isStarted = true;
        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
        createAndSendMessage(chatId, GET_COMPANY_ID_MESSAGE);
    }

    private void registration(long chatId, String messageText) {
        if (authInfo.isEmpty()) {
            authInfo.setSellerId(messageText);
            createAndSendMessage(chatId, GET_TOKEN_MESSAGE);
        } else if (authInfo.getSellerId() != null && authInfo.getToken() == null) {
            authInfo.setToken(messageText);
            createAndSendMessage(chatId, "Твои данные получены, давай начинать!");
            isRegistered = true;

            log.atInfo().log("company_ID: " + authInfo.getSellerId());
            log.atInfo().log("TOKEN: " + authInfo.getToken());
        }
    }

    private void getReviews(Update update) {
        if (!update.hasCallbackQuery()) {
            chooseReviewsFacet(update.getMessage().getChatId());
        } else {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();

            if (callbackData.equals(NEW_REVIEWS)) {
                getReviewsFromOzon(chatId, InteractionStatus.NOT_VIEWED);
            } else if (callbackData.equals(VIEWED_REVIEWS)) {
                getReviewsFromOzon(chatId, InteractionStatus.VIEWED);
            }
        }
    }

    private void chooseReviewsFacet(Long chatId) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Новые");
        button1.setCallbackData(NEW_REVIEWS);

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Просмотренные");
        button2.setCallbackData(VIEWED_REVIEWS);

        rowInline.add(button1);
        rowInline.add(button2);

        rowsInline.add(rowInline);

        markupInline.setKeyboard(rowsInline);

        createAndSendMessage(chatId, "Какие отзывы ты хочешь получить?", markupInline);
    }

    private void getReviewsFromOzon(long chatId, InteractionStatus facet) {
        PageFilter pageFilter = reviewService.getFilter(authInfo.getSellerId(), facet);
        reviewService.getReviews(pageFilter, authInfo.getToken());

        int countOfReviews = reviewService.getCountOfReviews();
        createAndSendMessage(chatId, "Получено " + countOfReviews + " отзывов");
        isReviewsReceived = true;

        Map<Integer, Integer> ratings = reviewService.getRatings();
        StringBuilder ratingsString = new StringBuilder();
        ratings.forEach((key, value) -> ratingsString.append(String.format("RATING %d: %d%n", key, value)));
        createAndSendMessage(chatId, ratingsString.toString());
    }
}
