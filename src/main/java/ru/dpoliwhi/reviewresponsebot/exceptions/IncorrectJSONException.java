package ru.dpoliwhi.reviewresponsebot.exceptions;

public class IncorrectJSONException extends RuntimeException {
    private static final long serialVersionUID = -3794775834824115544L;

    public IncorrectJSONException(String msg) {
        super(msg);
    }
}