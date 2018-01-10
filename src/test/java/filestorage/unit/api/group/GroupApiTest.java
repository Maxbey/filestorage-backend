package filestorage.unit.api.group;

import com.google.gson.Gson;
import filestorage.Application;
import filestorage.WebSecurity;
import filestorage.controllers.GroupController;
import filestorage.models.File;
import filestorage.models.Group;
import filestorage.models.User;
import filestorage.repositories.UserRepository;
import filestorage.requests.GroupRequest;
import filestorage.services.GroupService;
import filestorage.services.JWTService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(value = GroupController.class, secure = false)
@ContextConfiguration(classes = {WebSecurity.class, Application.class})
@AutoConfigureMockMvc
public class GroupApiTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private GroupService groupService;

    Gson serializer;

    private User existedUser;
    private String authToken;

    private Set<User> users;
    private Set<File> files;

    @Before
    public void setup(){
        existedUser = new User("email@email.com", "pass", "fname", "lname");
        existedUser.setId(1L);
        authToken = JWTService.createJWT(existedUser);

        users = new HashSet<User>();
        users.add(new User("fist", "pass", "fname", "lname"));
        users.add(new User("second", "pass", "fname", "lname"));

        files = new HashSet<File>();
        files.add(new File("first file", "something", existedUser));
        files.add(new File("second file", "something", existedUser));

        serializer = new Gson();
        when(userRepository.findOne(existedUser.getId())).thenReturn(existedUser);
    }

    @Test
    public void testGetGroupsList() throws Exception {
        Set<Group> groups = new HashSet<Group>();

        groups.add(new Group("first", existedUser, users, files));
        groups.add(new Group("second", existedUser, users, files));

        when(groupService.getUserGroups(existedUser)).thenReturn(groups);
        mockMvc.perform(get("/group/")
                .header("Authorization", "Token " + authToken))
                .andExpect(status().isOk())
                .andExpect(content().json(new Gson().toJson(groups)));

        verify(userRepository, times(1)).findOne(existedUser.getId());
        verify(groupService, times(1)).getUserGroups(existedUser);
    }

    @Test
    public void testGetSingleGroup() throws Exception {
        Group group = new Group("group", existedUser, users, files);
        Long groupId = 1L;

        when(groupService.getGroup(groupId, existedUser)).thenReturn(group);
        mockMvc.perform(get("/group/{id}", groupId)
                .header("Authorization", "Token " + authToken))
                .andExpect(status().isOk())
                .andExpect(content().json(new Gson().toJson(group)));

        verify(userRepository, times(1)).findOne(existedUser.getId());
        verify(groupService, times(1)).getGroup(groupId, existedUser);
    }

    @Test
    public void testGetMissingGroup() throws Exception {
        Long groupId = 1L;

        when(groupService.getGroup(groupId, existedUser)).thenReturn(null);
        mockMvc.perform(get("/group/{id}", groupId)
                .header("Authorization", "Token " + authToken))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{'error': 'Resource not found.'}"));

        verify(userRepository, times(1)).findOne(existedUser.getId());
        verify(groupService, times(1)).getGroup(groupId, existedUser);
    }

    @Test
    public void testAttemptToCreateGroupWithInvalidBody() throws Exception {
        mockMvc.perform(post("/group/")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Token " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateGroup() throws Exception {
        Group group = new Group("second", existedUser, users, files);

        HashMap<String, Object> payload = new HashMap<>();

        ArrayList<Long> fileIds = new ArrayList<>();
        fileIds.add(1L);
        fileIds.add(2L);

        ArrayList<Long> userIds = new ArrayList<>();
        userIds.add(3L);
        userIds.add(4L);

        payload.put("name", "groupname");
        payload.put("files", fileIds);
        payload.put("users", userIds);

        when(groupService.createGroup(any(GroupRequest.class), eq(existedUser))).thenReturn(group);

        mockMvc.perform(post("/group/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializer.toJson(payload))
                .header("Authorization", "Token " + authToken))
                .andExpect(status().isCreated())
                .andExpect(content().json(serializer.toJson(group)));

        verify(userRepository, times(1)).findOne(existedUser.getId());
        verify(groupService, times(1)).createGroup(any(GroupRequest.class), eq(existedUser));
    }

    @Test
    public void testAttemptToEditMissingGroup() throws Exception {
        Long groupId = 1L;
        HashMap<String, Object> payload = new HashMap<>();

        payload.put("name", "groupname");
        payload.put("files", new ArrayList<>());
        payload.put("users", new ArrayList<>());

        when(groupService.getGroup(groupId, existedUser)).thenReturn(null);

        mockMvc.perform(put("/group/{id}/", groupId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializer.toJson(payload))
                .header("Authorization", "Token " + authToken))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{'error': 'Resource not found.'}"));

        verify(userRepository, times(1)).findOne(existedUser.getId());
        verify(groupService, times(1)).getGroup(groupId, existedUser);
    }

    @Test
    public void testEditGroup() throws Exception {
        Long groupId = 1L;
        Group group = new Group("second", existedUser, new HashSet<User>(), new HashSet<File>());
        Group updatedGroup = new Group("updated", existedUser, users, files);

        HashMap<String, Object> payload = new HashMap<>();

        payload.put("name", "groupname");
        payload.put("files", new ArrayList<>());
        payload.put("users", new ArrayList<>());

        when(groupService.getGroup(groupId, existedUser)).thenReturn(group);
        when(groupService.updateGroup(eq(group), any(GroupRequest.class), eq(existedUser))).thenReturn(updatedGroup);

        mockMvc.perform(put("/group/{id}/", groupId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializer.toJson(payload))
                .header("Authorization", "Token " + authToken))
                .andExpect(status().isAccepted())
                .andExpect(content().json(serializer.toJson(updatedGroup)));

        verify(userRepository, times(1)).findOne(existedUser.getId());
        verify(groupService, times(1)).getGroup(groupId, existedUser);
        verify(groupService, times(1)).updateGroup(
                eq(group), any(GroupRequest.class), eq(existedUser)
        );
    }

    @Test
    public void testAttemptToRemoveMissingGroup() throws Exception {
        Long groupId = 1L;

        when(groupService.getGroup(groupId, existedUser)).thenReturn(null);
        mockMvc.perform(delete("/group/{id}", groupId)
                .header("Authorization", "Token " + authToken))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{'error': 'Resource not found.'}"));

        verify(userRepository, times(1)).findOne(existedUser.getId());
        verify(groupService, times(1)).getGroup(groupId, existedUser);
    }

    @Test
    public void testRemoveGroup() throws Exception {
        Group group = new Group("second", existedUser, users, files);
        Long groupId = 1L;

        when(groupService.getGroup(groupId, existedUser)).thenReturn(group);
        mockMvc.perform(delete("/group/{id}", groupId)
                .header("Authorization", "Token " + authToken))
                .andExpect(status().isNoContent());

        verify(userRepository, times(1)).findOne(existedUser.getId());
        verify(groupService, times(1)).getGroup(groupId, existedUser);
        verify(groupService, times(1)).removeGroup(group);
    }
}
