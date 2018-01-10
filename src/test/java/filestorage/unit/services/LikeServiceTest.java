package filestorage.unit.services;

import filestorage.models.File;
import filestorage.models.Like;
import filestorage.models.User;
import filestorage.repositories.LikeRepository;
import filestorage.services.LikeService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@PrepareForTest(Like.class)
@WebMvcTest(value = LikeService.class)
public class LikeServiceTest {
    @MockBean
    private LikeRepository likeRepository;

    @Autowired
    private LikeService service;

    private User user;
    private File file;

    @Before
    public void setup(){
        user = new User("email", "pass", "fname", "lname");
        user.setId(1L);

        file = new File("file", "content", user);
        file.setId(2L);
    }

    @Test
    public void testGetLike() throws Exception {
        service.getLike(user, file);

        verify(likeRepository, times(1)).findByUserIdAndFileId(1L, 2L);
    }

    @Test
    public void testCreateLike() throws Exception {
        service.createLike(user, file);

        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    public void testRemoveLike() throws Exception {
        Like like = new Like(user, file);
        service.removeLike(like);

        verify(likeRepository, times(1)).delete(like);
    }
}
