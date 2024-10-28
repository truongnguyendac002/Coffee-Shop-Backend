package com.ptit.coffee_shop.repository;

import com.ptit.coffee_shop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsUserByEmail(String email);

    @Query("SELECT u FROM User u where u.role.name = 'ROLE_USER' ")
    List<User> getAllUser();
}
