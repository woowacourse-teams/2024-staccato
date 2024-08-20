package com.staccato.config.log;

public class LogForm {
    private static final String DELIMITER = ",\n    ";
    private static final String INDENT = "    ";

    public static final String REQUEST_LOGGING_FORM = "\n{\n" +
            INDENT + "\"httpStatus\": \"{}\"" + DELIMITER +
            "\"httpMethod\": \"{}\"" + DELIMITER +
            "\"requestUri\": \"{}\"" + DELIMITER +
            "\"tokenExists\": \"{}\"" + DELIMITER +
            "\"processingTimeMs\": \"{}\"\n" +
            "}";

    public static final String LOGIN_MEMBER_FORM = "\n{\n" +
            INDENT + "\"loginMemberId\": \"{}\"" + DELIMITER +
            "\"loginMemberNickname\": \"{}\"\n" +
            "}";

    public static final String CUSTOM_EXCEPTION_LOGGING_FORM = "\n{\n" +
            INDENT + "\"exceptionResponse\": \"{}\"\n" +
            "}";

    public static final String EXCEPTION_LOGGING_FORM = "\n{\n" +
            INDENT + "\"exceptionResponse\": \"{}\"\n" + DELIMITER +
            "\"exceptionMessage\": \"{}\"\n" +
            "}";

    public static final String ERROR_LOGGING_FORM = "\n{\n" +
            INDENT + "\"exceptionResponse\": \"{}\"" + DELIMITER +
            "\"exceptionMessage\": \"{}\"\n" +
            "}";
}
