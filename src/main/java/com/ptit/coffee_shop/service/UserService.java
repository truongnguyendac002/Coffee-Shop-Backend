package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.common.enums.Status;
import com.ptit.coffee_shop.model.User;
import com.ptit.coffee_shop.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUserById(Long id){
        return userRepository.getById(id);
    }

    public Optional<User> getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }

    public User banUser(Long id){
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setStatus(Status.INACTIVE);
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }

    public User unbanUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setStatus(Status.ACTIVE);
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }

    public User updateUserInfo(Long userId, User updatedUser){
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User currentUser = optionalUser.get();
            currentUser.setName(updatedUser.getName());
            currentUser.setPhone(updatedUser.getPhone());
            currentUser.setUpdated_at(new Date());
            currentUser.setProfile_img(updatedUser.getProfile_img());
            return userRepository.save(currentUser);
        }
        throw new RuntimeException("User not found");
    }
}