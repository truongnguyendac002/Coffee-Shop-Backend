package com.ptit.coffee_shop.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ConversationResponse {
    @JsonProperty("id")
    private long id;
    @JsonProperty("hostId")
    private long hostId;
    @JsonProperty("hostName")
    private String hostName;
    @JsonProperty("hostAvatar")
    private String hostAvatar;
    @JsonProperty("messageList")
    private List<ChatMessageResponse> messageList;
}
