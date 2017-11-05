package filestorage.controllers;

import filestorage.models.User;
import filestorage.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractController {
    @Autowired
    protected UserRepository userRepository;

    protected User getCurrentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(auth.getPrincipal().toString());

        return userRepository.findOne(userId);
    }

    protected ResponseEntity<?> notFound(){
        Map<String, String> responseData = new HashMap<String, String>();
        responseData.put("error", "Resource not found.");

        return new ResponseEntity<>(responseData, HttpStatus.NOT_FOUND);
    }
}
