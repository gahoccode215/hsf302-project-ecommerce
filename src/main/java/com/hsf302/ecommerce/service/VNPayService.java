package com.hsf302.ecommerce.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPayService {


    @Value("${vnpay.tmnCode}")
    private String tmnCode;

    @Value("${vnpay.hashSecret}")
    private String hashSecret;

    @Value("${vnpay.url}")
    private String vnpUrl;


    public String createPaymentUrl(Long orderId, Double amount, String ipAddress) throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<>();
        String returnUrl1 = "http://localhost:5173/payment-success?orderId=" + orderId;
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", tmnCode);
        params.put("vnp_Amount", String.valueOf(amount.longValue() * 100));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", orderId.toString());
        params.put("vnp_OrderInfo", "Thanh toan don hang#" + orderId);
        params.put("vnp_OrderType", "other"); // Loại hàng hóa, dịch vụ
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", returnUrl1);
        params.put("vnp_IpAddr", ipAddress);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String createDate = sdf.format(new Date());
        params.put("vnp_CreateDate", createDate);

        Calendar expireCalendar = Calendar.getInstance();
        expireCalendar.setTime(new Date());
        expireCalendar.add(Calendar.MINUTE, 50);
        String expireDate = sdf.format(expireCalendar.getTime());
        params.put("vnp_ExpireDate", expireDate);

        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8)).append('&');
                query.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8)).append('&');
            }
        }
        hashData.deleteCharAt(hashData.length() - 1); // Xóa ký tự '&' cuối cùng
        query.deleteCharAt(query.length() - 1); // Xóa ký tự '&' cuối cùng
        String secureHash = hmacSHA512(hashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(URLEncoder.encode(secureHash, StandardCharsets.UTF_8));
        return vnpUrl + "?" + query;
    }


    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKeySpec);
            byte[] hash = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Lỗi khi tạo chữ ký HMAC SHA512", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public boolean validateCallback(Map<String, String> params) throws UnsupportedEncodingException {
        String receivedHash = params.get("vnp_SecureHash");
        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString());
                hashData.append(fieldName).append('=').append(encodedValue).append('&');
            }
        }
        if (hashData.length() > 0) {
            hashData.deleteCharAt(hashData.length() - 1);
        }
        String calculatedHash = hmacSHA512(hashSecret, hashData.toString());
        if (!calculatedHash.equalsIgnoreCase(receivedHash)) {
            return false;
        }
        return true;
    }

}
