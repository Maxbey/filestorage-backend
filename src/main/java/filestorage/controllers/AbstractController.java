package filestorage.controllers;

import filestorage.models.User;
import filestorage.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class AbstractController {
    @Autowired
    protected UserRepository userRepository;

    protected User getCurrentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(auth.getPrincipal().toString());

        return userRepository.findOne(userId);
    }
}
