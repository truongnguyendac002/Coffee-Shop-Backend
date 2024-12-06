package com.ptit.coffee_shop.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChatMessageResponse {
    @JsonProperty("id")
    private long id;
    @JsonProperty("senderId")
    private long senderId;
    @JsonProperty("senderName")
    private String senderEmail;
    @JsonProperty("content")
    private String content;
}
