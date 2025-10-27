package com.example.assignmentpod.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.assignmentpod.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentRepository {
    private static final String TAG = "PaymentRepository";

    public interface PaymentCallback {
        void onSuccess(String paymentUrl);
        void onError(String error);
    }

    // Tạo đơn hàng MoMo
    public void createMoMoOrder(Context context, int amount, PaymentCallback callback) {
        new Thread(() -> {
            try {
                String orderId = Constants.MOMO_PARTNER_CODE + System.currentTimeMillis();
                String requestId = orderId;
                String orderInfo = "Thanh toán MoMo - Đơn hàng #" + orderId;
                String redirectUrl = Constants.MOMO_REDIRECT_URL;
                String ipnUrl = Constants.MOMO_IPN_URL;
                String requestType = "payWithMethod";
                String extraData = "";

                String rawSignature = "accessKey=" + Constants.MOMO_ACCESS_KEY +
                        "&amount=" + amount +
                        "&extraData=" + extraData +
                        "&ipnUrl=" + ipnUrl +
                        "&orderId=" + orderId +
                        "&orderInfo=" + orderInfo +
                        "&partnerCode=" + Constants.MOMO_PARTNER_CODE +
                        "&redirectUrl=" + redirectUrl +
                        "&requestId=" + requestId +
                        "&requestType=" + requestType;

                String signature = hmacSHA256(rawSignature, Constants.MOMO_SECRET_KEY);

                JSONObject requestBody = new JSONObject();
                requestBody.put("partnerCode", Constants.MOMO_PARTNER_CODE);
                requestBody.put("partnerName", "test");
                requestBody.put("storeId", "MomotestStore");
                requestBody.put("accessKey", Constants.MOMO_ACCESS_KEY);
                requestBody.put("requestId", requestId);
                requestBody.put("amount", amount);
                requestBody.put("orderId", orderId);
                requestBody.put("orderInfo", orderInfo);
                requestBody.put("redirectUrl", redirectUrl);
                requestBody.put("ipnUrl", ipnUrl);
                requestBody.put("extraData", extraData);
                requestBody.put("requestType", requestType);
                requestBody.put("signature", signature);
                requestBody.put("lang", "vi");

                URL url = new URL(Constants.MOMO_ENDPOINT);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                conn.getOutputStream().write(requestBody.toString().getBytes(StandardCharsets.UTF_8));
                conn.getOutputStream().flush();
                conn.getOutputStream().close();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A");
                    String responseBody = scanner.hasNext() ? scanner.next() : "";
                    JSONObject response = new JSONObject(responseBody);
                    int resultCode = response.getInt("resultCode");
                    String message = response.getString("message");

                    if (resultCode == 0) {
                        String payUrl = response.getString("payUrl");
                        callback.onSuccess(payUrl);
                    } else {
                        callback.onError("Lỗi từ MoMo: " + message);
                    }
                } else {
                    callback.onError("Lỗi kết nối MoMo: " + responseCode);
                }
            } catch (Exception e) {
                callback.onError("Lỗi MoMo: " + e.getMessage());
            }
        }).start();
    }

    // Tạo đơn hàng ZaloPay
    public void createZaloPayOrder(Context context, int amount, PaymentCallback callback) {
        new Thread(() -> {
            try {
                String appTransId = getVietnamDatePrefix() + "_" + (System.currentTimeMillis() % 1000000);
                String appUser = "ZaloPayDemo";
                long appTime = System.currentTimeMillis();
                String description = "Thanh toán ZaloPay - Đơn hàng #" + appTransId;
                String callbackUrl = Constants.ZALOPAY_REDIRECT_URL;

                JSONObject embedDataObj = new JSONObject();
                embedDataObj.put("redirecturl", callbackUrl);
                String embedDataStr = embedDataObj.toString();

                JSONArray itemsArr = new JSONArray();
                JSONObject itemObj = new JSONObject();
                itemObj.put("itemid", "knb");
                itemObj.put("itemname", "kim nguyen bao");
                itemObj.put("itemprice", amount);
                itemObj.put("itemquantity", 1);
                itemsArr.put(itemObj);
                String itemStr = itemsArr.toString();

                String data = Constants.ZALOPAY_APP_ID + "|" + appTransId + "|" + appUser + "|" +
                        amount + "|" + appTime + "|" + embedDataStr + "|" + itemStr;
                String mac = hmacSHA256(data, Constants.ZALOPAY_KEY1);

                JSONObject requestBody = new JSONObject();
                requestBody.put("app_id", Integer.parseInt(Constants.ZALOPAY_APP_ID));
                requestBody.put("app_user", appUser);
                requestBody.put("app_time", appTime);
                requestBody.put("amount", amount);
                requestBody.put("app_trans_id", appTransId);
                requestBody.put("bank_code", "");
                requestBody.put("embed_data", embedDataStr);
                requestBody.put("item", itemStr);
                requestBody.put("callback_url", callbackUrl);
                requestBody.put("description", description);
                requestBody.put("mac", mac);

                URL url = new URL(Constants.ZALOPAY_ENDPOINT);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                conn.getOutputStream().write(requestBody.toString().getBytes(StandardCharsets.UTF_8));
                conn.getOutputStream().flush();
                conn.getOutputStream().close();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A");
                    String responseBody = scanner.hasNext() ? scanner.next() : "";
                    JSONObject response = new JSONObject(responseBody);
                    int returnCode = response.getInt("return_code");
                    String returnMessage = response.getString("return_message");

                    if (returnCode == 1) {
                        String orderUrl = response.getString("order_url");
                        callback.onSuccess(orderUrl);
                    } else {
                        callback.onError("Lỗi từ ZaloPay: " + returnMessage);
                    }
                } else {
                    callback.onError("Lỗi kết nối ZaloPay: " + responseCode);
                }
            } catch (Exception e) {
                callback.onError("Lỗi ZaloPay: " + e.getMessage());
            }
        }).start();
    }

    private String getVietnamDatePrefix() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+7"));
        return sdf.format(new Date());
    }

    private String hmacSHA256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKey);
        byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}