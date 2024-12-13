package com.ptit.coffee_shop.repository;

import com.ptit.coffee_shop.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findByHostId(long hostId);
}
