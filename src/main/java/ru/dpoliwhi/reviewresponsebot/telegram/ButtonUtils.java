package ru.dpoliwhi.reviewresponsebot.telegram;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class ButtonUtils {

    public static final String NEW_REVIEWS = "new";

    public static final String VIEWED_REVIEWS = "viewed";

    public static final String PROCESS_REVIEWS = "process";

    public static final String REPEAT_FACETS = "repeat";

    public static InlineKeyboardMarkup getReviewFacetButtons() {
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

        return markupInline;
    }

    public static InlineKeyboardMarkup getStartButtonOrRepeatFacets() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Отвечаем!");
        button1.setCallbackData(PROCESS_REVIEWS);

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Посмотреть другие отзывы");
        button2.setCallbackData(REPEAT_FACETS);

        rowInline.add(button1);
        rowInline.add(button2);

        rowsInline.add(rowInline);

        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }
}
