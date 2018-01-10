package filestorage.unit.services;

import com.sun.tools.javac.util.List;
import filestorage.models.File;
import filestorage.models.Group;
import filestorage.models.User;
import filestorage.repositories.FileRepository;
import filestorage.services.FileService;
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
@WebMvcTest(value = FileService.class)
public class FileServiceTest {
    @MockBean
    private FileRepository fileRepository;

    @Autowired
    private FileService service;

    private User user;
    private File file;

    private Set<User> users;
    private Set<File> files;
    private Set<Group> groups;

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

        groups = new HashSet<>();

        Group groupOne = new Group("one", user, users, files);
        Group groupTwo = new Group("two", user, users, files);

        groupOne.setId(11L);
        groupTwo.setId(12L);
        groups.add(groupOne);
        groups.add(groupTwo);
    }

    @Test
    public void testGetUserFile() throws Exception {
        File file = new File("file", "content", user);
        file.setId(1L);

        when(fileRepository.findByIdAndUserId(file.getId(), user.getId())).thenReturn(file);

        assertEquals(service.getUserFile(file.getId(), user), file);
        verify(fileRepository, times(1)).findByIdAndUserId(file.getId(), user.getId());
    }

    @Test
    public void testCreateFile() throws Exception {
        service.createFile(user, "", "");
        verify(fileRepository, times(1)).save(any(File.class));
    }

    @Test
    public void testGetUserFavoriteFiles() throws Exception {
        when(fileRepository.findByLikes_user_id(user.getId())).thenReturn(files);

        assertEquals(service.getUserFavoriteFiles(user), files);

        verify(fileRepository, times(1)).findByLikes_user_id(user.getId());
    }

    @Test
    public void testRemoveFile() throws Exception {
        service.removeFile(file);
        verify(fileRepository, times(1)).delete(file);
    }

    @Test
    public void testGetUserAvaliableFiles() throws Exception {
        user.setGroups(groups);
        when(fileRepository.findByUserIdOrGroups_idIn(user.getId(), new HashSet<>(List.of(11L, 12L))))
                .thenReturn(files);

        assertEquals(service.getUserAvailableFiles(user), files);
        verify(fileRepository, times(1)).
                findByUserIdOrGroups_idIn(user.getId(), new HashSet<>(List.of(11L, 12L)));
    }

    @Test
    public void testGetUserAvaliableFileWhenOwner() throws Exception {
        user.setGroups(groups);
        when(fileRepository.findByIdAndUserId(file.getId(), user.getId()))
                .thenReturn(file);

        assertEquals(service.getUserAvailableFile(file.getId(), user), file);
        verify(fileRepository, times(1)).
                findByIdAndUserId(file.getId(), user.getId());
    }

    @Test
    public void testGetUserAvaliableFileWhenGroupMember() throws Exception {
        user.setGroups(groups);
        when(fileRepository.findByIdAndGroups_idIn(file.getId(), new HashSet<Long>(List.of(11L, 12L))))
                .thenReturn(file);

        assertEquals(service.getUserAvailableFile(file.getId(), user), file);
        verify(fileRepository, times(1))
                .findByIdAndGroups_idIn(file.getId(), new HashSet<Long>(List.of(11L, 12L)));
    }
}

