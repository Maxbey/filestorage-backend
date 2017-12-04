package filestorage.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import filestorage.models.User;
import filestorage.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public boolean isEmailFree(String email){
        User user = userRepository.findByEmail(email);

        return user == null;
    }

    public User createUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public boolean checkUserPassword(User user, String toCheck){
        return bCryptPasswordEncoder.matches(toCheck, user.getPassword());
    }

    public User getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }
}
