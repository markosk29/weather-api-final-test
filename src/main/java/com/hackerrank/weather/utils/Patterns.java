package com.hackerrank.weather.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

/**
 * Default RegularExpression patterns.<br/>
 * Put all patterns in one place like this class
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Patterns {

    /**
     * YYYY_MM_DD_PATTERN
     */
    public static final String YYYY_MM_DD_REGEXP = "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$";
    public static final Pattern YYYY_MM_DD_PATTERN = Pattern.compile(YYYY_MM_DD_REGEXP);
}
