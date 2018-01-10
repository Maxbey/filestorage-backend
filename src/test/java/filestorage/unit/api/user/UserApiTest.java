package filestorage.unit.api.user;

import com.google.gson.Gson;
import filestorage.Application;
import filestorage.WebSecurity;
import filestorage.controllers.UserController;
import filestorage.models.User;
import filestorage.repositories.UserRepository;
import filestorage.services.AuthenticationService;
import filestorage.services.JWTService;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(value = UserController.class, secure = false)
@ContextConfiguration(classes = {WebSecurity.class, Application.class})
@AutoConfigureMockMvc()
public class UserApiTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthenticationManager authenticationManager;

    private User existedUser;
    private String authToken;

    @Before
    public void setup(){
        existedUser = new User("email@email.com", "pass", "fname", "lname");
        existedUser.setId(1L);

        authToken = JWTService.createJWT(existedUser);

        when(userRepository.findOne(existedUser.getId())).thenReturn(existedUser);
    }

    @Test
    public void testAttemptToGetCurrentUserWithoutToken() throws Exception {
        mockMvc.perform(get("/user/current"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetCurrentUserWithInvalidToken() throws Exception {
        mockMvc.perform(get("/user/current")
                .header("Authorization", "Token aa"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetCurrentUserWithInvalidFormatToken() throws Exception {
        mockMvc.perform(get("/user/current")
                .header("Authorization", "Token"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetCurrentUser() throws Exception {
        mockMvc.perform(get("/user/current/")
                .header("Authorization", "Token " + authToken))
                .andExpect(status().isOk())
                .andExpect(content().json(new Gson().toJson(existedUser)));

        verify(userRepository, times(1)).findOne(existedUser.getId());
    }

    @Test
    public void testGetUsersWithoutEmailParam() throws Exception {
        mockMvc.perform(get("/user/")
                .header("Authorization", "Token " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetUsersByEmail() throws Exception {
        String emailParam = "email";
        List<User> users = new ArrayList<>();

        users.add(new User("first", "pass", "fname", "lname"));
        users.add(new User("second", "pass", "fname", "lname"));

        when(userRepository.findByEmailContaining(emailParam)).thenReturn(users);

        mockMvc.perform(get("/user/?email=" + emailParam)
                .header("Authorization", "Token " + authToken))
                .andExpect(status().isOk())
                .andExpect(content().json(new Gson().toJson(users)));
    }

}
