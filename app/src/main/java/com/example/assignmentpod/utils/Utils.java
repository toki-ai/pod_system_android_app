package com.example.assignmentpod.utils;

import android.util.Log;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class Utils {
    public static String convertDisplayDateToApi(String TAG, String displayDate) {
        SimpleDateFormat display = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat api = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            return api.format(Objects.requireNonNull(display.parse(displayDate)));
        } catch (ParseException e) {
            Log.e(TAG, "Date parse error", e);
            return displayDate;
        }
    }
    public static String formatPrice(int price) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(price).replace("₫", "VND");
    }

    public static String formatPrice(double price) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(price).replace("₫", "VND");
    }
}
