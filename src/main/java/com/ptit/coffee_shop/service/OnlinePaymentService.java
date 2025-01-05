package com.ptit.coffee_shop.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.common.enums.OrderStatus;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.config.OnlinePaymentConfig;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.model.Order;
import com.ptit.coffee_shop.model.Transaction;
import com.ptit.coffee_shop.payload.response.PaymentResponse;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.repository.OrderRepository;
import com.ptit.coffee_shop.repository.TransactionRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.RedirectView;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OnlinePaymentService {

    @Autowired
    private MessageBuilder messageBuilder;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Value("${frontend-url}")
    private String frontEndUrl;

    @Value("${backend-url}")
    private String backEndUrl;


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

        vnp_Params.put("vnp_ReturnUrl", backEndUrl + "/api/payment/return");
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

    public RedirectView handleVNPayReturn(HttpServletRequest request) {
        String responseCode = request.getParameter("vnp_ResponseCode");
        RedirectView redirectView = new RedirectView();
        String status = responseCode.equals("00") ? "success" : "fail";

        String url = frontEndUrl + "/order-status"
                + "?status=" + status;

        if (responseCode.equals("00")) {
            url += "&txnRef=" + request.getParameter("vnp_TxnRef")
                    + "&transactionNo=" + request.getParameter("vnp_TransactionNo")
                    + "&amount=" + request.getParameter("vnp_Amount")
                    + "&payDate=" + request.getParameter("vnp_PayDate");
        }

        redirectView.setUrl(url);
        return redirectView;
    }

    @Transactional
    public RespMessage handleVNPayRefund(long orderId, HttpServletRequest request) {
        try {
            String vnp_RequestId = OnlinePaymentConfig.getRandomNumber(8);
//            Transaction transaction = transactionRepository.getOne(orderId);
            Optional<Transaction> transactionOptional = transactionRepository.findByOrderId(orderId);
            Transaction transaction = new Transaction();
            if( transactionOptional.isPresent() ) {
                transaction = transactionOptional.get();
            } else {
                throw new RuntimeException("Transaction not found");
            }

            String vnp_TxnRef = transaction.getTxnRef();

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_TransactionDate = formatter.format(transaction.getPayDate());
            String vnp_CreateDate = formatter.format(transaction.getPayDate());
            String vnp_IpAddr = OnlinePaymentConfig.getIpAddress(request);
            String vnp_Amount = String.valueOf((long) (transaction.getAmount()));
            String vnp_TransactionNo = transaction.getTransactionNo();
            String vnp_CreateBy = transaction.getOrder().getShippingAddress().getUser().getEmail();
            String vnp_OrderInfo = "Hoan tien GD OrderId:" + vnp_TxnRef;


            JsonObject vnp_Params = new JsonObject ();
            vnp_Params.addProperty("vnp_RequestId", vnp_RequestId);
            vnp_Params.addProperty("vnp_Version", OnlinePaymentConfig.vnp_Version);
            vnp_Params.addProperty("vnp_Command", OnlinePaymentConfig.vnp_Refund);
            vnp_Params.addProperty("vnp_TmnCode", OnlinePaymentConfig.vnp_TmnCode);
            vnp_Params.addProperty("vnp_TransactionType", OnlinePaymentConfig.vnp_TransactionType);
            vnp_Params.addProperty("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.addProperty("vnp_Amount", vnp_Amount);
            vnp_Params.addProperty("vnp_OrderInfo", vnp_OrderInfo);
            vnp_Params.addProperty("vnp_TransactionNo", vnp_TransactionNo);
            vnp_Params.addProperty("vnp_TransactionDate", vnp_TransactionDate);
            vnp_Params.addProperty("vnp_CreateBy", vnp_CreateBy);
            vnp_Params.addProperty("vnp_CreateDate", vnp_CreateDate);
            vnp_Params.addProperty("vnp_IpAddr", vnp_IpAddr);

            String hash_Data= String.join("|", vnp_RequestId, OnlinePaymentConfig.vnp_Version, OnlinePaymentConfig.vnp_Refund, OnlinePaymentConfig.vnp_TmnCode,
                    OnlinePaymentConfig.vnp_TransactionType, vnp_TxnRef, vnp_Amount, vnp_TransactionNo, vnp_TransactionDate,
                    vnp_CreateBy, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);

            String vnp_SecureHash = OnlinePaymentConfig.hmacSHA512(OnlinePaymentConfig.secretKey, hash_Data.toString());

            vnp_Params.addProperty("vnp_SecureHash", vnp_SecureHash);

            URL url = new URL(OnlinePaymentConfig.vnp_ApiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(vnp_Params.toString());
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            System.out.println("Sending 'POST' request to URL : " + url);
            System.out.println("Post Data : " + vnp_Params);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String output;
            StringBuilder response = new StringBuilder();
            while ((output = in.readLine()) != null) {
                response.append(output);
            }
            in.close();

            System.out.println("Response: " + response);

            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();

            String vnp_ResponseCode = jsonResponse.get("vnp_ResponseCode").getAsString();

            if (vnp_ResponseCode.equals("00")) {
                Transaction transaction1 = new Transaction();
                transaction1.setTxnRef(vnp_TxnRef);
                transaction1.setTransactionNo(vnp_TransactionNo);
                transaction1.setPayDate(new Date());
                transaction1.setAmount(transaction.getAmount());
                transaction1.setCommand("refund");
                transaction1.setOrder(transaction.getOrder());
                Optional<Order> order = orderRepository.findById(orderId);
                if (order.isPresent()) {
                    Order order1 = order.get();
                    order1.setStatus(OrderStatus.Cancelled);
                    try {
                        orderRepository.save(order1);
                        transactionRepository.save(transaction1);
                        return messageBuilder.buildSuccessMessage(transaction1.toTransactionResponse());
                    } catch (CoffeeShopException e ){
                        throw new CoffeeShopException(Constant.SYSTEM_ERROR,null, "Cannot save transaction");
                    }
                } else {
                    throw new CoffeeShopException(Constant.NOT_FOUND,null, "No order found");
                }
            } else {
                throw new Exception("Transaction not found");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error in sending request");
        }
    }
}
