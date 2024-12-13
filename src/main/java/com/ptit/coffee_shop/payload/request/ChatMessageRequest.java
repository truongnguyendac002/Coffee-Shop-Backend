package com.ptit.coffee_shop.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChatMessageRequest {
    @JsonProperty("senderId")
    private long senderId;
    @JsonProperty("content")
    private String content;
    @JsonProperty("conversationId")
    private long conversationId;
}
