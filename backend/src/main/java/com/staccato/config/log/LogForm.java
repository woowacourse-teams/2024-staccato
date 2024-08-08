package com.staccato.config.log;

public class LogForm {
    private static final String DELIMITER = " | ";
    public static final String REQUEST_LOGGING_FORM =
            "HTTP Method : {}" + DELIMITER + "Request URI : {}" + DELIMITER + "Token is Exist : {}" + DELIMITER + "HTTP Status : {}" + DELIMITER + "Processing Time(ms) : {}";
    public static final String EXCEPTION_LOGGING_FORM = "Exception Response : {} ";
    public static final String ERROR_LOGGING_FORM = "Exception Response : {} " + DELIMITER + "Exception Message : {}";
}
