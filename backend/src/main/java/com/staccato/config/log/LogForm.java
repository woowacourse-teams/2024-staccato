package com.staccato.config.log;

public class LogForm {
    public static final String REQUEST_LOGGING_FORM =
            "HTTP Method : {} " + "Request URI : {} " + "HTTP Status : {} " + "Processing Time(ms) : {}";
    public static final String EXCEPTION_LOGGING_FORM = "Exception Response : {} ";
    public static final String ERROR_LOGGING_FORM = "Exception Response : {} " + "Exception Message : {}";
}
