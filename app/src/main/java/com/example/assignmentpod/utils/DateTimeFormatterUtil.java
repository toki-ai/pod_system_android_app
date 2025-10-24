package com.example.assignmentpod.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for formatting dates and times
 */
public class DateTimeFormatterUtil {
    private static final Locale VIETNAM_LOCALE = new Locale("vi", "VN");

    /**
     * Format date for display (e.g., "Oct 22, 2025 2:30 PM")
     * @param dateString ISO 8601 formatted date string
     * @return Formatted date string
     */
    public static String formatOrderDate(String dateString) {
        try {
            DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime dateTime = LocalDateTime.parse(dateString, isoFormatter);
            DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern("MMM dd, yyyy h:mm a")
                    .withLocale(VIETNAM_LOCALE);
            return dateTime.format(formatter);
        } catch (Exception e) {
            return dateString;
        }
    }

    /**
     * Format date in Vietnamese style (e.g., "22/10/2025 14:30")
     * @param dateString ISO 8601 formatted date string
     * @return Formatted date string in Vietnamese format
     */
    public static String formatDateVietnamese(String dateString) {
        try {
            DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime dateTime = LocalDateTime.parse(dateString, isoFormatter);
            DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern("dd/MM/yyyy HH:mm");
            return dateTime.format(formatter);
        } catch (Exception e) {
            return dateString;
        }
    }

    /**
     * Format time for display (e.g., "09:00 - 12:00")
     * @param startTimeString ISO 8601 formatted start time
     * @param endTimeString ISO 8601 formatted end time
     * @return Formatted time range
     */
    public static String formatTimeRange(String startTimeString, String endTimeString) {
        try {
            DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime startTime = LocalDateTime.parse(startTimeString, isoFormatter);
            LocalDateTime endTime = LocalDateTime.parse(endTimeString, isoFormatter);
            
            DateTimeFormatter timeFormatter = DateTimeFormatter
                    .ofPattern("HH:mm");
            
            return startTime.format(timeFormatter) + " - " + endTime.format(timeFormatter);
        } catch (Exception e) {
            return startTimeString + " - " + endTimeString;
        }
    }

    /**
     * Calculate duration between two times
     * @param startTimeString ISO 8601 formatted start time
     * @param endTimeString ISO 8601 formatted end time
     * @return Duration string like "3 hours" or "1 hour 30 minutes"
     */
    public static String calculateDuration(String startTimeString, String endTimeString) {
        try {
            DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime startTime = LocalDateTime.parse(startTimeString, isoFormatter);
            LocalDateTime endTime = LocalDateTime.parse(endTimeString, isoFormatter);
            
            long hours = ChronoUnit.HOURS.between(startTime, endTime);
            long minutes = ChronoUnit.MINUTES.between(
                    startTime.plusHours(hours), 
                    endTime
            );
            
            if (hours == 0 && minutes > 0) {
                return minutes + " minute" + (minutes > 1 ? "s" : "");
            } else if (hours > 0 && minutes == 0) {
                return hours + " hour" + (hours > 1 ? "s" : "");
            } else if (hours > 0 && minutes > 0) {
                return hours + " hour" + (hours > 1 ? "s" : "") + " " + 
                       minutes + " minute" + (minutes > 1 ? "s" : "");
            } else {
                return "0 minutes";
            }
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Get relative time string (e.g., "2 days ago")
     * @param dateString ISO 8601 formatted date string
     * @return Relative time string or formatted date if not recent
     */
    public static String getRelativeTime(String dateString) {
        try {
            DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime dateTime = LocalDateTime.parse(dateString, isoFormatter);
            LocalDateTime now = LocalDateTime.now();
            
            long secondsAgo = ChronoUnit.SECONDS.between(dateTime, now);
            
            if (secondsAgo < 60) {
                return "just now";
            }
            
            long minutesAgo = ChronoUnit.MINUTES.between(dateTime, now);
            if (minutesAgo < 60) {
                return minutesAgo + " minute" + (minutesAgo > 1 ? "s" : "") + " ago";
            }
            
            long hoursAgo = ChronoUnit.HOURS.between(dateTime, now);
            if (hoursAgo < 24) {
                return hoursAgo + " hour" + (hoursAgo > 1 ? "s" : "") + " ago";
            }
            
            long daysAgo = ChronoUnit.DAYS.between(dateTime, now);
            if (daysAgo < 7) {
                return daysAgo + " day" + (daysAgo > 1 ? "s" : "") + " ago";
            }
            
            // Return formatted date if older than 7 days
            return formatOrderDate(dateString);
        } catch (Exception e) {
            return dateString;
        }
    }

    /**
     * Format short date (e.g., "Oct 22")
     * @param dateString ISO 8601 formatted date string
     * @return Formatted short date
     */
    public static String formatShortDate(String dateString) {
        try {
            DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime dateTime = LocalDateTime.parse(dateString, isoFormatter);
            DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern("MMM dd")
                    .withLocale(VIETNAM_LOCALE);
            return dateTime.format(formatter);
        } catch (Exception e) {
            return dateString;
        }
    }
}
