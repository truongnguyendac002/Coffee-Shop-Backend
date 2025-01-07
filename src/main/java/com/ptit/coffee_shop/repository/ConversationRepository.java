package com.ptit.coffee_shop.repository;

import com.ptit.coffee_shop.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Query("SELECT c FROM Conversation c WHERE c.host.status = 'ACTIVE'")
    List<Conversation> findByUserIsActive();
    Optional<Conversation> findByHostId(long hostId);
}
