package com.staccato.config.log;

public class LogForm {
    private static final String START_DELIMITER = "[";
    private static final String END_DELIMITER = "]";
    private static final String DELIMITER = " | ";
    public static final String REQUEST_LOGGING_FORM = START_DELIMITER +
            "HTTP Status : {}" + DELIMITER +
            "HTTP Method : {}" + DELIMITER +
            "Request URI : {}" + DELIMITER +
            "Token is Exist : {}" + DELIMITER +
            "Processing Time(ms) : {}" + END_DELIMITER;
    public static final String LOGIN_MEMBER_FORM = START_DELIMITER +
            "Login Member ID : {}" + DELIMITER +
            "Login Member Nickname : {}" + END_DELIMITER;
    public static final String EXCEPTION_LOGGING_FORM = "Exception Response : {} ";
    public static final String ERROR_LOGGING_FORM = "Exception Response : {} " + DELIMITER + "Exception Message : {}";
}
