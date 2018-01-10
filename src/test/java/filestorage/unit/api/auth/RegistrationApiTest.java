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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(value = AuthenticationController.class, secure = false)
@AutoConfigureMockMvc(secure = false)
public class RegistrationApiTest {
    private static final String url = "/auth/register/";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    private HashMap<String, String> validPayload;
    private HashMap<String, String> authError;

    @Before
    public void setup(){
        validPayload = new HashMap<>();
        validPayload.put("email", "email@email.com");
        validPayload.put("firstName", "fname");
        validPayload.put("lastName", "lname");
        validPayload.put("password", "passpass1");
    }

    @Test
    public void testRequestWithInvalidPayload() throws Exception {
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());


    }

    @Test
    public void testAttemptToRegisterWithExistedEmail() throws Exception {
        User existedUser = new User(validPayload.get("email"), "pass", "lname", "fname");
        HashMap<String, String> expectedError = new HashMap<String, String>();
        expectedError.put("field", "email");
        expectedError.put("defaultMessage", "User with this email already exists");

        when(authenticationService.getUserByEmail(existedUser.getEmail())).thenReturn(existedUser);

        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new JSONObject(validPayload).toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is(expectedError)));

        verify(authenticationService, times(1)).isEmailFree(existedUser.getEmail());
    }

    @Test
    public void testSuccessfulRegistration() throws Exception {
        User user = new User(validPayload.get("email"), "pass", "lname", "fname");
        user.setId(1L);
        when(authenticationService.createUser(any(User.class))).thenReturn(user);
        when(authenticationService.isEmailFree(user.getEmail())).thenReturn(true);

        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new JSONObject(validPayload).toString()))
                .andExpect(status().isCreated())
                .andExpect(content().json(
                        "{'email':'email@email.com', 'firstName': 'lname', 'lastName': 'fname'}")
                );

        verify(authenticationService, times(1)).isEmailFree(user.getEmail());
    }

}