package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.common.GsonUtil;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.config.OnlinePaymentConfig;
import com.ptit.coffee_shop.payload.response.PaymentResponse;
import com.ptit.coffee_shop.payload.response.RespMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OnlinePaymentService {

    @Autowired
    private MessageBuilder messageBuilder;

    public RespMessage createVNPayPayment(int amount, HttpServletRequest request) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be greater than 0");
        }

        String vnp_TxnRef = OnlinePaymentConfig.getRandomNumber(8);
        String vnp_IpAddr = OnlinePaymentConfig.getIpAddress(request);
        String vnp_Amount = String.valueOf(amount * 100);

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", OnlinePaymentConfig.vnp_Version);
        vnp_Params.put("vnp_Command", OnlinePaymentConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", OnlinePaymentConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", vnp_Amount);
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB");

        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", OnlinePaymentConfig.orderType);
        vnp_Params.put("vnp_Locale", "vn");

        vnp_Params.put("vnp_ReturnUrl", OnlinePaymentConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        try {
            String vnp_SecureHash = OnlinePaymentConfig.hmacSHA512(OnlinePaymentConfig.secretKey, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = OnlinePaymentConfig.vnp_PayUrl + "?" + queryUrl;
            PaymentResponse paymentResponse = new PaymentResponse();
            paymentResponse.setStatus("OK");
            paymentResponse.setMessage("Successfully created payment");
            paymentResponse.setURL(paymentUrl);
            return messageBuilder.buildSuccessMessage(paymentResponse);
        } catch (Exception e) {
            throw new RuntimeException("Error in generating secure hash");
        }
    }

//    public boolean verifyVNPayReturn(Map<String, String> fields, String vnp_SecureHash) {
//        fields.remove("vnp_SecureHash");
//        String hashData = OnlinePaymentConfig.hashAllFields(fields);
//        return vnp_SecureHash.equals(hashData);
//    }

    public RespMessage handleVNPayReturn(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String paramName = params.nextElement();
            String paramValue = request.getParameter(paramName);
            if ((paramValue != null) && (!paramValue.isEmpty())) {
                fields.put(paramName, paramValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHash");

        String hashData = OnlinePaymentConfig.hashAllFields(fields);
        if (vnp_SecureHash.equals(hashData)) {
            return messageBuilder.buildSuccessMessage(vnp_SecureHash);
        } else {
            throw new RuntimeException("VNP Secure Hash does not match");
        }
    }
}
