package com.example.blackbox.utils;

import java.text.DecimalFormat;

/**
 * This class provides static methods for formatting certain data
 * common throughout the program as strings
 */
public class StringFormatter {
    /**
     * Converts a double into a monetary value string to be displayed
     * @param value
     *      The value that we want to represent
     * @return
     *      The string representation
     */
    public static String getMonetaryString(Double value){
        // Define the desired format
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        String formattedStr = "$" + decimalFormat.format(value);

        return formattedStr;
    }

}
