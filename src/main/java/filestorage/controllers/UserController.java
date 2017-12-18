package filestorage.controllers;

import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import filestorage.models.User;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController extends AbstractController{
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Collection<User>> searchUsers(@RequestParam(name = "email") String email) {
        return new ResponseEntity<>(userRepository.findByEmailContaining(email), HttpStatus.OK);
    }

    @RequestMapping(path = "/current", method = RequestMethod.GET)
    public ResponseEntity<?> getCurrent() {
        return new ResponseEntity<>(getCurrentUser(), HttpStatus.OK);
    }

}
