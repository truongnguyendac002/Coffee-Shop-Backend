package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.model.ChatMessage;
import com.ptit.coffee_shop.model.Conversation;
import com.ptit.coffee_shop.model.User;
import com.ptit.coffee_shop.payload.request.ChatMessageRequest;
import com.ptit.coffee_shop.payload.response.ChatMessageResponse;
import com.ptit.coffee_shop.payload.response.ConversationResponse;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.repository.ChatMessageRepository;
import com.ptit.coffee_shop.repository.ConversationRepository;
import com.ptit.coffee_shop.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final MessageBuilder messageBuilder;
    @Transactional
    public RespMessage updateMessage(ChatMessageRequest message, long conversationId) {
        ChatMessage chatMessage = new ChatMessage();

        Optional<Conversation> conversation = conversationRepository.findById(conversationId);
        if (conversation.isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"conversation Id"} ,"conversation id not found");
        }
        chatMessage.setConversation(conversation.get());

        Optional<User> sender = userRepository.findById(message.getSenderId());
        if (sender.isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"sender Id"} ,"sender id not found");
        }
        chatMessage.setSender(sender.get());

        chatMessage.setContent(message.getContent());

        try {
            chatMessageRepository.save(chatMessage);
            ConversationResponse response = getConversationResponse(chatMessage.getConversation());
            return messageBuilder.buildSuccessMessage(response);
        } catch (Exception e) {
            throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[]{"chat message"} ,"save chat message error");
        }
    }

    private ChatMessageResponse convertMessageResponse(ChatMessage chatMessage) {
        ChatMessageResponse response = new ChatMessageResponse();
        response.setId(chatMessage.getId());
        response.setSenderId(chatMessage.getSender().getId());
        response.setContent(chatMessage.getContent());
        response.setSenderEmail(chatMessage.getSender().getEmail());
        return response;
    }

    private ConversationResponse getConversationResponse(Conversation conversation){
        ConversationResponse response = new ConversationResponse();

        response.setId(conversation.getId());

        User host = conversation.getHost();
        response.setHostId(host.getId());
        response.setHostName(host.getEmail());
        response.setHostAvatar(host.getProfile_img());

        List<ChatMessage> messageList = chatMessageRepository.findByConversationId(conversation.getId());
        List<ChatMessageResponse> chatMessageResponseList = new ArrayList<>();
        for (ChatMessage message : messageList) {
            ChatMessageResponse chatMessageResponse = convertMessageResponse(message);
            chatMessageResponseList.add(chatMessageResponse);
        }
        response.setMessageList(chatMessageResponseList);

        return response;
    }


    public RespMessage getAllConversation() {
        List<Conversation> allConversation = conversationRepository.findByUserIsActive();
        List<ConversationResponse> conversationResponseList = new ArrayList<>();
        for (Conversation conversation : allConversation) {
            ConversationResponse response = getConversationResponse(conversation);
            conversationResponseList.add(response);
        }
        return messageBuilder.buildSuccessMessage(conversationResponseList);

    }

    public RespMessage getConversationById(long id) {
        Optional<Conversation> conversation = conversationRepository.findById(id);
        if (conversation.isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"conversation Id"} ,"conversation id not found");
        }
        ConversationResponse response = getConversationResponse(conversation.get());
        return messageBuilder.buildSuccessMessage(response);
    }

    public RespMessage getConversationByHostId(long userId) {
        Optional<User> hostOp = userRepository.findById(userId);
        if (hostOp.isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"user Id"} ,"user id not found");
        }
        Optional<Conversation> conversation = conversationRepository.findByHostId(hostOp.get().getId());
        if (conversation.isEmpty()) {
            return createConversation(userId);
        }
        ConversationResponse response = getConversationResponse(conversation.get());
        return messageBuilder.buildSuccessMessage(response);
    }

    public RespMessage createConversation(long hostId) {
        Optional<User> host = userRepository.findById(hostId);
        if (host.isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"user 1 Id"} ,"user 1 id not found");
        }

        Conversation conversation = new Conversation();
        conversation.setHost(host.get());

        conversationRepository.save(conversation);
        return messageBuilder.buildSuccessMessage(getConversationResponse(conversation));
    }
}
