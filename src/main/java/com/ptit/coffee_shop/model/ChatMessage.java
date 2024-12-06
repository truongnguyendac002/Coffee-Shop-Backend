package com.ptit.coffee_shop.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "chat_message")
@Data
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    private String content;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;
}
