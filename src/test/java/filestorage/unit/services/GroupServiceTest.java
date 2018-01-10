package filestorage.unit.services;

import filestorage.models.File;
import filestorage.models.Group;
import filestorage.models.User;
import filestorage.repositories.FileRepository;
import filestorage.repositories.GroupRepository;
import filestorage.repositories.UserRepository;
import filestorage.requests.GroupRequest;
import filestorage.services.GroupService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(value = GroupService.class)
public class GroupServiceTest {
    @MockBean
    private GroupRepository groupRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private FileRepository fileRepository;

    @Autowired
    private GroupService service;

    private User user;
    private File file;

    private Set<User> users;
    private Set<File> files;

    @Before
    public void setup(){
        user = new User("email", "pass", "fname", "lname");
        user.setId(1L);

        file = new File("file", "content", user);
        file.setId(2L);

        users = new HashSet<>();
        users.add(user);

        files = new HashSet<>();
        files.add(file);
    }

    @Test
    public void testGetGroup() throws Exception {
        Group group = new Group("group", user, new HashSet<User>(), new HashSet<File>());
        group.setId(1L);

        when(groupRepository.findByIdAndOwnerId(group.getId(), user.getId())).thenReturn(group);

        assertEquals(service.getGroup(group.getId(), user), group);
        verify(groupRepository, times(1)).findByIdAndOwnerId(
                group.getId(), user.getId()
        );
    }

    @Test
    public void testGetUserGroups() throws Exception {
        HashSet<Group> groups = new HashSet<>();

        groups.add(new Group("group", user, new HashSet<User>(), new HashSet<File>()));
        groups.add(new Group("group1", user, new HashSet<User>(), new HashSet<File>()));

        when(groupRepository.findByOwnerId(user.getId())).thenReturn(groups);

        assertEquals(service.getUserGroups(user), groups);
        verify(groupRepository, times(1)).findByOwnerId(
                user.getId()
        );
    }

    @Test
    public void testCreateGroup() throws Exception {
        GroupRequest request = new GroupRequest();
        HashSet<Long> fileIds = new HashSet<>();
        HashSet<Long> userIds = new HashSet<>();
        fileIds.add(1L);
        userIds.add(2L);

        request.setName("group");
        request.setFiles(fileIds);
        request.setUsers(userIds);
        when(userRepository.findByIdIn(request.getUsers())).thenReturn(users);
        when(fileRepository.findByIdInAndUserId(request.getFiles(), user.getId())).thenReturn(files);

        service.createGroup(request, user);

        verify(userRepository, times(1)).findByIdIn(request.getUsers());
        verify(fileRepository, times(1)).findByIdInAndUserId(request.getFiles(), user.getId());
        verify(groupRepository, times(1)).save(any(Group.class));
    }

    @Test
    public void testUpdateGroup() throws Exception {
        Group group = new Group("old", user, new HashSet<>(), new HashSet<>());
        GroupRequest request = new GroupRequest();
        HashSet<Long> fileIds = new HashSet<>();
        HashSet<Long> userIds = new HashSet<>();
        fileIds.add(1L);
        userIds.add(2L);

        request.setName("group");
        request.setFiles(fileIds);
        request.setUsers(userIds);

        when(groupRepository.save(group)).thenReturn(group);
        when(userRepository.findByIdIn(request.getUsers())).thenReturn(users);
        when(fileRepository.findByIdInAndUserId(request.getFiles(), user.getId())).thenReturn(files);

        assertEquals(service.updateGroup(group, request, user), group);

        verify(userRepository, times(1)).findByIdIn(request.getUsers());
        verify(fileRepository, times(1)).findByIdInAndUserId(request.getFiles(), user.getId());
        verify(groupRepository, times(1)).save(group);

        assertEquals(group.getName(), request.getName());
        assertEquals(group.getUsers(), users);
        assertEquals(group.getFiles(), files);
    }

    @Test
    public void testRemoveGroup() throws Exception {
        Group group = new Group("group", user, new HashSet<User>(), new HashSet<File>());
        group.setId(1L);

        service.removeGroup(group);
        verify(groupRepository, times(1)).delete(group);
    }
}

