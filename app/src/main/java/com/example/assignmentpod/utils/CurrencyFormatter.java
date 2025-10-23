package com.example.assignmentpod.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Utility class for formatting currency values in Vietnamese Dong (VND)
 */
public class CurrencyFormatter {
    private static final Locale VIETNAM_LOCALE = new Locale("vi", "VN");
    private static final DecimalFormat decimalFormat;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(VIETNAM_LOCALE);
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        decimalFormat = new DecimalFormat("#,##0", symbols);
    }

    /**
     * Format amount as Vietnamese Dong currency
     * @param amount The amount to format
     * @return Formatted string like "150,000 VND"
     */
    public static String formatVND(double amount) {
        return decimalFormat.format(amount) + " VND";
    }

    /**
     * Format amount as Vietnamese Dong with symbol
     * @param amount The amount to format
     * @return Formatted string like "150,000 đ"
     */
    public static String formatVNDWithSymbol(double amount) {
        return decimalFormat.format(amount) + " đ";
    }

    /**
     * Format amount without currency unit
     * @param amount The amount to format
     * @return Formatted string like "150,000"
     */
    public static String formatAmount(double amount) {
        return decimalFormat.format(amount);
    }

    /**
     * Parse formatted currency string back to double
     * @param formattedAmount Formatted amount string
     * @return Double value
     */
    public static double parseFormattedAmount(String formattedAmount) {
        try {
            // Remove currency symbols and spaces
            String cleaned = formattedAmount
                    .replaceAll("[^0-9,.]", "")
                    .replaceAll(",", "");
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Calculate discount from original amount and discount percentage
     * @param originalAmount The original amount
     * @param discountPercentage The discount percentage (0-100)
     * @return The discount amount
     */
    public static double calculateDiscount(double originalAmount, double discountPercentage) {
        return (originalAmount * discountPercentage) / 100;
    }

    /**
     * Calculate final amount after discount
     * @param originalAmount The original amount
     * @param discountPercentage The discount percentage (0-100)
     * @return The final amount after discount
     */
    public static double calculateFinalAmount(double originalAmount, double discountPercentage) {
        return originalAmount - calculateDiscount(originalAmount, discountPercentage);
    }
}
