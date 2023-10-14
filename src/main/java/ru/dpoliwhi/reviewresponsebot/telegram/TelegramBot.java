package ru.dpoliwhi.reviewresponsebot.telegram;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.dpoliwhi.reviewresponsebot.config.BotConfig;
import ru.dpoliwhi.reviewresponsebot.model.getreviews.request.PageFilter;
import ru.dpoliwhi.reviewresponsebot.model.getreviews.request.enums.InteractionStatus;
import ru.dpoliwhi.reviewresponsebot.registration.AuthInfo;
import ru.dpoliwhi.reviewresponsebot.service.AdminService;
import ru.dpoliwhi.reviewresponsebot.service.ResponseService;
import ru.dpoliwhi.reviewresponsebot.service.ReviewService;

import java.util.Map;

@Log4j2
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final String GET_COMPANY_ID_MESSAGE = "Введи id компании";

    private final String GET_TOKEN_MESSAGE = "Введи токен";

    private AuthInfo authInfo;

    private boolean isStarted = false;

    private boolean isRegistered = false;

    private boolean isAdmin = false;

    private boolean isReviewsReceived = false;

    private final BotConfig botConfig;

    private ReviewService reviewService;

    private ResponseService responseService;

    private AdminService adminService;

    @Autowired
    public TelegramBot(BotConfig botConfig, ReviewService reviewService, AuthInfo authInfo, AdminService adminService, ResponseService responseService) {
        this.botConfig = botConfig;
        this.reviewService = reviewService;
        this.authInfo = authInfo;
        this.adminService = adminService;
        this.responseService = responseService;
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

            if (messageText.equals("/start")) {
                isAdmin = false;
                isStarted = false;
                isRegistered = false;
            }

            if (messageText.equals("/admin")) {
                isAdmin = true;
                isStarted = false;
                isRegistered = false;
                createAndSendMessage(chatId, "Введи пароль учетки администратора");
                return;
            }

            if (isAdmin) {
                processAdminLogic(messageText, chatId);
                return;
            }

            if (!isStarted) {

                if (!messageText.equals("/start")) {
                    createAndSendMessage(chatId, "Присылай команду /start");
                } else {
                    isAdmin = false;
                    startBot(update);
                }
            } else {
                if (!isRegistered) {
                    registration(chatId, messageText);
                }

                if (messageText.equals("/start")) {
                    isStarted = false;
                    isRegistered = false;
                    onUpdateReceived(update);
                }
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            getReviews(callbackData, chatId);
            startToProceedOrRepeatFacets(callbackData, chatId);
        }
    }

    private void processAdminLogic(String messageText, long chatId) {
        if (!isRegistered) {
            if (adminService.loginAdmin(messageText)) {
                isRegistered = true;
                createAndSendMessage(chatId, "Введи новый ответ на отзыв. Замени имя покупетля символами %s.");
            } else {
                createAndSendMessage(chatId, "Пароль неверный. Попробуй еще раз");
            }
        } else {
            adminService.addNewReviewToDB(messageText);
            createAndSendMessage(chatId, "Отзыв записан. Пиши еще или вводи /start для входа как пользователь");
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

            chooseReviewsFacet(chatId);
        }
    }

    private void getReviews(String callbackData, Long chatId) {
        if (callbackData.equals(ButtonUtils.NEW_REVIEWS)) {
            getReviewsFromOzon(chatId, InteractionStatus.NOT_VIEWED);
            chooseStartOrRepeat(chatId);
        } else if (callbackData.equals(ButtonUtils.VIEWED_REVIEWS)) {
            getReviewsFromOzon(chatId, InteractionStatus.VIEWED);
            chooseStartOrRepeat(chatId);
        }
    }

    private void startToProceedOrRepeatFacets(String callbackData, Long chatId) {
        if (callbackData.equals(ButtonUtils.PROCESS_REVIEWS)) {
            responseService.sendResponses(authInfo.getToken());
        } else if (callbackData.equals(ButtonUtils.REPEAT_FACETS)) {
            chooseReviewsFacet(chatId);
        }
    }

    private void chooseReviewsFacet(Long chatId) {
        createAndSendMessage(chatId, "Какие отзывы ты хочешь получить?", ButtonUtils.getReviewFacetButtons());
    }

    private void chooseStartOrRepeat(Long chatId) {
        createAndSendMessage(chatId, "Отвечаем на отзывы?", ButtonUtils.getStartButtonOrRepeatFacets());
    }

    private void getReviewsFromOzon(long chatId, InteractionStatus facet) {
        reviewService.clearReviewStorage();
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
