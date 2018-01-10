package filestorage.unit.services;

import filestorage.models.User;
import filestorage.repositories.UserRepository;
import filestorage.services.AuthenticationService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(value = AuthenticationService.class)
public class AuthenticationServiceTest {
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AuthenticationService service;

    private User user;

    @Before
    public void setup(){
        user = new User("email", "pass", "fname", "lname");
        user.setId(1L);
    }

    @Test
    public void testIsEmailFreeWhenFree() throws Exception {
        String email = "email";

        when(userRepository.findByEmail(email)).thenReturn(null);
        assertTrue(service.isEmailFree(email));

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void testIsEmailFreeWhenAlreadyTaken() throws Exception {
        String email = "email";

        when(userRepository.findByEmail(email)).thenReturn(user);
        assertFalse(service.isEmailFree(email));

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void testCreateUser() throws Exception {
        String oldPass = user.getPassword();
        String encodedPass = "encoded";
        when(bCryptPasswordEncoder.encode(user.getPassword())).thenReturn(encodedPass);

        service.createUser(user);
        assertEquals(user.getPassword(), encodedPass);

        verify(userRepository, times(1)).save(any(User.class));
        verify(bCryptPasswordEncoder, times(1)).encode(oldPass);
    }

    @Test
    public void testCheckUserPassword() throws Exception {
        String toCheck = "encoded";
        when(bCryptPasswordEncoder.matches(toCheck, user.getPassword())).thenReturn(true);

        assertTrue(service.checkUserPassword(user, toCheck));
        verify(bCryptPasswordEncoder, times(1)).matches(toCheck, user.getPassword());
    }

    @Test
    public void getUserByEmail() throws Exception {
        String email = "enail";

        when(userRepository.findByEmail(email)).thenReturn(user);
        assertEquals(service.getUserByEmail(email), user);

        verify(userRepository, times(1)).findByEmail(email);
    }
}

