
package com.example.pizzastore.service;

import com.example.pizzastore.model.User;
import com.example.pizzastore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Register only with username and password
    public String registerUser(String username, String password) {
        if (userRepository.findByUserName(username).isPresent()) {
            return "User already exists";
        }

        User user = new User();
        user.setUserName(username);
        user.setPassword(password);

        userRepository.save(user);
        return "User registered successfully";
    }

    public Optional<User> updateUser(Long id, User updatedUser) {
        Optional<User> existingUserOpt = userRepository.findById(id);

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            // Only update fields if they are provided and valid
            if (updatedUser.getUserName() != null && !updatedUser.getUserName().isEmpty()) {
                existingUser.setUserName(updatedUser.getUserName());
            }
            if (updatedUser.getEmail() != null && !updatedUser.getEmail().isEmpty()) {
                existingUser.setEmail(updatedUser.getEmail());
            }
            if (updatedUser.getPhoneNumber() != null && !updatedUser.getPhoneNumber().isEmpty()) {
                existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
            }
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                existingUser.setPassword(updatedUser.getPassword());
            }

            return Optional.of(userRepository.save(existingUser));
        } else {
            return Optional.empty();
        }

    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
