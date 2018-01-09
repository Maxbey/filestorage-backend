package filestorage.unit.api.auth;

import filestorage.controllers.AuthenticationController;
import filestorage.models.User;
import filestorage.services.AuthenticationService;
import filestorage.services.JWTService;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(value = AuthenticationController.class, secure = false)
public class LoginApiTest {
    private static final String url = "/auth/login/";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    private HashMap<String, String> validPayload;
    private HashMap<String, String> authError;

    @Before
    public void setup(){
        validPayload = new HashMap<>();
        validPayload.put("email", "someemail");
        validPayload.put("password", "pass");

        authError = new HashMap<>();
        authError.put("field", "email");
        authError.put("defaultMessage", "Invalid email or password");
    }

    @Test
    public void testRequestWithInvalidPayload() throws Exception {
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());


    }

    @Test
    public void testRequestWithNonExistentEmail() throws Exception {
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new JSONObject(validPayload).toString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errors[0]", is(authError)));


    }

    @Test
    public void testRequestWithInvalidPassword() throws Exception {
        User userForAuth = new User(validPayload.get("email"), "pass", "lname", "fname");

        when(authenticationService.getUserByEmail(userForAuth.getEmail())).thenReturn(userForAuth);

        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new JSONObject(validPayload).toString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errors[0]", is(authError)));

        verify(authenticationService, times(1)).getUserByEmail(userForAuth.getEmail());


    }

    @Test
    public void testSuccessfulLogin() throws Exception {
        String authToken = "some token";
        User userForAuth = new User(validPayload.get("email"), "pass", "lname", "fname");
        userForAuth.setId(1L);

        when(authenticationService.getUserByEmail(userForAuth.getEmail())).thenReturn(userForAuth);
        when(authenticationService.checkUserPassword(
                userForAuth, validPayload.get("password"))
        ).thenReturn(true);

        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new JSONObject(validPayload).toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", is(JWTService.createJWT(userForAuth))))
                .andExpect(jsonPath("$.user_id", is(userForAuth.getId().toString())));

        verify(authenticationService, times(1)).getUserByEmail(userForAuth.getEmail());
        verify(authenticationService, times(1)).checkUserPassword(
                userForAuth, validPayload.get("password")
        );


    }

}