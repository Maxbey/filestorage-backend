package filestorage.controllers;

import javax.validation.Valid;

import filestorage.validation.ValidationErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import filestorage.models.User;
import filestorage.requests.LoginRequest;
import filestorage.services.AuthenticationService;
import filestorage.services.JWTService;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        User createdUser = authenticationService.createUser(user);

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @RequestMapping(path = "/login",method = RequestMethod.POST)
    public ResponseEntity<?> performLogin(@Valid @RequestBody LoginRequest request) {
        User user = authenticationService.getUserByEmail(request.getEmail());

        if (user == null) {
            return unathorized();
        }

        if (!authenticationService.checkUserPassword(user, request.getPassword())) {
            return unathorized();
        }

        String token = JWTService.createJWT(user);

        Map<String, String> responseData = new HashMap<String, String>();
        responseData.put("token", token);

        return new ResponseEntity<>(responseData, HttpStatus.CREATED);
    }

    private ResponseEntity<?> unathorized(){
        ValidationErrorResponse response = new ValidationErrorResponse();
        response.addError("email", "Invalid email or password.");

        return new ResponseEntity<>(response.getResponse(), HttpStatus.UNAUTHORIZED);
    }
}
