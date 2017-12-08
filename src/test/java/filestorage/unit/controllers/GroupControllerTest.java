package filestorage.unit.controllers;

import filestorage.controllers.GroupController;
import filestorage.models.File;
import filestorage.models.Group;
import filestorage.models.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.*;

@RunWith(SpringRunner.class)
@WebMvcTest(value = GroupController.class, secure = false)
@AutoConfigureMockMvc

public class GroupControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupController groupController;

    private User mockUser;
    private User otherMockUser;

    @Before
    public void setup() {
        mockUser = new User("test@test.com", "pass", "fName", "sName", new HashSet<File>(), new HashSet<Group>());
        otherMockUser = new User("fowler@gmail.com", "passWORD", "martin", "fowler", new HashSet<File>(), new HashSet<Group>());
    }

    @Test
    @WithMockUser
    public void testGetGroupById() throws Exception {
        long groupId = 1;
        Group group = new Group("group", mockUser, new HashSet<User>(), new HashSet<File>());
        ResponseEntity responseEntity = new ResponseEntity(group, HttpStatus.OK);

        when(groupController.getGroup(Mockito.anyLong())).thenReturn(responseEntity);

        mockMvc.perform(get("/group/{id}/", groupId)
                .header("Content-Type", "application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("name", is(group.getName())))
                .andExpect(jsonPath("owner.email", is(mockUser.getEmail())))
                .andExpect(jsonPath("owner.firstName", is(mockUser.getFirstName())))
                .andExpect(jsonPath("owner.lastName", is(mockUser.getLastName())))
                .andExpect(jsonPath("users", empty()))
                .andExpect(jsonPath("files", empty()))
                .andReturn();
    }

    @Test
    @WithMockUser
    public void testGetListGroups() throws Exception {
        List<Group> groups = new ArrayList<>();
        groups.add(new Group("group", mockUser, new HashSet<User>(), new HashSet<File>()));
        groups.add(new Group("group1", otherMockUser, new HashSet<User>(), new HashSet<File>()));
        groups.add(new Group("group_new", mockUser, new HashSet<User>(), new HashSet<File>()));

        ResponseEntity responseEntity = new ResponseEntity(groups, HttpStatus.OK);

        when(groupController.listGroups()).thenReturn(responseEntity);

        mockMvc.perform(get("/group/")
                .header("Content-Type", "application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name", is(groups.get(0).getName())))
                .andExpect(jsonPath("$[1].name", is(groups.get(1).getName())))
                .andExpect(jsonPath("$[2].name", is(groups.get(2).getName())))
                .andExpect(jsonPath("$[0].owner.email", is(groups.get(0).getOwner().getEmail())))
                .andExpect(jsonPath("$[1].owner.email", is(groups.get(1).getOwner().getEmail())))
                .andExpect(jsonPath("$[2].owner.email", is(groups.get(2).getOwner().getEmail())))
                .andReturn();
    }
}
