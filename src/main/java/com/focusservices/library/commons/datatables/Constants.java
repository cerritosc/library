package com.focusservices.library.commons.datatables;

public class Constants {

    public static final String RGX_GREATER_THAN_OR_EQUAL_TO = "gtoe";
    public static final String RGX_LESS_THAN_OR_EQUAL_TO = "ltoe";
    public static final String RGX_BETWEEN = "btw";
    public static final String FUNCTION_TRUNC = "DATE_TRUNC";
    public static final String FUNCTION_TO_DATE = "TO_DATE";
    public static final String FUNCTION_TO_CHAR = "TO_CHAR";
    public static final Boolean IS_FILTER_CRITERIA_CASE_SENSITIVE = Boolean.FALSE;

    private Constants() {
        throw new IllegalStateException("Constants class must not be instantiated!");
    }

}
